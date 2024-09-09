package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shokii.kedwi.databinding.FragmentCalendarPageBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CalendarPage extends Fragment {
    FragmentCalendarPageBinding binding;
    LocalDate currentDate;
    int currentDayOfWeek;
    TextView[] textViews = new TextView[7];

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru"));

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    public CalendarPage() {
        super (R.layout.fragment_calendar_page);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCalendarPageBinding.inflate(getLayoutInflater());

        currentDate = LocalDate.now();
        currentDayOfWeek = currentDate.getDayOfWeek().getValue() - 1;

        textViews = new TextView[]{ binding.d1, binding.d2, binding.d3,
                binding.d4, binding.d5, binding.d6, binding.d7
        };

        binding.backWeek.setOnClickListener(this::backWeek);
        binding.nextWeek.setOnClickListener(this::nextWeek);

        selectedDate(currentDate, currentDayOfWeek);
    }

    private void updateTextViews() {
        for (int i = 0; i < textViews.length; i++)
            textViews[i].setText(String.valueOf(currentDate.plusDays(i - currentDayOfWeek).getDayOfMonth()));

        if (!textViews[2].getText().toString().isEmpty() && Integer.parseInt(textViews[2].getText().toString()) < 7)
            binding.currentDate.setText(currentDate.format(formatter));
    }

    private void backWeek(View view) {
        currentDate = currentDate.minusWeeks(1);
        updateTextViews();
    }

    private void nextWeek(View view) {
        currentDate = currentDate.plusWeeks(1);
        updateTextViews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding.currentDate.setText(currentDate.format(formatter));
        updateTextViews();

        for (int i = 0; i < textViews.length; i++) {
            int finalI = i;
            textViews[i].setOnClickListener(view -> selectedDate(currentDate, finalI));
        }

        return binding.getRoot();
    }

    private void selectedDate(LocalDate dataClicked, int i) {
        String selected = dataClicked.plusDays(i - currentDayOfWeek).format(DateTimeFormatter.ofPattern("dd|MM|yyyy"));

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GameItem> list = new ArrayList<>();

                DataSnapshot user_GamesList =
                        snapshot.child("users").child(mAuth.getUid()).child("game statistic").child("announcement");

                for (DataSnapshot dates : user_GamesList.getChildren()) {
                    if (Objects.equals(dates.getKey(), selected)) {
                        for (DataSnapshot games : dates.getChildren()) {
                            String gameName = games.getKey();

                            String imgSrc = "@null";
                            if (snapshot.child("games").child("announcement").child(gameName).child("cover").getValue() != null)
                                imgSrc = snapshot.child("games").child("announcement").child(gameName).child("cover").getValue().toString();

                            GameItem gameItem = new GameItem(gameName, imgSrc, "not played");
                            list.add(gameItem);
                        }
                    }
                }

                GridViewAdapter adapter = new GridViewAdapter(getContext(), list);

                if (!list.isEmpty()) {
                    binding.gamesGrid.setAdapter(adapter);
                    binding.message.setVisibility(View.GONE);
                }
                else {
                    binding.gamesGrid.setAdapter(adapter);
                    binding.message.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}