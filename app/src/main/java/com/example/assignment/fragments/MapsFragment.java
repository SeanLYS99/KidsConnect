package com.example.assignment.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assignment.Activity.AddGeofenceActivity;
import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.GeofenceHelper;
import com.example.assignment.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment {

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID;
    private LatLng latlng;
    private static final String TAG = "MapsFragment";
    private int count, radius;
    private double lat, lng;
    List<Address> address_list;

    //private int FINE_LOCATION_ACCESS_REQUEST_CODE = 101;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 102;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMaxZoomPreference(18);

            //enableUserLocation();
            initUI();

            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeofenceHelper(getContext());
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        /*final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);*/
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /*private void enableUserLocation(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
        else{
            // Ask for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // granted permission
                mMap.setMyLocationEnabled(true);
            }
        }
    }*/

    private void initUI() {
        if(Build.VERSION.SDK_INT >= 29){
            // need background location permission
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                loginAccount();
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                // show a permission request dialog
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

            }
            else{
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

            }
        }
        else{
            loginAccount();
        }
    }

    private void loginAccount(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            drawMap();
            addGeofence(new FirebaseCallBack() {
                @Override
                public void onCallback(List<Double> lat, List<Double> lng, List<Integer> radius) {
                    /*Log.e("id_list", String.valueOf(id_list.size()));
                    Log.e("lat_list", String.valueOf(lat_list.size()));
                    Log.e("lng_list", String.valueOf(lng_list.size()));
                    Log.e("radius_list", String.valueOf(radius_list.size()));*/
                }
            });
        }
    }

    private void drawMap() {
        // Functionality coming next step
        String uid = firebaseAuth.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(uid);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to read value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // Functionality coming next step
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        try {
            String key = dataSnapshot.getKey();
            DataSnapshot loc = dataSnapshot.child("location");
            Log.e("key", loc.toString());
            HashMap<String, Object> value = (HashMap<String, Object>) loc.getValue();
            double lat = Double.parseDouble(value.get("latitude").toString());
            double lng = Double.parseDouble(value.get("longitude").toString());


            LatLng location = new LatLng(lat, lng);
            if (!mMarkers.containsKey(key)) {
                mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key + " is here").position(location).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", 100, 100)))));
                Marker marker = mMap.addMarker(new MarkerOptions().title(key + " is here").position(location).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", 100, 100))));
                marker.showInfoWindow();
            } else {
                mMarkers.get(key).setPosition(location);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : mMarkers.values()) {
                builder.include(marker.getPosition());
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void addCircle (LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.rgb(245,99,91));
        circleOptions.fillColor(Color.argb(20,245,99,91));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void addGeofence(FirebaseCallBack firebaseCallBack) {
        CollectionReference ref = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Declare list
                    List<String> id_list = new ArrayList<>();
                    List<Integer> radius_list = new ArrayList<>();
                    List<Double> lat_list = new ArrayList<>();
                    List<Double> lng_list = new ArrayList<>();

                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        id_list.add(snapshot.getId());
                    }
                    Log.e("lsit", id_list.toString());
                    for(count = 0; count < id_list.size(); count++)
                    {
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
                                        try {
                                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                            address_list = geocoder.getFromLocationName(address, 1);
                                            Log.d(TAG, address_list.toString());

                                            // add retrieve location data into respective list
                                            lat = address_list.get(0).getLatitude();
                                            lng = address_list.get(0).getLongitude();
                                            lat_list.add(lat);
                                            lng_list.add(lng);
                                        }
                                        catch (Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    firebaseCallBack.onCallback(lat_list, lng_list, radius_list);
                                }
                                else
                                {
                                    Log.d(TAG, "Error getting documents: " + task.getException());
                                }
                            }
                        });
                    }

                    /*for(count = 0; count < id_list.size(); count++)
                    {
                        // Add geofence
                        Log.e("after ref", String.valueOf(count));
                        Log.e("id_list", String.valueOf(id_list.size()));
                        Log.e("lat_list", String.valueOf(lat_list.size()));
                        Log.e("lng_list", String.valueOf(lng_list.size()));
                        Log.e("radius_list", String.valueOf(radius_list.size()));
                        // passing all the data inside the list into GeofenceHelper class for process
                        Geofence geofence = geofenceHelper.getGeofence(id_list.get(count), lat_list.get(count), lng_list.get(count), radius_list.get(count), Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
                        GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);
                        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                        int permission = ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION);
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            geofencingClient.addGeofences(request, pendingIntent)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            addCircle(latlng, radius);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String errorMessage = geofenceHelper.getErrorString(e);
                                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }*/
                }
                else{
                    Toast.makeText(getContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void readData(FirebaseCallBack firebaseCallBack){

    }

    private interface FirebaseCallBack{
        void onCallback(List<Double> lat, List<Double> lng, List<Integer> radius);
    }
}