package com.example.assignment.fragments.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment.Activity.AppActivity;
import com.example.assignment.Activity.GeofenceActivity;
import com.example.assignment.Activity.TrackingActivity;
import com.example.assignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeaturesFragment extends Fragment {

    private static final String TAG = "FeaturesFragment";
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<String> nameList = new ArrayList<>();
    AlertDialog alertDialog;
    /*private FeaturesViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(FeaturesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_features, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @OnClick(R.id.Geofence_CardView)
    public void open_geofence() {
        Intent geofence = new Intent(getActivity(), GeofenceActivity.class);
        startActivity(geofence);
        //getActivity().finish();
    }

    @OnClick(R.id.Tracking_CardView)
    public void startTracking() {
         Intent tracking = new Intent(getActivity(), TrackingActivity.class);
         startActivity(tracking);
//        String token;
//        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid());
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String token = snapshot.child("token").getValue().toString();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @OnClick(R.id.Applock_CardView)
    public void applock(){
        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(nameList.size() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        nameList.add(snapshot.getKey());
                    }
                }
                Log.d(TAG, "nameList: "+nameList);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, nameList);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Your Choice");
                builder.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        Log.d(TAG, "singlechoice: "+item);
                    }
                });
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        ListView lw = ((AlertDialog)dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        Log.d(TAG, "onClick: "+checkedItem);
                        Intent applock = new Intent(getActivity(), AppActivity.class);
                        applock.putExtra("child", checkedItem.toString());
                        startActivity(applock);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
