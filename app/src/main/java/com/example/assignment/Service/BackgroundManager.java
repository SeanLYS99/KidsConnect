package com.example.assignment.Service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.assignment.Activity.ServiceAppLockIntent;
import com.example.assignment.AutoStartBroadcastReceiver;

import static com.google.firebase.messaging.Constants.TAG;

public class BackgroundManager {
    private static final int period = 15*10000; //15 mins
    private static final int ALARM_ID = 159874;
    private static BackgroundManager instance;
    private Context context;

    public static BackgroundManager getInstance(){
        if(instance == null){
            instance = new BackgroundManager();
        }
        return instance;
    }

    public BackgroundManager init(Context c){
        context = c;
        return this;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.d(TAG, "isMyServiceRunning: yes");
                return true;
            }
        }
        Log.d(TAG, "isMyServiceRunning: no");
        return false;
    }

    public void startService(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(!isMyServiceRunning(ServiceAppLockIntent.class)){
                Intent intent = new Intent(context, ServiceAppLockIntent.class);
                ServiceAppLockIntent.enqueueWork(context, intent);
            }
            else{
                if(!isMyServiceRunning(ServiceAppLock.class)){
                    context.startService(new Intent(context, ServiceAppLock.class));
                }
            }
        }
    }

    public void stopService(Class<?> serviceClass){
        if(!isMyServiceRunning(serviceClass)){
            context.stopService(new Intent(context, serviceClass));
        }
    }

    public void startAlarmManager(){
        Intent intent = new Intent(context, AutoStartBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+period, pendingIntent);
    }

    public void stopAlarmManager(){
        Intent intent = new Intent(context, AutoStartBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
