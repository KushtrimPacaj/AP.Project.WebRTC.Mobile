package com.ap.project.webrtcmobile.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ap.project.webrtcmobile.databinding.ActivityIncomingBinding;
import com.ap.project.webrtcmobile.events.AcceptCallEvent;
import com.ap.project.webrtcmobile.events.DeclineCallEvent;
import com.ap.project.webrtcmobile.events.DestroyIncomingView;
import com.ap.project.webrtcmobile.interactors.CallModelInteractor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IncomingActivity extends AppCompatActivity {

    private ActivityIncomingBinding binding;
    private EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventBus.register(this);
        fillView();
        setActions();
    }

    private void setActions() {
        binding.acceptBtn.setOnClickListener(v -> eventBus.post(new AcceptCallEvent()));
        binding.declineBtn.setOnClickListener(v -> eventBus.post(new DeclineCallEvent()));
    }

    private void fillView() {
        binding.incomingCallInfo.setText(String.format("Incoming call from:\n%s", CallModelInteractor.getInstance().getCallerName()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DestroyIncomingView event) {
        finish();
    }

}
