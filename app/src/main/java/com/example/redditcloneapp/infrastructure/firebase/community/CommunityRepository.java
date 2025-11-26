package com.example.redditcloneapp.infrastructure.firebase.community;

import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.FirebaseDataSource;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.common.AbstractFirebaseDataSource;
import com.example.redditcloneapp.model.exception.database.EntityNotValidException;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

public class CommunityRepository extends AbstractFirebaseDataSource {

    public CommunityRepository() {
        this.dataSource = new FirebaseDataSource();
    }

    public void getAllCommunities(DbCallback<List<Community>> callback) {
        dataSource.getAllCommunities(callback);
    }

    public void getUserCommunities(String userId, DbCallback<List<Community>> callback) {
        dataSource.getCommunitiesByUser(userId, callback);
    }

    public void getUserCommunities(List<String> communityIds, DbCallback<List<Community>> callback) {
        dataSource.getCommunities(communityIds, callback);
    }

    public void userFollowsDefaultCommunity(String userId) {
        dataSource.userFollowsDefaultCommunity(userId);
    }

    public void createCommunity(Community community, DbCallback<Community> callback) {
        if (community.getName() == null || community.getName().isEmpty()) {
            callback.onError(new EntityNotValidException(Community.class, "Name can't be empty!"));
        }

        community.setCreatedAt(Date.from(Instant.now()));
        dataSource.createCommunity(community, callback);
    }

    public void getDefaultCommunity(DbCallback<Community> callback) {
        dataSource.getDefaultCommunity(callback);
    }

    public void getCommunityById(String communityId, DbCallback<Community> callback){
        dataSource.getCommunityById(communityId, callback);
    }

    public void ensureDefaultCommunityExists() {
        dataSource.ensureDefaultCommunityExists();
    }

    public void userFollowingCommunities(String userId, DbCallback<List<Community>> callback){
        dataSource.userFollowingCommunities(userId, callback);
    }

    public void userNotFollowingCommunities(String userId, DbCallback<List<Community>> callback) {
        dataSource.userNotFollowingCommunities(userId, callback);
    }

    public void followCommunity(String userId, String communityId, DbCallback<Community> callback) {
        dataSource.followCommunity(userId, communityId, callback);
    }

    public void unfollowCommunity(String userId, String communityId, DbCallback<Community> callback) {
        dataSource.unfollowCommunity(userId, communityId, callback);
    }
}
