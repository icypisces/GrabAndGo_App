package com.example.ntut.grabandgo.orders_daily;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.ViewPagerFragmentAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
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

    private List<Order> orderList = null;
    private List<Order> orderListAll = new ArrayList<>();
    private int readOrder ;
    private String ord_id;
    private AsyncTask OrderIsReadTask;
    private ProgressDialog progressDialog;

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
        orderListAll.addAll(orderList);

        getOrderDataFromServlet("unpaid");
        //將自資料庫取得之資料送給CompletedOrderFragment
        FragmentTransaction ft2 = manager.beginTransaction();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("orderList", (Serializable) orderList);
        fragment2.setArguments(bundle2);
        ft2.replace(R.id.viewPaper, fragment2);
        ft2.commit();
        orderListAll.addAll(orderList);

        getOrderDataFromServlet("paid");
        //將自資料庫取得之資料送給PaidOrderFragment
        FragmentTransaction ft3 = manager.beginTransaction();
        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("orderList", (Serializable) orderList);
        fragment3.setArguments(bundle3);
        ft3.replace(R.id.viewPaper, fragment3);
        ft3.commit();
        orderListAll.addAll(orderList);

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

    public void onSearchClick(View view) throws ClassNotFoundException {
        String searchPhone = etSearch.getText().toString().trim();
        String orderPhone = "";
        String orderStatus = "";
        Order orderSearch = null;
        Boolean searchResult = false;
        for (int i=0; i<orderListAll.size(); i++) {
            orderPhone = orderListAll.get(i).getOrd_tel();
            if (searchPhone.equals(orderPhone)){
                orderStatus = orderListAll.get(i).getOrd_status();
                orderSearch = orderListAll.get(i);
                readOrder = orderListAll.get(i).getIsRead();
                ord_id = String.valueOf(orderListAll.get(i).getOrd_id());
                searchResult = true;
            }
        }
        if (searchResult) {
            Intent intent;
            if (orderStatus.equals("inprogress")) {
                if (orderSearch.getIsRead() == 0) {
                    updateIsRead(orderSearch);
                } else {
                    intent = new Intent(DailyOrdersActivity.this,InprogressOrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", orderSearch);
                    bundle.putSerializable("readOrder", readOrder);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            } else {
                if (orderStatus.equals("unpaid")) {
                    intent = new Intent(DailyOrdersActivity.this, CompletedOrderDetailActivity.class);
                } else if (orderStatus.equals("paid")) {
                    intent = new Intent(DailyOrdersActivity.this, PaidOrderDetailActivity.class);
                } else {
                    intent = new Intent(DailyOrdersActivity.this, DailyOrdersActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",orderSearch);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else {
            Common.showToast(DailyOrdersActivity.this, R.string.searchNoneDaily);
        }
        InputMethodManager imm = (InputMethodManager)DailyOrdersActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    //未讀取訂單轉為已讀取
    private void updateIsRead(Order order) {
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(DailyOrdersActivity.this)) {
            OrderIsReadTask = new OrderIsReadTask().execute(url, order);
        } else {
            Common.showToast(DailyOrdersActivity.this, R.string.msg_NoNetwork);
        }
    }

    class OrderIsReadTask extends AsyncTask<Object, Void, List<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DailyOrdersActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<Object> doInBackground(Object... params) {
            List<Object> list = new ArrayList<>();
            readOrder = -1;
            String url = (String)params[0];
            Order order = (Order)params[1];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("param", "OrderIsRead");
            jsonObject.addProperty("ord_id", ord_id);
            try {
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return list;
            }

            Gson gson = new Gson();
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            int readOrder = Integer.parseInt(joResult.get("readOrder").getAsString());
            list.add(readOrder);
            list.add(order);
            return list;
        }

        @Override
        protected void onPostExecute(List<Object> list) {
            super.onPostExecute(list);
            Log.d(TAG, "list = " + list);
            int readOrder = (int) list.get(0);
            Order order = (Order)list.get(1);
            if( readOrder == 1 ){
                Intent intent = new Intent(DailyOrdersActivity.this,InprogressOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                bundle.putSerializable("readOrder", readOrder);
                intent.putExtras(bundle);
                startActivity(intent);
                progressDialog.cancel();
            } else if( readOrder == -1 ){
                Common.showToast(DailyOrdersActivity.this, "更新訂單為已讀取失敗");
                progressDialog.cancel();
            }
        }
    }
}
