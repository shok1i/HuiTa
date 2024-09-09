package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shokii.kedwi.databinding.FragmentEnterAccountBinding;


public class EnterAccount extends Fragment {
    public EnterAccount() {
        super (R.layout.fragment_enter_account);
    }

    private FragmentEnterAccountBinding _binding;
    private FirebaseAuth _mAuth;
    private FirebaseDatabase _dataBase;
    private DatabaseReference _userRefs;
    private EnterPassword nextFragment = new EnterPassword();
    private Bundle _bundle = new Bundle();



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mAuth = FirebaseAuth.getInstance();
        _dataBase = FirebaseDatabase.getInstance();
        _userRefs = _dataBase.getReference("users");


        _binding = FragmentEnterAccountBinding.inflate(getLayoutInflater());


        _binding.loginContinue.setOnClickListener(this::EnterEmail);
        _binding.loginVk.setOnClickListener(this::InDev);
        _binding.loginGoogle.setOnClickListener(this::InDev);

        return _binding.getRoot();
    }

    public void EnterEmail(View v) {
        String email = _binding.loginText.getText().toString();

        _userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEXIST = false;

                for (DataSnapshot userUID :snapshot.getChildren()) {
                    String databaseEmail = userUID.child("email").getValue().toString();
                    if(databaseEmail.equals(email))
                        isEXIST = true;
                }

                _bundle.putString("EMAIL", email);
                _bundle.putBoolean("isEXIST", isEXIST);
                nextFragment.setArguments(_bundle);

                if (getFragmentManager() != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, nextFragment).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void InDev(View v) {
        Toast.makeText(getContext(), "Данная функция находиться в разработке", Toast.LENGTH_SHORT).show();
    }
}