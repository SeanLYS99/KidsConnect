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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.AddGeofenceActivity;
import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.GeofenceActivity;
import com.example.assignment.Model.geofencingModel;
import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GeofencingAdapter extends FirestoreRecyclerAdapter<geofencingModel, GeofencingAdapter.geofencingHolder> {

    private Context context;
    private int status;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    List<String> token_list = new ArrayList<>();

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
        /*holder.arrow_right_btn.setOnClickListener(v -> {
            if(status == 0){
                Toast.makeText(context, "Geofence deleted...", Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utils.WarningSweetDialog(context, "Are you sure?", "This action cannot be undone. Are you sure to remove this geofence?", "Confirm", GeofenceActivity.pb, "Geofence has been removed.", model.getName());
                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("Are you sure?");
                dialog.setContentText("This action cannot be undone. Are you sure to remove this geofence from the map?");
                dialog.setConfirmText("Confirm");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    token_list.add(snapshot.child("token").getValue().toString());
                                    Log.d("GeofencingAdapter", "tokenlist: "+token_list);
                                    //Utils.StructureJSON("Remove Geofence", model.getId(), token_list);
                                }
                                for(int i=0; i<token_list.size(); i++){
                                    Log.d("GeofencingAdapter", "Geofence ID: "+model.getId());
                                    Utils.StructureJSON("Remove Geofence", model.getId(), token_list.get(i), context);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        /*DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getUid());
                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult() != null){
                                    String token = task.getResult().getString("parentToken");
                                    Utils.StructureJSON("Remove Geofence", model.getId(), token, context);
                                }
                            }
                        });*/

                        //DeviceListActivity.deleteSharedPreferences();
                    }

                });
                dialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
                dialog.show();
            }
        });
    }


    class geofencingHolder extends RecyclerView.ViewHolder{
        CardView card;
        TextView name;
        ImageView delete_btn;

        public geofencingHolder(@NonNull View view){
            super(view);
            card = view.findViewById(R.id.geofence_card);
            name = view.findViewById(R.id.geofence_name);
            delete_btn = view.findViewById(R.id.geofence_deletebtn);
            //delete_btn = view.findViewById(R.id.geofence_deletebtn);
        }
    }
    @NonNull
    @Override
    public GeofencingAdapter.geofencingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_geofence, parent, false);
        return new GeofencingAdapter.geofencingHolder(view);
    }
}
