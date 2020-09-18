package com.example.assignment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListAdapter extends FirebaseRecyclerAdapter<deviceModel, DeviceListAdapter.myDeviceListHolder> {

    public DeviceListAdapter(@NonNull FirebaseRecyclerOptions<deviceModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myDeviceListHolder viewHolder, int i, @NonNull deviceModel deviceModel) {
        Log.e("model", deviceModel.getName());
        viewHolder.deviceText.setText(deviceModel.getName() + "'s device");
    }

    @NonNull
    @Override
    public myDeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicelist_recycler_view, parent, false);
        return new myDeviceListHolder(view);
    };

    class myDeviceListHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.childDeviceText) TextView deviceText;

        public myDeviceListHolder(@NonNull View itemView) {
            super(itemView);
            Log.e("im", "here");

            ButterKnife.bind(this, itemView);
        }
    }

}
