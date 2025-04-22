package com.example.reclaimcse489;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {

    private String userID;
    private long timestamp;
    private String commentText;

    public Comment(String userID, long timestamp, String commentText) {
        this.userID = userID;
        this.timestamp = timestamp;
        this.commentText = commentText;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    // Fetching username from Firestore using userID
    public static void getUsername(String userID, final OnUserNameFetchedListener listener) {
        FirebaseFirestore.getInstance().collection("users").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        listener.onUserNameFetched(username);
                    } else {
                        listener.onUserNameFetched("Unknown User");
                    }
                })
                .addOnFailureListener(e -> listener.onUserNameFetched("Unknown User"));
    }

    public static String convertTimestampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public interface OnUserNameFetchedListener {
        void onUserNameFetched(String username);
    }
}
