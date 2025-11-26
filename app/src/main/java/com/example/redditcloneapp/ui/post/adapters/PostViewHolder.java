package com.example.redditcloneapp.ui.post.adapters;

import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemPostBinding;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.ui.post.listeners.OnPostClickListener;
import com.example.redditcloneapp.util.ImageHelper;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private final ItemPostBinding binding;

    public PostViewHolder(ItemPostBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(Post post, OnPostClickListener listener) {

        binding.tvCommunity.setText("c/" + post.getCommunityName());
        binding.tvUser.setText("u/" + post.getUserUsername());
        binding.tvPostTitle.setText(post.getTitle());
        binding.tvPostContent.setText(post.getContent());

        int score = (post.getUserUpvotes() != null ? post.getUserUpvotes().size() : 0)
                - (post.getUserDownvotes() != null ? post.getUserDownvotes().size() : 0);
        binding.tvScore.setText(String.valueOf(score));

        int commentsCount = post.getComments() != null ? post.getComments().size() : 0;
        binding.tvCommentsCount.setText(commentsCount + " comments");

        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            var firstImage = post.getImageUrls().getFirst();

            try {
                Uri uri = Uri.parse(firstImage);
                if (!ImageHelper.isImageFileExists(uri)) {
                    throw new Exception("Image doesn't exist");
                }
                binding.cardPostImage.setVisibility(View.VISIBLE);
                binding.ivPostImage.setImageURI(uri);
            } catch (Exception e) {
                binding.cardPostImage.setVisibility(View.GONE);
                binding.ivPostImage.setVisibility(View.GONE);
            }
        } else {
            binding.cardPostImage.setVisibility(View.GONE);
            binding.ivPostImage.setVisibility(View.GONE);
        }

        binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post);
        });

        binding.btnComments.setOnClickListener(v -> {
            if (listener != null) listener.onCommentsClick(post);
        });

        binding.btnUpvote.setOnClickListener(v -> {
            if (listener != null) listener.onUpvoteClick(post);
        });

        binding.btnDownvote.setOnClickListener(v -> {
            if (listener != null) listener.onDownvoteClick(post);
        });
    }
}