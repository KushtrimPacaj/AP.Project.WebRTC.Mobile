package com.ap.project.webrtcmobile.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ap.project.webrtcmobile.interactors.PermissionsInteractor;
import com.ap.project.webrtcmobile.utils.APPreferences;

public class SplashActivity extends AppCompatActivity implements PermissionsInteractor.GrantedCallback {

    PermissionsInteractor permissionsInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsInteractor = new PermissionsInteractor(this, this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (permissionsInteractor.areAllPermissionsGranted()) {
            onPermissionsGranted();
        } else {
            permissionsInteractor.requestPermissionsForApp();
        }

    }

    public void onPermissionsGranted() {
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
