package com.example.reclaimcse489;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FirebaseFirestore firestore;
    private ListView listViewPosts;
    private TextView userGreetingTextView;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts;
    private Handler mHandler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firestore = FirebaseFirestore.getInstance();
        listViewPosts = view.findViewById(R.id.listViewPosts);
        userGreetingTextView = view.findViewById(R.id.userGreetingTextView);
        fetchUserData();
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        listViewPosts.setAdapter(postAdapter);

        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadPosts();
                    }
                });
            }
        }).start();

        listViewPosts.setOnItemClickListener((parent, view1, position, id) -> {
            Post selectedPost = posts.get(position);
            Intent intent = new Intent(getContext(), ViewDetailsActivity.class);
            intent.putExtra("postId", selectedPost.getPostId());
            startActivity(intent);
        });

        return view;
    }

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("username");
                        userGreetingTextView.setText("Hello, " + (userName != null ? userName : "User"));
                    } else {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPosts() {
        firestore.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String postId = document.getId(); // Get postId (document ID)
                        String title = document.getString("title");
                        String description = document.getString("description");
                        String userId = document.getString("userId"); // Get userId from the post
                        long timestamp = document.getLong("timestamp");
                        String imageUrl = document.getString("imageUrl");
                        String itemType = document.getString("type");
                        fetchUserNameAndEmail(postId, userId, title, description, timestamp, itemType, imageUrl);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserNameAndEmail(String postId, String userId, String title, String description, long timestamp, String itemType, String imageUrl) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("username");
                        String userEmail = documentSnapshot.getString("email");
                        Post post = new Post(postId, userName, userEmail, title, description, timestamp, itemType, imageUrl);
                        posts.add(post);
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

}
