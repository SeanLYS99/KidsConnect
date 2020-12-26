package com.example.assignment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.LoginActivity;
import com.example.assignment.Model.deviceModel;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeviceListAdapter extends FirebaseRecyclerAdapter<deviceModel, DeviceListAdapter.myDeviceListHolder> {

    private static final String TAG = "DeviceListAdapter";
    private Context context;
    String token;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    public DeviceListAdapter(@NonNull FirebaseRecyclerOptions<deviceModel> options, Context context) {
        super(options);
        this.context = context;
    }

    class myDeviceListHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView remove;

        public myDeviceListHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.childDeviceText);
            remove = itemView.findViewById(R.id.removebtn);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull myDeviceListHolder viewHolder, int position, @NonNull deviceModel deviceModel) {
        viewHolder.name.setText(deviceModel.getName() + "'s device");
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utils.WarningSweetDialog(context, "Are you sure?", "This action cannot be undone. All the data about this device will be cleared. Are you sure to disconnect the device?", "Confirm", DeviceListActivity.pb, );
                try {
                    SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    dialog.setTitleText("Are you sure?");
                    dialog.setContentText("This action cannot be undone. All the data about this device will be cleared. Are you sure to disconnect the device?");
                    dialog.setConfirmText("Confirm");
                    dialog.setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        DeviceListActivity.pb.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onClick: "+deviceModel.getName());
                        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               Log.d(TAG, "onMessageReceived: hi");
                               token = dataSnapshot.child(deviceModel.getName()).child("token").getValue().toString();
                               Utils.StructureJSON("remove child", "none", token, context);
                               DeviceListActivity.deleteRealtimeDatabase(context, deviceModel.getName());
                           }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    });
                    dialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    });
                    dialog.show();
                }
                catch (Exception e){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public DeviceListAdapter.myDeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_device, parent, false);
        return new DeviceListAdapter.myDeviceListHolder(view);
    };
}
