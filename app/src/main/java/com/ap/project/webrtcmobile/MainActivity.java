package com.ap.project.webrtcmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ap.project.webrtcmobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
