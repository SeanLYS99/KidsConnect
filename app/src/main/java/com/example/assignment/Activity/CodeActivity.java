package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.wynsbin.vciv.VerificationCodeInputView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CodeActivity extends AppCompatActivity {

    @BindView(R.id.vciv_code) VerificationCodeInputView code_input;
    @BindView(R.id.createPIN_input) VerificationCodeInputView createPIN_input;
    @BindView(R.id.invalidPIN) TextView invalidPIN;
    @BindView(R.id.code_actionbar) Toolbar actionbar;
    @BindView(R.id.back_btn) ImageView back;
    @BindView(R.id.actionbar_title) TextView title;
    @BindView(R.id.enterPIN_layout) ConstraintLayout enterPIN;
    @BindView(R.id.createPIN_layout) ConstraintLayout createPIN;
    @BindView(R.id.createPIN_error) TextView createPIN_error;
    @BindView(R.id.progressbar_pin) ConstraintLayout progressBar;

    private final String TAG = "CodeActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    SharedPreferences sp_userType;
    private String temp_pin = "1";
    private String intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        ButterKnife.bind(this);
        setSupportActionBar(actionbar);

        title.setText("Enter PIN");
        back.setVisibility(View.VISIBLE);
        intent = getIntent().getStringExtra("Intent");
        sp_userType = getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);

        // check which layout to show
        if(intent.equals("PickRoleActivity")){ // haven't created PIN
            title.setText("Create PIN");
            createPIN.setVisibility(View.VISIBLE);
            createPIN_input.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
                @Override
                public void onComplete(String code) {
                    temp_pin = code;
                    hideKeyboard();
                }
                @Override
                public void onInput() {

                }
            });
        }
        else if(intent.equals("ChildActivity")){
            enterPIN.setVisibility(View.VISIBLE);
            showKeyboard(code_input);
            code_input.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
                @Override
                public void onComplete(String code) {
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                String pin = snapshot.getString("pin");
                                if(code.equals(pin)){
                                    SharedPreferences.Editor editor = sp_userType.edit();
                                    editor.putString("userType", "-");
                                    editor.apply();

                                    FirebaseAuth.getInstance().signOut();
                                    progressBar.setVisibility(View.GONE);
                                    code_input.clearCode();
                                    Intent signOut = new Intent(CodeActivity.this, LoginActivity.class);
                                    startActivity(signOut);
                                    finish();
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    code_input.clearCode();
                                    invalidPIN.setText("Invalid PIN!");
                                    invalidPIN.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CodeActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                @Override
                public void onInput() {

                }
            });
        }
        else if(intent.equals("MainActivity")){
            enterPIN.setVisibility(View.VISIBLE);
            showKeyboard(code_input);
            code_input.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
                @Override
                public void onComplete(String code) {
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                String pin = snapshot.getString("pin");
                                if(code.equals(pin)){
                                    Intent go_main = new Intent(CodeActivity.this, ParentActivity.class);
                                    startActivity(go_main);
                                    finish();
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    code_input.clearCode();
                                    invalidPIN.setText("Invalid PIN!");
                                    invalidPIN.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CodeActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                @Override
                public void onInput() {

                }
            });
        }
        else if(intent.equals("AppService")){
            code_input.setVisibility(View.VISIBLE);
            code_input.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
                @Override
                public void onComplete(String code) {
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                String pin = snapshot.getString("pin");
                                if (code.equals(pin)) {
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    code_input.clearCode();
                                    invalidPIN.setText("Invalid PIN!");
                                    invalidPIN.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CodeActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onInput() {

                }
            });
        }
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void showKeyboard(View view){
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isShowing = imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            if (!isShowing)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }


    }

    @OnClick(R.id.createPIN_createBtn)
    public void setCreatePIN(){
        Log.d(TAG, "code length: "+temp_pin.length());
        if(temp_pin.length() == 4) { // make sure user has entered 4-digit PIN
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> pinMap = new HashMap<>();
            pinMap.put("pin", temp_pin);
            DocumentReference ref = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
            ref.set(pinMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CodeActivity.this, "PIN set successfully!", Toast.LENGTH_SHORT).show();
                        Intent parent = new Intent(CodeActivity.this, ParentActivity.class);
                        startActivity(parent);
                        finish();
                    }
                }
            });
        }
        else{
            createPIN_error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        switch (getIntent().getStringExtra("Intent")){
            case "PickRoleActivity":
                Intent intent = new Intent(CodeActivity.this, PickRoleActivity.class);
                startActivity(intent);
                finish();
                break;
            case "ChildActivity":
                Intent child = new Intent(CodeActivity.this, ChildActivity.class);
                startActivity(child);
                finish();
                break;
            case "MainActivity":
                finish();
                break;
            case "AppService":
                finish();
                break;
        }
    }

    @OnClick(R.id.back_btn)
    public void back()
    {
        switch (getIntent().getStringExtra("Intent")){
            case "PickRoleActivity":
                Intent intent = new Intent(CodeActivity.this, PickRoleActivity.class);
                startActivity(intent);
                finish();
                break;
            case "ChildActivity":
                Intent child = new Intent(CodeActivity.this, ChildActivity.class);
                startActivity(child);
                finish();
                break;
            case "MainActivity":
                finish();
                break;
            case "AppService":
                finish();
                break;
        }
    }
}