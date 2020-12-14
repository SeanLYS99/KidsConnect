package com.example.assignment.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    String token;
    private final String TAG = "AppAdapter";
    private List<String> appList = new ArrayList<>();
    private List<String> packageNameList = new ArrayList<>();
    private Context context;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();


    public AppAdapter(@NonNull Context context, List<String> appList) {
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_app, parent, false);
        return new AppAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.appname.setText(appList.get(position));
        holder.app_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) { // from uncheck to check
                    DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: "+dataSnapshot.child("token").getValue());
//                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                token = snapshot.child("token").getValue().toString();
//                                int i = 0;
//                                i +=1;
//                                Log.d(TAG, "eye: "+i);
////                            Utils.StructureJSON("Block Apps", appList.get(position), token, context);
//                            }
                            token = dataSnapshot.child("token").getValue().toString();
                            DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar/installedApps");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("FamiSafe").getValue();
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        packageNameList.add(snapshot.child("packageName").getValue().toString());
                                    }
                                    Log.d(TAG, "AppAdapter: "+packageNameList+", "+token);
                                    if(isChecked) {
                                        Utils.StructureJSON("Block Apps", packageNameList.get(position), token, context);
                                    }
                                    else {
                                        Utils.StructureJSON("Unblock Apps", packageNameList.get(position), token, context);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView appname;
        Switch app_switch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appname = itemView.findViewById(R.id.app_title);
            app_switch = itemView.findViewById(R.id.app_switch);
        }
    }

}
