package com.ap.project.webrtcmobile.view_interfaces;

import com.ap.project.webrtcmobile.models.ClientInfo;
import com.hannesdorfmann.mosby.mvp.MvpView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MainView extends MvpView {

    void initView();

    void showOnlineUsers(@NotNull List<? extends ClientInfo> users);

    void onUserCameOnline(@NotNull ClientInfo model);

    void onUserWentOffline(@NotNull ClientInfo model);
}
