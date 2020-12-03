package com.example.assignment.fragments.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment.Activity.GeofenceActivity;
import com.example.assignment.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeaturesFragment extends Fragment {

    /*private FeaturesViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(FeaturesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_features, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @OnClick(R.id.Geofence_CardView)
    public void open_geofence(){
        Intent geofence = new Intent(getActivity(), GeofenceActivity.class);
        startActivity(geofence);
        //getActivity().finish();
    }
}