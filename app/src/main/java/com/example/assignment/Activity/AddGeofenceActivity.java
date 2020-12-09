package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.Constants;
import com.example.assignment.MySingleton;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGeofenceActivity extends AppCompatActivity {

    private static final String TAG = "AddGeofenceActivity";
    double lat, lng;
    private GoogleMap mMap;
    private LatLng map_location;
    private int RADIUS = 100;
    private Circle circle;
    private int VALID_ADDRESS_CODE = 1;
    String from_where;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 102;

    // List
    List<String> child_token_list = new ArrayList<>();

    // Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    // to differentiate save button function
    private String id = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing").document().getId();


    @BindView(R.id.geofence_location_input) TextInputEditText input;
    @BindView(R.id.geofence_name_input) TextInputEditText name_input;
    @BindView(R.id.custom_toolbar_title) TextView title;
    @BindView(R.id.seekBar1) SeekBar seekbar;
    @BindView(R.id.radius) TextView radius;
    @BindView(R.id.custom_toolbar_options) TextView options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofence);
        ButterKnife.bind(this);

        options.setVisibility(View.VISIBLE);
        options.setText("Save");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.geofence_map);
        mapFragment.getMapAsync(callback);

        handleSeekbar();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            mMap.setMaxZoomPreference(15);

            from_where = getIntent().getStringExtra("UniqueID");
            if(getIntent() != null) {
                if (from_where.equals("fromAddBtn")) {
                    title.setText("Add Geofence");
                    drawMap();
                    updateMap();
                }
                else {
                    // store data passed by adapter into variables;
                    id = getIntent().getStringExtra("documentId");
                    String existing_name = getIntent().getStringExtra("name");
                    String existing_address = getIntent().getStringExtra("address");
                    int existing_radius = getIntent().getIntExtra("radius", 100);

                    // change layout data
                    title.setText("Edit Geofence");
                    name_input.setText(existing_name);
                    input.setText(existing_address);
                    seekbar.post(new Runnable() {
                        @Override
                        public void run() {
                            seekbar.setProgress(existing_radius - 100);
                        }
                    });

                    map_location = Utils.convertNameToLatLng(AddGeofenceActivity.this, getIntent().getStringExtra("address"), "");
                    Log.d(TAG, "onMapReady: "+map_location);
                    if(map_location != null){
                        setMarker();
                        addCircle(map_location, getIntent().getIntExtra("radius", 100));
                    }
                }
            }
            updateMap();
        }
    };

    @OnClick(R.id.custom_toolbar_back)
    public void back() {
        /*Intent back = new Intent(this, GeofenceActivity.class);
        startActivity(back);
        finish();*/
        super.onBackPressed();
    }

    @OnClick(R.id.custom_toolbar_options)
    public void save(){
        if(Utils.isEmpty(name_input))
        {
            Toast.makeText(getApplicationContext(), "Please give geofence a name", Toast.LENGTH_SHORT).show();
            return;
        }

        checkAddress();

        if(Utils.isEmpty(input) | VALID_ADDRESS_CODE == 0){
            Toast.makeText(getApplicationContext(), "Place not found, please provide a proper address.", Toast.LENGTH_SHORT).show();
            return;
        }

        readToken(new FirebaseCallBack() {
            @Override
            public void onCallBack(List<String> token_list) {
                if(token_list != null){
                    for(int i=0; i<token_list.size(); i++){
                        JSONObject notification = new JSONObject();
                        JSONObject notificationBody = new JSONObject();
                        try {
                            notificationBody.put("title", "Background Service");
                            notificationBody.put("message", "Geofence");

                            notification.put("to", token_list.get(i));
                            notification.put("data", notificationBody);
                        }
                        catch (JSONException e) {
                            Log.d(TAG, "onCallBack: "+e.getMessage());
                        }
                        sendBackgroundNotification(notification);
                    }
                }
            }
        });
        updateFirestore(id, name_input.getText().toString(), input.getText().toString(), RADIUS, map_location);
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    // Hide keyboard after user clicking somewhere
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        input.clearFocus();
        return true;
    }

    private void updateFirestore(String docID, String name, String address, int radius, LatLng loc){
        Map<String, Object> geofencingMap = new HashMap<>();
        geofencingMap.put("radius", radius);
        geofencingMap.put("name", name);
        geofencingMap.put("address", address);
        geofencingMap.put("id", docID);
        geofencingMap.put("LatLng", loc);

        try {
            db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing").document(docID)
                    .set(geofencingMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(from_where.equals("fromAddBtn")) {
                                //Toast.makeText(getApplicationContext(), "Geofence Added Successfully", Toast.LENGTH_SHORT).show();
                                Utils.SuccessSweetDialog(AddGeofenceActivity.this, "Success!", "Geofence added successfully.", "OK", null);
                            }
                            else{
                                Utils.SuccessSweetDialog(AddGeofenceActivity.this, "Success!", "Geofence updated successfully", "OK", null);
                            }
                            /*Intent intent = new Intent(AddGeofenceActivity.this, GeofenceActivity.class);
                            intent.putExtra("fromAddGeofence", "yes");
                            startActivity(intent);
                            finish();*/
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        catch(Exception e){
            Toast.makeText(AddGeofenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMap(){
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(input.getText() != null) {
                        checkAddress();
                    }
                    if(VALID_ADDRESS_CODE == 1) {
                        mMap.clear();
                        setMarker();
                        addCircle(map_location, RADIUS);
                    }
                }
            }
        });
    }

    private void drawMap() {
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
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            map_location = new LatLng(lat, lng);

                            String geolocation = Utils.convertLatLngToAddress(AddGeofenceActivity.this, lat, lng, "");
                            if(geolocation != null){
                                input.setText(geolocation);
                                setMarker();
                                updateCircle();
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
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        map_location = new LatLng(lat, lng);

                        String geolocation = Utils.convertLatLngToAddress(AddGeofenceActivity.this, lat, lng, "");
                        if(geolocation != null){
                            input.setText(geolocation);
                            setMarker();
                            updateCircle();
                        }
                    }
                }
            });
        }
    }

    private void setMarker(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(map_location);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", 100, 100)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map_location, 30));
        mMap.addMarker(markerOptions);
    }

    private void handleSeekbar(){
        int min = 100;
        int max = 1000;
        seekbar.setMax(max-min);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hideKeyboard();
                input.clearFocus();
                name_input.clearFocus();
                RADIUS = min + progress;
                radius.setText(RADIUS + "m");
                updateCircle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void checkAddress(){
        map_location = Utils.convertNameToLatLng(AddGeofenceActivity.this, input.getText().toString(), "");
        if(map_location != null){
            VALID_ADDRESS_CODE = 1;
        }
        else{
            VALID_ADDRESS_CODE = 0;
        }
        /*try {
            Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> address;
            address = coder.getFromLocationName(input.getText().toString(), 1);
            try {
                Address location = address.get(0);
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                map_location = new LatLng(lat, lng);
                VALID_ADDRESS_CODE = 1;
            }
            catch (Exception e) {
                VALID_ADDRESS_CODE = 0;
                Log.d(TAG, "checkAddress: " + e.getMessage());
            }
        } catch (IOException e) {
            VALID_ADDRESS_CODE = 0;
            Log.d(TAG, "Something happen: " + e.getMessage());
        }*/
    }

    private void updateCircle()
    {
        // Check if there is already a circle on Google Map, if yes, update the radius, otherwise add a circle
        if(circle != null){
            circle.setRadius(RADIUS);
        }
        else {
            addCircle(map_location, RADIUS);
        }
    }

    private void addCircle (LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.rgb(245,99,91));
        circleOptions.fillColor(Color.argb(20,245,99,91));
        circleOptions.strokeWidth(4);
        circle = mMap.addCircle(circleOptions);
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void readToken(FirebaseCallBack firebaseCallBack){
        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+snapshot);
                    child_token_list.add(snapshot.child("token").getValue().toString());
                    firebaseCallBack.onCallBack(child_token_list);
                    Log.d(TAG, "onDataChange: "+child_token_list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallBack{
        void onCallBack(List<String> token_list);
    }

    private void sendBackgroundNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                response -> {
                    Log.d(TAG, "sendNotification response: "+response.toString());
                },
                error -> {
                    Log.d(TAG, "onErrorResponse: "+error.getMessage());
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }



//        doc.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.get("parentToken") != null) {
//                    JSONObject notification = new JSONObject();
//                    JSONObject notificationBody = new JSONObject();
//                    try {
//                        notificationBody.put("title", "SOS!");
//                        notificationBody.put("message", child_name + " is in danger!");
//
//                        notification.put("to", document.getString("parentToken"));
//                        notification.put("data", notificationBody);
//                    }
//                    catch (JSONException e) {
//                        Log.e(TAG, "onCreate: " + e.getMessage() );
//                    }
//                    sendNotification(notification);
//                }
//                else {
//                    Toast.makeText(ChildActivity.this, "You haven't connect to your parent yet.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
}