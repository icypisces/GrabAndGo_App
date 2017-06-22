package com.example.ntut.grabandgo;

import android.content.Context;
import android.content.Intent;
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

import com.example.ntut.grabandgo.login_logout_register.LoginActivity;
import com.example.ntut.grabandgo.login_logout_register.RegisterActivity;

public class NavigationDrawerSetup extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    protected NavigationView navigationView;    //其他Activity共用
    protected Toolbar toolbar;

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
    }


    public void setUpNavigation() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);          //設定被點選的Item有被點選的效果(灰色)
                drawerLayout.closeDrawers();    //強制在點選後抽屜會關掉
                switch (item.getItemId()){
                    case R.id.item_login:
                        Intent intent1 = new Intent(NavigationDrawerSetup.this,LoginActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item_register:
                        Intent intent2 = new Intent(NavigationDrawerSetup.this,RegisterActivity.class);
                        startActivity(intent2);
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
    public void setUpToolBar(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//左側
            }
        });

        //被點擊的Icon設定會轉動
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                        R.string.drawer_open,R.string.drawer_close){
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

}
