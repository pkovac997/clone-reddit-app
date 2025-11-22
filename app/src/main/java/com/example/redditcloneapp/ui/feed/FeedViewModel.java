package com.example.redditcloneapp.ui.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;

import java.util.List;

public class FeedViewModel extends ViewModel {
    private final PostRepository repository;
    private final MutableLiveData<FeedUiState> uiState = new MutableLiveData<>();

    public FeedViewModel() {
        this.repository = new PostRepository();
    }

    public LiveData<FeedUiState> getUiState() {
        return uiState;
    }

    public void loadAllPosts() {
        uiState.setValue(FeedUiState.loading());

        repository.getAllPosts(new PostRepository.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                uiState.postValue(FeedUiState.success(posts));
            }

            @Override
            public void onError(Exception e) {
                String message = e != null ? e.getMessage() : "Unknown error";
                uiState.postValue(FeedUiState.error(message));
            }
        });
    }
}
