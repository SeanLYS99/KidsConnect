package com.example.assignment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.assignment.Activity.AddGeofenceActivity;
import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.LoginActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.Activity.SignUpActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

import static com.google.firebase.messaging.Constants.TAG;

public class Utils {

    private String EXTRA_LAST_APP = "EXTRA_LAST_APP";
    private Context context;
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
    private static List<String> appnameList = new ArrayList<>();
    private static int temp_isLock = 2;

    public Utils(Context context){
        this.context = context;
        Paper.init(context);
    }

    public boolean isLock(String packageName){
//        if(packageName == ""){
//            return true;
//        }
        return Paper.book().read(packageName) != null;
    }

    public void lock(String pk){
        Paper.book().write(pk, pk);
    }

    public void unlock(String pk){
        Paper.book().delete(pk);
    }

    public void setLastApp(String pk){
        Paper.book().write(EXTRA_LAST_APP, pk);
    }

    public String getLastApp(){
        return Paper.book().read(EXTRA_LAST_APP);
    }

    public void clearLastApp(){
        Paper.book().delete(EXTRA_LAST_APP);
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public static boolean checkPermission(Context context){
//        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, P)
//    }

    UsageStatsManager usageStatsManager;
    public String getLauncherTopApp() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Log.d(TAG, "usageStatsManager: " + usageStatsManager);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //Log.d(TAG, "getLauncherTopApp: topAPP");
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(1);
            Log.d(TAG, "getLauncherTopApp: taskInfoList= " + taskInfoList);
            if (taskInfoList != null && !taskInfoList.isEmpty()) {
                return taskInfoList.get(0).topActivity.getPackageName();
            }
        }
        else {
//            long endtime = System.currentTimeMillis();
//            long begintime = endtime - 10000;
//            String result = "";
//            UsageEvents.Event event = new UsageEvents.Event();
//            UsageEvents usageEvents = usageStatsManager.queryEvents(begintime, endtime);
//
//            while (usageEvents.hasNextEvent()){ // if found new app opened
//                Log.d(TAG, "getLauncherTopApp: HAS NEXT EVENT");
//                usageEvents.getNextEvent(event);
//                if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND){
//                    Log.d(TAG, "getLauncherTopApp: MOVE_TO_FOREGROUND");
//                    result = event.getPackageName();
//                }
//            }
//            if(!TextUtils.isEmpty(result)){
//                return result;
//            }
//        }
//        return "";
            String currentApp = "NULL";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager usm = (UsageStatsManager) this.context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                }
            } else {
                ActivityManager am = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                currentApp = tasks.get(0).processName;
            }
            return currentApp;
        }
        return "";
    }

    /** Separator **/

//    private static void getPackageName(callBack callBack){ // get packageName from firebase
//        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar/installedApps"); // TODO: Change hardcode approach
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    appnameList.add(snapshot.child("packageName").getValue().toString());
//                }
//                callBack.onCallback(appnameList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public static void isLock(String packageName){
//        // read packageName from firebase
//        getPackageName(new callBack() {
//            @Override
//            public void onCallback(List<String> list) {
//                for(int i=0; i<list.size(); i++){
//                    Log.d(TAG, "isLock list: "+list.get(i));
//                    Log.d(TAG, "passed packageName: "+packageName);
//                    if(packageName.toLowerCase().contains(list.get(i))){
//                        temp_isLock = 1;
//                        break;
//                    }
//                    else{
//                        temp_isLock = 0;
//                    }
//                }
//                Log.d(TAG, "isLock_temp: "+temp_isLock);
//            }
//        });
//
//    }
//
//    private interface callBack{
//        void onCallback(List<String> list);
//    }


//    public static void isLock(String packageName){
//        // read true false
//        List<String> appnameList = new ArrayList<>();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
//        Log.d(TAG, "isLock: called");
//        DatabaseReference ref = realtime_db.getReference(firebaseAuth.getUid()+"/Alistar/installedApps");
//        Log.d(TAG, "isLock: called");
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    appnameList.add(snapshot.child("packageName").getValue().toString());
//                }
//                Log.d(TAG, "Utils: "+appnameList);
//                for(int i=0; i<appnameList.size();i++){
//                    if(packageName.toLowerCase().contains(appnameList.get(i))){
//                        temp = 1; // the app is locked
//                    }
//                    else{
//                        temp = 0;
//                    }
//                    callBack
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private interface callBack{
//        void onCallback(int value);
//    }


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
