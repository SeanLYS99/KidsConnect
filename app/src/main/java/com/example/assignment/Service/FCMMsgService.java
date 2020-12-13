package com.example.assignment.Service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment.Activity.ChildActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.GeofenceHelper;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.example.assignment.fragments.MapsFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class FCMMsgService extends FirebaseMessagingService {
    private final String TAG = "FCMMessagingService";
    private final String ADMIN_CHANNEL_ID = "admin_channel";
    ChildActivity child_act;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    TrackerService service = new TrackerService();

    List<String> id_list = new ArrayList<>();
    List<Integer> radius_list = new ArrayList<>();
    List<LatLng> latlng_list = new ArrayList<>();
    private static ArrayList<Long> alreadyNotifiedTimestamps = new ArrayList<>();
    private int count;
    private Handler handler = new Handler();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!isDuplicate(remoteMessage.getSentTime())) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            Log.d(TAG, "onMessageReceived: " + title);
            if (title.equals("SOS!") || title.equals("Sapphire has entered geofence") || title.equals("Sapphire has left geofence")) {
                Log.d(TAG, "onMessageTitle: SOS");
                if(title.equals("SOS!")){
                    Log.d(TAG, "call: ");
                    DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot snapshot = task.getResult();
                                String phone = snapshot.getString("phone");
                                Intent call = new Intent(Intent.ACTION_CALL);
                                call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                call.setData(Uri.parse("tel:" + phone));
                                startActivity(call);
                            }
                        }
                    });

                }
                final Intent intent = new Intent(this, ParentActivity.class);
                //Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT).show();
                //startService(new Intent(this, TrackerService.class));
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int notificationID = new Random().nextInt(3000);

              /*
                Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
                to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
              */
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setupChannels(notificationManager);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher);

                //DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("notification").document()
                Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        //.setContentText(child_act.child_name + " is in danger!")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(notificationSoundUri)
                        .setContentIntent(pendingIntent);

                //Set notification color to match your app color template
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                notificationManager.notify(notificationID, notificationBuilder.build());

            } else if (title.equals("Background Service")) {
                String parent_token = message;
                Log.d(TAG, "onMessageReceived: " + parent_token);
                geofencingClient = LocationServices.getGeofencingClient(getBaseContext());
                geofenceHelper = new GeofenceHelper(getApplicationContext(), parent_token);

                retrieveData(new FirebaseCallBack() {
                    @Override
                    public void onCallback(List<String> id, List<LatLng> latlng_list, List<Integer> rad) {
                        //Log.d(TAG, "id_list: " + id_list.size() + "latlng_list: " + latlng_list.size() + "rad_list: " + rad.size());
                        if (id_list.size() == rad.size() && id_list.size() == latlng_list.size()) { // make sure all the values are stored in the list
                            setGeofence();
                            Log.d(TAG, "id_list: " + id_list.get(0) + "latlng_list: " + latlng_list.get(0) + "rad_list: " + rad.get(0));


                            Log.d(TAG, "onCallback: DONE");

                        }
                    }
                });
            } else if (title.equals("Remove Geofence")) {
                geofencingClient = LocationServices.getGeofencingClient(getBaseContext());
                geofenceHelper = new GeofenceHelper(getApplicationContext(), message);
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
                geofencingClient.removeGeofences(pendingIntent)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Geofence Removed... ");
                            DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid()).collection("geofencing").document(message);
                            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to remove geofence: " + e.getMessage());
                        });
            } else if (title.equals("Tracking")) {
                if (message.equals("Start")) {
                    startService(new Intent(this, TrackerService.class));
                } else {

                    stopService(new Intent(this, TrackerService.class));
                    unregisterReceiver(service.stopReceiver);
                    stopSelf();
                }
            }
            else if(title.equals("Block Apps")){
                Log.d(TAG, "onMessageReceived: Block Apps yes");
                Intent a = new Intent(this, AppService.class);
                a.putExtra("message", message);
                startService(a);
                /*Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        String packageName = manager.getRunningAppProcesses().get(0).processName;
                        Log.d(TAG, "packageName: "+packageName);
                        Log.d(TAG, "message: "+message);
                        if (packageName.toLowerCase().contains(message)) {
                            Intent i = new Intent(getBaseContext(), ParentActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(runnable);

                // Lollipop and above
                UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -1);
                long start = cal.getTimeInMillis();
                long end = System.currentTimeMillis();
                List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                for (UsageStats stats : queryUsageStats) {
                    Log.e("TAG", "Usage stats for: " + stats.getPackageName());
                }*/

//                for (ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
//                    Log.e(TAG, "Running process: " + info.processName);
//                    Log.d(TAG, "blocked app name: "+message);
//                    if (message.toLowerCase().equals(info.processName)) {
//                        Intent i = new Intent(getBaseContext(), ParentActivity.class);
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(i);
//                    }
//                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    private void retrieveData(FirebaseCallBack firebaseCallBack) {
        // Refer to firestore collection
        CollectionReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid()).collection("geofencing");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // successfully read all the document inside this collection
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        // save all the documentId inside id_list
                        id_list.add(snapshot.getId());
                    }

                    for (count = 0; count < id_list.size(); count++) {
                        Log.e("before ref", String.valueOf(count));
                        ref.document(id_list.get(count)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        // declare variables and store values
                                        String address = doc.getString("address");

                                        // add retrieved radius into a list
                                        radius_list.add(doc.getLong("radius").intValue());

                                        // transfer location name into latlng
                                        latlng_list.add(Utils.convertNameToLatLng(getBaseContext(), address, ""));
                                        /*try {
                                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                            address_list = geocoder.getFromLocationName(address, 1);
                                            Log.d(TAG, address_list.toString());

                                            // add retrieve location data into respective list
                                            lat = address_list.get(0).getLatitude();
                                            lng = address_list.get(0).getLongitude();
                                            LatLng latLng = new LatLng(lat, lng);
                                            latlng_list.add(latLng);
                                        }
                                        catch (Exception e) {
                                            Log.d(TAG, "onComplete: false");
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }*/
                                        firebaseCallBack.onCallback(id_list, latlng_list, radius_list);
                                        Log.d(TAG, "onComplete: yes");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: " + task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "onComplete Error: " + task.getException());
                }
            }
        });
    }

    // A way to deal with firebase asynchronous API
    private interface FirebaseCallBack {
        void onCallback(List<String> id, List<LatLng> latLngList, List<Integer> radius);
    }

    private void setGeofence() {
        int permission = ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
        // If permission is granted
        // read the list size and send every items inside different list to GeofenceHelper
            Log.d(TAG, "setGeofence: before");
            for (count = 0; count < latlng_list.size(); count++) {
                Log.d(TAG, "setGeofence: loop started");
                Log.d(TAG, "id_list: " + id_list.get(count) + "latlng_list: " + latlng_list.get(count) + "rad_list: " + radius_list.get(count));
                // Set geofence's rule. example: lat,lng,radius,initial trigger ...
                Geofence geofence = geofenceHelper.getGeofence(id_list.get(count), latlng_list.get(count), radius_list.get(count), Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
                GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                geofencingClient.addGeofences(request, pendingIntent)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Because geofence doesn't come with an indicator, so add a circle to the map for better user experience
                                //addCircle(latlng_list.get(temp), radius_list.get(temp));
                                Log.d(TAG, "onSuccess: Geofence Added....");
                            }
                        })
                        .addOnFailureListener(e -> {
                            String errorMsg = geofenceHelper.getErrorString(e);
                            Log.d(TAG, "onFailure: " + errorMsg);
                        });
            }
        }
    }

    // Workaround for Firebase duplicate pushes
    private boolean isDuplicate(long timestamp) {
        if (alreadyNotifiedTimestamps.contains(timestamp)) {
            alreadyNotifiedTimestamps.remove(timestamp);
            return true;
        } else {
            alreadyNotifiedTimestamps.add(timestamp);
        }

        return false;
    }

    /*protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };*/

}
