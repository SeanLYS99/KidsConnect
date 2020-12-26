package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.assignment.R;
import com.example.assignment.Service.BackgroundManager;

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        //BackgroundManager.getInstance().init(this).startService();
    }

    @Override
    public void onBackPressed() {
        // close the foreground application, and show home screen
        Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomescreen);
//        System.exit(0);
//        finish();
//        super.onBackPressed();
    }
}