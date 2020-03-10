package com.ap.project.webrtcmobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ap.project.webrtcmobile.R;
import com.ap.project.webrtcmobile.utils.APPreferences;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //TODO replace this with logic that gets the input from user
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            APPreferences.setUserName("Kushtrim");
            APPreferences.setUserId(UUID.randomUUID().toString());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }, 2000);

    }
}
