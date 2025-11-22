package com.example.redditcloneapp.ui.adapter.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.databinding.ItemPostBinding;
import com.example.redditcloneapp.domain.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    private final OnPostClickListener listener;
    private List<Post> items = new ArrayList<>();

    public PostAdapter(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Post> posts) {
        this.items = posts != null ? posts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPostBinding binding = ItemPostBinding.inflate(inflater, parent, false);
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
}
