package com.example.ntut.grabandgo.login_logout_register;

import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class SendEmailActivity extends NavigationDrawerSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        setUpToolBar();

        //有空可以加倒數計時器，然後跳到會員資料頁面．

    }
}
