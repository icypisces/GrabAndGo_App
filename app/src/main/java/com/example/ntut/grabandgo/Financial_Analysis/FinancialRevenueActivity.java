package com.example.ntut.grabandgo.Financial_Analysis;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.ViewPagerFragmentAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FinancialRevenueActivity extends NavigationDrawerSetup{
    private static final String TAG = "FinancialRevenueActivity";
    private String ServletName = "/AppRevenueServlet";

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private int[] OrderTabTitles = {R.string.revenueDaily_s, R.string.revenueMonthly_s, R.string.revenueYearly_s};

    //Login
    private SharedPreferences sharedPreferencesLogin = null;
    private String rest_name;

    private List<OrderItem> orderItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.financial_activity_revenue);
        setUpToolBar();
        findView();

        getRestaurantName();
//        getOrderDataFromServlet();

        setViewPager();
        tabLayout.setupWithViewPager(viewPaper);
        setTabLayoutTitle();
        turnToFragment();

    }

    private void findView() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPaper = (ViewPager) findViewById(R.id.viewPaper);
    }

//--------------------------連線至伺服器取得OrderItem資料--------------------------------------------
    private void getRestaurantName() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        rest_name = sharedPreferencesLogin.getString("rest_name", "");
    }

    private void getOrderDataFromServlet(String interval) {
        String url = Common.URL + ServletName ;
        //取得訂單收入相關資訊
        if (Common.networkConnected(FinancialRevenueActivity.this)) {
            try {
                orderItemList = new RevenueGetTask().execute(url, rest_name, interval).get();
                Log.e(TAG, orderItemList.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }


//--------------------------------------連結Fragment--------------------------------------------

    private void setViewPager() {
        RevenueDailyFragment fragment1 = new RevenueDailyFragment();
        RevenueMonthlyFragment fragment2 = new RevenueMonthlyFragment();
        RevenueYearlyFragment fragment3 = new RevenueYearlyFragment();

        getOrderDataFromServlet("daily");
        //將自資料庫取得之資料送給RevenueDailyFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderItemList", (Serializable) orderItemList);
        fragment1.setArguments(bundle);
        ft.replace(R.id.viewPaper, fragment1);
        ft.commit();


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

//------------------------------------fragment轉換頁面--------------------------------------------

    private void turnToFragment() {
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new RevenueDailyFragment())
                    .addToBackStack(null)//按下返回鍵會回到上一個Fragment
                    .commit();
            viewPaper.setCurrentItem(0);
        } else if (id == 2) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new RevenueMonthlyFragment())
                    .addToBackStack(null)
                    .commit();
            viewPaper.setCurrentItem(1);
        } else if (id == 3) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new RevenueYearlyFragment())
                    .addToBackStack(null)
                    .commit();
            viewPaper.setCurrentItem(2);
        }
    }

}