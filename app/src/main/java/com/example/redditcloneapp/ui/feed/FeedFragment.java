package com.example.redditcloneapp.ui.feed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.databinding.FeedFragmentBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.domain.models.User;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;
import com.example.redditcloneapp.infrastructure.firebase.user.UserRepository;
import com.example.redditcloneapp.ui.post.adapters.PostAdapter;
import com.example.redditcloneapp.ui.post.PostDetailsFragment;
import com.example.redditcloneapp.ui.post.listeners.OnPostClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedFragment extends Fragment {

    private FeedFragmentBinding binding;

    private static final int SDK_33 = Build.VERSION_CODES.TIRAMISU;

    private PostAdapter adapter;
    private PostRepository postRepository;
    private CommunityRepository communityRepository;
    private UserRepository userRepository;

    private Uri attachedImageUri;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private FirebaseAuth auth;

    private final List<Community> userCommunities = new ArrayList<>();
    private ArrayAdapter<String> communitySpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FeedFragmentBinding.inflate(inflater, container, false);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            attachedImageUri = uri;
                            binding.ivImagePreview.setImageURI(uri);
                            binding.ivImagePreview.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        postRepository = new PostRepository();
        communityRepository = new CommunityRepository();
        userRepository = new UserRepository();

        // UMESTO da odmah zoveš loadFeed()
        if (hasImagePermission()) {
            loadFeed();
        } else {
            requestImagePermission();
        }

        binding.btnAttachImage.setOnClickListener(v -> {
            if (hasImagePermission()) {
                openImagePicker();
            } else {
                requestImagePermission();
            }
        });

        setupRecycler();
        setupCommunitySpinner();
        setupSubmitPost();

        loadUserCommunities();
        loadFeed();
    }

    private void setupRecycler() {
        adapter = new PostAdapter(new OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                openPostDetails(post);
            }

            @Override
            public void onCommentsClick(Post post) {
                openPostDetails(post);
            }

            @Override
            public void onUpvoteClick(Post post) {
                var current = FirebaseAuth.getInstance().getCurrentUser();
                if (current == null) {
                    Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                postRepository.upvotePost(current.getUid(), post.getId(), new DbCallback<>() {

                    @Override
                    public void onSuccess(Post post) {
                        adapter.updatePost(post);
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getContext(), "Failed to vote", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownvoteClick(Post post) {
                var current = FirebaseAuth.getInstance().getCurrentUser();
                if (current == null) {
                    Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                postRepository.downvotePost(current.getUid(), post.getId(), new DbCallback<>() {
                    @Override
                    public void onSuccess(Post post) {
                        adapter.updatePost(post);
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getContext(), "Failed to vote", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFeed.setAdapter(adapter);
    }

    private void openPostDetails(Post post) {
        if (post.getId() == null) {
            return;
        }

        PostDetailsFragment fragment = PostDetailsFragment.newInstance(post.getId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupCommunitySpinner() {
        communitySpinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );
        communitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCommunity.setAdapter(communitySpinnerAdapter);
    }

    private void setupSubmitPost() {
        binding.btnSubmitPost.setOnClickListener(v -> {

            FirebaseUser current = auth.getCurrentUser();

            if (current == null) {
                Toast.makeText(getContext(), "You must be logged in to post", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = binding.etPostTitle.getText().toString().trim();
            String content = binding.etPostContent.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                Toast.makeText(getContext(), "Title and content are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPos = binding.spinnerCommunity.getSelectedItemPosition();
            if (selectedPos < 0 || selectedPos >= userCommunities.size()) {
                Toast.makeText(getContext(), "Select a community", Toast.LENGTH_SHORT).show();
                return;
            }

            Community selectedCommunity = userCommunities.get(selectedPos);

            userRepository.getUserById(current.getUid(), new DbCallback<>() {
                @Override
                public void onSuccess(User user) {
                    createPost(user, selectedCommunity, title, content);
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(getContext(), "Failed to load user", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void createPost(User user, Community community, String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCommunityId(community.getId());
        post.setCommunityName(community.getName());
        post.setUserId(user.getId());
        post.setUserUsername(user.getUsername());
        post.setCreatedAt(Date.from(Instant.now()));
        post.setImageUrls(new ArrayList<>());
        post.setUserUpvotes(new ArrayList<>());
        post.setUserDownvotes(new ArrayList<>());
        post.setComments(new ArrayList<>());

        if (attachedImageUri != null) {
            post.setImageUrls(List.of(attachedImageUri.toString()));
        }

        postRepository.createPost(post, new DbCallback<>() {
            @Override
            public void onSuccess(Post entity) {
                Toast.makeText(getContext(), "Post created", Toast.LENGTH_SHORT).show();
                // Clear inputs
                binding.etPostTitle.setText("");
                binding.etPostContent.setText("");
                attachedImageUri = null;
                binding.ivImagePreview.setImageDrawable(null);
                binding.ivImagePreview.setVisibility(View.GONE);
                // Reload feed or optimistically add to adapter
                loadFeed();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserCommunities() {
        FirebaseUser current = auth.getCurrentUser();

        if (current == null) {
            return;
        }

        userRepository.getUserById(current.getUid(), new DbCallback<>() {
            @Override
            public void onSuccess(User user) {
                // Assuming your FirebaseDataSource has a method like:
                // getCommunitiesByIds(List<String> ids, DbCallback<List<Community>>)
                communityRepository.getUserCommunities(user.getCommunityFollows(), new DbCallback<>() {
                    @Override
                    public void onSuccess(List<Community> communities) {
                        userCommunities.clear();
                        if (communities != null) {
                            userCommunities.addAll(communities);
                        }

                        List<String> labels = new ArrayList<>();
                        for (Community c : userCommunities) {
                            labels.add("c/" + c.getName());
                        }
                        communitySpinnerAdapter.clear();
                        communitySpinnerAdapter.addAll(labels);
                        communitySpinnerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getContext(), "Failed to load communities", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load user communities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFeed() {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            return; // or load some default public feed
        }

        postRepository.getPostByCommunityFromUserId(current.getUid(), new DbCallback<>() {
            @Override
            public void onSuccess(List<Post> posts) {
                adapter.setItems(posts);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasImagePermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= SDK_33) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        return ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestImagePermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= SDK_33) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        permissionLauncher.launch(permission);
    }

    private void openImagePicker() {
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent intent = new Intent(Intent.ACTION_PICK, imagesUri);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}
