package com.example.redditcloneapp.ui.post.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

public class CreatePostFragment extends DialogFragment {

    public interface OnPostCreatedListener {
        void onPostCreated();
    }

    private OnPostCreatedListener listener;

    private TextInputEditText etTitle;
    private TextInputEditText etContent;
    private Button btnUploadImage;
    private Button btnSubmit;

    public static CreatePostFragment newInstance() {
        return new CreatePostFragment();
    }

    public void setOnPostCreatedListener(OnPostCreatedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_post, null, false);
        dialog.setContentView(view);

        initViews(view);
        setupListeners(dialog);

        // širina dialoga da bude skoro full width
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        return dialog;
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.etPostTitle);
        etContent = view.findViewById(R.id.etPostContent);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnSubmit = view.findViewById(R.id.btnSubmitPost);
    }

    private void setupListeners(Dialog dialog) {
        btnUploadImage.setOnClickListener(v -> {
            // TODO: implement image upload later
            Toast.makeText(getContext(),
                    "Image upload coming soon",
                    Toast.LENGTH_SHORT).show();
        });

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

            if (TextUtils.isEmpty(title)) {
                etTitle.setError("Title is required");
                return;
            }

            if (TextUtils.isEmpty(content)) {
                etContent.setError("Content is required");
                return;
            }

            String userId = null;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            if (userId == null) {
                Toast.makeText(getContext(),
                        "User not logged in",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            PostRepository repository = new PostRepository();
            var post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setCommunityId("I2Uq5GoDrTIo0HylZ1Ug");
            post.setUserId(userId);

            repository.createPost(
                    post,
                    new DbCallback<>() {
                        @Override
                        public void onSuccess(Post result) {
                            Toast.makeText(getContext(),
                                    "Post created",
                                    Toast.LENGTH_SHORT).show();

                            if (listener != null) {
                                listener.onPostCreated();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception exception) {
                            Toast.makeText(getContext(),
                                    "Failed to create post: " +
                                            (exception != null ? exception.getMessage() : "Unknown error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
    }
}