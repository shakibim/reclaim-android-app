package com.example.reclaimcse489;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, descriptionEditText;
    private RadioGroup lostFoundRadioGroup;
    private Button postButton, addImageButton;
    private Uri imageUri;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        lostFoundRadioGroup = view.findViewById(R.id.lostFoundRadioGroup);
        postButton = view.findViewById(R.id.postButton);
        addImageButton = view.findViewById(R.id.addImageButton);
        postButton.setOnClickListener(v -> postData());
        addImageButton.setOnClickListener(v -> openImageChooser());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
        }
    }

    private void postData() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        int selectedId = lostFoundRadioGroup.getCheckedRadioButtonId();

        // Validate the input fields
        if (TextUtils.isEmpty(title)) {
            titleEditText.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Description is required");
            return;
        }

        if (selectedId == -1) {
            Toast.makeText(getContext(), "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedButton = lostFoundRadioGroup.findViewById(selectedId);
        String lostOrFound = selectedButton.getText().toString();

        if (imageUri != null) {
            uploadImageToFirebase(title, description, lostOrFound);
        } else {
            savePostToFirestoreAndSQLite(title, description, lostOrFound, null);
        }
    }

    private void uploadImageToFirebase(String title, String description, String lostOrFound) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("post_images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        savePostToFirestoreAndSQLite(title, description, lostOrFound, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePostToFirestoreAndSQLite(String title, String description, String lostOrFound, String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        // Create a map to store the post data
        Map<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("timestamp", timestamp);
        post.put("title", title);
        post.put("description", description);
        post.put("type", lostOrFound);
        if (imageUrl != null) {
            post.put("imageUrl", imageUrl);
        }

        // Save to Firestore
        firestore.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Post added successfully", Toast.LENGTH_SHORT).show();
                    savePostToSQLite(title, description, lostOrFound, imageUrl);
                    resetForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add post", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePostToSQLite(String title, String description, String lostOrFound, String imageUrl) {
        db = getContext().openOrCreateDatabase("AppDatabase", getContext().MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Posts (id INTEGER PRIMARY KEY, title TEXT, description TEXT, lostOrFound TEXT, imageUrl TEXT, timestamp LONG)");

        // Insert post into SQLite
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("lostOrFound", lostOrFound);
        values.put("imageUrl", imageUrl);
        values.put("timestamp", System.currentTimeMillis());

        db.insert("Posts", null, values);
    }

    private void resetForm() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        lostFoundRadioGroup.clearCheck();
        imageUri = null;
    }
}
