package com.example.redditcloneapp.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.databinding.DialogCreateCommunityBinding;
import com.example.redditcloneapp.databinding.FragmentCommunitiesBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;
import com.example.redditcloneapp.ui.community.adapters.CommunityFollowingAdapter;
import com.example.redditcloneapp.ui.community.adapters.CommunityToFollowAdapter;
import com.example.redditcloneapp.ui.community.listeners.OnCommunityFollowClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CommunitiesFragment extends Fragment {

    private FragmentCommunitiesBinding binding;

    private DialogCreateCommunityBinding communityBinding;

    private FirebaseAuth auth;
    private CommunityRepository communityRepository;

    private CommunityToFollowAdapter toFollowAdapter;
    private CommunityFollowingAdapter followingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        communityRepository = new CommunityRepository();

        binding = FragmentCommunitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        setupFab();

        loadCommunitiesToFollow();
        loadCommunitiesYouFollow();
    }

    private void setupRecyclerViews() {
        // Horizontalni za "to follow"
        toFollowAdapter = new CommunityToFollowAdapter(new OnCommunityFollowClickListener() {
            @Override
            public void onCommunityClick(Community community) {
                // TODO: otvori community details (kasnije)
            }

            @Override
            public void onFollowClick(Community community) {

                var currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                communityRepository.followCommunity(currentUser.getUid(), community.getId(), new DbCallback<>() {
                    @Override
                    public void onSuccess(Community entity) {
                        loadCommunitiesToFollow();
                        loadCommunitiesYouFollow();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getContext(), "Failed to follow community", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        var horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvCommunitiesToFollow.setLayoutManager(horizontalLayout);
        binding.rvCommunitiesToFollow.setAdapter(toFollowAdapter);

        // Vertikalni za "you follow"
        followingAdapter = new CommunityFollowingAdapter(new OnCommunityFollowClickListener() {
            @Override
            public void onCommunityClick(Community community) {
                // TODO: otvori community details
            }

            @Override
            public void onFollowClick(Community community) {
                var currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                communityRepository.unfollowCommunity(currentUser.getUid(), community.getId(), new DbCallback<>() {
                    @Override
                    public void onSuccess(Community entity) {
                        loadCommunitiesToFollow();
                        loadCommunitiesYouFollow();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getContext(), "Failed to unfollow community", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.rvCommunitiesYouFollow.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCommunitiesYouFollow.setAdapter(followingAdapter);
    }

    private void setupFab() {
        binding.fabCreateCommunity.setOnClickListener(v -> {
            showCreateCommunityDialog();
        });
    }

    private void showCreateCommunityDialog() {
        communityBinding = DialogCreateCommunityBinding.inflate(getLayoutInflater());

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Create community")
                        .setView(communityBinding.getRoot())
                        .setPositiveButton("Create", null)   // override posle
                        .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                        .create();

        dialog.setOnShowListener(x -> {
            var btnCreate = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            btnCreate.setOnClickListener(view -> handleCreateCommunity(dialog));
        });

        dialog.show();
    }

    private void handleCreateCommunity(AlertDialog dialog) {
        String name = communityBinding.etCommunityName.getText().toString().trim();
        String description = communityBinding.etCommunityDescription.getText().toString().trim();

        if (name.isEmpty()) {
            communityBinding.etCommunityName.setError("Name is required");
            return;
        }

        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        String userId = current.getUid();

        Community community = new Community();
        community.setName(name);
        community.setDescription(description);
        community.setAdminUser(userId);
        community.setFollowers(List.of(userId));

        // createdAt će createCommunity da popuni, kao što si napisao
        communityRepository.createCommunity(community, new DbCallback<>() {
            @Override
            public void onSuccess(Community created) {
                Toast.makeText(getContext(),
                        "Community c/" + created.getName() + " created",
                        Toast.LENGTH_SHORT).show();
                loadCommunitiesToFollow();
                loadCommunitiesYouFollow();
                dialog.dismiss();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(),
                        "Failed to create community: " + exception.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCommunitiesToFollow() {
        var current = auth.getCurrentUser();
        if (current == null) {
            toFollowAdapter.setItems(null);
            return;
        }

        // userNotFollowingCommunities = communities koje user još NE prati
        communityRepository.userNotFollowingCommunities(current.getUid(), new DbCallback<>() {
            @Override
            public void onSuccess(List<Community> communities) {
                toFollowAdapter.setItems(communities);

                if (communities == null || communities.isEmpty()) {
                    binding.tvCommunitiesToFollowSubtitle.setText("No more communities to discover.");
                } else {
                    binding.tvCommunitiesToFollowSubtitle.setText("Discover new communities you might like");
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load communities to follow", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCommunitiesYouFollow() {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            followingAdapter.setItems(null);
            return;
        }

        communityRepository.userFollowingCommunities(current.getUid(), new DbCallback<>() {
            @Override
            public void onSuccess(List<Community> communities) {
                followingAdapter.setItems(communities);

                if (communities == null || communities.isEmpty()) {
                    binding.tvCommunitiesYouFollowSubtitle.setText("You are not following any communities yet.");
                } else {
                    binding.tvCommunitiesYouFollowSubtitle.setText("Manage the communities you are following");
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load followed communities", Toast.LENGTH_SHORT).show();
            }
        });
    }
}