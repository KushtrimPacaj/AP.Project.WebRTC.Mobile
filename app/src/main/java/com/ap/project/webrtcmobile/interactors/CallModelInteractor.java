package com.ap.project.webrtcmobile.interactors;

import org.jetbrains.annotations.NotNull;

public class CallModelInteractor {

    private static CallModelInteractor instance;

    private String callerId = null;
    private String callerName = null;


    public synchronized static CallModelInteractor getInstance() {
        if (instance == null) {
            instance = new CallModelInteractor();
        }
        return instance;
    }

    public void reset() {
        callerId = null;
        callerName = null;
    }

    public boolean areWeInCall() {
        return callerId != null && callerName != null;
    }

    public void setCallModel(@NotNull String callerId, String callerName) {
        this.callerId = callerId;
        this.callerName = callerName;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCallerName() {
        return callerName;
    }
}
