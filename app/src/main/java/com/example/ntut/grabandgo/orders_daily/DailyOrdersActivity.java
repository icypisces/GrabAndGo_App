package com.example.ntut.grabandgo.orders_daily;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.ViewPagerFragmentAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyOrdersActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreOrderDailyServlet";
    private final static String TAG = "DailyOrdersActivity";

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private int[] OrderTabTitles = {R.string.unprocessed, R.string.completed, R.string.paid};

    private EditText etSearch;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;
    private String rest_id;

    List<Order> orderList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_activity_daily);
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
        etSearch = (EditText) findViewById(R.id.etSearch);
    }

    //--------------------------連線至伺服器取得OrderItem資料--------------------------------------------
    private void getRestaurantName() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(), MODE_PRIVATE);
        rest_id = sharedPreferencesLogin.getString("rest_id", "");
    }

    private void getOrderDataFromServlet(String status) {
        String url = Common.URL + ServletName;
        //取得今日訂單(含未來訂單)
        if (Common.networkConnected(DailyOrdersActivity.this)) {
            try {
                orderList = new DailyOrderGetTask().execute(url, rest_id, status, TAG).get();
                Log.e(TAG, orderList.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

//--------------------------------------連結Fragment--------------------------------------------

    private void setViewPager() {
        InprogressOrderFragment fragment1 = new InprogressOrderFragment();
        CompletedOrderFragment fragment2 = new CompletedOrderFragment();
        PaidOrderFragment fragment3 = new PaidOrderFragment();

        getOrderDataFromServlet("inprogress");
        //將自資料庫取得之資料送給InprogressOrderFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft1 = manager.beginTransaction();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("orderList", (Serializable) orderList);
        fragment1.setArguments(bundle1);
        ft1.replace(R.id.viewPaper, fragment1);
        ft1.commit();

        getOrderDataFromServlet("unpaid");
        //將自資料庫取得之資料送給CompletedOrderFragment
        FragmentTransaction ft2 = manager.beginTransaction();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("orderList", (Serializable) orderList);
        fragment2.setArguments(bundle2);
        ft2.replace(R.id.viewPaper, fragment2);
        ft2.commit();

        getOrderDataFromServlet("paid");
        //將自資料庫取得之資料送給PaidOrderFragment
        FragmentTransaction ft3 = manager.beginTransaction();
        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("orderList", (Serializable) orderList);
        fragment3.setArguments(bundle3);
        ft3.replace(R.id.viewPaper, fragment3);
        ft3.commit();

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
        for (int i = 0; i < OrderTabTitles.length; i++) {
            tabLayout.getTabAt(i).setText(OrderTabTitles[i]);
        }
    }

    private void turnToFragment() {
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.viewPaper, new InprogressOrderFragment())
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

    public void onSearchClick(View view) {
        //待補!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
}
