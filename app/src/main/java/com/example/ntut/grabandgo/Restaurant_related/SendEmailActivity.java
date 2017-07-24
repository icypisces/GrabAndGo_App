package com.example.ntut.grabandgo.Restaurant_related;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class SendEmailActivity extends NavigationDrawerSetup {
    private TextView tvSendEmailTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activity_send_email);
        setUpToolBar();
        findViews();

        Bundle bundle = getIntent().getExtras();
        String from = (String) bundle.getSerializable("from");
        if (from.equals("resendEmail")) {
            tvSendEmailTitle.setText(R.string.resendEmailOK);
        }

        //有空可以加倒數計時器，然後跳到會員資料頁面．
    }

    private void findViews() {
        tvSendEmailTitle = (TextView) findViewById(R.id.tvSendEmailTitle);
    }
}
