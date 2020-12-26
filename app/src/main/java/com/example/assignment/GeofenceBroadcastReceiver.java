package com.example.assignment;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;

import com.example.assignment.Activity.ParentActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, "onReceive parent token: "+intent.getStringExtra("token"));
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            String errorMsg = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.d(TAG, errorMsg);
            return;
        }

        /*for (Geofence geofence : geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }*/

        // get transition type
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        Log.d(TAG, "onReceive: "+geofenceList);
        int transition_type = geofencingEvent.getGeofenceTransition();

        switch (transition_type){
            case Geofence.GEOFENCE_TRANSITION_ENTER: // send noti to fcm
                //notificationHelper.sendHighPriorityNotification("Geofence ENTERED", "your kid has entered home", ParentActivity.class);
                Utils.StructureJSON("Sapphire has entered geofence", "Tap to open app and see where Sapphire is", intent.getStringExtra("token"), context);
//                String id = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid()).collection("notification").document().getId();
//                DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid()).collection("notification").document(id);
//
//                Map<String, Object> notificationMap = new HashMap<>();
//                notificationMap.put("id", id);
//                notificationMap.put("isClicked", "false");
//                notificationMap.put("title", "Geofence Crossed");
//                notificationMap.put("content", "Your kid has entered geofence");
//                notificationMap.put("datetime", datetime);
//                //notificationMap.put("time", time);
//                notificationMap.put("name", child_name);
//
//                doc.set(notificationMap, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //notificationHelper.sendHighPriorityNotification("Geofence EXIT", "your kid has exit home", ParentActivity.class);
                Utils.StructureJSON("Sapphire has left geofence", "Tap to open app and see where Sapphire is", intent.getStringExtra("token"), context);
                break;
        }
    }
}