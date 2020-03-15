package com.ap.project.webrtcmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ap.project.webrtcmobile.databinding.ActivityLoginBinding;
import com.ap.project.webrtcmobile.utils.APPreferences;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {
        binding.btnSave.setOnClickListener(v -> {
            String username = binding.txtUsername.getText().toString();
            if (username.length() < 5) {
                Toast.makeText(getApplicationContext(), "Username too short", Toast.LENGTH_SHORT).show();
            } else {
                APPreferences.setUserName(username);
                APPreferences.setUserId(UUID.randomUUID().toString());
                goToMainScreen();
            }
        });
    }

    private void goToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
