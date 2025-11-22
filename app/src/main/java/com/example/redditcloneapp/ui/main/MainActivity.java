package com.example.redditcloneapp.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.ui.adapter.post.PostAdapter;
import com.example.redditcloneapp.ui.post.PostViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private PostAdapter adapter;
    private PostViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupViewModel();
    }

    private void initViews() {
        rvPosts = findViewById(R.id.rvPosts);
        progressBar = findViewById(R.id.progressBarFeed);
        tvEmpty = findViewById(R.id.tvFeedEmpty);
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(post -> {
            // TODO: open PostDetailsActivity
            Toast.makeText(this,
                    "Clicked: " + post.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });
        rvPosts.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        viewModel.getUiState().observe(this, uiState -> {
            if (uiState == null) return;

            // Loading
            progressBar.setVisibility(uiState.isLoading() ? View.VISIBLE : View.GONE);

            // Error
            if (uiState.getErrorMessage() != null) {
                Toast.makeText(this,
                        "Failed to load posts: " + uiState.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            // Data
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

        // za glavni feed:
        viewModel.loadAllPosts();
        // ako želiš feed za community:
        // viewModel.loadPostsForCommunity(communityId);
    }
}
