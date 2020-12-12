package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.Activity.AddGeofenceActivity;
import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.LoginActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.Activity.SignUpActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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
                        if(ActivityToOpen != null) {
                            Intent back_to_login = new Intent(sweetAlertDialog.getContext(), ActivityToOpen);
                            sweetAlertDialog.getContext().startActivity(back_to_login);
                            ((Activity) sweetAlertDialog.getContext()).finish();
                        }
                        else{
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }
                })
                .show();
    }

    public static void WarningSweetDialog(Context activity, String title, String content, String confirm, ConstraintLayout pb, String success_msg, String name){ //Child's device has been disconnected.
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(String.valueOf(title))
                .setContentText(String.valueOf(content))
                .setConfirmText(String.valueOf(confirm))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        pb.setVisibility(View.VISIBLE);
                        Log.d("SuccessSweetDialog", "onClick: "+name);
                        sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pb.setVisibility(View.GONE);
                        //Utils.SuccessSweetDialog(activity, "Success!", success_msg, "OK", ParentActivity.class);
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

    public static boolean isValidPhoneNumber(EditText phone, TextInputLayout phoneLayout){
        if(phone.getText().toString().length() < 10 ||phone.getText().toString().length() > 11){
            phoneLayout.setError("Phone number must be 10 or 11 digits");
            return false;
        }
        return true;
    }

    public static String convertNameToAddress(String input, Context context){
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> location_object;
            location_object = geocoder.getFromLocationName(input, 1);
            Address location_details = location_object.get(0);
            return location_details.getAddressLine(0);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static LatLng convertNameToLatLng(Context context, String input, String errorMessage){
        LatLng latlng = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> address = geocoder.getFromLocationName(input, 1);

            for(Address loc : address){
                latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        }
        catch (Exception e) {
            if(!errorMessage.equals("")) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
            else{
                Log.d("[UtilsLatLng] status: ",  e.getMessage());
            }
        }
        return latlng;
    }

    public static String convertLatLngToAddress(Context context, double lat, double lng, String errorMessage){
        try {
            // Display Current Location
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> address;
            address = geocoder.getFromLocation(lat, lng, 1);
            return address.get(0).getAddressLine(0);
        }
        catch (Exception e)
        {
            if(!errorMessage.equals("")) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    public static void StructureJSON(String title, String message, String token, Context context){
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("message", message);

            notification.put("to", token);
            notification.put("data", notificationBody);
        }
        catch (JSONException e) {
            Log.d("StructureJSON", "Error: "+e.getMessage());
        }
        sendNotification(notification, context);
    }

    public static void sendNotification(JSONObject notification, Context context){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                response -> {
                    Log.d("Utils.SendNotification", "sendNotification response: "+response.toString());
                },
                error -> {
                    Log.d("Utils.SendNotification", "onErrorResponse: "+error.getMessage());
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    /*public static void showView(List<View> views){
        for (View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideView(List<View> views){
        for (View view : views){
            view.setVisibility(View.GONE);
        }
    }*/
}
