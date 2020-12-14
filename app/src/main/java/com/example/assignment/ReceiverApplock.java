package com.example.assignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment.Activity.CodeActivity;
import com.example.assignment.Activity.LockActivity;
import com.example.assignment.Service.AppService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.messaging.Constants.TAG;

public class ReceiverApplock extends BroadcastReceiver {

    // TODO: Read firebase database app, and inside for loop, match with appRunning string, if equals, open CodeActivity
//    private static List<String> appnameList = new ArrayList<>();
//    private static FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
//    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    private static int temp_isLock = 2;
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils utils = new Utils(context);
        String appRunning = utils.getLauncherTopApp();
        Log.d(TAG, "receiver app lock: "+appRunning);
        if(utils.isLock(appRunning)){
            Intent i = new Intent(context, LockActivity.class);
            intent.putExtra("Intent", "ReceiverApplock");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        }

        //if(appRunning.eq)

    }

//    private static void getPackageName(callBack callBack){ // get packageName from firebase
//        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar/installedApps"); // TODO: Change hardcode approach
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    appnameList.add(snapshot.child("packageName").getValue().toString());
//                }
//                callBack.onCallback(appnameList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public static void isLock(String packageName){
//        // read packageName from firebase
//        getPackageName(new callBack() {
//            @Override
//            public void onCallback(List<String> list) {
//                for(int i=0; i<list.size(); i++){
//                    if(packageName.toLowerCase().contains(list.get(i))){
//                        temp_isLock = 1;
//                        break;
//                    }
//                    else{
//                        temp_isLock = 0;
//                    }
//                }
//            }
//        });
//        Log.d(TAG, "isLock_temp: "+temp_isLock);;
//    }
//
//    private interface callBack{
//        void onCallback(List<String> list);
//    }
}
