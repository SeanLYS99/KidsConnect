package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.LoginActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.Activity.SignUpActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utils {

    public static void ErrorSweetDialog(Context activity, String title, String content, String confirm){
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(String.valueOf(title))
                .setContentText(String.valueOf(content))
                .setConfirmText(String.valueOf(confirm))
                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                .show();
    }

    public static void SuccessSweetDialog(Context activity, String title, String content, String confirm, final Class<? extends Activity> ActivityToOpen){
        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(String.valueOf(title))
                .setContentText(String.valueOf(content))
                .setConfirmText(String.valueOf(confirm))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent back_to_login = new Intent(sweetAlertDialog.getContext(), ActivityToOpen);
                        sweetAlertDialog.getContext().startActivity(back_to_login);
                        ((Activity) sweetAlertDialog.getContext()).finish();
                    }
                })
                .show();
    }

    public static void WarningSweetDialog(Context activity, String title, String content, String confirm, ConstraintLayout pb){
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(String.valueOf(title))
                .setContentText(String.valueOf(content))
                .setConfirmText(String.valueOf(confirm))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        pb.setVisibility(View.VISIBLE);
                        Utils.SuccessSweetDialog(activity, "Success!", "Child's device has been disconnected.", "OK", ParentActivity.class);
                    }

                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public static boolean emailType(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isEmpty(EditText editText){
        return  (editText.getText().length() == 0 || editText.getText().toString().trim().equals(""));
    }

    public static boolean hasEmail(EditText email, TextInputLayout emailLayout){
        if(isEmpty(email)){
            emailLayout.setError("Field can't be empty*");
            return false;
        }
        return true;
    }

    public static boolean hasPassword(EditText password, TextInputLayout passwordLayout){
        if(password.getText().length() == 0){
            passwordLayout.setError("Field can't be empty*");
            return false;
        }
        else if(password.getText().length() < 6){
            passwordLayout.setError("Password must be at least 6 characters*");
            return false;
        }
        return true;
    }

    public static boolean hasUsername(EditText username, TextInputLayout usernameLayout){
        if (username.getText().length() == 0){
            usernameLayout.setError("Field can't be empty*");
            return false;
        }
        return true;
    }

    public static boolean hasCPassword(EditText cpassword, TextInputLayout cpasswordLayout){
        if (cpassword.getText().length() == 0){
            cpasswordLayout.setError("Field can't be empty*");
            return false;
        }
        return true;
    }

    public static boolean passwordMatch(EditText password, EditText cpassword, TextInputLayout cpasswordLayout){
        if (!(password.getText().toString().equals(cpassword.getText().toString()))){
            cpasswordLayout.setError("Passwords do not match*");
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(EditText email, TextInputLayout emailLayout){
        if(!emailType(email.getText().toString())){
            emailLayout.setError("Invalid email: a@b.c*");
            return false;
        }
        return true;
    }
}
