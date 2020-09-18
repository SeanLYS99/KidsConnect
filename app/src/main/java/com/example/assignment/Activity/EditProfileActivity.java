package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.textInputLayout_email) TextInputLayout emailLayout;
    @BindView(R.id.textInputLayout_pw) TextInputLayout passwordLayout;
    @BindView(R.id.textInputLayout_username) TextInputLayout usernameLayout;
    @BindView(R.id.textInputLayout_phone) TextInputLayout phoneLayout;
    @BindView(R.id.edit_phone) TextView phone_icon;
    @BindView(R.id.edit_username) TextView username_icon;
    @BindView(R.id.edit_pw) TextView password_icon;
    @BindView(R.id.EditUsernameText) TextView username;
    @BindView(R.id.EditPasswordText) TextView password;
    @BindView(R.id.EditEmailText) TextView email;
    @BindView(R.id.EditPhoneText) TextView phone;
    @BindView(R.id.pb_editprofile) ConstraintLayout progressbar;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String key = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);
        loadData();
    }

    @OnClick(R.id.edit_username)
    public void username(){
        username.setEnabled(true);
        username.requestFocus();
    }

    @OnClick(R.id.edit_pw)
    public void password(){
        Intent intent = new Intent(EditProfileActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.edit_phone)
    public void phone(){
        phone.setEnabled(true);
        phone.requestFocus();
    }

    @OnClick(R.id.backButton)
    public void back(){
        Intent intent = new Intent(EditProfileActivity.this, ParentActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.SaveChangesButton)
    public void save(){
        String name = username.getText().toString();
        String phone_no = phone.getText().toString();

        try {
            if (!(name.equals(firebaseAuth.getCurrentUser().getDisplayName()))) {
                progressbar.setVisibility(View.VISIBLE);
                updateUserProfile(name, firebaseAuth.getCurrentUser(), "username", key);
            }
            DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
            doc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!phone_no.equals("") && !phone_no.equals(document.getString("phone"))) {
                        progressbar.setVisibility(View.VISIBLE);
                        key = "1";
                        updateUserProfile(phone_no, firebaseAuth.getCurrentUser(), "phone", key);
                    }
                    else {

                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData(){
        progressbar.setVisibility(View.VISIBLE);
        username.setText(firebaseAuth.getCurrentUser().getDisplayName());
        email.setText(firebaseAuth.getCurrentUser().getEmail());

        try {
            DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
            doc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.get("phone") != null) {
                        progressbar.setVisibility(View.INVISIBLE);
                        phone.setText(document.getString("phone"));
                    } else {
                        progressbar.setVisibility(View.INVISIBLE);
                        phone.setHint("eg. 012-34567890");
                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo(String uid, String name, String updateType){
        try {
            db.collection("UserInfo")
                    .document(uid).update(updateType, name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressbar.setVisibility(View.INVISIBLE);
                            email.setEnabled(false);
                            phone.setEnabled(false);
                            Utils.SuccessSweetDialog(EditProfileActivity.this, "Success!", "Your personal details have been updated.", "OK", EditProfileActivity.class);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar.setVisibility(View.INVISIBLE);
                            //Toast.makeText(SignUpActivity.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile(String name, FirebaseUser currentUser, String updateType, String key)
    {
        if (key.equals("0")){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            currentUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                saveUserInfo(currentUser.getUid(), name, updateType);

                            }
                        }
                    });
        }
        else{
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .build();
            currentUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                saveUserInfo(currentUser.getUid(), name, updateType);
                            }
                        }
                    });
        }
    }
}