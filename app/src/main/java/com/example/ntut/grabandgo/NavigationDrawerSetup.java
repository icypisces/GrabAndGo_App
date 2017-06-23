package com.example.ntut.grabandgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.ntut.grabandgo.Restaurant_related.RestInformationActivity;
import com.example.ntut.grabandgo.Restaurant_related.LoginActivity;
import com.example.ntut.grabandgo.Restaurant_related.RegisterActivity;
import com.example.ntut.grabandgo.orders_intraday.CompletedOrderActivity;
import com.example.ntut.grabandgo.orders_intraday.PaidOrderActivity;
import com.example.ntut.grabandgo.orders_intraday.UnprocessedOrderActivity;

public class NavigationDrawerSetup extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    protected NavigationView navigationView;    //其他Activity共用
    protected Toolbar toolbar;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        findViews();
        getLayoutInflater().inflate(layoutResID,frameLayout,true);
        //參數1 - resource  //參數2 - 成為哪個view的child view
        //參數3 - 最後一個參數 attachToRoot 是 inflate 的 view 是否要 attach 到 root view，
        // 這會影響到回傳的是哪個 view。假如 attachToRoot 為 true，
        // 則最後回傳的 view 為 root view (就是第二個參數的 view)；反之就是回傳 inflate 的 view。
        super.setContentView(drawerLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpNavigation();
        setDrawerMenu();
    }

    private void setDrawerMenu() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        if (sharedPreferencesLogin.getBoolean("UsPaIsKeep", false)){    //如果有取出Boolean為true
            navigationView.getMenu().setGroupVisible(R.id.group_sign,false);
            navigationView.getMenu().setGroupVisible(R.id.group_profile,true);
            navigationView.getMenu().setGroupVisible(R.id.group_intradayOrders,true);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_sign,true);
            navigationView.getMenu().setGroupVisible(R.id.group_profile,false);
            navigationView.getMenu().setGroupVisible(R.id.group_intradayOrders,false);
        }
        //增加判斷是否已驗證後，還要再分出登入/註冊，但是未驗證者...只能看profile．
    }


    public void setUpNavigation() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                item.setChecked(true);          //設定被點選的Item有被點選的效果(灰色)
                drawerLayout.closeDrawers();    //強制在點選後抽屜會關掉
                Intent intent;
                switch (item.getItemId()){
                    case R.id.item_login:
                        intent = new Intent(NavigationDrawerSetup.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_register:
                        intent = new Intent(NavigationDrawerSetup.this,RegisterActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_profile:
                        intent = new Intent(NavigationDrawerSetup.this,RestInformationActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_logout:
                        userLogout();
                        intent = new Intent(NavigationDrawerSetup.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_unprocessed:
                        intent = new Intent(NavigationDrawerSetup.this,UnprocessedOrderActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_completed:
                        intent = new Intent(NavigationDrawerSetup.this,CompletedOrderActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_paid:
                        intent = new Intent(NavigationDrawerSetup.this,PaidOrderActivity.class);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

    }


    private void findViews() {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.navigation_drawer_setup, null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content_frame);
        navigationView = (NavigationView) drawerLayout.findViewById(R.id.navigation_view);

    }


    //用以設置toolbar
    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//左側
            }
        });

        //被點擊的Icon設定會轉動
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                        R.string.drawer_open, R.string.drawer_close) {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }
                };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();  //讓ActionBar 中的返回箭號置換成Drawer 的三條線圖示
    }

    private void userLogout() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferencesLogin.edit();
        edit.clear();
        edit.commit();
    }

}
