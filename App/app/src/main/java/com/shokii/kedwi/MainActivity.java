package com.shokii.kedwi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shokii.kedwi.databinding.ActivityMainBinding;
import com.theartofdev.edmodo.cropper.CropImage;

public class MainActivity extends AppCompatActivity /* implements BottomNavigationView.OnNavigationItemSelectedListener */ {
    private ActivityMainBinding _binding;

    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference userRefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("user_image");

        _binding.bottomNav.setSelectedItemId(R.id.menu_home);


        HomePage homePage = new HomePage();
        CalendarPage calendarPage = new CalendarPage();
        BookmarksPage bookmarksPage = new BookmarksPage();
        UserPage userPage = new UserPage();

        _binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int curPage = _binding.bottomNav.getSelectedItemId();

                if (item.getItemId() == R.id.menu_home && curPage != R.id.menu_home) {
                    // menu_home
                    loadFragment(homePage);
                    return true;
                }
                if (item.getItemId() == R.id.menu_calendar && curPage != R.id.menu_calendar) {
                    // menu_calendar
                    loadFragment(calendarPage);
                    return true;
                }if (item.getItemId() == R.id.menu_bookmarks && curPage != R.id.menu_bookmarks) {
                    // menu_bookmarks
                    loadFragment(bookmarksPage);
                    return true;
                }if (item.getItemId() == R.id.menu_user && curPage != R.id.menu_user) {
                    // menu_user
                    loadFragment(userPage);
                    return true;
                }
                return false;
            }
        });

        userRefs = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("email").getValue().toString().equals("admin@kedwi.com"))
                    _binding.adminAdd.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        _binding.adminAdd.setOnClickListener(this::adminAdd);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, homePage).commit();
    }

    private void adminAdd(View view) {
        startActivity(new Intent(this, AdminGameAdd.class));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            Uri uri = CropImage.getActivityResult(data).getUri();
            storageRef.child(mAuth.getUid()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(MainActivity.this, "Фото обновлено", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}