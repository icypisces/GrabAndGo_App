package com.example.ntut.grabandgo.orders_intraday;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

//放置各子頁面（管理用），再放到OrderIntradayActivity的ViewPaper內．
public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentsList;

    public ViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> fragmentsList) {
        super(fm);
        this.fragmentsList = fragmentsList;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }
}
