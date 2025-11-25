package com.example.redditcloneapp.ui.main;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.databinding.ActivityMainBinding;
import com.example.redditcloneapp.ui.community.CommunitiesFragment;
import com.example.redditcloneapp.ui.feed.FeedFragment;
import com.example.redditcloneapp.ui.profile.ProfileFragment;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbarAndDrawer();
        setupNavigationView();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false); // allow normal back behavior
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        if (savedInstanceState == null) {
            // default: Feed
            openFeed();
            binding.navigationView.setCheckedItem(R.id.nav_feed);
        }
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(binding.topAppBar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.topAppBar,
                R.string.nav_open,
                R.string.nav_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                // TODO: open search UI
                return true;
            }
            return false;
        });
    }

    private void setupNavigationView() {
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_feed) {
                openFeed();
            } else if (id == R.id.nav_communities) {
                openCommunities();
            } else if (id == R.id.nav_followers) {
                // TODO: open Followers fragment
            } else if (id == R.id.nav_profile) {
                openProfile();
            } else if (id == R.id.nav_settings) {
                // TODO: open Settings fragment
            }

            binding.drawerLayout.closeDrawers();
            return true;
        });
    }

    private void openFeed() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new FeedFragment())
                .commit();
    }

    private void openCommunities() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new CommunitiesFragment())
                .commit();
    }

    private void openProfile() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new ProfileFragment())
                .commit();
    }
}

