package com.example.assignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.assignment.Service.BackgroundManager;

import static android.content.ContentValues.TAG;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AutoStartBroadCast");
        BackgroundManager.getInstance().init(context).startService();
    }
}
