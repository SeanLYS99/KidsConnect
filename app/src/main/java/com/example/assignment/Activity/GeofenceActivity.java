package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.assignment.R;
import com.example.assignment.fragments.dashboard.FeaturesFragment;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GeofenceActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.geofence_bakcbtn)
    public void back(){
        Intent back = new Intent(this, ParentActivity.class);
        startActivity(back);
        finish();
    }

    @OnClick(R.id.addGeofenceBtn)
    public void add(){
        Intent add = new Intent(this, AddGeofenceActivity.class);
        startActivity(add);
        finish();
    }
}