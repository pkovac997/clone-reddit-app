package com.example.redditcloneapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.ui.main.fragments.CommunitiesFragment;
import com.example.redditcloneapp.ui.main.fragments.HomeFragment;
import com.example.redditcloneapp.ui.main.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        setupBottomNavigation();

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.menu_home);
            loadFragment(new HomeFragment());
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                fragment = new HomeFragment();
            }

            if (itemId == R.id.menu_communities) {
                fragment = new CommunitiesFragment();
            }

            if (itemId == R.id.menu_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
