package com.example.assignment.Activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.assignment.ReceiverApplock;
import com.example.assignment.Service.BackgroundManager;
import com.example.assignment.Service.ServiceAppLock;

public class ServiceAppLockIntent extends JobIntentService {

    private static final int JOB_ID = 15462;

    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context, ServiceAppLockIntent.class, JOB_ID, work);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        runApplock();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startAlarmManager();

        super.onDestroy();
    }

    private void runApplock(){
        long endtime = System.currentTimeMillis()+210;
        while (System.currentTimeMillis() < endtime){
            synchronized (this){
                try {
                    Intent intent = new Intent(this, ReceiverApplock.class);
                    sendBroadcast(intent);
                    wait(endtime-System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
