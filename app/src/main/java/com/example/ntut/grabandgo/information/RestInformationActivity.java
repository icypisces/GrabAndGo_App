package com.example.ntut.grabandgo.information;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class RestInformationActivity extends NavigationDrawerSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_information);

        setUpToolBar();
    }
}
