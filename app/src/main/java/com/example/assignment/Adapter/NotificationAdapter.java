package com.example.assignment.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private List<notificationModel> noticeList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context context;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<notificationModel> options, Context context) {
        super(options);
        //this.noticeList = noticeList;
        this.context = context;
    }


    class noticeHolder extends RecyclerView.ViewHolder{
        private View view;
        TextView title;
        TextView content;
        TextView date;
        ImageView icon;

        public noticeHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            title = itemView.findViewById(R.id.noti_title);
            content = itemView.findViewById(R.id.noti_content);
            date = itemView.findViewById(R.id.noti_time);
            icon = itemView.findViewById(R.id.noti_icon);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationAdapter.noticeHolder holder, int position, @NonNull notificationModel model) {
        long sent_time = Long.parseLong(model.getTime()); // 2

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
        //String count_digits = display_time.replaceAll("\\D", "");
        /*if(count_digits == 3) {
            display_time = display_time.substring(0, 1) + ":" + display_time.substring(1, count_digits);
        }
        else{
            display_time = display_time.substring(0, 2) + ":" + display_time.substring(2, 4);
        }*/

        if(Math.abs(time_diff) <= 3){
            holder.date.setText("Just now");
        }
        else if (Math.abs(time_diff) <= 10){
            holder.date.setText(Math.abs(time_diff) + " minutes ago");
        }
        else{
            holder.date.setText(display_time);
        }

        switch (model.getTitle()){
            case "SOS!":
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.notification_danger));
                break;
        }
        holder.title.setText(model.getTitle());
        holder.content.setText(Html.fromHtml(model.getContent()));
        //holder.date.setText(model.getTime());
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
