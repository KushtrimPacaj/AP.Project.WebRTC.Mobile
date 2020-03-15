package com.ap.project.webrtcmobile.interactors.interfaces;


import com.ap.project.webrtcmobile.models.AnswerCallModel;
import com.ap.project.webrtcmobile.models.CallModel;
import com.ap.project.webrtcmobile.models.ClientInfo;
import com.ap.project.webrtcmobile.models.DeclineCallModel;
import com.ap.project.webrtcmobile.models.EndCallModel;
import com.ap.project.webrtcmobile.models.IceCandidateModel;
import com.ap.project.webrtcmobile.models.WebrtcOfferAnswerExchangeModel;

public interface SignalingChannelEvents {


    void onIceCandidateReceived(IceCandidateModel model);

    void onIncomingCall(CallModel callModel);

    void onCallAnswered(AnswerCallModel model);

    void onCallDeclined(DeclineCallModel model);

    void onReceivedOffer(WebrtcOfferAnswerExchangeModel model);

    void onReceivedAnswer(WebrtcOfferAnswerExchangeModel model);

    void onCallEnded(EndCallModel model);

    void onUserOffline(ClientInfo model);

    void onUserOnline(ClientInfo model);
}

