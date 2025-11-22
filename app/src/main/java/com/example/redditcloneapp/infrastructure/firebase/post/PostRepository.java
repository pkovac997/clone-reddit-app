package com.example.redditcloneapp.infrastructure.firebase.post;

import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;

import java.util.List;

public class PostRepository {

    private final FirebaseDataSource dataSource;

    public interface PostsCallback {
        void onSuccess(List<Post> posts);
        void onError(Exception e);
    }

    public interface CreatePostCallback {
        void onSuccess(Post createdPost);
        void onError(Exception e);
    }

    public PostRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getAllPosts(PostsCallback callback) {
        dataSource.getAllPosts(callback);
    }

    public void getPostsForCommunity(String communityId, PostsCallback callback) {
        dataSource.getPostsForCommunity(communityId, callback);
    }

    public void createPost(Post post, CreatePostCallback callback) {
        dataSource.createPost(post, callback);
    }
}
