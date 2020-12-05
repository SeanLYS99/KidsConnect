package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.MySingleton;
import com.example.assignment.R;
import com.example.assignment.Service.TrackerService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

    @BindView(R.id.CName) Button cname;

    private static final int PERMISSIONS_REQUEST = 1;
    private String m_FCMtoken;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAuehGYVg:APA91bGBm1WrCq8KYOp5wTZcFs5uz_BagLrHsLt9hBqBuVBdz2HhF7J-RlGJLeZPPuwJrraGO47I8ZcIGfRkNR3thu6HSc7_f6yAynVhV7JIVrURxiuykQNfqcXNPIwzxkkGgp2XGIZQ";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String child_name;
    //private Uri icon_uri;

    Date calendar_date = Calendar.getInstance().getTime();

    SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy HHmm", Locale.getDefault());
    //SimpleDateFormat tf = new SimpleDateFormat("HHmm", Locale.getDefault());
    String datetime = df.format(calendar_date);
    //String time = tf.format(calendar_date);


    SharedPreferences sp;
    //String date = new SimpleDateFormat("yyyy/MM/dd").format(GregorianCalendar.getInstance());
    //LocalDateTime now = LocalDateTime.now()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        ButterKnife.bind(this);

        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);

        /*LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("child_account"));*/

        m_FCMtoken = FirebaseInstanceId.getInstance().getToken();
        //child_name = getIntent().getStringExtra("child_name");
        child_name = sp.getString("name", null);
        cname.setText(child_name);

        // Check GPS is enabled
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
                finish();
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

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String cname = intent.getStringExtra("cname");
        }
    };

    private void startTrackerService() {
        startService(new Intent(this, TrackerService.class));
        //finish();
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
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
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
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
}