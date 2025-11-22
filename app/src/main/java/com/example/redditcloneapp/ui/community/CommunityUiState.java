package com.example.redditcloneapp.ui.community;

import com.example.redditcloneapp.domain.models.Community;

import java.util.List;

import lombok.Getter;

@Getter
public class CommunityUiState {

    private final boolean loading;
    private final List<Community> communities;
    private final String errorMessage;

    public CommunityUiState(boolean loading, List<Community> communities, String errorMessage) {
        this.loading = loading;
        this.communities = communities;
        this.errorMessage = errorMessage;
    }

    public static CommunityUiState loading() {
        return new CommunityUiState(true, null, null);
    }

    public static CommunityUiState success(List<Community> communities) {
        return new CommunityUiState(false, communities, null);
    }

    public static CommunityUiState error(String message) {
        return new CommunityUiState(false, null, message);
    }
}
