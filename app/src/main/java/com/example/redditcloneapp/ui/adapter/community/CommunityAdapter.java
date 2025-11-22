package com.example.redditcloneapp.ui.adapter.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Community;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder> {

    public interface OnCommunityClickListener {
        void onCommunityClick(Community community);
    }

    private List<Community> items = new ArrayList<>();
    private final OnCommunityClickListener listener;

    public CommunityAdapter(OnCommunityClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Community> communities) {
        this.items = communities != null ? communities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
