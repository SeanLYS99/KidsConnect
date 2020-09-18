package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.assignment.R;
import com.example.assignment.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.editPasswordEmailText) EditText emailText;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.backButton)
    public void back(){
        Intent intent = new Intent(ResetPasswordActivity.this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.ResetPasswordSubmitButton)
    public void submit(){
        String emailAddress = emailText.getText().toString();

        try {
            firebaseAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Utils.SuccessSweetDialog(
                                        ResetPasswordActivity.this,
                                        "Success!",
                                        "We just sent an email to " + emailAddress + " with a link to reset your password \n Please check your inbox to find instructions",
                                        "OK",
                                        EditProfileActivity.class);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
        }
        catch (Exception e)
        {
            Toast.makeText(ResetPasswordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}