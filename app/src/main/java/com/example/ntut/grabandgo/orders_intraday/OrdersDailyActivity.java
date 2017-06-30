package com.example.ntut.grabandgo.orders_intraday;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersDailyActivity extends NavigationDrawerSetup {

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private int[] OrderTabTitles = {R.string.unprocessed, R.string.completed, R.string.paid};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_activity_daily);
        setUpToolBar();
        findView();
        setViewPager();
        tabLayout.setupWithViewPager(viewPaper);
        setTabLayoutTitle();
        turnToFragment();
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

        // Setting the default Tab
        viewPaper.setCurrentItem(0);
    }


    private void setTabLayoutTitle() {
        for (int i=0; i<OrderTabTitles.length; i++) {
            tabLayout.getTabAt(i).setText(OrderTabTitles[i]);
        }
    }

    private void turnToFragment() {
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new UnprocessedOrderFragment())
                    .addToBackStack(null)//按下返回鍵會回到上一個Fragment
                    .commit();
            viewPaper.setCurrentItem(0);
        } else if (id == 2) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new CompletedOrderFragment())
                    .addToBackStack(null)
                    .commit();
            viewPaper.setCurrentItem(1);
        } else if (id == 3) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new PaidOrderFragment())
                    .addToBackStack(null)
                    .commit();
            viewPaper.setCurrentItem(2);
        }
    }



}
