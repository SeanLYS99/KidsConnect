package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.Constants;
import com.example.assignment.MySingleton;
import com.example.assignment.R;
import com.example.assignment.Service.TrackerService;
import com.example.assignment.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChildActivity extends AppCompatActivity {

    @BindView(R.id.child_actionbar) Toolbar action_bar;
    @BindView(R.id.three_dots_icon) ImageView three_dots_icon;
    @BindView(R.id.toolbar_title) TextView title;
    @BindView(R.id.greetings_msg) TextView greetings_msg;

    private static final int PERMISSIONS_REQUEST = 1;
    private String m_FCMtoken;
//    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
//    final private String serverKey = "key=" + "AAAAuehGYVg:APA91bGBm1WrCq8KYOp5wTZcFs5uz_BagLrHsLt9hBqBuVBdz2HhF7J-RlGJLeZPPuwJrraGO47I8ZcIGfRkNR3thu6HSc7_f6yAynVhV7JIVrURxiuykQNfqcXNPIwzxkkGgp2XGIZQ";
//    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    public static String child_name;
    private Map<String, Object> locMap = new HashMap<>();
    SharedPreferences sp, userType;
    private static int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 101;

    // double tap exit
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    //private Uri icon_uri;

    Date calendar_date = Calendar.getInstance().getTime();

    SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy HHmm", Locale.getDefault());
    //SimpleDateFormat tf = new SimpleDateFormat("HHmm", Locale.getDefault());
    String datetime = df.format(calendar_date);
    //String time = tf.format(calendar_date);

    //String date = new SimpleDateFormat("yyyy/MM/dd").format(GregorianCalendar.getInstance());
    //LocalDateTime now = LocalDateTime.now()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        ButterKnife.bind(this);
        setSupportActionBar(action_bar);
        three_dots_icon.setVisibility(View.VISIBLE);

        //action_bar.inflateMenu(R.menu.child_account_menu);

        //title.setTextColor(getResources().getColor(R.color.white));
        //title.setBackgroundColor(getResources().getColor(R.color.red));
        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        userType = getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userType.edit();
        editor.putString("userType", "child");
        editor.apply();

        /*LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("child_account"));*/

        //child_name = getIntent().getStringExtra("child_name");
        child_name = sp.getString("name", null);
        greetings_msg.setText("Hello, "+child_name);

        //cname.setText(child_name);

        // add child's token to firebase realtime database
        m_FCMtoken = FirebaseInstanceId.getInstance().getToken();
        saveToken();

        // Check GPS is enabled
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gps);
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
                //finish();
            }

            // Check location permission is granted - if it is, start
            // the service, otherwise request the permission
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackerService();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "onCreate: "+e.getMessage());
            Toast.makeText(ChildActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //exitToast.cancel();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit App", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String cname = intent.getStringExtra("cname");
        }
    };

    @OnClick(R.id.three_dots_icon)
    public void click(View view){
        PopupMenu popup = new PopupMenu(ChildActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.child_account_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent code = new Intent(ChildActivity.this, CodeActivity.class);
                code.putExtra("Intent", "ChildActivtiy");
                startActivity(code);
                finish();

                return true;
            }
        });
        popup.show();
    }

    private void startTrackerService() {
        //startService(new Intent(this, TrackerService.class));
        //finish();

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Request location updates and when an update is
                // received, store the location in Firebase
                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            try {
                                locMap.put("latitude", location.getLatitude());
                                locMap.put("longitude", location.getLongitude());
                                DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid() + "/" + child_name).child("location");
                                ref.updateChildren(locMap);
                            }
                            catch (Exception e){
                                Log.d(TAG, "getLocation error: "+e.getMessage());
                            }
                        }
                    }
                });
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                // show a permission request dialog
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

            }
            else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

            }
        }

        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        try {
                            locMap.put("latitude", location.getLatitude());
                            locMap.put("longitude", location.getLongitude());
                            DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid() + "/" + child_name).child("location");
                            ref.updateChildren(locMap);
                        }
                        catch (Exception e){
                            Log.d(TAG, "getLocation error: "+e.getMessage());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        try {
            if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start the service when the permission is granted
                startTrackerService();
            } else {
                finish();
            }
        }
        catch (Exception e){
            Log.d(TAG, "onRequestPermissionsResult: "+e.getMessage());
            Toast.makeText(ChildActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.SOS_btn)
    public void SOS(){
        try {
            DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
            doc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.get("parentToken") != null) {
                        JSONObject notification = new JSONObject();
                        JSONObject notificationBody = new JSONObject();
                        try {
                            notificationBody.put("title", "SOS!");
                            notificationBody.put("message", child_name + " is in danger!");

                            notification.put("to", document.getString("parentToken"));
                            notification.put("data", notificationBody);
                        }
                        catch (JSONException e) {
                            Log.e(TAG, "onCreate: " + e.getMessage() );
                        }
                        sendNotification(notification);
                    }
                    else {
                        Toast.makeText(ChildActivity.this, "You haven't connect to your parent yet.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "SOS: "+e.getMessage());
            Toast.makeText(ChildActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                response -> {
                    Toast.makeText(ChildActivity.this, "Sent", Toast.LENGTH_LONG).show();
                    Log.e("hih", "onResponse: " + response.toString());
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChildActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't works");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                Log.e("params", params.toString());
                return params;
            }
        };
        updateFirestore();
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void updateFirestore(){
        String id = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("notification").document().getId();
        DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid()).collection("notification").document(id);
        /*StorageReference storageReference = storage.getReference().child("icons/notification_danger.png");
        Log.d(TAG, "updateFirestore: "+storageReference);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // received image url
                // store data to firestore

            }
        });*/

        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("id", id);
        notificationMap.put("isClicked", "false");
        notificationMap.put("title", "SOS!");
        notificationMap.put("content", child_name + " is in danger!");
        notificationMap.put("datetime", datetime);
        //notificationMap.put("time", time);
        notificationMap.put("name", child_name);

        doc.set(notificationMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void saveToken(){
        Map<String, Object> token_map = new HashMap<>();
        token_map.put("token", m_FCMtoken);
        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/"+child_name);
        ref.updateChildren(token_map);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.child_account_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        Log.d(TAG, "onOptionsItemSelected: logout");
//        return super.onOptionsItemSelected(item);
//    }
}