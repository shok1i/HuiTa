package com.shokii.kedwi;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shokii.kedwi.databinding.FragmentEnterPasswordBinding;


public class EnterPassword extends Fragment {
    private FragmentEnterPasswordBinding _binding;
    private Bundle _bundle;

    private FirebaseAuth _mAuth;
    private FirebaseDatabase _dataBase;
    private DatabaseReference _userRefs;



    public EnterPassword() {
        super (R.layout.fragment_enter_password);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mAuth = FirebaseAuth.getInstance();
        _dataBase = FirebaseDatabase.getInstance();
        _userRefs = _dataBase.getReference().child("users");


        _binding = FragmentEnterPasswordBinding.inflate(getLayoutInflater());
        _bundle = getArguments();

        String email = _bundle.getString("EMAIL");

        if (_bundle.getBoolean("isEXIST")) {
            _binding.passwordTitle.setText("Введите пароль");

            _binding.passwordContinue.setOnClickListener((view) -> LogInWithEmailAndPassword (email));
        }
        else {
            _binding.passwordTitle.setText("Создайте пароль");

            _binding.passwordContinue.setOnClickListener((view) -> RegistrationWithEmailAndPassword (email));
        }


        _binding.showUpBtn.setOnClickListener(new View.OnClickListener() {
            boolean showPassword = true;

            @Override
            public void onClick(View v) {

                if (showPassword) {
                    _binding.passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    _binding.showUpBtn.setBackgroundResource(R.drawable.hide_password_btn);
                    showPassword = false;
                }
                else {
                    _binding.passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    _binding.showUpBtn.setBackgroundResource(R.drawable.show_password_btn);
                    showPassword = true;
                }
            }
        });

        _binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new EnterAccount()).commit();
            }
        });

        return _binding.getRoot();
    }

    private void LogInWithEmailAndPassword (String email) {

        _mAuth.signInWithEmailAndPassword(email, _binding.passwordText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(getContext(), "LOGIN: False", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void RegistrationWithEmailAndPassword(String email) {
        _mAuth.createUserWithEmailAndPassword(email, _binding.passwordText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Записываем в БД информацию пользователя
                            _userRefs.child(_mAuth.getUid()).child("email").setValue(email);
                            _userRefs.child(_mAuth.getUid()).child("password").setValue(_binding.passwordText.getText().toString());
                            _userRefs.child(_mAuth.getUid()).child("status").setValue("Статус не указан");

                            if (getFragmentManager() != null)
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new RegistrationContinue()).commit();
                        }
                        else {
                            Toast.makeText(getContext(), "REGISTRATION: False", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}