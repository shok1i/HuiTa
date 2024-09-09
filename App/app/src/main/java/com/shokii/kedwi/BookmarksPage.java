package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shokii.kedwi.databinding.FragmentBookmarksPageBinding;


public class BookmarksPage extends Fragment {

    public BookmarksPage () {
        super(R.layout.fragment_bookmarks_page);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBookmarksPageBinding _binding = FragmentBookmarksPageBinding.inflate(getLayoutInflater());

        TabLayout tabLayout = _binding.tabLayoutBookmarks;
        ViewPager viewPager = _binding.pagerBookmarks;
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

// Создайте адаптер для ViewPager
        ViewPagerAdapterBookmarks pagerAdapter = new ViewPagerAdapterBookmarks(getParentFragmentManager());
        viewPager.setAdapter(pagerAdapter);

// Свяжите TabLayout с ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return _binding.getRoot();
    }
}