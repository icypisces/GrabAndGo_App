package com.example.ntut.grabandgo.orders_intraday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class PaidOrderActivity extends NavigationDrawerSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_order);

        setUpToolBar();
    }
}