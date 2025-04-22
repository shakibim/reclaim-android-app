package com.example.reclaimcse489;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Post> posts;
    private LayoutInflater inflater;

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_post, parent, false);
        }

        Post post = posts.get(position);
        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView itemTypeTextView = convertView.findViewById(R.id.itemTypeTextView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView postImageView = convertView.findViewById(R.id.postImageView);
        usernameTextView.setText(post.getUsername());
        emailTextView.setText("Email: " + post.getUserEmail());
        String formattedTime = post.timestampToDate();
        timeTextView.setText("Posted on: " + formattedTime);
        itemTypeTextView.setText("Item Type: " + post.getItemType());
        titleTextView.setText(post.getTitle());
        descriptionTextView.setText(post.getDescription());
        if (!TextUtils.isEmpty(post.getImageUrl())) {
            Picasso.get().load(post.getImageUrl()).into(postImageView);
        }
        if (TextUtils.isEmpty(post.getImageUrl())) {
            postImageView.setVisibility(View.GONE);
        } else {
            postImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImageUrl()).into(postImageView);
        }
        return convertView;
    }
}
