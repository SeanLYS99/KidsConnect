package com.example.assignment.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends FirestoreRecyclerAdapter<notificationModel, NotificationAdapter.noticeHolder> {
    private List<notificationModel> noticeList = new ArrayList<>();
    private Context context;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<notificationModel> options, Context context) {
        super(options);
        //this.noticeList = noticeList;
        this.context = context;
    }


    class noticeHolder extends RecyclerView.ViewHolder{
        private View view;
        TextView content;
        TextView date;
        TextView name;

        public noticeHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            content = itemView.findViewById(R.id.notice_content);
            date = itemView.findViewById(R.id.notice_date);
            name = itemView.findViewById(R.id.notice_name);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationAdapter.noticeHolder holder, int position, @NonNull notificationModel model) {
        //model = noticeList.get(position);
        holder.name.setText(model.getName());
        holder.content.setText(Html.fromHtml("<font color='red'>[" + model.getTitle() + "]</font> " + model.getContent()));
        holder.date.setText(model.getDate() + ", " + model.getTime());
    }

    @NonNull
    @Override
    public NotificationAdapter.noticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recycler_view, parent, false);
        return new NotificationAdapter.noticeHolder(view);
    }

    /*@Override
    public int getItemCount() {
        return noticeList.size(); // Where mDataSet is the list of your items
    }*/
}
