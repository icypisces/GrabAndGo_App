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

public class FinancialSalesChartsActivity extends NavigationDrawerSetup {
    private static final String TAG = "FinancialSalesChartsActivity";
    private String ServletName = "/AppSalesChartsServlet";

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private int[] OrderTabTitles = {R.string.salesChartsDaily_s, R.string.salesChartsMonthly_s};

    //Login
    private SharedPreferences sharedPreferencesLogin = null;
    private String rest_name;

    private List<OrderItem> salesChartsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.financial_activity_sales_charts);
        setUpToolBar();
        findView();

        getRestaurantName();

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
        if (Common.networkConnected(FinancialSalesChartsActivity.this)) {
            try {
                salesChartsList = new RevenueGetTask().execute(url, rest_name, interval).get();
                Log.e(TAG, salesChartsList.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }


//--------------------------------------連結Fragment--------------------------------------------

    private void setViewPager() {
        SalesChartsDailyFragment fragment1 = new SalesChartsDailyFragment();
        SalesChartsMonthlyFragment fragment2 = new SalesChartsMonthlyFragment();

        getOrderDataFromServlet("daily");
        //將自資料庫取得之資料送給RevenueDailyFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft1 = manager.beginTransaction();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("salesChartsList", (Serializable) salesChartsList);
        fragment1.setArguments(bundle1);
        ft1.replace(R.id.viewPaper, fragment1);
        ft1.commit();

        getOrderDataFromServlet("monthly");
        //將自資料庫取得之資料送給RevenueDailyFragment
        FragmentTransaction ft2 = manager.beginTransaction();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("salesChartsList", (Serializable) salesChartsList);
        fragment2.setArguments(bundle2);
        ft2.replace(R.id.viewPaper, fragment2);
        ft2.commit();

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

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
                    .replace(R.id.viewPaper, new SalesChartsDailyFragment())
                    .addToBackStack(null)//按下返回鍵會回到上一個Fragment
                    .commit();
            viewPaper.setCurrentItem(0);
        } else if (id == 2) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new SalesChartsMonthlyFragment())
                    .addToBackStack(null)
                    .commit();
            viewPaper.setCurrentItem(1);
        }
    }
}
