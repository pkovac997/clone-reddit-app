package com.example.redditcloneapp.infrastructure.firebase.post;

import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.AbstractFirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;

import java.util.List;

public class PostRepository extends AbstractFirebaseDataSource {

    public PostRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getAllPosts(DbCallback<List<Post>> callback) {
        dataSource.getAllPosts(callback);
    }

    public void getPostsForCommunity(String communityId, DbCallback<List<Post>> callback) {
        dataSource.getPostsForCommunity(communityId, callback);
    }

    public void getPostById(String postId, DbCallback<Post> callback) {
        dataSource.getPostById(postId, callback);
    }

    public void getPostByCommunityFromUserId(String userId, DbCallback<List<Post>> callback) {
        dataSource.getPostByCommunityFromUserId(userId, callback);
    }

    public void createPost(Post post, DbCallback<Post> callback) {
        dataSource.createPost(post, callback);
    }

    public void upvotePost(String userId, String postId, DbCallback<Post> callback) {
        dataSource.upvotePost(userId, postId, callback);
    }

    public void downvotePost(String userId, String postId, DbCallback<Post> callback) {
        dataSource.downvotePost(userId, postId, callback);
    }
}
