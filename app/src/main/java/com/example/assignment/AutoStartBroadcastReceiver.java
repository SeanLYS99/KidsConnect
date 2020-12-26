package com.example.assignment;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.assignment.Activity.LockActivity;
import com.example.assignment.Service.BackgroundManager;
import com.google.firebase.messaging.Constants;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() { // repeat the checking task every 2 seconds
                    public void run(){
                        Log.d(TAG, "AutoStartBroadCast");
                        Utils utils = new Utils(context);
                        String appRunning = utils.getLauncherTopApp(); // get the current foreground app
                        Log.d(Constants.TAG, "autostart: "+appRunning);
                        if(utils.isLock(appRunning)){ // check database, if the app is locked, display LockActivity
                            ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                            mActivityManager.killBackgroundProcesses(appRunning);
                            Intent i = new Intent(context, LockActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(i);
                            handler.removeCallbacksAndMessages(null);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000, 2000);

    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run(){
                        //your code is here
                    }
                });
            }
        };
        timer.schedule(timerTask, 5000, 5000);
    }
}
