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
import com.shokii.kedwi.databinding.FragmentBookmarksPageBlankBinding;

import java.util.ArrayList;


public class BookmarksPageBlank extends Fragment {
    private String GAME_TYPE = "";
    FragmentBookmarksPageBlankBinding bookmarksPageBlank;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<GameItem> list = new ArrayList<>();

    public BookmarksPageBlank(String game_type) {
        super(R.layout.fragment_bookmarks_page_blank);
        GAME_TYPE = game_type;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookmarksPageBlank = FragmentBookmarksPageBlankBinding.inflate(getLayoutInflater());


        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                DataSnapshot
                        USER_GAMES = snapshot.child("users").child(mAuth.getUid()).child("game statistic"),
                        GAMES = snapshot.child("games").child("out");

                for (DataSnapshot snap : USER_GAMES.child(GAME_TYPE).getChildren()) {
                    String gameName = snap.getKey();
                    String imgSrc = "@null";
                    if (GAMES.child(gameName).child("cover").getValue() != null)
                        imgSrc  = GAMES.child(gameName).child("cover").getValue().toString();
                    String gameStatus = GAME_TYPE;

                    GameItem gameItem = new GameItem(gameName, imgSrc, gameStatus);

                    list.add(gameItem);
                }

                GridViewAdapter adapter = new GridViewAdapter(getContext(), list);
                bookmarksPageBlank.gamesGridBookmarks.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bookmarksPageBlank.gamesGridBookmarks.setOnItemClickListener(this::itemClick);
        return bookmarksPageBlank.getRoot();
    }

    private void itemClick(AdapterView<?> adapterView, View view, int i, long l) {
        GameItem item = list.get(i);

        Bundle bundle = new Bundle();
        bundle.putString("NAME", item.gameTitle);
        bundle.putString("IMG_SRC", item.imgSrc);
        bundle.putString("GAME_STATUS", "out");
        bundle.putString("USER_GAME_STATUS", item.gameStatus);
        bundle.putBoolean("PAGE", true);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setArguments(bundle);

        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_view, gameInfo).commit();
    }
}