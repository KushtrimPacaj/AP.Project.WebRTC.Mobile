package com.ap.project.webrtcmobile.activities;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventBus.register(this);
        fillView();
        setActions();

        ringtone = RingtoneManager.getRingtone(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        ringtone.play();
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
        ringtone.stop();
        eventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eventBus.post(new DeclineCallEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DestroyIncomingView event) {
        finish();
    }

}
