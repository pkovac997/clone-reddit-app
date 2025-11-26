package com.example.redditcloneapp.ui.community.listeners;

import com.example.redditcloneapp.domain.models.Community;

public interface OnCommunityFollowClickListener {
    void onCommunityClick(Community community);
    void onFollowClick(Community community);
}