package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.MySingleton;
import com.example.assignment.R;
import com.example.assignment.TrackerService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChildActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    private String m_FCMtoken;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAuehGYVg:APA91bGBm1WrCq8KYOp5wTZcFs5uz_BagLrHsLt9hBqBuVBdz2HhF7J-RlGJLeZPPuwJrraGO47I8ZcIGfRkNR3thu6HSc7_f6yAynVhV7JIVrURxiuykQNfqcXNPIwzxkkGgp2XGIZQ";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        m_FCMtoken = FirebaseInstanceId.getInstance().getToken();

        ButterKnife.bind(this);
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
            Toast.makeText(ChildActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

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
                            notificationBody.put("message", "Your child is in danger!");

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
                        Log.i(TAG, "onErrorResponse: Didn't work");
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        try {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("sos", "true");

            db.collection("UserInfo")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .set(userMap, SetOptions.merge())
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
        } catch (Exception e) {
            Toast.makeText(ChildActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.e("send", "yes");
    }
}