package com.example.ntut.grabandgo.HistoryOrders;

import android.os.Bundle;
import android.widget.ListView;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class HistoryOrdersActivity extends NavigationDrawerSetup {
    private ListView lvHistoryOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity_orders);
        setUpToolBar();
        findView();
    }

    private void findView() {
        lvHistoryOrders= (ListView) findViewById(R.id.lvHistoryOrders);
    }
}
