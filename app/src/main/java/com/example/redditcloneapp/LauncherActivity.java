package com.example.redditcloneapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redditcloneapp.ui.intro.IntroActivity;
import com.example.redditcloneapp.ui.auth.LoginActivity;
import com.example.redditcloneapp.util.SharedPreferencesHelper;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPreferencesHelper.isIntroShown(this)) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, IntroActivity.class));
        }

        finish();
    }
}