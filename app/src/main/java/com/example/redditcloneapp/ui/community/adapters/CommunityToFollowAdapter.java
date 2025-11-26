package com.example.redditcloneapp.ui.community.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommunityToFollowBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityFollowClickListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityToFollowAdapter extends RecyclerView.Adapter<CommunityToFollowViewHolder> {

    private final List<Community> items = new ArrayList<>();
    private final OnCommunityFollowClickListener listener;

    public CommunityToFollowAdapter(OnCommunityFollowClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Community> communities) {
        items.clear();
        if (communities != null) {
            items.addAll(communities);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommunityToFollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemCommunityToFollowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CommunityToFollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityToFollowViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}