package com.shokii.kedwi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shokii.kedwi.databinding.FragmentEnterGenderBinding;



public class EnterGender extends Fragment {
    public EnterGender() {
        super (R.layout.fragment_enter_gender);
    }

    private FragmentEnterGenderBinding _binding;
    private FirebaseAuth _mAuth;
    private FirebaseDatabase _dataBase;
    private DatabaseReference _userRefs;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mAuth = FirebaseAuth.getInstance();
        _dataBase = FirebaseDatabase.getInstance();
        _userRefs = _dataBase.getReference("users");

        _binding = FragmentEnterGenderBinding.inflate(getLayoutInflater());


        _binding.maleBtn.setOnClickListener(this::setGender);
        _binding.femaleBtn.setOnClickListener(this::setGender);
        _binding.wontSayBtn.setOnClickListener(this::setGender);

        _binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new EnterBirthdate()).commit();
            }
        });

        return _binding.getRoot();
    }

    public void setGender(View v) {
        String gender;
        if (v.getId() == _binding.maleBtn.getId())
            _userRefs.child(_mAuth.getUid().toString()).child("gender").setValue("male");
        else if (v.getId() == _binding.femaleBtn.getId())
            _userRefs.child(_mAuth.getUid().toString()).child("gender").setValue("female");
        else
            _userRefs.child(_mAuth.getUid().toString()).child("gender").setValue("wontSay");

        startActivity(new Intent(getContext(), MainActivity.class));
    }
}