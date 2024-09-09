package com.shokii.kedwi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shokii.kedwi.databinding.FragmentGameInfoBinding;

import java.util.ArrayList;
import java.util.Objects;

public class GameInfo extends Fragment {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference gameRef;
    private FragmentGameInfoBinding binding;
    Bundle bundle = new Bundle();

    FirebaseAuth mAuth;
    DatabaseReference userGameRef;

    private int[] backgroundResources = {
            R.drawable.status_not_played,
            R.drawable.status_passing,
            R.drawable.status_planned,
            R.drawable.status_pass,
            R.drawable.status_postponed,
            R.drawable.status_abandoned,
            R.drawable.rounded_btn_game_sec,
            R.drawable.rounded_btn_game
    };

    String[] options = {"Не играл", "Прохожу", "В планах", "Пройдено", "Отложено", "Брошено"};
    String[] temp = {"not played", "passing", "planned", "pass", "postponed", "abandoned"};

    private String[] _monthMap = { "январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь" };

    public GameInfo() {
        super(R.layout.fragment_game_info);
    }
    String gameName ;

    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentGameInfoBinding.inflate(getLayoutInflater());
    bundle = getArguments();
    mAuth = FirebaseAuth.getInstance();
    userGameRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("game statistic");
    gameName = bundle.getString("NAME");
    String imgSrc = bundle.getString("IMG_SRC");
    String gameStatus = bundle.getString("GAME_STATUS");
    String userGameStatus = bundle.getString("USER_GAME_STATUS");
    Button btn = binding.changeGameStatus;
    int i;
    for (i = 0; i < options.length - 1; i++) {
        if (userGameStatus.equals(temp[i])) break;
    }
    btn.setBackground(getResources().getDrawable(backgroundResources[i]));
    btn.setTextColor(getResources().getColor(R.color.grey));
    if (i == 0) btn.setTextColor(getResources().getColor(R.color.black));
    btn.setText(options[i]);
    if (gameStatus.equals("announcement")) {
        binding.changeGameStatus.setVisibility(View.GONE);
        binding.addAnocement.setVisibility(View.VISIBLE);
    }
    database = FirebaseDatabase.getInstance();
    gameRef = database.getReference().child("games").child(gameStatus).child(gameName);
    storage = FirebaseStorage.getInstance();
    storageRef = storage.getReference().child("games_covers");
    gameRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            storageRef.child(imgSrc).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext())
                            .load(uri)
                            .optionalCenterCrop()
                            .into(binding.gameLogo);
                }
            });
            if (!snapshot.child("date").getValue().toString().equals("")) {
                String[] date = snapshot.child("date").getValue().toString().split("/");
                binding.dateDesc.setText(date[0] + " " + _monthMap[Integer.parseInt(date[1]) - 1] + " " + date[2]);
            }
            else
                binding.dateDesc.setText("Дата будет объявлена позже");
            ArrayList<String> temp = new ArrayList<>();
            for (DataSnapshot developers : snapshot.child("developers").getChildren())
                temp.add(developers.getKey());
            for (int i = 0; i < temp.size() - 1; i++)
                binding.developerDesc.append(temp.get(i) + "\n");
            binding.developerDesc.append(temp.get(temp.size() - 1));
            if (!snapshot.child("publishers").getValue().toString().equals(""))
                binding.publisherDesc.setText(snapshot.child("publishers").getValue().toString());
            if (!snapshot.child("pegi").getValue().toString().equals("")) {
                int pegiValue = Integer.parseInt(snapshot.child("pegi").getValue().toString());
                int resourceId = getResources().getIdentifier("pegi" + pegiValue, "drawable", "com.shokii.kedwi");
                binding.pegiDesc.setImageResource(resourceId);
            }

            String OutDate = binding.dateDesc.getText().toString();
            String[] OutDateGame;

            if (OutDate.equals("Дата будет объявлена позже"))
                OutDate = "@null";
            else {
                OutDateGame = OutDate.split(" ");
                String day = OutDateGame[0];
                String month = " ";
                for (int j = 0; j < _monthMap.length; j++)
                    if (Objects.equals(OutDateGame[1], _monthMap[j])) {
                        month = String.valueOf(j + 1);
                        if (month.length() == 1) {
                            month = "0" + month;
                        }
                    }
                String year = OutDateGame[2];
                OutDate = day + "|" + month + "|" + year;
            }
            String finalOutDate = OutDate;
            userGameRef.child("announcement").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(finalOutDate).child(gameName).getValue() == null)
                        binding.addAnocement.setText("Добавить в список отслеживания");
                    else
                        binding.addAnocement.setText("Удалить из списока отслеживания");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    binding.backBtn.setOnClickListener(this::back);
    binding.changeGameStatus.setOnClickListener(this::changeGameStatus);
    binding.gameName.setText(gameName);





    binding.addAnocement.setOnClickListener(this::addAnnouncement);
    return binding.getRoot();
}
    private void addAnnouncement (View view) {
        String OutDate = binding.dateDesc.getText().toString();
        String[] OutDateGame;

        if (OutDate.equals("Дата будет объявлена позже"))
            OutDate = "@null";
        else {
            OutDateGame = OutDate.split(" ");
            String day = OutDateGame[0];
            String month = " ";
            for (int j = 0; j < _monthMap.length; j++)
                if (Objects.equals(OutDateGame[1], _monthMap[j])) {
                    month = String.valueOf(j + 1);
                    if (month.length() == 1) {
                        month = "0" + month;
                    }
                }
            String year = OutDateGame[2];
            OutDate = day + "|" + month + "|" + year;
        }
        if (binding.addAnocement.getText().toString().charAt(0) == 'Д') {
            userGameRef.child("announcement").child(OutDate).child(gameName).setValue("flag");
            binding.addAnocement.setText("Удалить из списока отслеживания");
        }
        else {
            userGameRef.child("announcement").child(OutDate).child(gameName).removeValue();
            binding.addAnocement.setText("Добавить в список отслеживания");
        }
    }
    private void changeGameStatus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выберите один вариант");

        Button btn = binding.changeGameStatus;
        String search = btn.getText().toString();
        int i;
        for (i = 0; i < options.length; i++) {
            if (search.equals(options[i])) break;
        }
        String currentStatus = temp[i];
        String gameName = binding.gameName.getText().toString();

        builder.setItems(options, (dialog, which) -> {
            btn.setBackground(getResources().getDrawable(backgroundResources[which]));
            btn.setTextColor(getResources().getColor(R.color.grey));
            if (which == 0) btn.setTextColor(getResources().getColor(R.color.black));
            btn.setText(options[which]);

            changeUserGameStatus(gameName, currentStatus, temp[which]);
        });

        builder.show();
    }
    private void changeUserGameStatus(String gameName, String prevStatus, String newStatus) {
        userGameRef.child(prevStatus).child(gameName).removeValue();
        userGameRef.child(newStatus).child(gameName).setValue("flag");
    }
    private void back(View view) {
        startActivity(new Intent(getContext(), MainActivity.class));
//        if (bundle.getBoolean("PAGE"))
//            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new BookmarksPage()).commit();
//        else
//            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomePage()).commit();
    }
}