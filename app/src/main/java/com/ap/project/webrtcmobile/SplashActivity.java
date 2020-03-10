package com.ap.project.webrtcmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ap.project.webrtcmobile.activities.LoginActivity;
import com.ap.project.webrtcmobile.activities.MainActivity;
import com.ap.project.webrtcmobile.utils.APPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APPreferences.getUserName() == null) {
            goToLogin();
        } else {
            goToMain();
        }

    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
