package com.example.assignment.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment.Activity.CodeActivity;
import com.example.assignment.Activity.DeviceListActivity;
import com.example.assignment.Activity.EditProfileActivity;
import com.example.assignment.Activity.GeofenceActivity;
import com.example.assignment.Activity.LoginActivity;
import com.example.assignment.R;
import com.example.assignment.fragments.notifications.NotificationsFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.grpc.InternalNotifyOnServerBuild;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    @BindView(R.id.SignOutButton) Button signoutbtn;
    @BindView(R.id.progress_bar_parent) FrameLayout progressbar;
    @BindView(R.id.profile_username) TextView username;
    @BindView(R.id.profile_email) TextView email;
    @BindView(R.id.edit_name) TextView edit_name;
    @BindView(R.id.edit_email) TextView edit_email;
    @BindView(R.id.edit_icon) ImageView edit_icon;
    @BindView(R.id.edit_phone) TextView edit_phone;
    @BindView(R.id.circularImageView) CircularImageView addImageView;
    @BindView(R.id.AddProfilePic) ImageButton addIcon;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    SharedPreferences sp;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri;
    private UploadTask uploadTask;
    private StorageReference mStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Transparent Status Bar
        //View decorView = getActivity().getWindow().getDecorView();
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        mStorageRef = FirebaseStorage.getInstance().getReference("users_photo");
        sp = this.getActivity().getSharedPreferences("com.example.assignment.userType", Context.MODE_PRIVATE);
        loadUser();

        refresh.setOnRefreshListener(() -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                transaction.setReorderingAllowed(false);
            }
            getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit(); // recreate fragment
            //loadUser();
            /*try {
                getFragmentManager().beginTransaction().(R.id.fragNavHost, ProfileFragment.class.newInstance()).commit();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }*/
            refresh.setRefreshing(false);
        });
        return root;
    }

    @OnClick(R.id.edit_icon)
    public void edit(){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
        //getActivity().finish();
    }

    @OnClick(R.id.SignOutButton)
    public void signOut(){
        try {
            progressbar.setVisibility(View.VISIBLE);
            //updateUserInfo(firebaseAuth.getCurrentUser(), "");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userType", "-");
            editor.apply();

            FirebaseAuth.getInstance().signOut();
            Intent signOut = new Intent(getActivity(), LoginActivity.class);
            startActivity(signOut);
            getActivity().finish();
            progressbar.setVisibility(View.INVISIBLE);
        }
        catch (Exception e){
            Log.d(TAG, "signOut: "+e.getMessage());
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.kidsDevice)
    public void device(){
        Intent device = new Intent(getActivity(), DeviceListActivity.class);
        startActivity(device);
        //getActivity().finish();
    }

    @OnClick(R.id.AddProfilePic)
    public void addImage(){
        pickFromGallery();

    }

    /*private void saveUserInfo(String userUID, String type){
        db.collection("UserInfo")
                .document(userUID).update("userType", type)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("success", type);
                        FirebaseAuth.getInstance().signOut();
                        Intent signOut = new Intent(getActivity(), LoginActivity.class);
                        startActivity(signOut);
                        getActivity().finish();
                        progressbar.setVisibility(View.INVISIBLE);
                        //Utils.SuccessSweetDialog(SignUpActivity.this, "Success!", "Your account has been created.", "OK");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fail", "failure");
                        progressbar.setVisibility(View.INVISIBLE);
                        //Toast.makeText(SignUpActivity.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    /*private void updateUserInfo(final FirebaseUser currentUser, String type){
        if(currentUser != null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .build();

            currentUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                saveUserInfo(currentUser.getUid(), type);
                            }
                        }
                    });
        }
        else{
            Log.e("current user", "null");
        }
    }*/

    private void loadUser(){
        try {
            username.setText(firebaseAuth.getCurrentUser().getDisplayName());
            email.setText(firebaseAuth.getCurrentUser().getEmail());
            edit_name.setText(firebaseAuth.getCurrentUser().getDisplayName());
            edit_email.setText(firebaseAuth.getCurrentUser().getEmail());

            DocumentReference docRef = db.collection("UserInfo").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.get("userPhotoUrl") != null) {
                        String url = document.get("userPhotoUrl").toString();
                        Picasso.get().load(url).into(addImageView);
                        /*Glide.with(getActivity())
                                .load(document.get("userPhotoUrl"))
                                .into(addImageView);*/
                        addIcon.setVisibility(View.GONE);
                    }
                    if (document.get("phone") != null) {
                        String phone = document.getString("phone");
                        edit_phone.setText(phone);
                    }
                }
            });
        }
        catch (Exception e){
            Log.d(TAG, "loadUser: "+e.getMessage());
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            addImageView.setImageURI(mImageUri);
            addIcon.setVisibility(View.GONE);
            updateUserInfo(firebaseAuth.getCurrentUser());
        }
    }

    private void saveUserInfo(String userUID, String url){
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", userUID);
        userMap.put("userPhotoUrl", url);

        db.collection("UserInfo")
                .document(userUID)
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void uploadImage(final FirebaseUser currentUser){
        if(mImageUri!= null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                String imageUrl = task.getResult().toString();
                                saveUserInfo(currentUser.getUid(), imageUrl);
                            }
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUserInfo(final FirebaseUser user){
        if(user != null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(mImageUri)
                    .build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                uploadImage(user);
                            }
                        }
                    });
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadUser();
        //getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
        Log.d(TAG, "onResume: ");
    }

    /*@Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }*/
}