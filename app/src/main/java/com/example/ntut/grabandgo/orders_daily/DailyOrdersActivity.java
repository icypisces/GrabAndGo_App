package com.example.ntut.grabandgo.orders_daily;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.ViewPagerFragmentAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DailyOrdersActivity extends NavigationDrawerSetup {

    private TabLayout tabLayout;
    private ViewPager viewPaper;
    private int[] OrderTabTitles = {R.string.unprocessed, R.string.completed, R.string.paid};

    private String ServletName = "/AppStoreDailyOrdersServlet";
    private final static String TAG = "DailyOrdersActivity";
    private AsyncTask DailyOrdersTask;
    private ProgressDialog progressDialog;

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

//        String url = Common.URL + ServletName ;
//        //取得當日訂單資料
//        if (Common.networkConnected(DailyOrdersActivity.this)) {
//            DailyOrdersTask = new DailyOrdersTask().execute(url);
//        } else {
//            Common.showToast(this, R.string.msg_NoNetwork);
//        }
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

//    //取得當日訂單資料
//    class DailyOrdersTask extends AsyncTask<String, Void, List<String>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(DailyOrdersActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
//            progressDialog.setMessage("Loading...");
//            progressDialog.show();
//        }
//
//        @Override
//        protected List<String> doInBackground(String... params) {
//            String url = params[0];
//            String jsonIn;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("param", "DailyOrders");
//            try {
//                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
//            } catch (IOException e) {
//                Log.e(TAG, e.toString());
//                return null;
//            }
//
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<String>>() {
//            }.getType();
//
//            return gson.fromJson(jsonIn, listType);
//        }
//
//        @Override
//        protected void onPostExecute(List<String> items) {      //items->從doInBackground傳來的回傳結果
//            //待完.........
//        }
//    }



}
