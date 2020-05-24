package com.example.welcomebot;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * The page adapater for HospitalFragment which has 3 segmments.This adapter handles the fragment navigations
 */
public class PageAdapter extends FragmentPagerAdapter {

    private int noOfTabs;
    public PageAdapter(FragmentManager fm,int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }
    //------------------------------------------------------------------------------------------------------//


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HospitalListFragment();
            case 1:
                return new DoctorListFragment();
            case 2:
                return new ChineseMedical();
            default:
                return  new DoctorListFragment();
        }
    }

    //------------------------------------------------------------------------------------------------------//

    @Override
    public int getCount() {
        return noOfTabs;
    }
    //------------------------------------------------------------------------------------------------------//

}
