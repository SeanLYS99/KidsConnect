package com.example.assignment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.NotificationDetailActivity;
import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends FirestoreRecyclerAdapter<notificationModel, NotificationAdapter.noticeHolder> {
    private static final String TAG = "NotificationAdapter";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context context;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<notificationModel> options, Context context) {
        super(options);
        //this.noticeList = noticeList;
        this.context = context;
    }


    class noticeHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView content;
        TextView date;
        ImageView icon;
        ConstraintLayout noti_card;
        ImageView unread_icon;

        public noticeHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noti_title);
            content = itemView.findViewById(R.id.noti_content);
            date = itemView.findViewById(R.id.noti_time);
            icon = itemView.findViewById(R.id.noti_icon);
            noti_card = itemView.findViewById(R.id.noti_holder);
            unread_icon = itemView.findViewById(R.id.unread_icon);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationAdapter.noticeHolder holder, int position, @NonNull notificationModel model) {
        int leng = model.getDatetime().length(); // count number of digits of firestore datetime field
        String sent_date = model.getDatetime().substring(0, leng-5); // then separate out the date within the string
        int sent_time = Integer.parseInt(model.getDatetime().substring(leng-4, leng)); // separate out the time within the string as well

        Date calendar_date = Calendar.getInstance().getTime(); // get calendar
        SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy", Locale.getDefault()); // get current date in same format as firestore datetime field date
        SimpleDateFormat tf = new SimpleDateFormat("HHmm", Locale.getDefault()); // get current time in same format as firestore datetime field time
        String curr_date = df.format(calendar_date); // store current date into this string
        int curr_time = Integer.parseInt(tf.format(calendar_date)); // store current time into this integer
        int time_diff = Math.abs(curr_time - sent_time); // find the time diff between sent time and current time, and use absolute value to convert negative result to positive

        String temp_curr_time = Integer.toString(curr_time); // convert current time into string, because need to separate the first 2 digits, integer can't do that
        String temp_sent_time = Integer.toString(sent_time); // convert sent time into string, because (same)
        Log.d(TAG, "temp_curr_time: "+ temp_curr_time+" temp_sent_time: "+temp_sent_time);
        int count_curr_time = 4 - temp_curr_time.length();
        int count_sent_time = 4 - temp_sent_time.length();

        switch (count_curr_time){ // check curr time has how many digits, then add (4-number of digit) of zero
            case 1:
                temp_curr_time = "0" + temp_curr_time;
                break;
            case 2:
                temp_curr_time = "00" + temp_curr_time;
                break;
            case 3:
                temp_curr_time = "000" + temp_curr_time;
                break;
        }

        switch (count_sent_time){ // check sent time has how many digits, then add (4-number of digit) of zero
            case 1:
                temp_sent_time = "0" + temp_sent_time;
                break;
            case 2:
                temp_sent_time = "00" + temp_sent_time;
                break;
            case 3:
                temp_sent_time = "000" + temp_sent_time;
                break;
        }
        if(!(temp_sent_time.substring(0, 2).equals(temp_curr_time.substring(0, 2)))){ // if the hour number is not same, final time difference value minus 40, because minutes is 60-based, but integer number is 100-based
            time_diff = time_diff - 40;
        }


        String display_time = Integer.toString(sent_time); // convert sent time to string, used to display on screen(final output)
        Log.d(TAG, "display time: "+display_time);
        Log.d(TAG, "time diff: "+time_diff);
        int count = 4 - display_time.length(); // if final output is having less than 4 digits, we have to add 0 infront. Because integer can't starts with 0, but 24-hour based time can have 0 infront like 0024, meaning 12:24am
        switch (count){ // check the final output has how many digits, then add (4-number of digit) of zero
            case 1:
                display_time = "0" + display_time;
                break;
            case 2:
                display_time = "00" + display_time;
                break;
            case 3:
                display_time = "000" + display_time;
                break;
        }

        if(sent_date.equals(curr_date)){ // check if the sent date is same as the current date, if yes meaning that the notification is being sent on that day
            if(time_diff <= 1){ // then check the time difference, if it is being sent from 1 minute or earlier, display "Just now" instead of the sent time
                holder.date.setText("Just now");
            }
            else if(time_diff < 60){ // if less than one hour, display how many minutes ago
                holder.date.setText(time_diff + " minutes ago");
            }
            else if(time_diff >= 60 && time_diff < 100) { // more than 1 hour, less than 2hours, display "1 hour ago"
                holder.date.setText("1 hour ago");
            }
            else { // 2hours or longer will display sent time
                holder.date.setText(display_time.substring(0, 2) + ":" + display_time.substring(2, 4));
            }
        }
        else{ // if the notification is being sent on different day
            display_time = sent_date; // assign sent date to final output
            holder.date.setText(display_time); // and display the date instead
        }
        //String count_digits = display_time.replaceAll("\\D", "");
        /*else if(time_diff <= 1){
            holder.date.setText("Just now");
        }
        else if(time_diff < 60){ // less than one hour
            holder.date.setText(time_diff + " minutes ago");
        }
        else if(time_diff >= 60 && time_diff < 120) { // more than 1 hour, less than 2hours
            holder.date.setText("1 hour ago");
        }
        else{
            display_time = display_time.substring(0, 2) + ":" + display_time.substring(2, 4); // display_time = 16:55
            //holder.date.setText(display_time);
        }*/

        /*long sent_time = Long.parseLong(model.getTime()); // 2

        Date calendar_date = Calendar.getInstance().getTime();
        SimpleDateFormat tf = new SimpleDateFormat("HHmm", Locale.getDefault());
        long curr_time = Long.parseLong(tf.format(calendar_date)); // 34
        long time_diff = curr_time - sent_time; // 34 - 2

        String display_time = Long.toString(sent_time); // "2"
        int count = 4- display_time.length(); // 3
        if (count == 1){
            display_time = "0" + display_time;
        }
        else if(count == 2){
            display_time = "00" + display_time;
        }
        else if(count == 3){
            display_time = "000" + display_time;
        }
        Log.d(TAG, "display time = "+display_time);

        display_time = display_time.substring(0, 2) + ":" + display_time.substring(2, 4);
        String count_digits = display_time.replaceAll("\\D", "");
        if(count_digits == 3) {
            display_time = display_time.substring(0, 1) + ":" + display_time.substring(1, count_digits);
        }
        else{
            display_time = display_time.substring(0, 2) + ":" + display_time.substring(2, 4);
        }

        if(Math.abs(time_diff) <= 3){
            holder.date.setText("Just now");
        }
        else if (Math.abs(time_diff) <= 10){
            holder.date.setText(Math.abs(time_diff) + " minutes ago");
        }
        else{
            holder.date.setText(display_time);
        }*/

        switch (model.getTitle()){ // read firestore notification's title, and display different icons according to the title
            case "SOS!":
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.notification_danger));
                break;
        }

        holder.title.setText(model.getTitle()); // display title
        holder.content.setText(Html.fromHtml(model.getContent())); // display content

        // if the notification has been clicked before, hide the unread icon, indicating the notification has been opened
        if(!model.getIsClicked().equals("false")){
            holder.unread_icon.setVisibility(View.GONE);
        }
        holder.noti_card.setOnClickListener(new View.OnClickListener() { // when user click on the notification body
            @Override
            public void onClick(View v) {
                holder.unread_icon.setVisibility(View.GONE); // hide unread_icon
                DocumentReference doc = db.collection("UserInfo").document(firebaseAuth.getUid()).collection("notification").document(model.getId());
                doc.update("isClicked", "true") // update firestore notification isClicked value to "true"
                        .addOnSuccessListener(Void -> Log.d(TAG, "onSuccess: isClicked updated"))
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
                Intent go = new Intent(context, NotificationDetailActivity.class);
                go.putExtra("name", model.getName());
                go.putExtra("datetime", model.getDatetime());
                go.putExtra("title", model.getTitle());
                go.putExtra("content", model.getContent());
                context.startActivity(go);
                //((Activity) context).finish();
            }
        });
    }

    @NonNull
    @Override
    public NotificationAdapter.noticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_notification, parent, false);
        return new NotificationAdapter.noticeHolder(view);
    }

    /*@Override
    public int getItemCount() {
        return noticeList.size(); // Where mDataSet is the list of your items
    }*/
}
