package com.example.ntut.grabandgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ntut.grabandgo.Restaurant_related.RestInformationActivity;
import com.example.ntut.grabandgo.Restaurant_related.LoginActivity;
import com.example.ntut.grabandgo.Restaurant_related.RegisterActivity;
import com.example.ntut.grabandgo.orders_intraday.CompletedOrderFragment;
import com.example.ntut.grabandgo.orders_intraday.OrdersDailyActivity;
import com.example.ntut.grabandgo.orders_intraday.PaidOrderFragment;
import com.example.ntut.grabandgo.orders_intraday.UnprocessedOrderFragment;

public class NavigationDrawerSetup extends AppCompatActivity {
    private final static String TAG = "NavigationDrawerSetup";
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private View navigationHeader;
    private ImageView ivHeader;
    private TextView tvHeaderTitle, tvHeaderSubtitle;
    //其他Activity共用
    protected NavigationView navigationView;
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
        setDrawerHeader();
    }

    private void findViews() {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.navigation_drawer_setup, null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content_frame);
        navigationView = (NavigationView) drawerLayout.findViewById(R.id.navigation_view);
        navigationHeader = navigationView.getHeaderView(0);
        ivHeader = (ImageView) navigationHeader.findViewById(R.id.ivHeader);
        tvHeaderTitle = (TextView) navigationHeader.findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = (TextView) navigationHeader.findViewById(R.id.tvHeaderSubtitle);
    }

    private void setDrawerMenu() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        if (sharedPreferencesLogin.getBoolean("UsPaIsKeep", false)){            //如果已登入Boolean為true
            if (sharedPreferencesLogin.getBoolean("rest_validate", false)) {        //已登入且已驗證
                navigationView.getMenu().setGroupVisible(R.id.group_sign,false);    //顯示除了登入註冊以外選項
                navigationView.getMenu().setGroupVisible(R.id.group_profile,true);
                navigationView.getMenu().setGroupVisible(R.id.group_intradayOrders,true);
            } else {                                                                //已登入但未驗證
                navigationView.getMenu().setGroupVisible(R.id.group_sign,false);
                navigationView.getMenu().setGroupVisible(R.id.group_profile,true);  //僅顯示會員資料相關選項
                navigationView.getMenu().setGroupVisible(R.id.group_intradayOrders,false);
                Common.showToast(this, R.string.notBeenValidate);
            }
        } else {                                                                //如果未登入
            navigationView.getMenu().setGroupVisible(R.id.group_sign,true);         //僅顯示登入註冊選項
            navigationView.getMenu().setGroupVisible(R.id.group_profile,false);
            navigationView.getMenu().setGroupVisible(R.id.group_intradayOrders,false);
        }
        //增加判斷是否已驗證後，還要再分出登入/註冊，但是未驗證者...只能看profile．
    }

    private void setDrawerHeader() {
        String rest_name_def = (String) tvHeaderTitle.getText();
        String rest_name = sharedPreferencesLogin.getString("rest_name", rest_name_def);
        Log.d(TAG, "rest_name=" + rest_name);
        tvHeaderTitle.setText(rest_name);

        String rest_branch_def = (String) tvHeaderSubtitle.getText();
        String rest_branch = sharedPreferencesLogin.getString("rest_branch", rest_branch_def);
        if(rest_branch == null || rest_branch.trim().length() ==0){
            rest_branch = "";
        } else if (rest_branch.equals("null")) {
            rest_branch = "";
        }
        Log.d(TAG, "rest_branch=" + rest_branch);
        tvHeaderSubtitle.setText(rest_branch);

        String logo = sharedPreferencesLogin.getString("logo", "");
        Log.d(TAG, "logo=" + logo);
        if(logo != null && logo.trim().length() !=0){
            byte[] decodedString = Base64.decode(logo, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivHeader.setMaxWidth(100);
            ivHeader.setImageBitmap(decodedByte);
        }
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
                        intent = new Intent(NavigationDrawerSetup.this,OrdersDailyActivity.class);
                        intent.putExtra("id", 1);
                        startActivity(intent);
                        break;
                    case R.id.item_completed:
                        intent = new Intent(NavigationDrawerSetup.this,OrdersDailyActivity.class);
                        intent.putExtra("id", 2);
                        startActivity(intent);
                        break;
                    case R.id.item_paid:
                        intent = new Intent(NavigationDrawerSetup.this,OrdersDailyActivity.class);
                        intent.putExtra("id", 3);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

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
