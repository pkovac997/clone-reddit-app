package com.example.redditcloneapp.infrastructure.firebase.community.interfaces;

import com.example.redditcloneapp.domain.models.Community;

import java.util.List;

public interface CommunitiesCallbackInterface {
    void onSuccess(List<Community> communities);
    void onError(Exception e);
}
