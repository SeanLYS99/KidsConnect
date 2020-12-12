package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.Adapter.DeviceListAdapter;
import com.example.assignment.EmptyRecyclerView;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.example.assignment.Model.deviceModel;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListActivity extends AppCompatActivity {

    public static ConstraintLayout pb;
    public static ConstraintLayout empty;
    private DeviceListAdapter adapter;
    static SharedPreferences sp;
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    @BindView(R.id.emptyDeviceDisplay) ConstraintLayout empty_icon;
    @BindView(R.id.progressbar_device) ConstraintLayout pb_icon;
    @BindView(R.id.device_list_toolbar) ConstraintLayout toolbar;
    @BindView(R.id.custom_toolbar_title) TextView title;
    @BindView(R.id.deviceListRecyclerView)
    EmptyRecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        pb = findViewById(R.id.progressbar_device);
        empty = findViewById(R.id.emptyDeviceDisplay);
        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        ButterKnife.bind(this);

        //toolbar_title = toolbar.findViewById(R.id.custom_toolbar_holder).findViewById(R.id.custom_toolbar_title);
        title.setText("Registered Devices");
        list.setLayoutManager(new LinearLayoutManager(this));

        setupDevice();
    }

    public static void deleteRealtimeDatabase(Context context, String name){
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child(name);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ref.removeValue();
                    pb.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.e("refer", ref.toString());
        }
        catch (Exception e){
            Log.d("DeviceListActivity", "deleteRealtimeDatabase: "+e.getMessage());
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteFirestore(Context context){
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
                            context,
                            "Success!",
                            "Child's device has been disconnected.",
                            "OK",
                            ParentActivity.class);
                }
            });
        }
        catch (Exception e){
            Log.d("DeviceListActivity", "deleteFirestore: "+e.getMessage());
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteSharedPreferences(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasChild", false);
        pb.setVisibility(View.INVISIBLE);
        empty.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.custom_toolbar_back)
    public void back(){
        super.onBackPressed();
        /*Intent back = new Intent(DeviceListActivity.this, ParentActivity.class);
        startActivity(back);
        finish();*/
    }

    /*@OnClick(R.id.devicelist_fab)
    public void add_Device(){
        Intent add = new Intent(this, ChildDetailsActivity.class);
        add.putExtra("fromDeviceList","yes");
        startActivity(add);
        //finish();
    }*/

    private void setupDevice(){
        pb.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        Log.e("qq", query.toString());
        FirebaseRecyclerOptions<deviceModel> options = new FirebaseRecyclerOptions.Builder<deviceModel>()
                .setQuery(query, deviceModel.class)
                .build();

        DocumentReference ab = db.collection("UserInfo").document();

        ab.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot a = task.getResult();
                    Log.d("TAG", "onComplete: "+a);
                }
            }
        });
        adapter = new DeviceListAdapter(options, DeviceListActivity.this);
        list.setEmptyView(empty_icon);
        list.setAdapter(adapter);
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }
}