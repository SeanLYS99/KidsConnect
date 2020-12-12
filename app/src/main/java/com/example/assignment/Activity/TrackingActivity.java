package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.firebase.ui.database.FirebaseArray;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackingActivity extends AppCompatActivity {

    private final String TAG = "TrackingActivity";
    private GoogleMap mMap;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    List<String> name_list = new ArrayList<>();
    List<String> token_list = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    @BindView(R.id.tracking_map_pb) ConstraintLayout pb;
    @BindView(R.id.custom_toolbar_title) TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        ButterKnife.bind(this);
        toolbar_title.setText("Real-time Location Tracking");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tracking_map);
        mapFragment.getMapAsync(callback);
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMaxZoomPreference(18);

            pb.setVisibility(View.VISIBLE);
            pushFakeNotification();
            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        }
    };

    @OnClick(R.id.custom_toolbar_back)
    public void back(){
        Utils.StructureJSON("Tracking", "Stop", token_list.get(0), TrackingActivity.this);
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Utils.StructureJSON("Tracking", "Stop", token_list.get(0), TrackingActivity.this);
        super.onBackPressed();
    }

    private void pushFakeNotification(){
        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    token_list.add(snapshot.child("token").getValue().toString());
                    name_list.add(snapshot.child("name").getValue().toString());
                    Log.d(TAG, "token: "+token_list+" name: "+name_list);
                }
                for(int i=0; i<token_list.size(); i++){
                    Utils.StructureJSON("Tracking", "Start", token_list.get(i), TrackingActivity.this);
                    updateMap(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateMap(int i){
            DatabaseReference ref = realtime_db.getReference(firebaseAuth.getCurrentUser().getUid()+"/"+name_list.get(i));
            Log.d(TAG, "updateMap: "+ref);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    setMarker(dataSnapshot, name_list.get(i));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    private void setMarker(DataSnapshot dataSnapshot, String name) {
        try {
            String key = dataSnapshot.getKey();
            DataSnapshot snapshot = dataSnapshot.child("location");
            HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();
            double lat = Double.parseDouble(value.get("latitude").toString());
            double lng = Double.parseDouble(value.get("longitude").toString());
            //if(key.equals("location")) {
//                Log.d(TAG, "loc: " + loc);
//                HashMap<String, Object> value = (HashMap<String, Object>) loc.getValue();
//                double lat = Double.parseDouble(value.get("latitude").toString());
//                double lng = Double.parseDouble(value.get("longitude").toString());


                LatLng location = new LatLng(lat, lng);
                Log.d(TAG, "setMarker: " + location);

                if (!mMarkers.containsKey(key)) {
                    mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(name).position(location).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", 100, 100)))));
                    Marker marker = mMap.addMarker(new MarkerOptions().title(name).position(location).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_marker", 100, 100))));
                    marker.showInfoWindow();
                } else {
                    mMarkers.get(key).setPosition(location);
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : mMarkers.values()) {
                    builder.include(marker.getPosition());
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
                pb.setVisibility(View.GONE);
            //}
        } catch (Exception e) {
            Log.d(TAG, "setMarker error: "+e.getMessage());
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

}