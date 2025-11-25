package com.example.redditcloneapp.ui.comment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditcloneapp.databinding.ItemCommentBinding;
import com.example.redditcloneapp.domain.models.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private final List<Comment> items = new ArrayList<>();

    public void setItems(List<Comment> comments) {
        items.clear();
        if (comments != null) {
            items.addAll(comments);
        }
        notifyDataSetChanged();
    }

    public void addCommentAtTop(Comment comment) {
        items.add(0, comment);
        notifyItemInserted(0);
    }

    public int getCommentCount() {
        return items.size();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
