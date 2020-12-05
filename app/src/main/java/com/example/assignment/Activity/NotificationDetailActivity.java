package com.example.assignment.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.TextureView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assignment.R;
import com.example.assignment.fragments.notifications.NotificationsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationDetailActivity extends AppCompatActivity {

    @BindView(R.id.notice_name) TextView name;
    @BindView(R.id.notice_datetime) TextView date;
    @BindView(R.id.custom_toolbar_title) TextView title;
    @BindView(R.id.notice_content) TextView content;

    private final Fragment fragment = new NotificationsFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment selectedFragment;

    TextView toolbar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        ButterKnife.bind(this);
        //fragmentManager.beginTransaction().add(R.id.nav_host_fragment_container, fragment).commit();
        toolbar_title = title;
        toolbar_title.setText("Notifications");
        String datetime = getIntent().getStringExtra("datetime");
        int leng = datetime.length();
        String sent_date = datetime.substring(0, leng-5);
        String temp_sent_time = datetime.substring(leng-4, leng);
        String sent_time = temp_sent_time.substring(0, 2) + ":" + temp_sent_time.substring(2, 4);

        date.setText(sent_date + " at " + sent_time);
        content.setText(Html.fromHtml("<b>["+getIntent().getStringExtra("title")+"]</b> "+ getIntent().getStringExtra("content")));
        name.setText(getIntent().getStringExtra("name"));
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(NotificationDetailActivity.this, ParentActivity.class);
        startActivity(intent);
        finish();
    }*/

    @OnClick(R.id.custom_toolbar_back)
    public void back(){
        super.onBackPressed();
        /*Intent back = new Intent(NotificationDetailActivity.this, ParentActivity.class);
        startActivity(back);
        finish();*/
    }
}