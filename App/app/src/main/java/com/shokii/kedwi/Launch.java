package com.shokii.kedwi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.shokii.kedwi.databinding.ActivityLaunchBinding;


public class Launch extends AppCompatActivity {
    private ActivityLaunchBinding _binding;
    private FirebaseAuth _mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _mAuth = FirebaseAuth.getInstance();
        //  Если пользователь уже входил на устройстве то пропускаем вход/регистрацию
        if (_mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, new EnterAccount()).commit();
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container_view, new HomePage()).commit();
    }

}