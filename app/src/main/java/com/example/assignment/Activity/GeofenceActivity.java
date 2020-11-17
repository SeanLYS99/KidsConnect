package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.assignment.Adapter.GeofencingAdapter;
import com.example.assignment.EmptyRecyclerView;
import com.example.assignment.Model.geofencingModel;
import com.example.assignment.R;
import com.example.assignment.fragments.dashboard.FeaturesFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GeofenceActivity extends AppCompatActivity {

    @BindView(R.id.geofence_list) EmptyRecyclerView list;
    @BindView(R.id.geofence_emptyview) ConstraintLayout emptyView;

    private GeofencingAdapter adapter;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        setupGeofence();

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
        add.putExtra("UniqueID", "fromAddBtn");
        startActivity(add);
        finish();

        /*Intent addgeo = new Intent();
        addgeo.setClass(GeofenceActivity.this, AddGeofenceActivity.class);
        addgeo.putExtra("UniqueID", "fromAddBtn");
        GeofenceActivity.this.startActivity(addgeo);*/
    }

    private void setupGeofence(){
        try{
            Query query = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing");
            FirestoreRecyclerOptions<geofencingModel> options = new FirestoreRecyclerOptions.Builder<geofencingModel>()
                    .setQuery(query, geofencingModel.class)
                    .build();

            adapter = new GeofencingAdapter(options, GeofenceActivity.this);
            list.setEmptyView(emptyView);
            list.setAdapter(adapter);

        }
        catch (Exception e){
            Toast.makeText(GeofenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }

}