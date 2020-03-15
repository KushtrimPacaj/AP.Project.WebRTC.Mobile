package com.ap.project.webrtcmobile.interactors;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ap.project.webrtcmobile.interactors.interfaces.SignalingChannel;
import com.ap.project.webrtcmobile.interactors.interfaces.SignalingChannelEvents;
import com.ap.project.webrtcmobile.models.AnswerCallModel;
import com.ap.project.webrtcmobile.models.CallModel;
import com.ap.project.webrtcmobile.models.ClientInfo;
import com.ap.project.webrtcmobile.models.DeclineCallModel;
import com.ap.project.webrtcmobile.models.EndCallModel;
import com.ap.project.webrtcmobile.models.IceCandidateModel;
import com.ap.project.webrtcmobile.models.SdpModel;
import com.ap.project.webrtcmobile.models.WebrtcOfferAnswerExchangeModel;
import com.ap.project.webrtcmobile.utils.APPreferences;
import com.ap.project.webrtcmobile.utils.LogTag;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.EngineIOException;
import rx.Observable;
import rx.Subscription;


@SuppressWarnings("FieldCanBeLocal")
public class SignalingChannelImpl implements SignalingChannel {

    private Socket socket;
    private String oldSocketId;
    private SignalingChannelEvents signalingChannelEvents;
    private Subscription loggingSubscription;

    public SignalingChannelImpl(SignalingChannelEvents signalingChannelEvents) {
        this.signalingChannelEvents = signalingChannelEvents;
    }

    public void connect() {
        Log.d(LogTag.CALLS, "Connect to calls server was invoked");
        try {
            socket = IO.socket(getServerURL(), null);
            subscribeOnSocketLifecycleMethods();
            subscribeOnCustomMethods();
            socket.connect();

            loggingSubscription = Observable.interval(5, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        Log.d(LogTag.CALLS, "SocketIoStatus conected: " + socket.connected() + " " + ", socketId: " + socket.id());
                    }, Throwable::printStackTrace);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void subscribeOnSocketLifecycleMethods() {
        socket.on(Socket.EVENT_CONNECT, args -> {

            Log.d(LogTag.CALLS, "event_connect. Id : " + socket.id());
            saveUserInfo();
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d(LogTag.CALLS, "event_disconnect. lastSocketId " + oldSocketId);

        });
        socket.on(Socket.EVENT_ERROR, args -> {
            Log.d(LogTag.CALLS, "event_error. " + args[0].toString() + " .lastSocketId " + oldSocketId);

        });
        socket.on(Socket.EVENT_CONNECTING, args -> {
            Log.d(LogTag.CALLS, "event_connecting. lastSocketId " + oldSocketId);

        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.d(LogTag.CALLS, "event_connect_error. " + parseDetailsMessage(args) + " " + ".lastSocketId " + oldSocketId);

        });
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            Log.d(LogTag.CALLS, "event_connect_timeout. lastSocketId " + oldSocketId);

        });
        socket.on(Socket.EVENT_RECONNECT, args -> {
            Log.d(LogTag.CALLS, "event_reconnect. lastSocketId " + oldSocketId);
        });
        socket.on(io.socket.engineio.client.Socket.EVENT_CLOSE, args -> {
            Log.d(LogTag.CALLS, "EVENT_CLOSE." + parseDetailsMessage(args) + " lastSocketId " + oldSocketId);
        });
    }


    private String parseDetailsMessage(Object[] args) {
        if (args == null) {
            return "Argument is null";
        }
        if (args.length != 1) {
            return "More than one argument";
        }
        Object arg = args[0];
        if (arg instanceof EngineIOException) {
            Throwable cause = ((EngineIOException) arg).getCause();
            if (cause != null) {
                return cause.getMessage();
            } else {
                return "Detailed message not available. Throwable cause is null.";
            }
        } else {
            return "Argument is not an instance of EngineIOException. " + (arg != null ?
                    arg.toString() : "");
        }

    }


    private void subscribeOnCustomMethods() {


        socket.on(SocketIOMethods.ON_INCOMING_CALL, args -> {
            CallModel model = parseSingleArgument(CallModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_INCOMING_CAL " + model);

            new Handler(Looper.getMainLooper()).post(() -> signalingChannelEvents.onIncomingCall(model));
        });

        socket.on(SocketIOMethods.ON_CALL_ANSWERED, args -> {
            AnswerCallModel model = parseSingleArgument(AnswerCallModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_CALL_ANSWERED " + model);

            signalingChannelEvents.onCallAnswered(model);
        });
        socket.on(SocketIOMethods.ON_CALL_ENDED, args -> {
            EndCallModel model = parseSingleArgument(EndCallModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_CALL_ENDED " + model);

            signalingChannelEvents.onCallEnded(model);
        });


        socket.on(SocketIOMethods.ON_CALL_DECLINED, args -> {
            DeclineCallModel model = parseSingleArgument(DeclineCallModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_CALL_DECLINED " + model);

            signalingChannelEvents.onCallDeclined(model);
        });

        socket.on(SocketIOMethods.ON_ICE_CANDIDATE_RECEIVED, args -> {
            IceCandidateModel model = parseSingleArgument(IceCandidateModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_ICE_CANDIDATE_RECEIVED " + model);

            signalingChannelEvents.onIceCandidateReceived(model);
        });

        socket.on(SocketIOMethods.ON_RECEIVED_OFFER, args -> {
            WebrtcOfferAnswerExchangeModel model = parseSingleArgument(WebrtcOfferAnswerExchangeModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_RECEIVED_OFFER " + model);

            signalingChannelEvents.onReceivedOffer(model);
        });


        socket.on(SocketIOMethods.ON_RECEIVED_ANSWER, args -> {
            WebrtcOfferAnswerExchangeModel model = parseSingleArgument(WebrtcOfferAnswerExchangeModel.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_RECEIVED_ANSWER " + model);

            signalingChannelEvents.onReceivedAnswer(model);
        });

        socket.on(SocketIOMethods.ON_USER_ONLINE, args -> {
            ClientInfo model = parseSingleArgument(ClientInfo.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_USER_ONLINE " + model);

            signalingChannelEvents.onUserOnline(model);
        });
        socket.on(SocketIOMethods.ON_USER_DISCONNECTED, args -> {
            ClientInfo model = parseSingleArgument(ClientInfo.class, args);
            Log.d(LogTag.CALLS, "SocketIOMethods.ON_USER_DISCONNECTED " + model);

            signalingChannelEvents.onUserOffline(model);
        });

    }


    private void saveUserInfo() {

        ClientInfo clientInfo = new ClientInfo(
                APPreferences.getUserId(),
                APPreferences.getUserName()
        );
        Log.d(LogTag.CALLS, "Save client info. new socketId : " + socket.id() + ", model: " + clientInfo);


        JSONObject data = jsonify(clientInfo);


        Log.d(LogTag.CALLS, "Emitting event." + SocketIOMethods.SAVE_CLIENT_INFO + " args " + clientInfo);

        socket.emit(SocketIOMethods.SAVE_CLIENT_INFO, data, (Ack) args -> {
            Log.d(LogTag.CALLS, "Store Client Info ACK");
        });

        oldSocketId = socket.id();
    }

    @Override
    public void callUser(CallModel model) {
        Log.d(LogTag.CALLS, "SocketIOMethods.CALL_USER: \n" + model.toString());

        socket.emit(SocketIOMethods.CALL_USER, jsonify(model));

    }

    @Override
    public void answerCall(AnswerCallModel model) {
        Log.d(LogTag.CALLS, "SocketIOMethods.ANSWER_CALL: \n" + model.toString());

        socket.emit(SocketIOMethods.ANSWER_CALL, jsonify(model));
    }


    @Override
    public void declineCall(DeclineCallModel model) {
        Log.d(LogTag.CALLS, "SocketIOMethods.DECLINE_CALL: \n" + model.toString());
        new Throwable().printStackTrace();
        socket.emit(SocketIOMethods.DECLINE_CALL, jsonify(model));
    }


    @Override
    public void sendIceCandidate(IceCandidateModel model) {
        Log.d(LogTag.CALLS, "SocketIOMethods.SEND_ICE_CANDIDATE: \n" + model.toString());

        socket.emit(SocketIOMethods.SEND_ICE_CANDIDATE, jsonify(model));
    }


    @Override
    public void endCall(EndCallModel model) {
        Log.d(LogTag.CALLS, "SocketIOMethods.END_CALL: \n" + model.toString());
        socket.emit(SocketIOMethods.END_CALL, jsonify(model));
    }


    @Override
    public void sendOffer(String targetUserId, SessionDescription sdp) {
        Log.d(LogTag.CALLS, String.format("SocketIOMethods.SEND_OFFER: toUserId %s , sdp %s", targetUserId, sdp.description));
        String myUserId = APPreferences.getUserId();
        WebrtcOfferAnswerExchangeModel model = new WebrtcOfferAnswerExchangeModel(
                myUserId,
                targetUserId,
                new SdpModel(sdp)
        );

        socket.emit(SocketIOMethods.SEND_OFFER, jsonify(model));

    }


    @Override
    public void sendAnswer(String targetUserId, SessionDescription sdp) {
        Log.d(LogTag.CALLS, String.format("SocketIOMethods.SEND_ANSWER:  toUserId %s , sdp %s", targetUserId, sdp.description));
        String myUserId = APPreferences.getUserId();
        WebrtcOfferAnswerExchangeModel model = new WebrtcOfferAnswerExchangeModel(
                myUserId,
                targetUserId,
                new SdpModel(sdp)
        );

        socket.emit(SocketIOMethods.SEND_ANSWER, jsonify(model));

    }


    public void destroyInstance() {
        Log.d(LogTag.CALLS, "destroy socket instance");

        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
        if (loggingSubscription != null) {
            loggingSubscription.unsubscribe();
        }
    }

    //helper methods
    private static JSONObject jsonify(Object object) {
        try {
            return new JSONObject(new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T parseSingleArgument(Class<T> tClass, Object... args) {
        return new Gson().fromJson(args[0].toString(), tClass);
    }

    private static String getServerURL() {
        if ((Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")) {
            //emulators can refer to their host via this IP.
            return "http://10.0.2.2:3000/";
        } else {
            return "http://192.168.1.118:3000/";
        }
    }
}
