package com.ap.project.webrtcmobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ap.project.webrtcmobile.R;
import com.ap.project.webrtcmobile.utils.APPreferences;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnSave= (Button) findViewById(R.id.btnSave);
        EditText txtUsername = (EditText)findViewById(R.id.txtUsername);

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = txtUsername.getText().toString();

                if(username.length() < 5) {
                    Toast.makeText(getApplicationContext(),"Username too short",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    APPreferences.setUserName(username);
                    APPreferences.setUserId(UUID.randomUUID().toString());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });

        //TODO replace this with logic that gets the input from user
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//
//            APPreferences.setUserName("Kushtrim");
//            APPreferences.setUserId(UUID.randomUUID().toString());
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }, 2000);

    }
}
