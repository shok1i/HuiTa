package com.shokii.kedwi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shokii.kedwi.databinding.FragmentHomePageBinding;

public class HomePage extends Fragment {
    public HomePage () {
        super(R.layout.fragment_home_page);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentHomePageBinding _binding = FragmentHomePageBinding.inflate(getLayoutInflater());

        TabLayout tabLayout = _binding.tabLayout;
        ViewPager viewPager = _binding.pager;

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getParentFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        return _binding.getRoot();
    }
}