package com.shokii.kedwi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_TABS = 2; // Количество вкладок
    private static final String[][] TABS_LABEL = {
            {"Вышедшие", "Анонсировано"},
            {"out", "announcement"}};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new HomePage_Blank(TABS_LABEL[1][position]);
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TABS_LABEL[0][position];
    }
}
