package com.example.ntut.grabandgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ntut.grabandgo.login_logout_register.LoginActivity;
import com.example.ntut.grabandgo.login_logout_register.RegisterActivity;

public class MainActivity extends AppCompatActivity {
    private Button login, register;
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // 為navigatin_view設置點擊事件
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.item_login) {
                    // 按下「首頁」要做的事
                    Toast.makeText(MainActivity.this, "登入", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.item_register) {
                    // 按下「使用說明」要做的事
                    Toast.makeText(MainActivity.this, "註冊", Toast.LENGTH_SHORT).show();
                    return true;
                }
                // 略..

                return false;
            }
        });





        // 取得Header
        View header = navigation_view.getHeaderView(0);
        // 取得Header中的TextView
        TextView txtHeader = (TextView) header.findViewById(R.id.tvHeader);
        txtHeader.setText("123");


    }




    private void findViews() {
        login = (Button) findViewById(R.id.btBeginLogin);
        register = (Button) findViewById(R.id.btBeginRegister);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
