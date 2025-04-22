package com.example.reclaimcse489;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class EditPost extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private RadioGroup lostFoundRadioGroup;
    private ImageView postImageView;
    private AppCompatButton saveButton, addImageButton, deleteImageButton, deleteButton;
    private String postId, imageUrl;
    private Uri imageUri;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        lostFoundRadioGroup = findViewById(R.id.lostFoundRadioGroup);
        postImageView = findViewById(R.id.postImageView);
        saveButton = findViewById(R.id.save);
        addImageButton = findViewById(R.id.addImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);
        deleteButton = findViewById(R.id.delete);
        sqliteHelper = new SqliteHelper(this);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        imageUrl = intent.getStringExtra("imageUrl");

        titleEditText.setText(intent.getStringExtra("title"));
        descriptionEditText.setText(intent.getStringExtra("description"));
        if ("Lost Item".equals(intent.getStringExtra("itemType"))) {
            lostFoundRadioGroup.check(R.id.lostRadioButton);
        } else {
            lostFoundRadioGroup.check(R.id.foundRadioButton);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(postImageView);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePostChanges();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageFromFirebase();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePostFromFirebase();
            }
        });
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImageToFirebase();
                }
            }
    );

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference imageRef = storage.getReference().child("post_images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        Picasso.get().load(imageUrl).into(postImageView);
                        updateFirestore("imageUrl", imageUrl);
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteImageFromFirebase() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                imageUrl = null;
                postImageView.setImageResource(0);
                updateFirestore("imageUrl", null);
                Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePostChanges() {
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedDescription = descriptionEditText.getText().toString().trim();
        String updatedItemType = (lostFoundRadioGroup.getCheckedRadioButtonId() == R.id.lostRadioButton) ? "Lost Item" : "Found Item";

        // Update in Firebase
        Map<String, Object> updatedPost = new HashMap<>();
        updatedPost.put("title", updatedTitle);
        updatedPost.put("description", updatedDescription);
        updatedPost.put("type", updatedItemType);
        if (imageUrl != null) updatedPost.put("imageUrl", imageUrl);

        firestore.collection("posts").document(postId)
                .update(updatedPost)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();

                    // Update in SQLite
                    sqliteHelper.updatePost(postId, updatedTitle, updatedDescription, updatedItemType, imageUrl);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show());
    }

    private void updateFirestore(String key, Object value) {
        firestore.collection("posts").document(postId)
                .update(key, value)
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show());
    }

    private void deletePostFromFirebase() {
        firestore.collection("posts").document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();

                    // Delete from SQLite
                    sqliteHelper.deletePost(postId);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                });
    }
}
