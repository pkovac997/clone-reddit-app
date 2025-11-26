package com.example.redditcloneapp.ui.post.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemPostBinding;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.ui.post.listeners.OnPostClickListener;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final List<Post> items = new ArrayList<>();
    private final OnPostClickListener listener;

    public PostAdapter(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Post> posts) {
        items.clear();
        if (posts != null) {
            items.addAll(posts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updatePost(Post updated) {
        for (int i = 0; i < items.size(); i++) {
            Post p = items.get(i);
            if (p.getId().equals(updated.getId())) {
                items.set(i, updated);
                notifyItemChanged(i);
                return;
            }
        }
    }
}
