package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GeofenceActivity extends AppCompatActivity {

    @BindView(R.id.geofence_list) EmptyRecyclerView list;
    @BindView(R.id.geofence_emptyview) ConstraintLayout emptyView;
    @BindView(R.id.EditGeofenceBtn) TextView edit_btn;
    @BindView(R.id.geofence_design) ConstraintLayout design;

    private GeofencingAdapter adapter;
    private int GEOFENCE_STATUS = 1;
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

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(GeofenceActivity.this, ParentActivity.class);
        startActivity(intent);
        finish();*/
        super.onBackPressed();
    }

    @OnClick(R.id.geofence_bakcbtn)
    public void back(){
        /*Intent back = new Intent(this, ParentActivity.class);
        startActivity(back);
        finish();*/
        super.onBackPressed();
    }

    @OnClick(R.id.addGeofenceBtn)
    public void add(){
        Intent add = new Intent(this, AddGeofenceActivity.class);
        add.putExtra("UniqueID", "fromAddBtn");
        startActivity(add);
        //finish();

        /*Intent addgeo = new Intent();
        addgeo.setClass(GeofenceActivity.this, AddGeofenceActivity.class);
        addgeo.putExtra("UniqueID", "fromAddBtn");
        GeofenceActivity.this.startActivity(addgeo);*/
    }

    @OnClick(R.id.EditGeofenceBtn)
    public void edit(){
        ImageView next_btn = design.findViewById(R.id.geofence_nextbtn);
        ImageView delete_btn = design.findViewById(R.id.geofence_deletebtn);
        if(edit_btn.getText().equals("Edit")) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //next_btn.setImageDrawable(getResources().getDrawable(R.drawable.thin_outlined_delete, getApplicationContext().getTheme()));
                //next_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.thin_outlined_delete));
                next_btn.setVisibility(View.GONE);
                delete_btn.setVisibility(View.VISIBLE);
            } else {
                //next_btn.setImageDrawable(getResources().getDrawable(R.drawable.thin_outlined_delete));
                //next_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.thin_outlined_delete));
                next_btn.setVisibility(View.GONE);
                delete_btn.setVisibility(View.VISIBLE);
            }*/
            next_btn.setVisibility(View.GONE);
            delete_btn.setVisibility(View.VISIBLE);
            edit_btn.setText("Save");
            GEOFENCE_STATUS = 0;
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //next_btn.setImageDrawable(getResources().getDrawable(R.drawable.thin_outlined_right_arrow, getApplicationContext().getTheme()));
                //next_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.thin_outlined_right_arrow));
                delete_btn.setVisibility(View.GONE);
                next_btn.setVisibility(View.VISIBLE);
            } else {
                //next_btn.setImageDrawable(getResources().getDrawable(R.drawable.thin_outlined_right_arrow));
                //next_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.thin_outlined_right_arrow));
                delete_btn.setVisibility(View.GONE);
                next_btn.setVisibility(View.VISIBLE);
            }
            edit_btn.setText("Edit");
            GEOFENCE_STATUS = 1;
        }
    }

    private void setupGeofence(){
        try{
            Query query = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("geofencing");
            FirestoreRecyclerOptions<geofencingModel> options = new FirestoreRecyclerOptions.Builder<geofencingModel>()
                    .setQuery(query, geofencingModel.class)
                    .build();

            adapter = new GeofencingAdapter(options, GeofenceActivity.this, GEOFENCE_STATUS);
            list.setEmptyView(emptyView);
            list.setAdapter(adapter);

        }
        catch (Exception e){
            Log.d("GeofenceActivity", "setupGeofence: "+e.getMessage());
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