package com.example.redditcloneapp.infrastructure.firebase.comment;

import com.example.redditcloneapp.domain.models.Comment;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.AbstractFirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;

import java.util.List;

public class CommentRepository extends AbstractFirebaseDataSource {

    public CommentRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getComments(String postId, DbCallback<List<Comment>> callback) {
        dataSource.getCommentsByPost(postId, callback);
    }

    public void createComment(Comment comment, DbCallback<Comment> callback) {
        dataSource.createComment(comment, callback);
    }

}
