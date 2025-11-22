package com.example.redditcloneapp.ui.adapter.post;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemPostBinding;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.util.TimeUtil;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private final ItemPostBinding binding;

    public PostViewHolder(@NonNull ItemPostBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void bind(Post post, PostAdapter.OnPostClickListener listener) {

        // Community header
        binding.tvCommunityName.setText("r/" + post.getCommunityId());

        // Date
        if (post.getCreatedAt() != null) {
            String timeAgo = TimeUtil.formatTimeAgo(post.getCreatedAt());
            binding.tvPostTime.setText("· " + timeAgo);
        } else {
            binding.tvPostTime.setText("");
        }

        // User info
        binding.tvUsername.setText("u/" + post.getUserId());

        // Title & content
        binding.tvPostTitle.setText(post.getTitle());
        binding.tvPostContent.setText(post.getContent());

        // Click on whole card
        itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post);
        });
    }
}
