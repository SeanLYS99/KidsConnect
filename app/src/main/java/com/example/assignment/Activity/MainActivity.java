package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.assignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;
    SharedPreferences sp;
    SharedPreferences sp_child;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        sp = getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);
        sp_child = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                clearUserType();

            }
        },SPLASH_TIME_OUT);

        // FullScreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void clearUserType(){
        String userType = sp.getString("userType", "null");
        boolean child = sp_child.getBoolean("hasChild", false);
        /*SharedPreferences.Editor editor = sp_child.edit();
        editor.putBoolean("hasChild", false);
        editor.apply();*/
        //Log.e("hi:::", userType);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Log.e("urrent user", user.toString());
        /*if(user != null){
            //progressBar.setVisibility(View.VISIBLE);
            Log.e("user null: ", "no");
            //updateUI();
            String uid = firebaseAuth.getCurrentUser().getUid();
            Log.e("UID: ",uid);
            DocumentReference docRef = db.collection("UserInfo").document(uid); //ZJkUy7J5UzTok7ioADAYqk77Opp1
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            String userType = document.getString("userType");
                            String childStatus = document.getString("child");
                            Log.e("LoginActivity",userType);
                            if (userType.contains("parents")){
                                Log.e("contain parents", "yes");
                                //progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getApplicationContext(), ParentActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (childStatus.contains("TRUE")){
                                //progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    else{
                        Log.e("task unsuccessful:", " yes");
                    }
                }
            });
        }*/
        Log.d("TAG", "UserType: "+userType);
        Log.d("TAGa", "user: "+user);
        if (user != null && userType.contains("parents")) // make sure it is parent logging in
        {
            Intent intent = new Intent(MainActivity.this, ParentActivity.class);
            startActivity(intent);
            finish();
        }
        else if (user != null && userType.contains("child")){

            Intent intent = new Intent(MainActivity.this, ChildActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent homeIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }
}
