package com.ap.project.webrtcmobile.models;

import com.google.gson.annotations.SerializedName;


public class ClientInfo {

    @SerializedName("userId")
    private String userId;

    @SerializedName("username")
    private String username;


    public ClientInfo(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String toString() {
        return "ClientInfo {" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
