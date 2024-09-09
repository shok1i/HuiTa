package com.shokii.kedwi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shokii.kedwi.databinding.FragmentEnterBirthdateBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;


// TODO:
//   Поменять окно с выбором дат на крутящиеся
//   Проверка на то что пользователь не изменял дату

/**
 В бд январь это нулевой месяц
 Хранение в виде:
        ДЕНЬ(число)//МЕСЯЦ(число)//ГОД(число)
 **/



public class EnterBirthdate extends Fragment {
    public EnterBirthdate() { super   (R.layout.fragment_enter_birthdate); }

    private FragmentEnterBirthdateBinding _binding;
    private FirebaseAuth _mAuth;
    private FirebaseDatabase _dataBase;
    private DatabaseReference _usersRefs, _curentUser;
    private String[] _monthMap = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" };
    private int[] _pickDate = new int[3];


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mAuth = FirebaseAuth.getInstance();
        _dataBase = FirebaseDatabase.getInstance();
        _usersRefs = _dataBase.getReference("users");

        _curentUser = _usersRefs.child(_mAuth.getUid());


        _binding = FragmentEnterBirthdateBinding.inflate(getLayoutInflater());

        _curentUser.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _binding.textHighlight.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d  MMM  y");
        _binding.datePicker.setText(simpleDateFormat.format(cal.getTime()));

        _binding.backBtn.setOnClickListener(this::Back);
        _binding.datePicker.setOnClickListener(this::dataPick);
        _binding.birthdateContinue.setOnClickListener(this::Continue);

        return _binding.getRoot();
    }

    private void dataPick(View v) {
        final Calendar calendar = Calendar.getInstance();


        int year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog( getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _pickDate[0] = dayOfMonth;
                _pickDate[1] = monthOfYear;
                _pickDate[2] = year;
                _binding.datePicker.setText(_pickDate[0] + "  " + (_monthMap[_pickDate[1]]) + "  " + _pickDate[2]);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void Continue(View v) {
        _curentUser.child("birthdate").setValue(_pickDate[0] + "/" + _pickDate[1] + "/" + _pickDate[2]);

        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new EnterGender()).commit();
    }

    private void Back(View v) {
        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new EnterName()).commit();
    }

}