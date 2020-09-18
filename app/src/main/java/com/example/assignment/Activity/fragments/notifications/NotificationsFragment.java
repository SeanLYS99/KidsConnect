package com.example.assignment.Activity.fragments.notifications;

import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends Fragment {

    @BindView(R.id.msgList) CardView list;
    @BindView(R.id.notice_childName) TextView child;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.edit_profile);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View root = localInflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, root);
        setupMsg();

        return root;
    }

    private void setupMsg(){
        try {
            DocumentReference docRef = db.collection("UserInfo").document(firebaseAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.contains("sos")) {
                        Log.e("contain","yes");
                        child.setText(document.getString("childName"));
                        list.setVisibility(View.VISIBLE);
                    } else {

                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}