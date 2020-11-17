package com.example.assignment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.Activity.ChildActivity;
import com.example.assignment.Model.accountModel;
import com.example.assignment.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends FirebaseRecyclerAdapter<accountModel, AccountAdapter.accountHolder> {

    private Context context;
    private String cname;
    private List<accountModel> accountList = new ArrayList<>();

    /*private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ChildActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
            Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
        }
    }*/
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AccountAdapter(@NonNull FirebaseRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull accountHolder holder, int position, @NonNull accountModel accountModel) {
        holder.name.setText(accountModel.getName());
        cname = accountModel.getName();
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp;
                sp = context.getSharedPreferences("com.example.assignment.child", Context.MODE_PRIVATE);
                Intent intent = new Intent(context, ChildActivity.class);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", accountModel.getName());
                editor.apply();
                context.startActivity(intent);
                ((Activity)context).finish();
                Toast.makeText(context, accountModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, accountModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    class accountHolder extends RecyclerView.ViewHolder{ // implements View.OnClickListener
        TextView name;
        final AccountAdapter adapter;

        public accountHolder(@NonNull View itemView, AccountAdapter adapter) {
            super(itemView);
            name = itemView.findViewById(R.id.childAccountName);
            this.adapter = adapter;
        }


        /*@Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            if(accountList != null && accountList.size() > 0) {
                accountModel model = accountList.get(position);
                Toast.makeText(context, model.getName(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }*/
    }

    @NonNull
    @Override
    public AccountAdapter.accountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_childaccount, parent, false);
        //view.setOnClickListener(mOnClickListener);
        return new AccountAdapter.accountHolder(view, this);
    }


    /*@Override
    public int getItemCount() {
        return accountList.size();
    }*/
}
