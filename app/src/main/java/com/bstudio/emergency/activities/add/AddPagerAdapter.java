package com.bstudio.emergency.activities.add;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * class สำหรับการนำ fragment มาแสดงในหน้า AddActivity
 */
public class AddPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

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
