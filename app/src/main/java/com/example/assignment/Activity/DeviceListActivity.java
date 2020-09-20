package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.DeviceListAdapter;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.example.assignment.deviceModel;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListActivity extends AppCompatActivity {

    //private static DocumentReference db;
    long num;
    String childName;

    //@BindView(R.id.childDeviceText) TextView device;
    //@BindView(R.id.deviceList) CardView list;
    @BindView(R.id.noDeviceText) ConstraintLayout empty_icon;
    @BindView(R.id.progressbar_device) ConstraintLayout pb_icon;
    //@BindView(R.id.removebtn) TextView remove;
    public static ConstraintLayout pb;
    public static ConstraintLayout empty;
    @BindView(R.id.deviceListRecyclerView) RecyclerView list;
    //@BindView(R.id.childDeviceText) TextView deviceText;
    private DeviceListAdapter adapter;
    //private FirebaseRecyclerAdapter<deviceModel, myDeviceListHolder> adapter;
    static SharedPreferences sp;

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    public List<deviceModel> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        pb = findViewById(R.id.progressbar_device);
        empty = findViewById(R.id.noDeviceText);
        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        ButterKnife.bind(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        //fillDeviceList();
        setupDevice();
    }

    /*@OnClick(R.id.removebtn)
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
    }*/

    public static void deleteRealtimeDatabase(Context context){
        try {
            DatabaseReference ref = (DatabaseReference) FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child(sp.getString("name", null)).orderByValue().equalTo(sp.getString("name", null));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ref.removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.e("refer", ref.toString());
            //ref.removeValue();
        }
        catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteFirestore(Context context){
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
                            context,
                            "Success!",
                            "Child's device has been disconnected.",
                            "OK",
                            ParentActivity.class);
                }
            });
        }
        catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteSharedPreferences(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasChild", false);
        pb.setVisibility(View.INVISIBLE);
        empty.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imageButton)
    public void back(){
        Intent back = new Intent(DeviceListActivity.this, ParentActivity.class);
        startActivity(back);
        finish();
    }

    /*private void fillDeviceList(){
        deviceList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        Log.e("childksjdhfkd", ref.toString());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                deviceList.add(new deviceModel(childName));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                deviceList.add(new deviceModel(childName));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/


    private void setupDevice(){
        pb.setVisibility(View.VISIBLE);
        /*DocumentReference docRef = db.collection("UserInfo").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(doc.contains("childName")){
                    //name = doc.getString("childName");
                }
            }
        });

        list = (RecyclerView)findViewById(R.id.deviceListRecyclerView);
        list.setLayoutManager(new LinearLayoutManager(this));*/
        FirebaseRecyclerOptions<deviceModel> options = new FirebaseRecyclerOptions.Builder<deviceModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference(currentUser.getUid()), deviceModel.class)
                .build();
        Log.e("option", options.toString());
        adapter = new DeviceListAdapter(options, DeviceListActivity.this);

        /*adapter = new FirebaseRecyclerAdapter<deviceModel, myDeviceListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myDeviceListHolder holder, int position, @NonNull deviceModel model) {
                Log.e("viewh", "SDF");
                holder.name.setText(model.getName() + "'s device");
            }

            @NonNull
            @Override
            public myDeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicelist_recycler_view, parent, false);
                return new myDeviceListHolder(view);
            }
        };*/
        list.setAdapter(adapter);
        pb.setVisibility(View.INVISIBLE);
        /*if (adapter.getItemCount() == 0) {
            empty.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.INVISIBLE);
        }
        else{
            empty.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.INVISIBLE);
        }*/
    }

    /*private class myDeviceListHolder extends RecyclerView.ViewHolder{

        private View view;
        TextView name;
        public myDeviceListHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.childDeviceText);
        }
    }*/

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