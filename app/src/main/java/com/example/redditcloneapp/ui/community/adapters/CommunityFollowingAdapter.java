package com.example.redditcloneapp.ui.community.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommunityFollowingBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityClickListener;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityFollowClickListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityFollowingAdapter extends RecyclerView.Adapter<CommunityFollowingViewHolder> {

    private final List<Community> items = new ArrayList<>();
    private final OnCommunityFollowClickListener listener;

    public CommunityFollowingAdapter(OnCommunityFollowClickListener listener) {
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
    public CommunityFollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemCommunityFollowingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CommunityFollowingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityFollowingViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}