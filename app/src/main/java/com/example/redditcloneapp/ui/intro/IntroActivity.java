package com.example.redditcloneapp.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redditcloneapp.R;
import com.example.redditcloneapp.ui.auth.LoginActivity;
import com.example.redditcloneapp.util.SharedPreferencesHelper;

public class IntroActivity extends AppCompatActivity {

    private Button getStartedButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getStartedButton = findViewById(R.id.getStartedButton);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper.setIntroShown(IntroActivity.this, true);
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}
