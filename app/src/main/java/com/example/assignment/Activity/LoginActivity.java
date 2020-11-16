package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.LoginEmailText) EditText email;
    @BindView(R.id.LoginPasswordText) EditText password;
    @BindView(R.id.LoginButton) Button loginBtn;
    @BindView(R.id.SignUpButton) Button signupBtn;
    @BindView(R.id.textInputLayout_email) TextInputLayout emailLayout;
    @BindView(R.id.textInputLayout_password) TextInputLayout passwordLayout;
    @BindView(R.id.progress_bar) RelativeLayout progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);

        //FirebaseApp.initializeApp(this.getBaseContext());
        firebaseAuth = FirebaseAuth.getInstance();
        //clearUserType();
    }

    // Hide keyboard after user clicking somewhere
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @OnClick(R.id.LoginButton)
    public void login(){
        progressBar.setVisibility(View.VISIBLE);
        if (!Utils.isValidEmail(email, emailLayout) | !Utils.hasEmail(email, emailLayout) | !Utils.hasPassword(password, passwordLayout)){
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        startSignIn(email.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.SignUpButton)
    public void signUpScreen(){
        Intent signup = new Intent(this, SignUpActivity.class);
        startActivity(signup);
    }

    @OnTextChanged(value = R.id.LoginEmailText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void emailChanged(CharSequence s){
        if(!(s.length() < 1)){
            emailLayout.setError(null);
        }
    }

    @OnTextChanged(value = R.id.LoginPasswordText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void passwordChanged(CharSequence s){
        if(!(s.length() < 1)){
            passwordLayout.setError(null);
        }
    }

    private void startSignIn(String email, String password){
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        try {
                            if (task.isSuccessful()) {
                                updateUI();
                            } else {
                                String error = task.getException().getMessage();
                                if (error.contains("There is no user record corresponding to this identifier. The user may have been deleted.") ||
                                        error.contains("The password is invalid or the user does not have a password.")) {
                                    Utils.ErrorSweetDialog(LoginActivity.this, "Login Failed", "Incorrect email or pasword. Please try again.",
                                            "OK");
                                } else if (error.contains("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                    Utils.ErrorSweetDialog(LoginActivity.this, "No Internet Connection",
                                            "Please check your internet connection and try again.", "OK");
                                } else {
                                    Utils.ErrorSweetDialog(LoginActivity.this, "Login Failed", error, "OK");
                                }
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            Utils.ErrorSweetDialog(LoginActivity.this, "Oops! Something went wrong.",
                                    "Sorry for the inconvenience. Please try again later.", "OK");
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(){
        Intent intent = new Intent(this, PickRoleActivity.class);
        startActivity(intent);
        finish();
    }

    /*private void clearUserType(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Log.e("urrent user", user.toString());
        try {
            if (user != null) {
                progressBar.setVisibility(View.VISIBLE);
                Log.e("user null: ", "no");
                //updateUI();
                String uid = firebaseAuth.getCurrentUser().getUid();
                Log.e("UID: ", uid);
                DocumentReference docRef = db.collection("UserInfo").document(uid); //ZJkUy7J5UzTok7ioADAYqk77Opp1
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userType = document.getString("userType");
                                String childStatus = document.getString("child");
                                Log.e("LoginActivity", userType);
                                if (userType.contains("parents")) {
                                    Log.e("contain parents", "yes");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), ParentActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (childStatus.contains("TRUE")) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        } else {
                            Log.e("task unsuccessful:", " yes");
                        }
                    }
                });
            } else {
                Log.e("NULL: ", "No Such User.");
            }
        }
        catch (Exception e){
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }*/
}
