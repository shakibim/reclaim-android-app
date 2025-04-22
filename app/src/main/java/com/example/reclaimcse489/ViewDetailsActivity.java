package com.example.reclaimcse489;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView userGreetingTextView, usernameTextView, timeTextView, itemTypeTextView, emailTextView;
    private TextView itemTitleTextView, itemDescriptionTextView;
    private ImageView postImageView;
    private ListView commentsListView;
    private EditText commentEditText;
    private Button addCommentButton;
    private ArrayList<Comment> comments;
    private CommentsAdapter commentsAdapter;
    private String postId;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));

        firestore = FirebaseFirestore.getInstance();


        userGreetingTextView = findViewById(R.id.userGreetingTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        timeTextView = findViewById(R.id.timeTextView);
        emailTextView = findViewById(R.id.emailTextView);
        itemTypeTextView = findViewById(R.id.itemTypeTextView);
        itemTitleTextView = findViewById(R.id.titleTextView);
        itemDescriptionTextView = findViewById(R.id.descriptionTextView);
        postImageView = findViewById(R.id.postImageView);
        commentsListView = findViewById(R.id.commentsListView);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);

        Intent intent = getIntent();
        postId = intent != null ? intent.getStringExtra("postId") : null;

        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Invalid Post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, comments);
        commentsListView.setAdapter(commentsAdapter);

        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadItemDetails();
                    }
                });
            }
        }).start();

        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadComments();
                    }
                });
            }
        }).start();

        addCommentButton.setOnClickListener(v -> addComment());
    }

    private void loadItemDetails() {
        new Thread(() -> {
            try {
                DocumentSnapshot postSnapshot = Tasks.await(firestore.collection("posts").document(postId).get());

                if (postSnapshot.exists()) {
                    String userID = postSnapshot.getString("userId");
                    long timestamp = postSnapshot.getLong("timestamp");
                    String itemType = postSnapshot.getString("type");
                    String title = postSnapshot.getString("title");
                    String description = postSnapshot.getString("description");
                    String time = convertTimestampToString(timestamp);
                    String username = getUsername(userID);
                    String email = getEmail(userID);
                    runOnUiThread(() -> {
                        usernameTextView.setText(username != null ? username : "Unknown User");
                        timeTextView.setText(time != null ? "Posted on: " +time : "Unknown Time");
                        itemTypeTextView.setText(itemType != null ? "Item Type: " + itemType : "Item Type: Unknown");
                        itemTitleTextView.setText(title != null ? title : "No Title");
                        itemDescriptionTextView.setText(description != null ? description : "No Description");
                        emailTextView.setText(email != null ? "Email: " +email : "not found");

                        // If post contains an image URL
                        String imageUrl = postSnapshot.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load image using Picasso
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(postImageView);  // The ImageView where the image will be loaded
                        }
                    });
                } else {

                    runOnUiThread(() ->
                            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to load item details: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void loadComments() {
        CollectionReference commentsRef = firestore.collection("posts").document(postId).collection("comments");


        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        comments.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userID = document.getString("userID");
                            String commentText = document.getString("commentText");
                            long timestamp = document.getLong("timestamp");
                            comments.add(new Comment(userID, timestamp, commentText));
                        }
                        commentsAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(commentsListView);
                    }
                });
    }
    private void addComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference commentsRef = firestore.collection("posts").document(postId).collection("comments");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();
        Comment newComment = new Comment(userId, timestamp, commentText);
        commentsRef.add(newComment)
                .addOnSuccessListener(documentReference -> {
                    commentEditText.setText("");
                    loadComments();
                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                });
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 120;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public String convertTimestampToString(Long timestamp) {
        try {
            long timeMillis = timestamp;
            Date date = new Date(timeMillis);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a", Locale.ENGLISH);
            return formatter.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Invalid timestamp";
        }
    }

    public String getUsername(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            DocumentSnapshot documentSnapshot = Tasks.await(db.collection("users").document(uid).get());
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                return username != null ? username : "null";
            } else {
                return "User document not found";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    public String getEmail(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            DocumentSnapshot documentSnapshot = Tasks.await(db.collection("users").document(uid).get());
            if (documentSnapshot.exists()) {
                String email = documentSnapshot.getString("email");
                return email != null ? email : "null";
            } else {
                return "User document not found";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
