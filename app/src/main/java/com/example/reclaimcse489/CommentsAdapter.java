package com.example.reclaimcse489;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> comments;

    public CommentsAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }
        TextView commentTextView = convertView.findViewById(R.id.commentTextView);
        TextView userTextView = convertView.findViewById(R.id.userTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        Comment comment = comments.get(position);
        commentTextView.setText(comment.getCommentText());
        Comment.getUsername(comment.getUserID(), new Comment.OnUserNameFetchedListener() {
            @Override
            public void onUserNameFetched(String username) {
                userTextView.setText(username);
            }
        });

        // Format and set the timestamp
        String formattedDate = Comment.convertTimestampToDate(comment.getTimestamp());
        dateTextView.setText(formattedDate);

        return convertView;
    }
}
