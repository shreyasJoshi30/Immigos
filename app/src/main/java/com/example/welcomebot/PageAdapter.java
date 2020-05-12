package com.example.welcomebot;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int noOfTabs;
    public PageAdapter(FragmentManager fm,int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ExerciseFragment();
            case 1:
                return new ExerciseFragment();
            case 2:
                return new ExerciseFragment();
            default:
                return  new ExerciseFragment();
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
