package com.example.redditcloneapp.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redditcloneapp.databinding.FragmentCommunityDetailsBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;

public class CommunityDetailsFragment extends Fragment {

    private static final String ARG_COMMUNITY_ID = "community_id";

    private FragmentCommunityDetailsBinding binding;
    private String communityId;

    private CommunityRepository communityRepository;

    public static CommunityDetailsFragment newInstance(String communityId) {
        CommunityDetailsFragment fragment = new CommunityDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMUNITY_ID, communityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            communityId = getArguments().getString(ARG_COMMUNITY_ID);
        }
        communityRepository = new CommunityRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadCommunity();
        // kasnije ovde možemo dodati loadCommunityPosts();
    }

    private void loadCommunity() {
        if (communityId == null) return;

        communityRepository.getCommunityById(communityId, new DbCallback<Community>() {
            @Override
            public void onSuccess(Community community) {
                if (community == null) {
                    Toast.makeText(getContext(), "Community not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.tvCommunityName.setText("c/" + community.getName());
                binding.tvCommunityDescription.setText(
                        community.getDescription() != null ? community.getDescription() : "");

                int followersCount = community.getFollowers() != null
                        ? community.getFollowers().size()
                        : 0;
                binding.tvCommunityFollowers.setText(followersCount + " followers");

                String admin = community.getAdminUser() != null
                        ? community.getAdminUser()
                        : "unknown";
                binding.tvCommunityAdmin.setText("Admin: " + admin);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getContext(), "Failed to load community", Toast.LENGTH_SHORT).show();
            }
        });
    }
}