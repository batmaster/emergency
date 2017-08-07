package com.bstudio.emergency.activities.user;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * class สำหรับการนำ fragment มาแสดงในหน้า UserActivity
 */
public class UserPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    public UserPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return UserFragment.getInstance(UserFragment.LIST_USER);
            case 1:
                return UserFragment.getInstance(UserFragment.LIST_OFFICER);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
