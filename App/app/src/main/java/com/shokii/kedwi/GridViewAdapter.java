package com.shokii.kedwi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

// TODO:
//   Сохранять картинки в кэше чтобы не было постоянной загрузки картинок при прокрутки
//   Пофиксить баг прокрутки (заключается в том что каждый раз мы заново качаем картинку)



public class GridViewAdapter extends ArrayAdapter<GameItem> {
    private FirebaseStorage storage;
    private StorageReference storageRef;

    String[] options = {"Не играл", "Прохожу", "В планах", "Пройдено", "Отложено", "Брошено"};
    String[] temp = {"not played", "passing", "planned", "pass", "postponed", "abandoned"};
    private int[] backgroundResources = {
            R.color.grey,
            R.color.passing,
            R.color.planned,
            R.color.pass,
            R.color.postponed,
            R.color.abandoned
    };

    public GridViewAdapter(@NonNull Context context, ArrayList<GameItem> gameItems) {
        super(context, 0, gameItems);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("games_covers");

        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);

        GameItem gameItem = getItem(position);

        ImageView gameLogo = (ImageView) listView.findViewById(R.id.gameLogo);
        TextView gameText = (TextView) listView.findViewById(R.id.gameText);
        TextView gameStatus = (TextView) listView.findViewById(R.id.gameStatus);

        storageRef.child(gameItem.imgSrc).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.game_logos)
                        .override(300, 450)
                        .optionalCenterCrop()
                        .into(gameLogo);
            }
        });

        gameText.setText(gameItem.gameTitle);

        int i;
        for (i = 0; i < options.length - 1; i++) {
            if (gameItem.gameStatus.equals(temp[i])) break;
        }
        if (i != 0) {
            gameStatus.setBackground(getContext().getResources().getDrawable(backgroundResources[i]));
            gameStatus.setTextColor(getContext().getResources().getColor(R.color.grey));
            gameStatus.setText(options[i]);
        }

        return listView;
    }
}
