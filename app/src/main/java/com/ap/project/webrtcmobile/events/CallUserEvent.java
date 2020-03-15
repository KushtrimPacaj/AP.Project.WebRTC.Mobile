package com.ap.project.webrtcmobile.events;

import com.ap.project.webrtcmobile.models.CallModel;

public class CallUserEvent {

    private final String targetUserId;
    private String targetUserName;
    private final String fromUserId;
    private final String fromUserName;

    public CallUserEvent(String targetUserId, String targetUserName, String fromUserId, String fromUserName) {
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public CallModel mapToCallModel() {
        return new CallModel(fromUserId, fromUserName, targetUserId);
    }

}
