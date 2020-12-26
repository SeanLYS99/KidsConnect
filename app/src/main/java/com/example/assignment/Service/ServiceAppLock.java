package com.example.assignment.Service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.assignment.ReceiverApplock;

public class ServiceAppLock extends IntentService {

    public ServiceAppLock() {
        super("ServiceAppLock");
    }

    private void runApplock(){
            Intent intent = new Intent(this, ReceiverApplock.class);
            sendBroadcast(intent);
            //wait(endtime-System.currentTimeMillis());

//        long endtime = System.currentTimeMillis()+210;
//        while (System.currentTimeMillis() < endtime){
//            synchronized (this){
//                try {
//                    Intent intent = new Intent(this, ReceiverApplock.class);
//                    sendBroadcast(intent);
//                    wait(endtime-System.currentTimeMillis());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        runApplock();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        BackgroundManager.getInstance().init(this).startAlarmManager();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BackgroundManager.getInstance().init(this).startAlarmManager();
    }
}
