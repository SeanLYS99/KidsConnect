package com.example.assignment.Activity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGeofenceActivity extends AppCompatActivity {

    double lat, lng;
    private GoogleMap mMap;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GeofencingClient geofencingClient;
    private LatLng map_location;
    private int RADIUS = 100;
    private Circle circle;
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private int CIRCLE_DELETED_CODE = 0;

    @BindView(R.id.geofence_location_input) TextInputEditText input;
    @BindView(R.id.geofence_name_input) TextInputEditText name_input;
    @BindView(R.id.seekBar1) SeekBar seekbar;
    @BindView(R.id.radius) TextView radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofence);

        ButterKnife.bind(this);

        //initPlace();

        geofencingClient = LocationServices.getGeofencingClient(this);

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
            drawMap();
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

    }

    @OnClick(R.id.geofence_location_input)
    public void search(){
        Log.e("popop", "d");
        /*List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(AddGeofenceActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(AUTOCOMPLETE_REQUEST_CODE == 100 && resultCode == RESULT_OK){
            // When success
            // Initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Set address on TextView
            input.setText(place.getAddress());
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            // When error
            // Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(AddGeofenceActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
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


    private void initPlace(){
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBnyGgr7uCBQJ3YxMxHgpeNHgyumwP-ahY");
        }
    }

    private void updateMap(){
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> address;
                    if(input.getText() != null) {
                        try {
                            address = coder.getFromLocationName(input.getText().toString(), 1);
                            try {
                                Address location = address.get(0);
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                map_location = new LatLng(lat, lng);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Place not found, please provide a proper address.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {

                    }

                    mMap.clear();
                    setMarker();
                    addCircle(map_location, RADIUS);
                }
            }
        });
        /*input.addTextChangedListener(new TextWatcher() {
            //boolean mToggle = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               // }
                //mToggle = !mToggle;
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("after", "changed");
                //if(mToggle){
            }
        });*/
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

    private void updateCircle()
    {
        // Check if there is already a circle on Google Map, if yes, update the radius, otherwise add a circle
        if(circle != null){
            Log.e("hai", "1");
            circle.setRadius(RADIUS);
        }
        else {
            Log.e("hai", "2");
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

    private void addGeofence(LatLng latlng, float radius){

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}