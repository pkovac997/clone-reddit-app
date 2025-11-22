package com.example.redditcloneapp.infrastructure.firebase.community;

import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.common.AbstractFirebaseDataSource;

import java.util.List;

public class CommunityRepository extends AbstractFirebaseDataSource {

    public CommunityRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getAllCommunities(DbCallback<List<Community>> callback) {
        dataSource.getAllCommunities(callback);
    }

}
