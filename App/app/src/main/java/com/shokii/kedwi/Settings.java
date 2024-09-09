package com.shokii.kedwi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shokii.kedwi.databinding.FragmentSettingsBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Settings extends Fragment {
    FragmentSettingsBinding SettingsBinding;

    public Settings() {
        super (R.layout.fragment_user_page);
    }

    FirebaseAuth mAuth;
    FirebaseDatabase dataBase;
    DatabaseReference userRefs;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance();
        userRefs = dataBase.getReference("users");

        userRefs.child(mAuth.getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SettingsBinding.changeUserStatusDescription.setText(snapshot.child("status").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SettingsBinding = FragmentSettingsBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingsBinding.backBtn.setOnClickListener(this::back);
        SettingsBinding.out.setOnClickListener(this::logOut);

        SettingsBinding.changeUserName.setOnClickListener(this::changeUserName);
        SettingsBinding.changeUserStatus.setOnClickListener(this::changeUserStatus);
        SettingsBinding.changeUserPassword.setOnClickListener(this::changeUserPassword);

        SettingsBinding.changeUserImage.setOnClickListener(this::changeUserImage);

        return SettingsBinding.getRoot();
    }

    private void changeUserImage(View view) {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(360, 360)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start((Activity) getContext());
    }



    private void changeUserPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Введите новый пароль");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.getCurrentUser().updatePassword(input.getText().toString());
                userRefs.child(mAuth.getUid()).child("password").setValue(input.getText().toString());
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void changeUserStatus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Введите новый статус");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userRefs.child(mAuth.getUid().toString()).child("status").setValue(input.getText().toString());
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void changeUserName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Введите новое имя пользователя");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userRefs.child(mAuth.getUid().toString()).child("name").setValue(input.getText().toString());
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void back(View view) {
        UserPage userPageFragment = new UserPage();
        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, userPageFragment).commit();
    }
    private void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), Launch.class));
    }
}