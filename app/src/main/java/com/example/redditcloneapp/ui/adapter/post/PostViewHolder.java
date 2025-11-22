package com.example.redditcloneapp.ui.adapter.post;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvTitle;
    private final TextView tvContent;
    private final TextView tvVotes;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvPostTitle);
        tvContent = itemView.findViewById(R.id.tvPostContent);
        tvVotes = itemView.findViewById(R.id.tvPostVotes);
    }

    public void bind(Post post, PostAdapter.OnPostClickListener listener) {
        tvTitle.setText(post.getTitle());
        tvContent.setText(post.getContent());

        int upvotes = post.getUserUpvotes() != null ? post.getUserUpvotes().size() : 0;
        int downvotes = post.getUserDownvotes() != null ? post.getUserDownvotes().size() : 0;

        tvVotes.setText(upvotes + " up • " + downvotes + " down");

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });
    }
}
