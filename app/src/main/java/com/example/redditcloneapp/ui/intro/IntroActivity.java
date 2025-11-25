package com.example.redditcloneapp.ui.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redditcloneapp.databinding.ActivityIntroBinding;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;
import com.example.redditcloneapp.ui.auth.LoginActivity;
import com.example.redditcloneapp.ui.auth.RegisterActivity;
import com.example.redditcloneapp.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding;
    private CommunityRepository communityRepository = new CommunityRepository();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        communityRepository.ensureDefaultCommunityExists();

        auth = FirebaseAuth.getInstance();

        // If already logged in, you can skip intro entirely:
        if (auth.getCurrentUser() != null) {
            openMain();
            finish();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        binding.btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });
    }

    private void openMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
