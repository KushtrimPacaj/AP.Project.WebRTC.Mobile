package com.ap.project.webrtcmobile.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ap.project.webrtcmobile.databinding.ActivityMainBinding;
import com.ap.project.webrtcmobile.events.CallUserEvent;
import com.ap.project.webrtcmobile.services.CallService;
import com.ap.project.webrtcmobile.utils.APPreferences;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupClicks();
        initiateCallService();
        //TODO Add list of users online in server here

    }

    private void initiateCallService() {
        ContextCompat.startForegroundService(this, new Intent(this, CallService.class));
    }

    private void setupClicks() {
        //TODO remove hardcoded ID and set clicks on online users list.
        binding.callUser.setOnClickListener(v -> {
            EventBus.getDefault().post(new CallUserEvent(
                    "a6eade3b-c28c-48b6-9065-1f8f9e589be4",
                    "Kushtrim",
                    APPreferences.getUserId(),
                    APPreferences.getUserName()
            ));
        });
    }
}
