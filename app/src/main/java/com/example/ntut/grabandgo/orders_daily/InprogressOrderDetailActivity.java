package com.example.ntut.grabandgo.orders_daily;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;

import java.util.List;

public class InprogressOrderDetailActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreOrderChange";
    private final static String TAG = "InprogressOrderDetailActivity";

    private TextView tvOrderStatus, tvPickerName, tvTotalPrice, tvPhone,
            tvSoNumber, tvPicktime;
    private LinearLayout linearLayoutOrder;
    private Button btBack, btCancel, btComplete;
    private TableLayout tlOrderDetail;

    private List<OrderItem> orderitemList = null;

    private String changeResult, url, ordId, param;
    private int readOrder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_inprogress_detail);
        setUpToolBar();
        findViews();

        //取得從InprogressOrderFragment送來的資料
        Bundle bundle = getIntent().getExtras();
        Order order = (Order) bundle.getSerializable("order");
        readOrder = (int) bundle.getSerializable("readOrder");

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
        btCancel = (Button) findViewById(R.id.btCancel);
        btComplete = (Button) findViewById(R.id.btComplete);
    }

    private void setInformations(Order order) {
        tvPickerName.setText(String.valueOf(order.getM_pickupname()));
        tvTotalPrice.setText(String.valueOf(order.getOrd_totalPrice()));
        tvPhone.setText(String.valueOf(order.getOrd_tel()));
        tvSoNumber.setText(String.valueOf(order.getOrd_id()));
        tvPicktime.setText(String.valueOf(order.getOrd_pickuptime()));
//        String orderStatus = "";
//        if ( order.getIsRead() == 0 ) {
//            linearLayoutOrder.setBackgroundResource(R.drawable.button_pink);
//            orderStatus = getResources().getString(R.string.notRead);
//        } else {
            tvOrderStatus.setHeight(0);
//        }
//        tvOrderStatus.setText(String.valueOf(orderStatus));

        orderitemList = order.getItems();
        int count = 0;
        int totalPrice = 0;
        for (int i = 0; i < orderitemList.size(); i++) {
            if (i > 0) {
                View line = new View(this);
                line.setBackgroundColor(Color.rgb(193, 193, 193));
                tlOrderDetail.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            }
            TableRow tableRow = new TableRow(this);
            String item_note = "";
            if (orderitemList.get(i).getItem_note() == null || orderitemList.get(i).getItem_note().equals("null")) {
                item_note = "";
            } else {
                item_note = orderitemList.get(i).getItem_note();
            }
            String[] textViews = {
                    String.valueOf(orderitemList.get(i).getProd_id()),
                    orderitemList.get(i).getItem_name(),
                    String.valueOf(orderitemList.get(i).getItem_price()),
                    String.valueOf(orderitemList.get(i).getItem_amount()),
                    String.valueOf(orderitemList.get(i).getItem_price()*orderitemList.get(i).getItem_amount()),
                    item_note
            };
            for (int j = 0; j < textViews.length; j++) {
                TextView textView = new TextView(this);
                textView.setText(textViews[j]);
                textView.setPadding(10, 0, 10, 0);
                textView.setMaxWidth(70);
                textView.setSingleLine(false);
                tableRow.addView(textView, j);  //j是編號
            }
            tlOrderDetail.addView(tableRow);
            count += orderitemList.get(i).getItem_amount();
            totalPrice += (orderitemList.get(i).getItem_price()*orderitemList.get(i).getItem_amount());
        }
        //分隔線
        View line = new View(this);
        line.setBackgroundColor(Color.rgb(107, 106, 106));
        tlOrderDetail.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));

        TableRow tableRowLast = new TableRow(this);
        String[] textViewsTotal = {getResources().getString(R.string.total), "", "", String.valueOf(count), String.valueOf(totalPrice), ""};
        for (int j = 0; j < textViewsTotal.length; j++) {
            TextView textView = new TextView(this);
            textView.setText(textViewsTotal[j]);
            textView.setPadding(10, 0, 10, 0);
            textView.setMaxWidth(75);
            textView.setSingleLine(false);
            tableRowLast.addView(textView, j);  //j是編號
        }
        tlOrderDetail.addView(tableRowLast);

        url = Common.URL + ServletName;
        ordId = tvSoNumber.getText().toString();
    }

    public void onBackClick(View view) {
        if ( readOrder == 1 ) {
            Intent intent = new Intent(InprogressOrderDetailActivity.this,DailyOrdersActivity.class);
            intent.putExtra("id", 1);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    public void onCancelClick(View view) {
        if (Common.networkConnected(this)) {
            param = "Cancel";
            try {
                changeResult = new DailyOrderChangeTask().execute(url, ordId, param).get();
                Log.e(TAG, changeResult.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        if ("ok".equals(changeResult)) {
            Common.showToast(this,R.string.orderCancelOK);
        } else if ("fail".equals(changeResult)) {
            Common.showToast(this,R.string.orderCancelFail);
        }
        Intent intent = new Intent(InprogressOrderDetailActivity.this,DailyOrdersActivity.class);
        intent.putExtra("id", 1);
        startActivity(intent);
        finish();
    }

    public void onCompleteClick(View view) {
        if (Common.networkConnected(this)) {
            param = "toComplete";
            try {
                changeResult = new DailyOrderChangeTask().execute(url, ordId, param).get();
                Log.e(TAG, changeResult.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        if ("ok".equals(changeResult)) {
            Common.showToast(this,R.string.orderTurnToCompleteOK);
        } else if ("fail".equals(changeResult)) {
            Common.showToast(this,R.string.orderStatusChangeFail);
        }
        Intent intent = new Intent(InprogressOrderDetailActivity.this,DailyOrdersActivity.class);
        intent.putExtra("id", 1);
        startActivity(intent);
        finish();
    }
}
