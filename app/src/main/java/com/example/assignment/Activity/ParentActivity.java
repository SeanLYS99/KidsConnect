package com.example.assignment.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.fragments.MapsFragment;
import com.example.assignment.fragments.ProfileFragment;
import com.example.assignment.fragments.dashboard.FeaturesFragment;
import com.example.assignment.fragments.home.HomeFragment;
import com.example.assignment.fragments.notifications.NotificationsFragment;
import com.example.assignment.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParentActivity extends AppCompatActivity {

    private static final String TAG = "ParentActivity";
    //?android:attr/windowBackground
    String a = "1";
    //@BindView(R.id.progress_bar_parent) FrameLayout pb;
    @BindView(R.id.bottomNavView) BottomNavigationView navView;
    @BindView(R.id.parent_toolbar) Toolbar toolbar;
    enum ENUM {HOME, FEATURES, NOTIFICATIONS, PROFILE};
    ENUM temp;
    TextView toolbar_title;
    private Fragment f1;
    private final Fragment f2 = new FeaturesFragment();
    private final Fragment f3 = new NotificationsFragment();
    private final Fragment f4 = new ProfileFragment();
    private final Fragment f5 = new HomeFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment selectedFragment;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sp;

    // double tap exit
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    private String getColoredSpanned(String text, String color){
        String input = "<b><font color=" + color + ">" + text + "</font></b>";
        return input;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
        //this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //exitToast.cancel();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit App", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        ButterKnife.bind(this);
        sp = getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userType", "parents");
        editor.apply();
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));*/

        // Transparent Status Bar
        //View decorView = getWindow().getDecorView();
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setSupportActionBar(toolbar);
        // Remove default toolbar title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //ActionBar bar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#ffffff"));

        // Set BackgroundDrawable
        /*** HERE*** /bar.setBackgroundDrawable(colorDrawable);
         /*** HERE*** /bar.setTitle(Html.fromHtml(getColoredSpanned("Kids", "#000000") + getColoredSpanned("Connect", "#000000")));


        /*actionBar.setIcon(R.drawable.ic_baseline_person_24);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);*/
        //actionBar.setTitle(Html.fromHtml(title+title_2));
        // Transparent Status Bar
        //View decorView = getWindow().getDecorView();
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Log.e("String a: ", a);
        if (a == "1")
        {
            f1 = new MapsFragment();
            selectedFragment = f1;
        }
        else{
            Log.e("g", "g");
            f1 = new HomeFragment();
            selectedFragment = f1;
        }


        /*DatabaseReference key = FirebaseDatabase.getInstance().getReference();
        key.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("sdf", "fsd");
                if (!dataSnapshot.child(firebaseAuth.getUid()).exists())
                {
                    f1 = new MapsFragment();
                }
                else{
                    Log.e("fal", "f");
                    f1 = new HomeFragment();
                }
                //selectedFragment = f1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        if(getIntent().getStringExtra("fragmentNo") != null){
            fragmentManager.beginTransaction().replace(R.id.fragNavHost, f3).commit();
        }
        //setupView();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //fragmentManager.beginTransaction().add(R.id.fragNavHost, f5).hide(f5).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f4).hide(f4).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f3).hide(f3).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f2).hide(f2).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f1).commit();
        //fragmentManager.beginTransaction().add(R.id.fragNavHost, f1).commit();

        /*FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        BottomNavigationView navView = findViewById(R.id.bottomNavView);
        NavHostFragment navHostFragment =  (NavHostFragment) supportFragmentManager.findFragmentById(R.id.fragNavHost);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);*/
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        //NavigationUI.setupWithNavController(navView, navController);

    }

    /*private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            toolbar_title.setText(level + "%");
            Log.d(TAG, "onReceive: yes");

            DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getUid());
            Map<String, Object> batMap = new HashMap<>();
            batMap.put("battery", level);
            ref.set(batMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment previousFragment = selectedFragment;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        previousFragment = selectedFragment;
                        selectedFragment = f1;
                        temp = ENUM.HOME;
                        toolbar_title.setText("KidsConnect");
                        //ActionBar actionBar = getSupportActionBar();
                        //actionBar.setTitle(Html.fromHtml(getColoredSpanned("Kids", "#000000") + getColoredSpanned("Connect", "#000000")));
                        break;
                    /*case R.id.navigation_home:
                        if (a == "1") {
                            previousFragment = selectedFragment;
                            selectedFragment = f1;
                            break;
                        }
                        else{
                            previousFragment = selectedFragment;
                            selectedFragment = f5;
                            break;
                        }*/
                    case R.id.navigation_dashboard:
                        previousFragment = selectedFragment;
                        selectedFragment = f2;
                        temp = ENUM.FEATURES;
                        toolbar_title.setText("Features");
                        //ActionBar action_bar = getSupportActionBar();
                        //action_bar.setTitle(Html.fromHtml(getColoredSpanned("Features", "#000000")));
                        break;
                    case R.id.navigation_notifications:
                        previousFragment = selectedFragment;
                        selectedFragment = f3;
                        temp = ENUM.NOTIFICATIONS;
                        toolbar_title.setText("Notifications");
                        //ActionBar action = getSupportActionBar();
                        //action.setTitle(Html.fromHtml(getColoredSpanned("<center>Notifications</center>", "#000000")));
                        break;
                    case R.id.navigation_profile:
                        previousFragment = selectedFragment;
                        selectedFragment = f4;
                        temp = ENUM.PROFILE;
                        toolbar_title.setText("Profile");
                        //ActionBar acbar = getSupportActionBar();
                        //acbar.setTitle(Html.fromHtml(getColoredSpanned("<b>Profile</b>", "#000000")));
                        break;

                }

                fragmentManager.beginTransaction().hide(previousFragment).show(selectedFragment).commit();
                return true;
            };

    void setupView(){
        FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        BottomNavigationView bottomNavView =  findViewById(R.id.bottomNavView);
        NavHostFragment navHostFragment =  (NavHostFragment) supportFragmentManager.findFragmentById(R.id.fragNavHost);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //fragmentManager.beginTransaction().show(f3).commit();
        Log.d(TAG, "onResume: "+temp);
    }
}