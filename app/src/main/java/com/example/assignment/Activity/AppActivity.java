package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.assignment.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppActivity extends AppCompatActivity {

    @BindView(R.id.custom_toolbar_title)
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        ButterKnife.bind(this);
        title.setText("Installed Apps");
    }

    @OnClick(R.id.custom_toolbar_back)
    public void back(){
        super.onBackPressed();
    }
}