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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

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

    double lat, lng;
    private GoogleMap mMap;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private LatLng map_location;
    private int RADIUS = 100;
    private Circle circle;
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private int address_code = 1;
    private int VALID_ADDRESS_CODE = 1;
    String from_where;

    // Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // to differentiate save button function
    private String id = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing").document().getId();


    @BindView(R.id.geofence_location_input) TextInputEditText input;
    @BindView(R.id.geofence_name_input) TextInputEditText name_input;
    @BindView(R.id.add_geofence_title) TextView title;
    @BindView(R.id.seekBar1) SeekBar seekbar;
    @BindView(R.id.radius) TextView radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofence);
        ButterKnife.bind(this);

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
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> address;
                        address = geocoder.getFromLocationName(getIntent().getStringExtra("address"), 1);
                        Address loc = address.get(0);
                        map_location = new LatLng(loc.getLatitude(), loc.getLongitude());
                        setMarker();
                        addCircle(map_location, getIntent().getIntExtra("radius", 100));
                    }
                    catch (Exception e) {
                        Toast.makeText(AddGeofenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            updateMap();

        }
    };

    @OnClick(R.id.addGeofenceBackBtn)
    public void back() {
        Intent back = new Intent(this, GeofenceActivity.class);
        startActivity(back);
        finish();
    }

    @OnClick(R.id.SaveGeofenceBtn)
    public void save(){
        if(Utils.isEmpty(name_input))
        {
            Toast.makeText(getApplicationContext(), "Please give geofence a name", Toast.LENGTH_SHORT).show();
        }

        if(Utils.isEmpty(name_input) | Utils.isEmpty(input) | VALID_ADDRESS_CODE == 0){
            return;
        }
        checkAddress();

        String fulladdress = geocoder(input.getText().toString());
        updateFirestore(id, name_input.getText().toString(), fulladdress, RADIUS);
    }

    private String geocoder(String input){
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> location_object;
            location_object = geocoder.getFromLocationName(input, 1);
            Address location_details = location_object.get(0);
            return location_details.getAddressLine(0);
        }
        catch (Exception e)
        {
            Toast.makeText(AddGeofenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
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

    private void updateFirestore(String docID, String name, String address, int radius){
        Map<String, Object> geofencingMap = new HashMap<>();
        geofencingMap.put("radius", radius);
        geofencingMap.put("name", name);
        geofencingMap.put("address", address);
        geofencingMap.put("id", docID);

        try {
            db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing").document(docID)
                    .set(geofencingMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(from_where.equals("fromAddBtn")) {
                                Toast.makeText(getApplicationContext(), "Geofence Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Geofence Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(AddGeofenceActivity.this, GeofenceActivity.class);
                            startActivity(intent);
                            finish();
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

                    mMap.clear();
                    setMarker();
                    addCircle(map_location, RADIUS);
                }
            }
        });
    }

    private void drawMap() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
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

                        try {
                            // Display Current Location
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> address;
                            address = geocoder.getFromLocation(lat, lng, 1);
                            String geolocation = address.get(0).getAddressLine(0);
                            input.setText(geolocation);

                            // Add Marker
                            setMarker();

                            // Update or Add Circle
                            updateCircle();

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        try {
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
                Toast.makeText(getApplicationContext(), "Place not found, please provide a proper address.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            VALID_ADDRESS_CODE = 0;
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
}