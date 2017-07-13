package com.example.ntut.grabandgo.Restaurant_related;

import android.os.Bundle;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;

public class RecoverPasswordActivity extends NavigationDrawerSetup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activity_recover_password);
        setUpToolBar();


    }
}
