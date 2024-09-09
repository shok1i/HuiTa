package com.shokii.kedwi;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shokii.kedwi.databinding.FragmentUserPageBinding;

import java.util.Objects;


public class UserPage extends Fragment {
    FragmentUserPageBinding UserPageBinding;

    FirebaseAuth mAuth;
    FirebaseDatabase dataBase;
    DatabaseReference userRefs;

    FirebaseStorage storage;
    StorageReference storageRef;

    public UserPage() {
        super (R.layout.fragment_user_page);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPageBinding = FragmentUserPageBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance();
        userRefs = dataBase.getReference("users");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("user_image").child(mAuth.getUid());

        userRefs.child(mAuth.getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserPageBinding.userName.setText(snapshot.child("name").getValue().toString());
                UserPageBinding.userText.setText(snapshot.child("status").getValue().toString());

                DataSnapshot games = snapshot.child("game statistic");
                UserPageBinding.userGameGoingNUM.setText(Objects.requireNonNull(games.child("passing").getChildrenCount()).toString());
                UserPageBinding.userGamePlanNUM.setText(Objects.requireNonNull(games.child("planned").getChildrenCount()).toString());
                UserPageBinding.userGamePassNUM.setText(Objects.requireNonNull(games.child("pass").getChildrenCount()).toString());
                UserPageBinding.userGamePostponedNUM.setText(Objects.requireNonNull(games.child("postponed").getChildrenCount()).toString());
                UserPageBinding.userGameAbandonedNUM.setText(Objects.requireNonNull(games.child("abandoned").getChildrenCount()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .into(UserPageBinding.userImage);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        UserPageBinding.btn.setOnClickListener(this::startSettingFragment);



        return UserPageBinding.getRoot();
    }

    private void startSettingFragment(View view) {
        Settings settingsFragment = new Settings();
        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, settingsFragment).commit();
    }
}