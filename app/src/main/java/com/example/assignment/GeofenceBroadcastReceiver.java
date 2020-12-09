package com.example.assignment;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleService;

import com.example.assignment.Activity.ParentActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

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
                notificationHelper.sendHighPriorityNotification("Geofence ENTERED", "your kid has entered home", ParentActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                notificationHelper.sendHighPriorityNotification("Geofence EXIT", "your kid has exit home", ParentActivity.class);
                break;
        }
    }
}