package com.example.ntut.grabandgo.HistoryOrders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryOrdersActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreOrderHistoryServlet";
    private final static String TAG = "HistoryOrdersActivity";
    private RecyclerView rvHistoryOrders;
    private Spinner spYearSelect, spMonthSelect;
    private EditText etSearch;
    private Button btSelectMonth;
    private int today_year, today_month;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;
    private String rest_id;

    List<Order> orderList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity_orders);
        setUpToolBar();
        findView();
        setSpinners();
        getRestaurantName();
        getOrderDataFromServlet();

        rvHistoryOrders.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rvHistoryOrders.setAdapter(new OrderAdapter(this, orderList));
    }

    private void findView() {
        rvHistoryOrders= (RecyclerView) findViewById(R.id.rvHistoryOrders);
        spYearSelect = (Spinner) findViewById(R.id.spYearSelect);
        spMonthSelect = (Spinner) findViewById(R.id.spMonthSelect);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btSelectMonth = (Button) findViewById(R.id.btSelectMonth);
    }

//------------------------------------------設定Spinner----------------------------------------------

    private void setSpinners() {
        // 設定初始日期 - 當天
        final Calendar today = Calendar.getInstance();
        today_year = today.get(Calendar.YEAR);
        today_month = today.get(Calendar.MONTH);

        final int yearMin = 2000;
        List<String> yearSelect = new ArrayList<>();
        for (int i=today_year; i>=yearMin; i--) {
            yearSelect.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, yearSelect);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearSelect.setAdapter(adapterYear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spYearSelect.setDropDownVerticalOffset(10);
        }

        final int monthMin = 1;
        final int monthMax = 12;
        List<String> monthSelect = new ArrayList<>();
        for (int i=monthMin; i<=monthMax; i++) {
            monthSelect.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, monthSelect);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonthSelect.setAdapter(adapterMonth);
        spMonthSelect.setSelection(today_month);
    }

//-------------------------------連線至伺服器取得HistoryOrder資料--------------------------------------

    private void getRestaurantName() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        rest_id = sharedPreferencesLogin.getString("rest_id", "");
    }

    private void getOrderDataFromServlet() {
        String url = Common.URL + ServletName ;
        String selectMonth = spYearSelect.getSelectedItem().toString().trim() + "-"
                + spMonthSelect.getSelectedItem().toString().trim();
        String customer = etSearch.getText().toString();
        //取得歷史訂單
        if (Common.networkConnected(HistoryOrdersActivity.this)) {
            try {
                orderList = new HistoryOrderGetTask().execute(url, rest_id, selectMonth, customer).get();
                Log.e(TAG, orderList.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    public void onSearchClick(View view) {
        getOrderDataFromServlet();
        rvHistoryOrders.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        rvHistoryOrders.setAdapter(new OrderAdapter(this, orderList));
    }

//-------------------------------------RecyclerView.Adapter-----------------------------------------

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
        private Context context;
        private List<Order> orderList;

        public OrderAdapter(Context context, List<Order> orderList) {
            this.context = context;
            this.orderList = orderList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrdertime, tvSoNumber, tvBuyerName, tvBuyerPhone, tvAmount, tvPicktime;

            MyViewHolder(View itemView) {
                super(itemView);
                tvOrdertime = (TextView) itemView.findViewById((R.id.tvOrdertime));
                tvSoNumber = (TextView) itemView.findViewById((R.id.tvSoNumber));
                tvBuyerName = (TextView) itemView.findViewById((R.id.tvBuyerName));
                tvBuyerPhone = (TextView) itemView.findViewById((R.id.tvBuyerPhone));
                tvAmount = (TextView) itemView.findViewById((R.id.tvAmount));
                tvPicktime = (TextView) itemView.findViewById((R.id.tvPicktime));
            }
        }

        @Override
        public int getItemCount() {
            return orderList.size();    //通知系統要傳幾個
        }

        @Override   //建立View    //RecyclerView.Adapter的onCreateViewHolder+onBindViewHolder -> BaseAdapter的getView
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {    //已指定泛型，如果沒有指定泛型就要使用RecyclerView.Adapter
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.history_item_view_orders, parent, false);
            return new MyViewHolder(itemView);  //呼叫MyviewGolder的建構子來接參數
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Order order = orderList.get(position);
            holder.tvOrdertime.setText(String.valueOf(order.getOrd_time()));
            holder.tvSoNumber.setText(String.valueOf(order.getOrd_id()));
            holder.tvBuyerName.setText(String.valueOf(order.getM_pickupname()));
            holder.tvBuyerPhone.setText(String.valueOf(order.getOrd_tel()));
            holder.tvAmount.setText(String.valueOf(order.getOrd_totalPrice()));
            holder.tvPicktime.setText(String.valueOf(order.getOrd_pickuptime()));
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
        }

    }
}
