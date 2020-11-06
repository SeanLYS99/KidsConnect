/*package com.example.assignment.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.assignment.Activity.fragments.MapsFragment;
import com.example.assignment.Activity.fragments.ProfileFragment;
import com.example.assignment.Activity.fragments.dashboard.DashboardFragment;
import com.example.assignment.Activity.fragments.home.HomeFragment;
import com.example.assignment.Activity.fragments.notifications.NotificationsFragment;
import com.example.assignment.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import butterknife.BindView;

public class ParentActivity extends AppCompatActivity {

    String a = "1";
    @BindView(R.id.progress_bar_parent) ConstraintLayout pb;
    private Fragment f1;
    //private final Fragment f2 = new DashboardFragment();
    private final Fragment f3 = new NotificationsFragment();
    private final Fragment f4 = new ProfileFragment();
    private final Fragment f5 = new HomeFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment selectedFragment;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

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

        /*DatabaseReference doc = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid());
        Log.e("doc", doc.toString());
        if (!doc.equals(firebaseAuth.getUid())){
            f1 = new HomeFragment();
        }
        else{
            f1 = new MapsFragment();
        }*/
        /*ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.child(firebaseAuth.getUid()).exists())
                {
                    f1 = new MapsFragment();
                }
                else{
                    f1 = new HomeFragment();
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };*/
        //DatabaseReference key = FirebaseDatabase.getInstance().getReference();
        //mPostReference.addValueEventListener(postListener);
        /*DatabaseReference key = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid()).getRef();
        FirebaseDatabase d = FirebaseDatabase.getInstance().getReference().getDatabase();
        Log.e("sdf", d.toString());
            if (d != null)
            {
                f1 = new MapsFragment();
            }
            else{
                Log.e("fal", "f");
                f1 = new HomeFragment();
            }
            selectedFragment = f1;*/

        /*key.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("sdf", "fsd");
                if (dataSnapshot.child(firebaseAuth.getUid()).exists())
                {
                    f1 = new MapsFragment();
                }
                else{
                    Log.e("fal", "f");
                    f1 = new HomeFragment();
                }
                selectedFragment = f1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //setupView();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //fragmentManager.beginTransaction().add(R.id.fragNavHost, f5).hide(f5).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f4).hide(f4).commit();
        fragmentManager.beginTransaction().add(R.id.fragNavHost, f3).hide(f3).commit();
        //fragmentManager.beginTransaction().add(R.id.fragNavHost, f2).hide(f2).commit();
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
        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        //NavigationUI.setupWithNavController(navView, navController);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment previousFragment = selectedFragment;
                FirebaseDatabase doc = FirebaseDatabase.getInstance();

                Log.e("skdaj", doc.toString());
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.e("g","g");
                        previousFragment = selectedFragment;
                        selectedFragment = f1;
                        break;

                    /*case R.id.navigation_dashboard:
                        previousFragment = selectedFragment;
                        selectedFragment = f2;
                        break;
                    case R.id.navigation_notifications:
                        previousFragment = selectedFragment;
                        selectedFragment = f3;
                        break;
                    case R.id.navigation_profile:
                        previousFragment = selectedFragment;
                        selectedFragment = f4;
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
    protected void onResumeFragments() {

    }
}*/

package com.example.assignment.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.assignment.fragments.MapsFragment;
import com.example.assignment.fragments.ProfileFragment;
import com.example.assignment.fragments.dashboard.DashboardFragment;
import com.example.assignment.fragments.home.HomeFragment;
import com.example.assignment.fragments.notifications.NotificationsFragment;
import com.example.assignment.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import butterknife.BindView;

public class ParentActivity extends AppCompatActivity {

    String a = "1";
    @BindView(R.id.progress_bar_parent)
    FrameLayout pb;
    private Fragment f1;
    private final Fragment f2 = new DashboardFragment();
    private final Fragment f3 = new NotificationsFragment();
    private final Fragment f4 = new ProfileFragment();
    private final Fragment f5 = new HomeFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment selectedFragment;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // double tap exit
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private Toast exitToast;

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
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            exitToast.cancel();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        exitToast.makeText(this, "Press again to exit App", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        ActionBar bar = getSupportActionBar();
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#ffffff"));

        // Set BackgroundDrawable
        bar.setBackgroundDrawable(colorDrawable);
        bar.setTitle(Html.fromHtml(getColoredSpanned("Kids", "#000000") + getColoredSpanned("Connect", "#000000")));
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment previousFragment = selectedFragment;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.e("g","g");
                        previousFragment = selectedFragment;
                        selectedFragment = f1;
                        ActionBar actionBar = getSupportActionBar();
                        actionBar.setTitle(Html.fromHtml(getColoredSpanned("Kids", "#000000") + getColoredSpanned("Connect", "#000000")));
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
                        ActionBar action_bar = getSupportActionBar();
                        action_bar.setTitle(Html.fromHtml(getColoredSpanned("Kids", "#000000") + getColoredSpanned("Connect", "#000000")));
                        break;
                    case R.id.navigation_notifications:
                        previousFragment = selectedFragment;
                        selectedFragment = f3;
                        ActionBar action = getSupportActionBar();
                        action.setTitle(Html.fromHtml(getColoredSpanned("<b>Notifications</b>", "#000000")));
                        break;
                    case R.id.navigation_profile:
                        previousFragment = selectedFragment;
                        selectedFragment = f4;
                        ActionBar acbar = getSupportActionBar();
                        acbar.setTitle(Html.fromHtml(getColoredSpanned("<b>Profile</b>", "#000000")));
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

}