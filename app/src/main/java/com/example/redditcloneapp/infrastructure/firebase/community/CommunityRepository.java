package com.example.redditcloneapp.infrastructure.firebase.community;

import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;

import java.util.List;

public class CommunityRepository {

    public interface CommunitiesCallback {
        void onSuccess(List<Community> communities);
        void onError(Exception e);
    }

    private final FirebaseDataSource dataSource;

    public CommunityRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getAllCommunities(CommunitiesCallback callback) {
        dataSource.getAllCommunities(callback);
    }

}
