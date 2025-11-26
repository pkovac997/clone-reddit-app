package com.example.redditcloneapp.ui.community.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommunityFollowingBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityClickListener;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityFollowClickListener;

public class CommunityFollowingViewHolder extends RecyclerView.ViewHolder {

    private final ItemCommunityFollowingBinding binding;

    public CommunityFollowingViewHolder(ItemCommunityFollowingBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Community community, OnCommunityFollowClickListener listener) {
        binding.tvCommunityName.setText("c/" + community.getName());
        binding.tvCommunityDescription.setText(community.getDescription() != null
                ? community.getDescription()
                : "");

        int followersCount = community.getFollowers() != null
                ? community.getFollowers().size()
                : 0;
        binding.tvCommunityFollowers.setText(followersCount + " followers");

        binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onCommunityClick(community);
        });
    }
}