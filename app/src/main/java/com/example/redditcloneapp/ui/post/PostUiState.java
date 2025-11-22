package com.example.redditcloneapp.ui.post;

import com.example.redditcloneapp.domain.models.Post;

import java.util.List;

import lombok.Getter;

@Getter
public class PostUiState {

    private final boolean loading;
    private final List<Post> posts;
    private final String errorMessage;

    public PostUiState(boolean loading, List<Post> posts, String errorMessage) {
        this.loading = loading;
        this.posts = posts;
        this.errorMessage = errorMessage;
    }

    public static PostUiState loading() {
        return new PostUiState(true, null, null);
    }

    public static PostUiState success(List<Post> posts) {
        return new PostUiState(false, posts, null);
    }

    public static PostUiState error(String message) {
        return new PostUiState(false, null, message);
    }
}
