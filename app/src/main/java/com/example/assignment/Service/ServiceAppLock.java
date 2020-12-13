package com.example.assignment.Service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.assignment.ReceiverApplock;

public class ServiceAppLock extends IntentService {
     /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ServiceAppLock(String name) {
        super(name);
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

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        runApplock();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startAlarmManager();

        super.onDestroy();
    }
}
