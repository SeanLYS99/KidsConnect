package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.assignment.Adapter.AppAdapter;
import com.example.assignment.Adapter.DeviceListAdapter;
import com.example.assignment.Model.deviceModel;
import com.example.assignment.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppActivity extends AppCompatActivity {

    private static final String TAG = "AppActivity";
    @BindView(R.id.custom_toolbar_title) TextView title;
    @BindView(R.id.app_recyclerview) RecyclerView app_recview;
    //@BindView(R.id.app_child_recyclerview) RecyclerView acc_recview;
    @BindView(R.id.app_pb) ConstraintLayout pb;

    private AppAdapter adapter;
    private List<String> appsNameList = new ArrayList<>();
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        ButterKnife.bind(this);
        title.setText("Installed Apps");
        app_recview.setLayoutManager(new LinearLayoutManager(AppActivity.this));
        //acc_recview.setLayoutManager(new LinearLayoutManager(AppActivity.this));
        initRecyclerView();
    }

    @OnClick(R.id.custom_toolbar_back)
    public void back(){
        super.onBackPressed();
    }

    private void initChildAccount(){
        pb.setVisibility(View.VISIBLE);

    }

    private void initRecyclerView() {
        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid() + "/Alistar/installedApps"); // TODO: Change hardcode approach
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    appsNameList.add(snapshot.getKey());
                    Log.d(TAG, "appList Inside: "+appsNameList);
                }
                adapter = new AppAdapter(AppActivity.this, appsNameList);
                app_recview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "appList: "+appsNameList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}