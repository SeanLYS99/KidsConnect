package com.example.assignment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.AddGeofenceActivity;
import com.example.assignment.Model.geofencingModel;
import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class GeofencingAdapter extends FirestoreRecyclerAdapter<geofencingModel, GeofencingAdapter.geofencingHolder> {

    private List<geofencingModel> geofenceList = new ArrayList<>();
    private Context context;
    private int status;

    public GeofencingAdapter(@NonNull FirestoreRecyclerOptions<geofencingModel> options, Context context, int status) {
        super(options);
        this.context = context;
        this.status = status;
    }

    @Override
    protected void onBindViewHolder(@NonNull GeofencingAdapter.geofencingHolder holder, int position, @NonNull geofencingModel model) {
        holder.name.setText(model.getName());
        holder.card.setOnClickListener(v -> {
            if(status == 1) {
                Intent intent = new Intent(context, AddGeofenceActivity.class);
                intent.putExtra("UniqueID", "fromList");
                intent.putExtra("name", model.getName());
                intent.putExtra("address", model.getAddress());
                intent.putExtra("radius", model.getRadius());
                intent.putExtra("documentId", model.getId());
                context.startActivity(intent);
                //((Activity) context).finish();
            }
        });
        holder.btn.setOnClickListener(v -> {
            if(status == 0){
                Toast.makeText(context, "Geofence deleted...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    class geofencingHolder extends RecyclerView.ViewHolder{
        ConstraintLayout card;
        TextView name;
        ImageView btn;

        public geofencingHolder(@NonNull View view){
            super(view);
            card = view.findViewById(R.id.geofence_card);
            name = view.findViewById(R.id.geofence_name);
            btn = view.findViewById(R.id.geofence_nextbtn);
        }
    }
    @NonNull
    @Override
    public GeofencingAdapter.geofencingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_geofence, parent, false);
        return new GeofencingAdapter.geofencingHolder(view);
    }
}
