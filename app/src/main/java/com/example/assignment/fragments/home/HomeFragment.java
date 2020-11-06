package com.example.assignment.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment.R;

public class HomeFragment extends Fragment {
    //Button signoutbtn;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Log.e("hi", "hi");

        /*signoutbtn = (Button) root.findViewById(R.id.SignOutButton);
        signoutbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View root){
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent signout = new Intent(this, LoginActivity.class);

            }
        });*/
        return root;
    }
}