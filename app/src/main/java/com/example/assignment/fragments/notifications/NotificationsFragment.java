package com.example.assignment.fragments.notifications;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.ParentActivity;
import com.example.assignment.Adapter.NotificationAdapter;
import com.example.assignment.EmptyRecyclerView;
import com.example.assignment.Model.deviceModel;
import com.example.assignment.Model.notificationModel;
import com.example.assignment.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class NotificationsFragment extends Fragment {

    @BindView(R.id.noticeRecyclerView)
    EmptyRecyclerView list;
    @BindView(R.id.emptyNoticeDisplay) ConstraintLayout empty;
    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout refresh;
    /*@BindView(R.id.noticeList) CardView list;
    @BindView(R.id.notice_date) TextView date;
    @BindView(R.id.notice_title) TextView title;
    @BindView(R.id.notice_content) TextView content;*/
    ArrayList<notificationModel> model;
    private NotificationAdapter adapter;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    List<notificationModel> noticeList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.edit_profile);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View root = localInflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, root);

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        //list.setEmptyView(empty);
        setupMsg();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    transaction.setReorderingAllowed(false);
                }
                getFragmentManager().beginTransaction().detach(NotificationsFragment.this).attach(NotificationsFragment.this).commit(); // recreate fragment
                refresh.setRefreshing(false);
            }
        });

        return root;
    }


    private void setupMsg(){
        try {
            Query query = db.collection("UserInfo").document(currentUser.getUid()).collection("notification").orderBy("datetime", Query.Direction.DESCENDING);
            //CollectionReference doc = db.collection("UserInfo").document(currentUser.getUid()).collection("notification");
            FirestoreRecyclerOptions<notificationModel> options = new FirestoreRecyclerOptions.Builder<notificationModel>()
                    .setQuery(query, notificationModel.class)
                    .build();
            adapter = new NotificationAdapter(options, getActivity());
            list.setEmptyView(empty);
            list.setAdapter(adapter);
            Log.d(TAG, "setupMsg: yes");

            /*doc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty())
                    {
                        empty.setVisibility(View.GONE);
                        for(QueryDocumentSnapshot notice : queryDocumentSnapshots){
                            notificationModel model = notice.toObject(notificationModel.class);
                            noticeList.clear();
                            adapter.notifyDataSetChanged();
                            noticeList.add(model);
                            adapter.notifyItemRangeInserted(0, noticeList.size());
                            System.out.println(noticeList.size());
                        }
                    }
                    else{
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });*/

            /*doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if(!task.getResult().isEmpty())
                        {
                            empty.setVisibility(View.GONE);
                        }
                        else{
                            empty.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });*/


        }
        catch (Exception e)
        {
            Log.d(TAG, "setupMsg: "+e.getMessage());
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }

    }
}