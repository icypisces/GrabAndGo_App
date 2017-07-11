package com.example.ntut.grabandgo.orders_daily;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InprogressOrderItemSummaryActivity extends NavigationDrawerSetup {
    private final static String TAG = "InprogressOrderItemSummaryActivity";
    private Button btBack;
    private TableLayout tlOrderDetail;

    private List<Order> orderList = null;
    private List<OrderItem> orderitemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_inprogress_item_summary);
        setUpToolBar();
        findViews();

        //取得從InprogressOrderFragment送來的資料
        Bundle bundle = getIntent().getExtras();
        orderList = (List<Order>) bundle.getSerializable("orderList");




//
//        List<String> prodIdArray = prodIdGetting(orderList);
//
//
//
//        DateFormat sdf = new SimpleDateFormat("HH:ii");
//        for (int i=0; i<orderList.size(); i++) {
//            Order order = orderList.get(i);
//            orderitemList = order.getItems();
//            for (int j=0; j<orderitemList.size(); j++) {
//                String prod_id = String.valueOf(orderitemList.get(j).getProd_id());
//                String item_name = String.valueOf(orderitemList.get(j).getItem_name());
//                String ord_id = String.valueOf(orderitemList.get(j).getOrd_id());
//                String item_amount = String.valueOf(orderitemList.get(j).getItem_amount());
//                String item_note = String.valueOf(orderitemList.get(j).getItem_note());
//                if ( item_note == null || item_note.trim().length() == 0 ) {
//                    item_note = "";
//                }
//                Timestamp ord_pickuptime = order.getOrd_pickuptime();
//                String pickuptime = sdf.format(ord_pickuptime);
//            }
//        }
    }

    private void findViews() {
        btBack = (Button) findViewById(R.id.btBack);
        tlOrderDetail = (TableLayout) findViewById(R.id.tlOrderDetail);
    }

    public List<String> prodIdGetting(List<Order> orderList){
        List<String> listProdId = new ArrayList<>();
        for (int i=0; i<orderList.size(); i++) {
            Order order = orderList.get(i);
            orderitemList = order.getItems();
            for (int j=0; j<orderitemList.size(); j++) {
                listProdId.add(String.valueOf(orderitemList.get(j).getProd_id()));
            }
        }
        //去除重複者
        for (int i=0; i<listProdId.size(); i++) {
            for (int j=(i+1); j<listProdId.size(); j++) {
                listProdId.remove(j);
            }
        }
        return listProdId;
    }

    public void onBackClick(View view) {

    }
}
