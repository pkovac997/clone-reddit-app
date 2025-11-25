package com.example.redditcloneapp.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.redditcloneapp.databinding.FragmentPostDetailsBinding;
import com.example.redditcloneapp.domain.models.Comment;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.comment.CommentRepository;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;
import com.example.redditcloneapp.ui.comment.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    private static final String ARG_POST_ID = "post_id";

    private FragmentPostDetailsBinding binding;

    private String postId;
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private CommentAdapter commentAdapter;

    public static PostDetailsFragment newInstance(String postId) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }

        postRepository = new PostRepository();
        commentRepository = new CommentRepository();
        commentAdapter = new CommentAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCommentsRecycler();
        loadPost();
        loadComments();

        binding.btnSubmitComment.setOnClickListener(v -> submitComment());
    }

    private void setupCommentsRecycler() {
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComments.setAdapter(commentAdapter);
    }

    private void loadPost() {
        if (postId == null) return;

        postRepository.getPostById(postId, new DbCallback<>() {
            @Override
            public void onSuccess(Post post) {
                if (post == null) return;

                binding.tvCommunity.setText("c/" + post.getCommunityName());
                binding.tvUser.setText("u/" + post.getUserUsername());
                binding.tvPostTitle.setText(post.getTitle());
                binding.tvPostContent.setText(post.getContent());

                int score = (post.getUserUpvotes() != null ? post.getUserUpvotes().size() : 0)
                        - (post.getUserDownvotes() != null ? post.getUserDownvotes().size() : 0);
                binding.tvScore.setText(String.valueOf(score));

                int commentsCount = post.getComments() != null ? post.getComments().size() : 0;
                binding.tvCommentsHeader.setText("Comments (" + commentsCount + ")");
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        if (postId == null) return;

        commentRepository.getComments(postId, new DbCallback<>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentAdapter.setItems(comments);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCommentsHeader(int count) {
        binding.tvCommentsHeader.setText("Comments (" + count + ")");
    }

    private void submitComment() {
        String text = binding.etComment.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Comment can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();

        if (current == null) {
            Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(current.getUid());
        comment.setUsername(current.getEmail());
        comment.setText(text);
        comment.setCreatedAt(Date.from(Instant.now()));

        commentRepository.createComment(comment, new DbCallback<>() {
            @Override
            public void onSuccess(Comment entity) {
                binding.etComment.setText("");

                commentAdapter.addCommentAtTop(entity);
                updateCommentsHeader(commentAdapter.getCommentCount());
                binding.rvComments.scrollToPosition(0);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
