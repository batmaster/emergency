package com.example.application.emergency.activities.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * class สำหรับการนำ fragment มาแสดงในหน้า ListActivity
 */
public class ListPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

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
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
