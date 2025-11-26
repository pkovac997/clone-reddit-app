package com.example.redditcloneapp.ui.community.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommunityToFollowBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityFollowClickListener;

public class CommunityToFollowViewHolder extends RecyclerView.ViewHolder {

    private final ItemCommunityToFollowBinding binding;

    public CommunityToFollowViewHolder(ItemCommunityToFollowBinding binding) {
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

        // klik na celu karticu
        binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onCommunityClick(community);
        });

        // klik na Follow dugme
        binding.btnFollow.setOnClickListener(v -> {
            if (listener != null) listener.onFollowClick(community);
        });
    }
}