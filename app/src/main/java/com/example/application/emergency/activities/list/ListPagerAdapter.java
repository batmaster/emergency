package com.example.application.emergency.activities.list;

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
                return ListFragment.getInstance(ListFragment.LIST_PENDING);
            case 1:
                return ListFragment.getInstance(ListFragment.LIST_PROGRESSING);
            case 2:
                return ListFragment.getInstance(ListFragment.LIST_DONE);
            case 3:
                return SummaryFragment.getInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
