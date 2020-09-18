package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PickRoleActivity extends AppCompatActivity {

    @BindView(R.id.ParentsButton) CardView parents;
    @BindView(R.id.KidsButton) CardView kids;
    @BindView(R.id.PickRoleBackButton) ImageButton backbtn;
    @BindView(R.id.progress_bar) RelativeLayout progressbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sp;
    private String m_FCMtoken;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_role);

        m_FCMtoken = FirebaseInstanceId.getInstance().getToken();

        sp = getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);
        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.ParentsButton)
    public void parent()
    {
        key = "parent";
        progressbar.setVisibility(View.VISIBLE);
        updateUserInfo(firebaseAuth.getCurrentUser(),  "parents", key);
    }

    @OnClick(R.id.KidsButton)
    public void kid()
    {
        key = "child";
        progressbar.setVisibility(View.VISIBLE);
        updateUserInfo(firebaseAuth.getCurrentUser(), "child", key);
        Intent kid = new Intent(this, ChildDetailsActivity.class);
        startActivity(kid);
        finish();
    }

    @OnClick(R.id.PickRoleBackButton)
    public void back(){
        FirebaseAuth.getInstance().signOut();
        Intent back = new Intent(this, LoginActivity.class);
        startActivity(back);
        finish();
    }

    // Parents
    private void saveUserInfo(String type, String key){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userType", type);
        editor.apply();
        Log.d("TOKEN", "FCM Example Token: " + m_FCMtoken);

        if (key.equals("parent")) {
            try {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("parentToken", m_FCMtoken);

                db.collection("UserInfo")
                        .document(firebaseAuth.getCurrentUser().getUid())
                        .set(userMap, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressbar.setVisibility(View.INVISIBLE);
                                Intent parent = new Intent(PickRoleActivity.this, ParentActivity.class);
                                startActivity(parent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressbar.setVisibility(View.INVISIBLE);
                                Toast.makeText(PickRoleActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(PickRoleActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserInfo(final FirebaseUser currentUser, String type, String key){
        try {
            if (currentUser != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .build();

                currentUser.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveUserInfo(type, key);
                                }
                            }
                        });
            }
        }
        catch (Exception e){
            Toast.makeText(PickRoleActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}