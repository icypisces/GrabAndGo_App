package com.example.ntut.grabandgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ntut.grabandgo.Restaurant_related.LoginActivity;
import com.example.ntut.grabandgo.Restaurant_related.RegisterActivity;
import com.example.ntut.grabandgo.orders_intraday.OrdersIntradayActivity;

public class MainActivity extends NavigationDrawerSetup {
    private Button login, register;

    //Login
    private SharedPreferences sharedPreferencesLogin=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
 //       toolbar.setTitle();   //設置toolbar的文字
        setUpToolBar();
        loginChangeToOrder();

    }

    private void loginChangeToOrder() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        if (sharedPreferencesLogin.getBoolean("UsPaIsKeep", false)){    //如果有取出Boolean為true
            Intent intent = new Intent(MainActivity.this, OrdersIntradayActivity.class);
            startActivity(intent);
        }
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
