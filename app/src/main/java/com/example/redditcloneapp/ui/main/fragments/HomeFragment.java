package com.example.redditcloneapp.ui.main.fragments;

import android.content.Intent;
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
import com.example.redditcloneapp.databinding.FragmentHomeBinding;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.ui.adapter.post.PostAdapter;
import com.example.redditcloneapp.ui.post.PostViewModel;
import com.example.redditcloneapp.ui.post.fragments.CreatePostFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostAdapter adapter;
    private PostViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFab();
        setupViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload posts whenever we return to Home (npr. posle kreiranja posta)
        if (viewModel != null) {
            viewModel.loadAllPosts();
        }
    }

    private void setupRecyclerView() {
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(post -> {
            // TODO: open PostDetails screen
            Toast.makeText(getContext(),
                    "Clicked: " + post.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });
        binding.rvPosts.setAdapter(adapter);

        // Show/hide FAB when scrolling
        binding.rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // scrolling down -> hide fab
                    binding.fabCreatePost.hide();
                } else if (dy < 0) {
                    // scrolling up -> show fab
                    binding.fabCreatePost.show();
                }
            }
        });
    }

    private void setupFab() {
        binding.fabCreatePost.setOnClickListener(v -> {
            CreatePostFragment dialog = CreatePostFragment.newInstance();
            dialog.setOnPostCreatedListener(() -> {
                // refresh feed after successful creation
                viewModel.loadAllPosts();
            });
            dialog.show(getParentFragmentManager(), CreatePostFragment.class.getName());
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState == null) return;

            // Loading
            binding.progressBarFeed.setVisibility(uiState.isLoading() ? View.VISIBLE : View.GONE);

            // Error
            if (uiState.getErrorMessage() != null) {
                Toast.makeText(getContext(),
                        "Failed to load posts: " + uiState.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            // Data
            List<Post> posts = uiState.getPosts();
            if (posts == null || posts.isEmpty()) {
                binding.tvFeedEmpty.setVisibility(View.VISIBLE);
                binding.rvPosts.setVisibility(View.GONE);
            } else {
                binding.tvFeedEmpty.setVisibility(View.GONE);
                binding.rvPosts.setVisibility(View.VISIBLE);
                adapter.setItems(posts);
            }
        });

        // Prvi put učitamo feed ovde (kasnije osvežavamo u onResume)
        viewModel.loadAllPosts();
    }
}