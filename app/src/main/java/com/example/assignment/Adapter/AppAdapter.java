package com.example.assignment.Adapter;//package com.example.assignment.Adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.CompoundButton;
//import android.widget.ListView;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.assignment.Model.appModel;
//import com.example.assignment.R;
//import com.example.assignment.Utils;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.paperdb.Paper;
//
////public class AppAdapter extends ArrayAdapter<appModel>{
////    public AppAdapter(@NonNull Context context, int resource) {
////        super(context, resource);
////    }
//
//public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
//    String token;
//    private final String TAG = "AppAdapter";
//    private List<String> appList = new ArrayList<>();
//    private List<String> packageNameList = new ArrayList<>();
//    private Context context;
//    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    private FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
//
//
//    public AppAdapter(@NonNull Context context, List<String> appList) {
//        this.context = context;
//        this.appList = appList;
//        Paper.init(context);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_app, parent, false);
//        return new AppAdapter.ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.appname.setText(appList.get(position));
//        if(Paper.book().read(appList.get(position)) != null){ // this app has been blocked
//            holder.app_switch.setChecked(true);
//        }
//
//        holder.app_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar");
//
//                ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "onDataChange: "+dataSnapshot.child("token").getValue());
////                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                                token = snapshot.child("token").getValue().toString();
////                                int i = 0;
////                                i +=1;
////                                Log.d(TAG, "eye: "+i);
//////                            Utils.StructureJSON("Block Apps", appList.get(position), token, context);
////                            }
//                        token = dataSnapshot.child("token").getValue().toString();
//                        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar/installedApps");
//                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                //dataSnapshot.child("FamiSafe").getValue();
//                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                                    packageNameList.add(snapshot.child("packageName").getValue().toString());
//                                }
//                                Log.d(TAG, "AppAdapter: "+packageNameList+", "+token);
//                                if(holder.app_switch.isChecked()) {
//                                    Utils.StructureJSON("Block Apps", packageNameList.get(position), token, context);
//                                    Paper.book().write(appList.get(position), appList.get(position));
//                                }
//                                else {
//                                    Utils.StructureJSON("Unblock Apps", packageNameList.get(position), token, context);
//                                    Paper.book().delete(appList.get(position));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return appList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        TextView appname;
//        Switch app_switch;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            appname = itemView.findViewById(R.id.app_title);
//            app_switch = itemView.findViewById(R.id.app_switch);
//        }
//    }
//
//}

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.example.assignment.Utils.firebaseAuth;
import static com.example.assignment.Utils.realtime_db;

public class AppAdapter extends ArrayAdapter<String>
{
    private Context context;
    private List<String> packageNameList = new ArrayList<>();
    String token, child_name;
    private int layout;
    private static final String TAG = "AppAdapter";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    public AppAdapter(@NonNull Context context, int resource, List<String> appList, String child_name) {
        super(context, resource, appList);
        this.context = context;
        this.child_name = child_name;
        layout = resource;
        Paper.init(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //ViewHolder mainHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.appname = convertView.findViewById(R.id.app_title);
        viewHolder.app_switch = convertView.findViewById(R.id.app_switch);
        viewHolder.appname.setText(getItem(position));
        if(Paper.book().read(getItem(position)) != null){ // this app has been blocked
            viewHolder.app_switch.setChecked(true);
        }
        viewHolder.app_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/"+child_name);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("token").getValue());
                        token = dataSnapshot.child("token").getValue().toString();
                        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid() + "/" + child_name + "/installedApps");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //dataSnapshot.child("FamiSafe").getValue();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    packageNameList.add(snapshot.child("packageName").getValue().toString());
                                }
                                Log.d(TAG, "AppAdapter: " + packageNameList + ", " + token);
                                if (viewHolder.app_switch.isChecked()) {
                                    Utils.StructureJSON("Block Apps", packageNameList.get(position), token, context);
                                    Paper.book().write(getItem(position), getItem(position));
                                } else {
                                    Utils.StructureJSON("Unblock Apps", packageNameList.get(position), token, context);
                                    Paper.book().delete(getItem(position));
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
        return convertView;
    }

    public class ViewHolder{
        TextView appname;
        Switch app_switch;
    }
}