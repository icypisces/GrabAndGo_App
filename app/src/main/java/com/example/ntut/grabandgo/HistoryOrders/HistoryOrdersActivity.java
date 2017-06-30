package com.example.ntut.grabandgo.HistoryOrders;

import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class HistoryOrdersActivity extends NavigationDrawerSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);
        setUpToolBar();
    }
}
