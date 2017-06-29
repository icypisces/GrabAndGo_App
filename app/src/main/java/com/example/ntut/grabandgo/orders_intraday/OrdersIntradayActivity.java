package com.example.ntut.grabandgo.orders_intraday;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersIntradayActivity extends NavigationDrawerSetup {

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private String[] OrderTabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_activity_intraday);
        setUpToolBar();
        findView();
        setViewPager();
        tabLayout.setupWithViewPager(viewPaper);
 //       setTabLayoutTitle();
    }

    private void findView() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPaper = (ViewPager) findViewById(R.id.viewPaper);
    }

    private void setViewPager() {
        UnprocessedOrderFragment fragment1 = new UnprocessedOrderFragment();
        CompletedOrderFragment fragment2 = new CompletedOrderFragment();
        PaidOrderFragment fragment3 = new PaidOrderFragment();

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);

        ViewPagerFragmentAdapter viewFragmentAdapter =
                new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList);
                                            //生命週期跟著Activity走
        viewPaper.setAdapter(viewFragmentAdapter);
    }


    private void setTabLayoutTitle() {
        for (int i=0; i<OrderTabTitles.length; i++) {
            tabLayout.getTabAt(i).setText(OrderTabTitles[i]);
        }
    }


}
