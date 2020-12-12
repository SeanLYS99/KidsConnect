package com.example.assignment.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.assignment.Adapter.AccountAdapter;
import com.example.assignment.Model.accountModel;
import com.example.assignment.Model.deviceModel;
import com.example.assignment.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChildAccountActivity extends AppCompatActivity {

    @BindView(R.id.childAccountList) RecyclerView list;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_account);

        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        setupAccount();

        // Transparent Status Bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChildAccountActivity.this, PickRoleActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.extended_fab)
    public void new_device(){
        Intent intent = new Intent(this, ChildDetailsActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.ChildAccountBackButton)
    public void back(){
        Intent back = new Intent(this, PickRoleActivity.class);
        startActivity(back);
        finish();
    }

    private void setupAccount(){
        readData(new FirebaseCallBack() {
            @Override
            public void onCallBack(List<String> list) {
                Log.e("dAtA: ", list.toString());
            }
        });

    }

    private void collectAccounts(Map<String, Object> users, ArrayList<String> accountList){

        for(Map.Entry<String, Object> entry : users.entrySet()){
            Map singleUser = (Map) entry.getValue();
            accountList.add((String) singleUser.get("name"));
        }

        Log.e("DATA: ", accountList.toString());
    }

    private void readData(FirebaseCallBack firebaseCallBack){
        ArrayList<String> accountList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        FirebaseRecyclerOptions<accountModel> options = new FirebaseRecyclerOptions.Builder<accountModel>()
                .setQuery(query, accountModel.class)
                .build();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) { // To against asynchronous behaviour
                    Log.e("data", String.valueOf(dataSnapshot.getChildrenCount()));
                    collectAccounts((Map<String, Object>) dataSnapshot.getValue(), accountList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e("datanew", accountList.toString());
        adapter = new AccountAdapter(options, ChildAccountActivity.this);
        list.setAdapter(adapter);

    }

    private interface FirebaseCallBack{
        void onCallBack(List<String> list);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }
}