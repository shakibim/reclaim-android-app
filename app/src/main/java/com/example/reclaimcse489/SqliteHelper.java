package com.example.reclaimcse489;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "posts_db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_POSTS = "posts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "user_email";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_ITEM_TYPE = "item_type";
    private static final String COLUMN_IMAGE_URL = "image_url";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create posts table
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_ITEM_TYPE + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    // Insert posts into SQLite
    public void insertPosts(ArrayList<Post> posts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Post post : posts) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_USERNAME, post.getUsername());
                values.put(COLUMN_EMAIL, post.getUserEmail());
                values.put(COLUMN_TITLE, post.getTitle());
                values.put(COLUMN_DESCRIPTION, post.getDescription());
                values.put(COLUMN_TIMESTAMP, post.getTimestamp());
                values.put(COLUMN_ITEM_TYPE, post.getItemType());
                values.put(COLUMN_IMAGE_URL, post.getImageUrl());

                db.insert(TABLE_POSTS, null, values); // Insert post into the database
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite Error", "Error inserting posts", e);
        } finally {
            db.endTransaction();
        }
    }

    public void updatePost(String postId, String title, String description, String itemType, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_ITEM_TYPE, itemType);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        db.update(TABLE_POSTS, values, COLUMN_ID + " = ?", new String[]{postId});
        db.close();
    }

    public void deletePost(String postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, COLUMN_ID + " = ?", new String[]{postId});
        db.close();
    }

}

