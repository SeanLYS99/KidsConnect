package com.example.assignment.Activity;

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
                    /*case R.id.navigation_dashboard:
                        previousFragment = selectedFragment;
                        selectedFragment = f2;
                        break;*/
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
}