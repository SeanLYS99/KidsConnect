package com.example.assignment.Service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.assignment.Activity.CodeActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.Activity.ResetPasswordActivity;

import static com.google.firebase.messaging.Constants.TAG;

public class AppService extends Service {

    private String message = "";
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String packageName = manager.getRunningAppProcesses().get(0).processName;
        Log.d(TAG, "packageName: "+packageName);
        Log.d(TAG, "message: "+message);
        if (packageName.toLowerCase().contains("com.example.assignment")) {
            Intent i = new Intent(getBaseContext(), CodeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Intent", "AppService");
            startActivity(i);
        }
        //if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                    String packageName = manager.getRunningAppProcesses().get(0).processName;
//                    Log.d(TAG, "packageName: "+packageName);
//                    Log.d(TAG, "message: "+message);
//                    if (packageName.toLowerCase().contains("com.example.assignment")) {
//                        Intent i = new Intent(getBaseContext(), ParentActivity.class);
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(i);
//                    }
//                    handler.postDelayed(this, 1000);
//                }
//            };
//            handler.post(runnable);
        //}
    }

    public BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}