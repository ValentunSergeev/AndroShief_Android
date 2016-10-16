package com.valentun.androshief.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.valentun.androshief.Fragments.SignInFragment;
import com.valentun.androshief.Fragments.SignUpFragment;


public class PageAdapter extends FragmentStatePagerAdapter {
    private int tabsCount;
    private SignInFragment signInFragment;
    private SignUpFragment signUpFragment;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.tabsCount = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                signUpFragment = new SignUpFragment();
                return signUpFragment;
            case 1:
                signInFragment = new SignInFragment();
                return signInFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsCount;
    }
}
