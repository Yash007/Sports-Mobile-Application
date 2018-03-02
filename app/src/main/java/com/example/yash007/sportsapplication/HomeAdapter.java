package com.example.yash007.sportsapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by yash007 on 2018-03-02.
 */

public class HomeAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public HomeAdapter(FragmentManager fm, int NumOfPos)    {
        super(fm);
        this.mNumOfTabs = NumOfPos;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TeamsFragment tab1 = new TeamsFragment();
                return tab1;
            case 1:
                HealthFragment tab2 = new HealthFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
