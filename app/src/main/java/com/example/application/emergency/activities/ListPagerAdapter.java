package com.example.application.emergency.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by batmaster on 4/15/2017 AD.
 */

public class ListPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ListFragment tab1 = ListFragment.getInstance(ListFragment.LIST_PENDING);
                return tab1;
            case 1:
                ListFragment tab2 = ListFragment.getInstance(ListFragment.LIST_PROGRESSING);
                return tab2;
            case 2:
                ListFragment tab3 = ListFragment.getInstance(ListFragment.LIST_DONE);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
