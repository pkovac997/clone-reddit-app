package com.example.redditcloneapp.infrastructure.firebase.user;

import com.example.redditcloneapp.domain.models.User;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.AbstractFirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.google.android.gms.tasks.Task;

public class UserRepository extends AbstractFirebaseDataSource {

    public UserRepository() {
        dataSource = new FirebaseDataSource();
    }

    public void getUserById(String userId, DbCallback<User> callback) {
        dataSource.getUserById(userId, callback);
    }

    public void createUser(User user, DbCallback<User> callback) {
        dataSource.createUser(user, callback);
    }
}
