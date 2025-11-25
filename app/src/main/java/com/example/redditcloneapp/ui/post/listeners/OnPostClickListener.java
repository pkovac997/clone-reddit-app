package com.example.redditcloneapp.ui.post.listeners;

import com.example.redditcloneapp.domain.models.Post;

public interface OnPostClickListener {
    void onPostClick(Post post);
    void onCommentsClick(Post post);
}