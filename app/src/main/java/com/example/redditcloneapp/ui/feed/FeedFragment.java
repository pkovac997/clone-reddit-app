package com.example.redditcloneapp.ui.feed;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.redditcloneapp.ui.post.PostAdapter;
import com.example.redditcloneapp.ui.post.PostDetailsFragment;
import com.example.redditcloneapp.ui.post.listeners.OnPostClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private FeedFragmentBinding binding;

    private PostAdapter adapter;
    private PostRepository postRepository;
    private CommunityRepository communityRepository;
    private UserRepository userRepository;

    private FirebaseAuth auth;

    private final List<Community> userCommunities = new ArrayList<>();
    private ArrayAdapter<String> communitySpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FeedFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        postRepository = new PostRepository();
        communityRepository = new CommunityRepository();
        userRepository = new UserRepository();

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
        });

        binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFeed.setAdapter(adapter);
    }

    private void openPostDetails(Post post) {
        if (post.getId() == null) {
            // ensure da post.id setuješ kada ga učitavaš iz Firestore-a: post.setId(doc.getId());
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
        Post p = new Post();
        p.setTitle(title);
        p.setContent(content);
        p.setCommunityId(community.getId());
        p.setCommunityName(community.getName());
        p.setUserId(user.getId());
        p.setUserUsername(user.getUsername());
        p.setCreatedAt(Date.from(Instant.now()));
        p.setImageUrls(new ArrayList<>());
        p.setUserUpvotes(new ArrayList<>());
        p.setUserDownvotes(new ArrayList<>());
        p.setComments(new ArrayList<>());

        postRepository.createPost(p, new DbCallback<>() {
            @Override
            public void onSuccess(Post entity) {
                Toast.makeText(getContext(), "Post created", Toast.LENGTH_SHORT).show();
                // Clear inputs
                binding.etPostTitle.setText("");
                binding.etPostContent.setText("");
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
}
