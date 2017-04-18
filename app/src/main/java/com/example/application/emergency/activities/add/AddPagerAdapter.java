package com.example.application.emergency.activities.add;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.application.emergency.activities.list.ListFragment;

/**
 * Created by batmaster on 4/15/2017 AD.
 */

public class AddPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public AddPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return DetailFragment.getInstance();
            case 1:
                return ImagesFragment.getInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
