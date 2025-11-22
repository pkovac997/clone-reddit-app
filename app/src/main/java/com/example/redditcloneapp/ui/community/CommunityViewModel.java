package com.example.redditcloneapp.ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;

import java.util.List;

public class CommunityViewModel extends ViewModel {
    private final CommunityRepository repository;
    private final MutableLiveData<CommunityUiState> uiState = new MutableLiveData<>();

    public CommunityViewModel() {
        this.repository = new CommunityRepository();
    }

    public LiveData<CommunityUiState> getUiState() {
        return uiState;
    }

    public void loadCommunities() {
        // prvo emituje loading stanje
        uiState.setValue(CommunityUiState.loading());

        repository.getAllCommunities(new CommunityRepository.CommunitiesCallback() {
            @Override
            public void onSuccess(List<Community> communities) {
                uiState.postValue(CommunityUiState.success(communities));
            }

            @Override
            public void onError(Exception e) {
                String message = e != null ? e.getMessage() : "Unknown error";
                uiState.postValue(CommunityUiState.error(message));
            }
        });
    }
}
