package com.example.assignment.Service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TrackerService extends Service {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = TrackerService.class.getSimpleName();
    SharedPreferences sp;

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        buildNotification();
        loginToFirebase();
    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.track_title))
                .setContentText(getString(R.string.track_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1, builder.build());
    }

    public BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

//    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //Log.d(TAG, "received stop broadcast");
//            // Stop the service when the notification is tapped
//            unregisterReceiver(stopReceiver);
//            stopSelf();
//        }
//    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loginToFirebase() {
        // Functionality coming next step
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            requestLocationUpdates();
        }
        /*String email2 = firebaseAuth.getCurrentUser().getEmail();
        Log.e("email",email2);
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    requestLocationUpdates();
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });*/
    }

    private void requestLocationUpdates() {
        String uid = firebaseAuth.getCurrentUser().getUid(); // Get UID of the current signed in user
        String name = sp.getString("name", null); // Get the name of the child, who is being tracked
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = uid + "/" + name;
        int permission = ContextCompat.checkSelfPermission(this, // check if user has enabled location permission
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) { // if yes only start tracking
            // Request location updates and when an update is
            // received, store the location in Firebase Realtime database
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child("location");
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        ref.setValue(location); // save all returned json data into firebase realtime database
                    }
                }
            }, null);
        }
    }
}
