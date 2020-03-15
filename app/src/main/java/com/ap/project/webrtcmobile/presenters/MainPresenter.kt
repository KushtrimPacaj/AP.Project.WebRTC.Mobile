package com.ap.project.webrtcmobile.presenters

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ap.project.webrtcmobile.WebRtcMobileApp
import com.ap.project.webrtcmobile.events.UserOfflineEvent
import com.ap.project.webrtcmobile.events.UserOnlineEvent
import com.ap.project.webrtcmobile.interactors.ApiInteractor
import com.ap.project.webrtcmobile.services.CallService
import com.ap.project.webrtcmobile.utils.APPreferences
import com.ap.project.webrtcmobile.view_interfaces.MainView
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit


class MainPresenter : MvpBasePresenter<MainView?>(), LifecycleObserver {


    private val compositeSubscription = CompositeSubscription()
    private val myUserId = APPreferences.userId

    fun init() {
        initiateCallService()
        EventBus.getDefault().register(this)
        view?.initView()
        fetchOnlineUsers()
    }

    private fun fetchOnlineUsers() {
        val subscription = ApiInteractor.getHelloApiService().onlineUsers
                .retryWhen { errors -> errors.flatMap { Observable.timer(5, TimeUnit.SECONDS) } }
                .repeatWhen { completed -> completed.delay(5, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { users ->
                    users.removeAll { it.userId == myUserId }
                    view?.showOnlineUsers(users)
                }
        compositeSubscription.add(subscription)

    }

    @Subscribe
    fun onEvent(event: UserOnlineEvent) {
        view?.onUserCameOnline(event.model)
    }

    @Subscribe
    fun onEvent(event: UserOfflineEvent) {
        view?.onUserWentOffline(event.model)
    }

    private fun initiateCallService() {
        val context: Context = WebRtcMobileApp.getInstance()
        ContextCompat.startForegroundService(context, Intent(context, CallService::class.java))
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        compositeSubscription.unsubscribe()
        EventBus.getDefault().unregister(this)

    }
}