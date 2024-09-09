package com.shokii.kedwi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shokii.kedwi.databinding.ActivityAdminGameAddBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AdminGameAdd extends AppCompatActivity {
    ActivityAdminGameAddBinding activityAdminGameAddBinding;
    LocalDate currentDate;

    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminGameAddBinding = ActivityAdminGameAddBinding.inflate(getLayoutInflater());

        currentDate = LocalDate.now();
        String date = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        activityAdminGameAddBinding.gameDateBtn.setText(date);


        // Кнопки
        activityAdminGameAddBinding.gameLogo.setOnClickListener(this::gameLogo);
        activityAdminGameAddBinding.backBtn.setOnClickListener(this::backBtn);
        activityAdminGameAddBinding.gameStatusBtn.setOnClickListener(this::changeGameStatus);
        activityAdminGameAddBinding.gameDateBtn.setOnClickListener(this::gameDateBtn);
        activityAdminGameAddBinding.gamePEGIBtn.setOnClickListener(this::gamePEGIBtn);
        activityAdminGameAddBinding.addBtn.setOnClickListener(this::addBtn);

        setContentView(activityAdminGameAddBinding.getRoot());
    }

    private void gameLogo(View view) {
        CropImage.activity()
                .setAspectRatio(2, 3)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start((Activity) this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            uri = CropImage.getActivityResult(data).getUri();
            activityAdminGameAddBinding.gameLogo.setImageURI(uri);
        }
    }

    private void changeGameStatus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите один вариант");

        String[] options = {"Вышла", "Анонсирована"};
        builder.setItems(options, (dialog, which) -> activityAdminGameAddBinding.gameStatusBtn.setText(options[which]));

        builder.show();
    }

    private void gameDateBtn(View view) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog( this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = String.valueOf(monthOfYear);
                if (monthOfYear < 10)
                    month = "0" + month;

                activityAdminGameAddBinding.gameDateBtn.setText((dayOfMonth + "/" + month + "/" + year).toString());
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void gamePEGIBtn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите один вариант");

        String[] options = {"3", "7", "12", "16", "18"};
        builder.setItems(options, (dialog, which) -> activityAdminGameAddBinding.gamePEGIBtn.setText(options[which]));

        builder.show();
    }

    private void backBtn(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void addBtn(View view) {
        String
                gameStatus = activityAdminGameAddBinding.gameStatusBtn.getText().toString().equals("Вышла") ? "out" : "announcement",
                gameName = activityAdminGameAddBinding.gameNameText.getText().toString(),
                gameDate = activityAdminGameAddBinding.gameDateBtn.getText().toString(),
                gameDeveloper = activityAdminGameAddBinding.gameDeveloperText.getText().toString(),
                gamePublisher = activityAdminGameAddBinding.gamePublisherText.getText().toString(),
                gamePEGI = activityAdminGameAddBinding.gamePEGIBtn.getText().toString();

        if (gameName.isEmpty() || gameDeveloper.isEmpty() || gamePublisher.isEmpty()) {
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("games").child(gameStatus).child(gameName);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("games_covers");

        // cover
        storageRef.child(gameName + "_cover").putFile(uri);
        databaseRef.child("cover").setValue(gameName + "_cover");

        // date
        databaseRef.child("date").setValue(gameDate);

        // developers
        // => DEVELOPER 1
        databaseRef.child("developers").child(gameDeveloper).setValue("flag");

        // publishers
        databaseRef.child("publishers").setValue(gamePublisher);

        // PEGI
        databaseRef.child("pegi").setValue(gamePEGI);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}