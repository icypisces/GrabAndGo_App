package com.example.ntut.grabandgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.ntut.grabandgo.login_logout_register.LoginActivity;
import com.example.ntut.grabandgo.login_logout_register.RegisterActivity;

public class MainActivity extends NavigationDrawerSetup {
    private Button login, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
 //       toolbar.setTitle();   //設置toolbar的文字
        setUpToolBar();

    }

    private void findViews() {
        login = (Button) findViewById(R.id.btBeginLogin);
        register = (Button) findViewById(R.id.btBeginRegister);
    }


    public void onLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }



}
