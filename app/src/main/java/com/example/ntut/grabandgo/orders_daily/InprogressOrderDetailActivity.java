package com.example.ntut.grabandgo.orders_daily;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;

import java.util.List;

public class InprogressOrderDetailActivity extends NavigationDrawerSetup {

    private TextView tvOrderStatus, tvPickerName, tvTotalPrice, tvPhone,
            tvSoNumber, tvPicktime;
    private LinearLayout linearLayoutOrder;
    private Button btBack;
    private TableLayout tlOrderDetail;

    private Order order = null;
    private List<OrderItem> orderitemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_inprogress_detail);
        setUpToolBar();
        findViews();

        //取得從InprogressOrderFragment送來的資料
        Bundle bundle = getIntent().getExtras();
        Order order = (Order) bundle.getSerializable("order");

        setInformations(order);


    }

    private void findViews() {
        tvOrderStatus = (TextView) findViewById(R.id.tvOrderStatus);
        tvPickerName = (TextView) findViewById(R.id.tvPickerName);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvSoNumber = (TextView) findViewById(R.id.tvSoNumber);
        tvPicktime = (TextView) findViewById(R.id.tvPicktime);
        linearLayoutOrder = (LinearLayout) findViewById(R.id.linearLayoutOrder);
        btBack = (Button) findViewById(R.id.btBack);
        tlOrderDetail = (TableLayout) findViewById(R.id.tlOrderDetail);
    }

    private void setInformations(Order order) {
        tvPickerName.setText(String.valueOf(order.getM_pickupname()));
        tvTotalPrice.setText(String.valueOf(order.getOrd_totalPrice()));
        tvPhone.setText(String.valueOf(order.getOrd_tel()));
        tvSoNumber.setText(String.valueOf(order.getOrd_id()));
        tvPicktime.setText(String.valueOf(order.getOrd_pickuptime()));
    }

}
