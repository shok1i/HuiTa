package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shokii.kedwi.databinding.FragmentEnterNameBinding;


public class EnterName extends Fragment {
    public EnterName () { super (R.layout.fragment_enter_name); }
    private FragmentEnterNameBinding _binding;

    private FirebaseAuth _mAuth;
    private FirebaseDatabase _dataBase;
    private DatabaseReference _userRefs;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mAuth = FirebaseAuth.getInstance();
        _dataBase = FirebaseDatabase.getInstance();
        _userRefs = _dataBase.getReference("users");

        _binding = FragmentEnterNameBinding.inflate(getLayoutInflater());

        _binding.nameContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _userRefs.child(_mAuth.getUid().toString()).child("name").setValue(_binding.nameText.getText().toString());
                if (getFragmentManager() != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new EnterBirthdate()).commit();
            }
        });

        _binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new RegistrationContinue()).commit();
            }
        });

        return _binding.getRoot();
    }
}