package com.ap.project.webrtcmobile.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ap.project.webrtcmobile.R
import com.ap.project.webrtcmobile.WebRtcMobileApp
import com.ap.project.webrtcmobile.activities.IncomingActivity
import com.ap.project.webrtcmobile.activities.OutgoingActivity
import com.ap.project.webrtcmobile.events.*
import com.ap.project.webrtcmobile.interactors.CallModelInteractor
import com.ap.project.webrtcmobile.interactors.SignalingChannelImpl
import com.ap.project.webrtcmobile.interactors.WebRtcInteractor
import com.ap.project.webrtcmobile.interactors.interfaces.SignalingChannel
import com.ap.project.webrtcmobile.interactors.interfaces.SignalingChannelEvents
import com.ap.project.webrtcmobile.models.*
import com.ap.project.webrtcmobile.utils.APPreferences
import com.ap.project.webrtcmobile.utils.LogTag
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.webrtc.EglBase
import java.util.*


@Suppress("UsePropertyAccessSyntax")
class CallService : Service(), SignalingChannelEvents {

    private var pendingActionsToExecuteAfterViewHasStarted = Collections.synchronizedList(ArrayList<() -> Unit>())

    private lateinit var webRtcInteractor: WebRtcInteractor
    private lateinit var signalingChannel: SignalingChannel
    private val callModelInteractor = CallModelInteractor.getInstance();

    private val eventBus get() = EventBus.getDefault()
    private val rootEglBase: EglBase = EglBase.create()


    override fun onCreate() {
        super.onCreate()
        signalingChannel = SignalingChannelImpl(this)
        signalingChannel.connect()
        WebRtcMobileApp.getInstance().eglBase = rootEglBase
        webRtcInteractor = WebRtcInteractor(signalingChannel)
        eventBus.register(this)
        setServiceAsForeground()
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(LogTag.CALLS, "CallService onDestroy")
        signalingChannel.destroyInstance()
        callModelInteractor.reset()
        webRtcInteractor.disposePeerConnectionsAndLocalStream()
        webRtcInteractor.destroy()
        rootEglBase.release()
    }

    private fun setServiceAsForeground() {

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CallService"
            val chan = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            channelId
        } else {
            ""
        }
        val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Call service!")
                .setContentText("WebRTC Mobile")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
                .setTicker("WebRTC Mobile")
                .build()

        startForeground(42, notification)
    }


    @Subscribe
    public fun onEvent(callUserEvent: CallUserEvent) {
        callModelInteractor.setCallModel(callUserEvent.targetUserId, callUserEvent.targetUserName)
        val pendingLambda: () -> Unit = {
            val audioTrack = webRtcInteractor.getOrCreateAudioTrack()
            val videoTrack = webRtcInteractor.getOrCreateVideoTrack()
            eventBus.post(OnLocalStreamChangeEvent(audioTrack, videoTrack))
            signalingChannel.callUser(callUserEvent.mapToCallModel())
        }
        pendingActionsToExecuteAfterViewHasStarted.add(pendingLambda)
        startOutgoingView()
    }


    @Subscribe
    public fun onEvent(event: AcceptCallEvent) {
        val pendingLambda: () -> Unit = {
            val audioTrack = webRtcInteractor.getOrCreateAudioTrack()
            val videoTrack = webRtcInteractor.getOrCreateVideoTrack()
            eventBus.post(OnLocalStreamChangeEvent(audioTrack, videoTrack))
            signalingChannel.answerCall(AnswerCallModel(
                    APPreferences.userId!!,
                    callModelInteractor.callerId
            ))
        }
        pendingActionsToExecuteAfterViewHasStarted.add(pendingLambda)
        startOutgoingView()
    }

    @Subscribe
    public fun onEvent(event: DeclineCallEvent) {
        signalingChannel.declineCall(DeclineCallModel(
                APPreferences.userId!!,
                callModelInteractor.callerId
        ))
        resetCall()
    }

    @Subscribe
    public fun onEvent(event: EndCallEvent) {
        if (callModelInteractor.areWeInCall()) {
            signalingChannel.endCall(EndCallModel(
                    callModelInteractor.callerId
            ))
            resetCall()
        }
    }

    private fun resetCall() {
        Log.d(LogTag.CALLS, "CallService resetCall")
        Throwable().printStackTrace()
        callModelInteractor.reset()
        webRtcInteractor.disposePeerConnectionsAndLocalStream()
        eventBus.post(DestroyIncomingView())
        eventBus.post(DestroyOutgoingView())
        webRtcInteractor.recreateFactory()
    }

    @Subscribe
    fun onEvent(event: OutgoingViewCreatedEvent) {
        synchronized(pendingActionsToExecuteAfterViewHasStarted) {
            val iterator = pendingActionsToExecuteAfterViewHasStarted.iterator()
            while (iterator.hasNext()) {
                val function = iterator.next()
                function.invoke()
                iterator.remove()
            }
        }
    }

    private fun startOutgoingView() {
        val intent = Intent(this, OutgoingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    override fun onIncomingCall(callModel: CallModel) {
        if (!callModelInteractor.areWeInCall()) {
            callModelInteractor.setCallModel(callModel.fromUserId, callModel.callerName)
            startIncomingActivity()
        } else {
            signalingChannel.declineCall(DeclineCallModel(
                    APPreferences.userId!!,
                    callModel.fromUserId
            ))
        }
    }


    override fun onCallAnswered(model: AnswerCallModel) {
        webRtcInteractor.onUserJoined(callModelInteractor.callerId)
    }

    override fun onReceivedFabricPath(model: FabricPathModel) {
        EventBus.getDefault().post(PathReceivedEvent(model.serializablePath))
    }


    override fun onReceivedOffer(model: WebrtcOfferAnswerExchangeModel?) {
        webRtcInteractor.onReceivedOffer(model)
    }

    override fun onReceivedAnswer(model: WebrtcOfferAnswerExchangeModel?) {
        webRtcInteractor.onReceivedAnswer(model)
    }


    override fun onIceCandidateReceived(model: IceCandidateModel?) {
        webRtcInteractor.onIceCandidateReceived(model)
    }

    override fun onCallEnded(model: EndCallModel?) {
        resetCall()
    }

    override fun onUserOnline(model: ClientInfo) {
        EventBus.getDefault().post(UserOnlineEvent(model))
    }

    override fun onUserOffline(model: ClientInfo) {
        EventBus.getDefault().post(UserOfflineEvent(model))
    }

    override fun onCallDeclined(model: DeclineCallModel) {
        resetCall()
    }

    private fun startIncomingActivity() {
        val intent = Intent(this, IncomingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @Subscribe
    fun onEvent(event: OnIceCandidateGeneratedEvent) {
        signalingChannel.sendIceCandidate(IceCandidateModel(
                event.candidate,
                APPreferences.userId!!,
                event.userId
        ))
    }

    @Subscribe
    public fun onEvent(event: PathCreatedEvent) {
        signalingChannel.sendFabric(callModelInteractor.callerId,event.serializablePath)
    }



    @Subscribe
    public fun onEvent(event: SwitchCameraEvent) {
       webRtcInteractor.switchCamera()
    }



    override fun onBind(intent: Intent): Nothing? = null

}