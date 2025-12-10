package com.example.datttph39843_ass.Screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datttph39843_ass.R;
import com.example.datttph39843_ass.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
