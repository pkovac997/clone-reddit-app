package com.example.redditcloneapp.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.ui.adapter.post.PostAdapter;

import java.util.List;

public class FeedFragment extends Fragment {
    private RecyclerView rvPosts;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private PostAdapter adapter;
    private FeedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();

        return view;
    }

    private void initViews(View view) {
        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBarFeed);
        tvEmpty = view.findViewById(R.id.tvFeedEmpty);
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(post -> {
            // TODO: open PostDetailsActivity
            Toast.makeText(getContext(),
                    "Clicked: " + post.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });
        rvPosts.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState == null) {
                return;
            }

            progressBar.setVisibility(uiState.isLoading() ? View.VISIBLE : View.GONE);

            if (uiState.getErrorMessage() != null) {
                Toast.makeText(getContext(),
                        "Failed to load posts: " + uiState.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            List<Post> posts = uiState.getPosts();
            if (posts == null || posts.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvPosts.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvPosts.setVisibility(View.VISIBLE);
                adapter.setItems(posts);
            }
        });

        viewModel.loadAllPosts();
    }
}
