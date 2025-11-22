package com.example.redditcloneapp.ui.feed;

import com.example.redditcloneapp.domain.models.Post;

import java.util.List;

import lombok.Getter;

@Getter
public class FeedUiState {
    private final boolean loading;
    private final List<Post> posts;
    private final String errorMessage;

    public FeedUiState(boolean loading,
                       List<Post> posts,
                       String errorMessage) {
        this.loading = loading;
        this.posts = posts;
        this.errorMessage = errorMessage;
    }

    public static FeedUiState loading() {
        return new FeedUiState(true, null, null);
    }

    public static FeedUiState success(List<Post> posts) {
        return new FeedUiState(false, posts, null);
    }

    public static FeedUiState error(String message) {
        return new FeedUiState(false, null, message);
    }
}
