package com.example.redditcloneapp.ui.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;

import java.util.List;

public class PostViewModel extends ViewModel {
    private final PostRepository repository;
    private final MutableLiveData<PostUiState> uiState = new MutableLiveData<>();

    public PostViewModel() {
        this.repository = new PostRepository();
    }

    public LiveData<PostUiState> getUiState() {
        return uiState;
    }

    public void loadAllPosts() {
        uiState.setValue(PostUiState.loading());

        repository.getAllPosts(new DbCallback<>() {
            @Override
            public void onSuccess(List<Post> posts) {
                uiState.postValue(PostUiState.success(posts));
            }

            @Override
            public void onError(Exception e) {
                String message = e != null ? e.getMessage() : "Unknown error";
                uiState.postValue(PostUiState.error(message));
            }
        });
    }

    public void loadPostsForCommunity(String communityId) {
        uiState.setValue(PostUiState.loading());

        repository.getPostsForCommunity(communityId, new DbCallback<>() {
            @Override
            public void onSuccess(List<Post> posts) {
                uiState.postValue(PostUiState.success(posts));
            }

            @Override
            public void onError(Exception exception) {
                String message = exception != null
                        ? exception.getMessage()
                        : "Unknown error";
                uiState.postValue(PostUiState.error(message));
            }
        });
    }
}
