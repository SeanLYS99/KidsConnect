package com.example.assignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.ParentActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeviceListAdapter extends FirebaseRecyclerAdapter<deviceModel, DeviceListAdapter.myDeviceListHolder> {

    private List<deviceModel> deviceList = new ArrayList<>();
    private Context context;

    public DeviceListAdapter(@NonNull FirebaseRecyclerOptions<deviceModel> options, Context context) {
        super(options);
        this.context = context;
    }

    class myDeviceListHolder extends RecyclerView.ViewHolder{
        private View view;
        TextView name;
        TextView remove;

        public myDeviceListHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.childDeviceText);
            remove = itemView.findViewById(R.id.removebtn);
        }
    }

    /*@Override
    public int getItemCount() {
        System.out.println(deviceList.size());
        return deviceList.size();
    }*/

    @Override
    public void onBindViewHolder(@NonNull myDeviceListHolder viewHolder, int position, @NonNull deviceModel deviceModel) {
        //deviceModel currentDevice = deviceList.get(position);
        //Log.e("model", deviceModel.getName());
        viewHolder.name.setText(deviceModel.getName() + "'s device");
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("This action cannot be undone. All the data about this device will be cleared. Are you sure to disconnect the device?")
                            .setConfirmText("Confirm")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    DeviceListActivity.pb.setVisibility(View.VISIBLE);
                                    DeviceListActivity.deleteRealtimeDatabase(context);
                                    DeviceListActivity.deleteSharedPreferences();
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
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @NonNull
    @Override
    public DeviceListAdapter.myDeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicelist_recycler_view, parent, false);
        return new DeviceListAdapter.myDeviceListHolder(view);
    };
}
