package com.example.redditcloneapp.ui.comment;

import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommentBinding;
import com.example.redditcloneapp.domain.models.Comment;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final ItemCommentBinding binding;

    public CommentViewHolder(ItemCommentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Comment comment) {
        String username = comment.getUsername() != null
                ? comment.getUsername()
                : "unknown";

        binding.tvCommentUser.setText("u/" + username);
        binding.tvCommentText.setText(comment.getText());

        // ako imaš tvAvatar:
        if (binding.tvAvatar != null) {
            char first = username.isEmpty() ? 'U' : Character.toUpperCase(username.charAt(0));
            binding.tvAvatar.setText(String.valueOf(first));
        }
    }
}