package com.example.redditcloneapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redditcloneapp.databinding.ActivityRegisterBinding;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.domain.models.User;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;
import com.example.redditcloneapp.infrastructure.firebase.user.UserRepository;
import com.example.redditcloneapp.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private UserRepository userRepository;

    private CommunityRepository communityRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        communityRepository = new CommunityRepository();

        binding.btnRegister.setOnClickListener(v -> onRegisterClicked());
        binding.tvGoToLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void onRegisterClicked() {
        String email = binding.etEmail.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirm = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser == null) {
                            binding.btnRegister.setEnabled(true);
                            Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 2. Kreiramo User dokument u Firestore/Realtime DB preko UserRepository
                        createUserInDatabase(firebaseUser, username);
                    } else {
                        binding.btnRegister.setEnabled(true);
                        String message = "Registration failed";
                        if (task.getException() != null && task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserInDatabase(FirebaseUser firebaseUser, String username) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setUsername(username);
        user.setProfileImageUrl(null);
        user.setCommunityFollows(List.of("DEFAULT"));
        user.setUserFollows(new ArrayList<>());
        user.setCreatedAt(Date.from(Instant.now()));

        communityRepository.userFollowsDefaultCommunity(user.getId());

        userRepository.createUser(user, new DbCallback<>() {
            @Override
            public void onSuccess(User entity) {
                binding.btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                openMain();
            }

            @Override
            public void onError(Exception exception) {
                binding.btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this,
                        "User saved in DB failed: " + exception.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
