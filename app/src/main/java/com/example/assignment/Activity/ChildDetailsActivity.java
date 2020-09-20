package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ChildDetailsActivity extends AppCompatActivity {

    @BindView(R.id.textInputLayout_ChildName) TextInputLayout childName;
    @BindView(R.id.textInputLayout_ChildAge) TextInputLayout childAge;
    @BindView(R.id.ChildNameText) EditText name;
    @BindView(R.id.ChildAgeText) EditText age;
    @BindView(R.id.NextButton) Button nextbtn;
    @BindView(R.id.cd_progress_bar)
    RelativeLayout pb;
    private SharedPreferences sp;
    public Map<String, Object> userMap = new HashMap<>();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);

        sp = getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);

        ButterKnife.bind(this);

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    // Hide keyboard after user clicking somewhere
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @OnClick(R.id.NextButton)
    public void next(){
        pb.setVisibility(View.VISIBLE);
        if (!Utils.hasUsername(name, childName) | !Utils.hasUsername(age, childAge)){
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        updateChildInfo(name.getText().toString(), age.getText().toString(), firebaseAuth.getCurrentUser());
    }

    @OnClick(R.id.ChildDetailsBackButton)
    public void back(){
        Intent back = new Intent(ChildDetailsActivity.this, PickRoleActivity.class);
        startActivity(back);
        finish();
    }

    @OnTextChanged(value = R.id.ChildNameText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void emailChanged(CharSequence s){
        if(!(s.length() < 1)){
            childName.setError(null);
        }
    }

    @OnTextChanged(value = R.id.ChildAgeText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void passwordChanged(CharSequence s){
        if(!(s.length() < 1)){
            childAge.setError(null);
        }
    }

    private void saveChildInfo(String userUID, String name, String age){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasChild", true);
        editor.putString("name", name);
        editor.apply();

        try {

            userMap.put("name", name);
            userMap.put("age", age);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid() + "/" + name);
            ref.updateChildren(userMap);
            pb.setVisibility(View.INVISIBLE);
            Intent child = new Intent(ChildDetailsActivity.this, ChildActivity.class);
            startActivity(child);
            finish();
            /*db.collection("UserInfo")
                    .document(userUID)
                    .set(userMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pb.setVisibility(View.INVISIBLE);
                            Intent child = new Intent(ChildDetailsActivity.this, ChildActivity.class);
                            startActivity(child);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(ChildDetailsActivity.this, "Failed to create kid account. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });*/
        }
        catch (Exception e){
            Toast.makeText(ChildDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Set Display Name Value
    private void updateChildInfo(final String nickname, final String age, final FirebaseUser currentUser){
        try {
            if (currentUser != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().build();
                //.setDisplayName(nickname)
                //.build();

                currentUser.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveChildInfo(currentUser.getUid(), nickname, age);
                                }
                            }
                        });
            }
        }
        catch (Exception e){
            Toast.makeText(ChildDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}