package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shokii.kedwi.databinding.FragmentHomePageBlankBinding;

import java.util.ArrayList;
import java.util.Objects;


// TODO:



public class HomePage_Blank extends Fragment {
    private String GAME_TYPE = "";
    FragmentHomePageBlankBinding bookmarksPageBlank;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<GameItem> list = new ArrayList<>();

    public HomePage_Blank(String game_type){
        super(R.layout.fragment_home_page_blank);
        GAME_TYPE = game_type;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookmarksPageBlank = FragmentHomePageBlankBinding.inflate(getLayoutInflater());

        bookmarksPageBlank.gamesGrid.setOnItemClickListener(this::itemClick);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                DataSnapshot
                        USER_GAMES = snapshot.child("users").child(mAuth.getUid()).child("game statistic"),
                        GAMES = snapshot.child("games").child(GAME_TYPE);

                for (DataSnapshot snap : GAMES.getChildren()) {
                    String gameName = snap.getKey();
                    String imgSrc = "@null";
                    if (snap.child("cover").getValue() != null)
                        imgSrc  = GAMES.child(gameName).child("cover").getValue().toString();
                    String gameStatus = "not played";


                    for (DataSnapshot users_list : USER_GAMES.getChildren())
                        for (DataSnapshot game : users_list.getChildren())
                            if (Objects.equals(gameName, game.getKey()))
                                gameStatus = users_list.getKey();

                    GameItem gameItem = new GameItem(gameName, imgSrc, gameStatus);
                    list.add(gameItem);
                }

                GridViewAdapter adapter = new GridViewAdapter(getContext(), list);
                bookmarksPageBlank.gamesGrid.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bookmarksPageBlank.gamesGrid.setOnItemClickListener(this::itemClick);
        return bookmarksPageBlank.getRoot();
    }

    private void itemClick(AdapterView<?> adapterView, View view, int i, long l) {
        GameItem item = list.get(i);
        Bundle bundle = new Bundle();
        bundle.putString("NAME", item.gameTitle);
        bundle.putString("IMG_SRC", item.imgSrc);
        bundle.putString("GAME_STATUS", GAME_TYPE);
        bundle.putString("USER_GAME_STATUS", item.gameStatus);
        bundle.putBoolean("PAGE", false);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setArguments(bundle);

        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, gameInfo).commit();
    }
}