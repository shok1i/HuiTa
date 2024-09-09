package com.shokii.kedwi;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapterBookmarks extends FragmentPagerAdapter {
    private static final int NUM_TABS = 5; // Количество вкладок
    private static final String[][] TABS_LABEL = {
            {"Прохожу", "В планах", "Пройдено", "Отложено", "Брошено"},
            {"passing", "planned", "pass", "postponed", "abandoned"}};

    public ViewPagerAdapterBookmarks(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new BookmarksPageBlank(TABS_LABEL[1][position]);
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
