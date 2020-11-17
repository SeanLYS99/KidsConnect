package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.SignUpUsernameLayout) TextInputLayout usernameLayout;
    @BindView(R.id.SignUpEmailLayout) TextInputLayout emailLayout;
    @BindView(R.id.SignUpPasswordLayout) TextInputLayout passwordLayout;
    @BindView(R.id.SignUpConfirmPasswordLayout) TextInputLayout cpasswordLayout;
    @BindView(R.id.SignUpUsernameText) EditText username;
    @BindView(R.id.SignUpEmailText) EditText email;
    @BindView(R.id.SignUpPasswordText) EditText password;
    @BindView(R.id.SignUpConfirmPasswordText) EditText cpassword;
    @BindView(R.id.SignUpButton2) Button signupBtn;
    @BindView(R.id.LoginButton2) Button loginBtn;
    @BindView(R.id.progress_bar) RelativeLayout progressbar;
    @BindView(R.id.SignUpBackButton) ImageButton backbtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Hide keyboard after user clicking somewhere
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @OnClick(R.id.SignUpButton2)
    public void signup(){
        progressbar.setVisibility(View.VISIBLE);
        if(!Utils.passwordMatch(password, cpassword, cpasswordLayout) | !Utils.isValidEmail(email, emailLayout) |
                !Utils.hasEmail(email, emailLayout) | !Utils.hasPassword(password, passwordLayout) |
                !Utils.hasUsername(username, usernameLayout) | !Utils.hasCPassword(cpassword, cpasswordLayout)){
            progressbar.setVisibility(View.INVISIBLE);
            return;
        }
        startSignUp(username.getText().toString(), email.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.SignUpBackButton)
    public void back(){
        Intent back = new Intent(this, LoginActivity.class);
        startActivity(back);
        finish();
    }

    @OnClick(R.id.LoginButton2)
    public void login(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    @OnTextChanged(value = R.id.SignUpUsernameText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void usernameChanged(CharSequence s){
        if(!(s.length() < 1)){
            usernameLayout.setError(null);
        }
    }

    @OnTextChanged(value = R.id.SignUpEmailText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void emailChanged(CharSequence s){
        if(!(s.length() < 1)){
            emailLayout.setError(null);
        }
    }

    @OnTextChanged(value = R.id.SignUpPasswordText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void passwordChanged(CharSequence s){
        if(!(s.length() < 1)){
            passwordLayout.setError(null);
        }
    }

    @OnTextChanged(value = R.id.SignUpConfirmPasswordText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void cpasswordChanged(CharSequence s){
        if(!(s.length() < 1)){
            cpasswordLayout.setError(null);
        }
    }

    private void startSignUp(String name, String email, String password){
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            updateUserInfo(name, firebaseAuth.getCurrentUser());
                        } else {
                            //Toast.makeText(SignUpActivity.this, "User sign up failed", Toast.LENGTH_SHORT).show();
                        /*FirebaseAuthException e = (FirebaseAuthException )task.getException();
                        Log.e("LoginActivity", "Failed Registration", e);*/
                            String error = task.getException().getMessage();
                            if (error.contains("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                Utils.ErrorSweetDialog(SignUpActivity.this, "No Internet Connection",
                                        "Please check your internet connection and try again.", "OK");
                            } else {
                                Utils.ErrorSweetDialog(SignUpActivity.this, "Registration Failed", error, "OK");
                            }
                            progressbar.setVisibility(View.INVISIBLE);
                        }

                    });
        }
        catch(Exception e){
            Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo(String userUID, String username){
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", userUID);
        userMap.put("username", username);

        try {
            db.collection("UserInfo")
                    .document(userUID)
                    .set(userMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressbar.setVisibility(View.INVISIBLE);
                            Utils.SuccessSweetDialog(SignUpActivity.this, "Success!", "Your account has been created.", "OK", LoginActivity.class);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo(final String name, final FirebaseUser currentUser){
        try {
            if (currentUser != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                currentUser.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveUserInfo(currentUser.getUid(), name);
                                }
                            }
                        });
            }
        }
        catch (Exception e){
            Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}