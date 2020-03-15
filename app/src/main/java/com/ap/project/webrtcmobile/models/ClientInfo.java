package com.ap.project.webrtcmobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;


public class ClientInfo {

    @SerializedName("userId")
    private String userId;

    @SerializedName("username")
    private String username;


    public ClientInfo(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "ClientInfo {" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
