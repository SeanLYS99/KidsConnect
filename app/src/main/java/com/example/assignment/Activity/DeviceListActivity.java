package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment.Activity.fragments.ProfileFragment;
import com.example.assignment.DeviceListAdapter;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.example.assignment.deviceModel;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeviceListActivity extends AppCompatActivity {

    @BindView(R.id.childDeviceText) TextView device;
    @BindView(R.id.deviceList)
    CardView list;
    @BindView(R.id.noDeviceText) ConstraintLayout empty;
    @BindView(R.id.removebtn) TextView remove;
    RecyclerView recview;
    @BindView(R.id.progressbar_device)
    ConstraintLayout pb;
    //@BindView(R.id.deviceListRecyclerView) RecyclerView list;
    DeviceListAdapter adapter;
    SharedPreferences sp;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        ButterKnife.bind(this);
        setupDevice();
    }

    @OnClick(R.id.removebtn)
    public void remove(){
        try {
            new SweetAlertDialog(DeviceListActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("This action cannot be undone. All the data about this device will be cleared. Are you sure to disconnect the device?")
                    .setConfirmText("Confirm")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            pb.setVisibility(View.VISIBLE);
                            deleteRealtimeDatabase();
                            deleteSharedPreferences();
                            deleteFirestore();
                        }

                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        catch (Exception e){
            Toast.makeText(DeviceListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRealtimeDatabase(){
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
            ref.removeValue();
        }
        catch (Exception e){
            Toast.makeText(DeviceListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFirestore(){
        // Remove field from firestore
        try {
            DocumentReference docRef = db.collection("UserInfo").document(currentUser.getUid());

            // Remove the 'capital' field from the document
            Map<String, Object> updates = new HashMap<>();
            updates.put("childName", FieldValue.delete());
            updates.put("childAge", FieldValue.delete());

            docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pb.setVisibility(View.INVISIBLE);
                    Utils.SuccessSweetDialog(
                            DeviceListActivity.this,
                            "Success!",
                            "Child's device has been disconnected.",
                            "OK",
                            ParentActivity.class);
                }
            });
        }
        catch (Exception e){
            Toast.makeText(DeviceListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSharedPreferences(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasChild", false);
    }

    @OnClick(R.id.imageButton)
    public void back(){
        Intent back = new Intent(DeviceListActivity.this, ParentActivity.class);
        startActivity(back);
        finish();
    }

    private void setupDevice(){
        /*DocumentReference docRef = db.collection("UserInfo").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(doc.contains("childName")){
                    name = doc.getString("childName");
                }
            }
        });

        recview = (RecyclerView)findViewById(R.id.deviceListRecyclerView);
        recview.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<deviceModel> options = new FirebaseRecyclerOptions.Builder<deviceModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()), deviceModel.class)
                .build();

        Log.e("UIDdd",FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()).toString());

        adapter = new DeviceListAdapter(options);
        recview.setAdapter(adapter);
        Log.e("hi", "bij");

        RecyclerView recyclerView = list;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<deviceModel> options = new FirebaseRecyclerOptions.Builder<deviceModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()), deviceModel.class)
                .build();

        adapter = new DeviceListAdapter(options);
        list.setAdapter(adapter);*/

        pb.setVisibility(View.VISIBLE);
        try {
            DocumentReference docRef = db.collection("UserInfo").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.contains("childName")) {
                        empty.setVisibility(View.INVISIBLE);
                        pb.setVisibility(View.INVISIBLE);
                        list.setVisibility(View.VISIBLE);
                        device.setText(document.getString("childName") + "'s device");
                    } else {
                        pb.setVisibility(View.INVISIBLE);
                        list.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(DeviceListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.stopListening();
    }*/
}