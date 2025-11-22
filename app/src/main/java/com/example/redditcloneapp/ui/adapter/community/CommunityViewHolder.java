package com.example.redditcloneapp.ui.adapter.community;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Community;

public class CommunityViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvName;
    private final TextView tvDescription;
    private final TextView tvFollowers;

    public CommunityViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvCommunityName);
        tvDescription = itemView.findViewById(R.id.tvCommunityDescription);
        tvFollowers = itemView.findViewById(R.id.tvCommunityFollowers);
    }

    public void bind(Community community, CommunityAdapter.OnCommunityClickListener listener) {
        tvName.setText(community.getName());
        tvDescription.setText(community.getDescription());

        int followersCount = community.getFollowers() != null
                ? community.getFollowers().size()
                : 0;

        tvFollowers.setText(followersCount + " followers");

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommunityClick(community);
            }
        });
    }
}