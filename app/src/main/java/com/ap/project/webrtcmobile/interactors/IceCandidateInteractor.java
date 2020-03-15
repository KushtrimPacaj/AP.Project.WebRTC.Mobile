package com.ap.project.webrtcmobile.interactors;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import com.ap.project.webrtcmobile.models.IceCandidateModel;
import com.ap.project.webrtcmobile.utils.LogTag;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;


/**
 * This class handles IceCandidate receiving and adding them to peer connection.
 *
 * If an ICE arrives from remote peer, but offer-answer flow hasn't yet finished (e.g. we dont have a remote description )
 * then we shouldn't add ICE to that peer, but wait until offer-answer finishes.
 *
 * If an ICE arrives before its time, it is saved in a list below, and when offer-answer finishes, the saved ICE are added
 * to the peer connection
 */
public class IceCandidateInteractor {

    private SimpleArrayMap<String, List<IceCandidate>> queuedIceCandidateMap = new SimpleArrayMap<>(5);


    void onIceCandidateReceived(PeerConnection peerConnection, IceCandidateModel model) {
        String otherUserId = model.getFromUserId();
        if (peerConnection.getRemoteDescription() == null) {
            Log.d(LogTag.CALLS, "Remote description for peer connection of user " + otherUserId + " is null. Adding the ICE candidate in a queue");
            addToQueue(model);
        } else {
            Log.d(LogTag.CALLS, "Adding ICE candidate to peer connection of user " + otherUserId);
            peerConnection.addIceCandidate(model.getCandidate());
        }
    }


    void onRemoteDescriptionSet(PeerConnection peerConnection, String userId) {
        List<IceCandidate> candidates = getCandidateListForUser(userId);
        for (IceCandidate iceCandidate : candidates) {
            Log.d(LogTag.CALLS, "Added queed ice candidate to peerConnection after the remote description was set " + iceCandidate);
            peerConnection.addIceCandidate(iceCandidate);
        }
        candidates.clear();
    }

    //private impl


    private void addToQueue(IceCandidateModel model) {
        String otherUserId = model.getFromUserId();
        List<IceCandidate> iceCandidates = getCandidateListForUser(otherUserId);
        iceCandidates.add(model.getCandidate());
    }


    @NonNull
    private List<IceCandidate> getCandidateListForUser(String userId) {
        SimpleArrayMap<String, List<IceCandidate>> map = queuedIceCandidateMap;

        List<IceCandidate> iceCandidates = map.get(userId);

        if (iceCandidates == null) {
            iceCandidates = new ArrayList<>();
            map.put(userId, iceCandidates);
        }
        return iceCandidates;
    }

}
