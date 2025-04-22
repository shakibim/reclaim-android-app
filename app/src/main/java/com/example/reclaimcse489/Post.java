package com.example.reclaimcse489;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {
    private String postId;
    private String username;
    private String userEmail;
    private String title;
    private String description;
    private long timestamp;
    private String itemType;
    private String imageUrl;

    public Post(String username, String userEmail, String title, String description, long timestamp, String itemType, String imageUrl) {
        this.username = username;
        this.userEmail = userEmail;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
    }
    public Post(String postId, String username, String userEmail, String title, String description, long timestamp, String itemType, String imageUrl) {
        this.postId=postId;
        this.username = username;
        this.userEmail = userEmail;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
    }

    public Post( String title, String description, String itemType, String imageUrl) {
        this.title = title;
        this.description = description;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters for the fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getPostId() {
        return postId;
    }

    public String timestampToDate() {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a", Locale.getDefault()); // Define the date format
        return dateFormat.format(date);
    }
}
