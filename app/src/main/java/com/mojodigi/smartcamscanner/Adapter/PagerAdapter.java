package com.mojodigi.smartcamscanner.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mojodigi.smartcamscanner.Fragments.AllFileFolderFragment;
import com.mojodigi.smartcamscanner.Fragments.RecentFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                RecentFragment tab1 = new RecentFragment();
                return tab1;
            case 1:
                AllFileFolderFragment tab2 = new AllFileFolderFragment();
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