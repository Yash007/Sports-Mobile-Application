package com.example.yash007.sportsapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by yash007 on 2018-03-30.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)   {
            case 0:
                TeamFragment teamFragment = new TeamFragment();
                return  teamFragment;
            case 1:
                MemberFragment memberFragment = new MemberFragment();
                return  memberFragment;
            case 2:
                EventFragment eventFragment = new EventFragment();
                return eventFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
