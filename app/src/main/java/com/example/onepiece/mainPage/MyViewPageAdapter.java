package com.example.onepiece.mainPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class MyViewPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private String[] titles;
    private final int FRAGMENT_COUNT = 3;

    public MyViewPageAdapter(FragmentManager fm, String[] titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
