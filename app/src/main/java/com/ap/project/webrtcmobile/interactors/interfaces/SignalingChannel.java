package com.ap.project.webrtcmobile.interactors.interfaces;

import com.ap.project.webrtcmobile.models.AnswerCallModel;
import com.ap.project.webrtcmobile.models.CallModel;
import com.ap.project.webrtcmobile.models.DeclineCallModel;
import com.ap.project.webrtcmobile.models.EndCallModel;
import com.ap.project.webrtcmobile.models.IceCandidateModel;

import org.webrtc.SessionDescription;


public interface SignalingChannel {

    void callUser(CallModel model);

    void sendIceCandidate(IceCandidateModel model);

    void answerCall(AnswerCallModel model);

    void declineCall(DeclineCallModel model);

    void endCall(EndCallModel model);

    void sendOffer(String targetUserId, SessionDescription sdp);

    void sendAnswer(String targetUserId, SessionDescription sdp);
}
