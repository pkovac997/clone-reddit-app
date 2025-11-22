package com.example.redditcloneapp.ui.community;

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
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.ui.adapter.community.CommunityAdapter;

import java.util.List;

public class CommunityFragment extends Fragment {
    private RecyclerView rvCommunities;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private CommunityAdapter adapter;
    private CommunityViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_communities, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();

        return view;
    }

    private void initViews(View view) {
        rvCommunities = view.findViewById(R.id.rvCommunities);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        rvCommunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter(community -> {
            // TODO: handle community click (open details, posts, etc.)
            Toast.makeText(getContext(),
                    "Clicked: " + community.getName(),
                    Toast.LENGTH_SHORT).show();
        });
        rvCommunities.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState == null) return;

            // Loading
            progressBar.setVisibility(uiState.isLoading() ? View.VISIBLE : View.GONE);

            // Error
            if (uiState.getErrorMessage() != null) {
                Toast.makeText(getContext(),
                        "Failed to load communities: " + uiState.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            // Data
            List<Community> communities = uiState.getCommunities();
            if (communities == null || communities.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvCommunities.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvCommunities.setVisibility(View.VISIBLE);
                adapter.setItems(communities);
            }
        });

        // prvi put učitavanje
        viewModel.loadCommunities();
    }
}
