package com.example.redditcloneapp.ui.community;

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
import com.example.redditcloneapp.ui.adapter.community.CommunityAdapter;

public class CommunityActivity  extends AppCompatActivity {
    private RecyclerView rvCommunities;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private CommunityAdapter adapter;
    private CommunityViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        initViews();
        setupRecyclerView();
        setupViewModel();
    }

    private void initViews() {
        rvCommunities = findViewById(R.id.rvCommunities);
        progressBar = findViewById(R.id.progressBarCommunities);
        tvEmpty = findViewById(R.id.tvCommunitiesEmpty);
    }

    private void setupRecyclerView() {
        rvCommunities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityAdapter(community -> {
            // TODO: open community details / posts
            Toast.makeText(this,
                    "Clicked: " + community.getName(),
                    Toast.LENGTH_SHORT).show();
        });
        rvCommunities.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);

        viewModel.getUiState().observe(this, uiState -> {
            if (uiState == null) return;

            // loading
            progressBar.setVisibility(uiState.isLoading() ? View.VISIBLE : View.GONE);

            // error
            if (uiState.getErrorMessage() != null) {
                Toast.makeText(this,
                        "Failed to load communities: " + uiState.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            // data
            if (uiState.getCommunities() == null || uiState.getCommunities().isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvCommunities.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvCommunities.setVisibility(View.VISIBLE);
                adapter.setItems(uiState.getCommunities());
            }
        });

        viewModel.loadCommunities();
    }
}
