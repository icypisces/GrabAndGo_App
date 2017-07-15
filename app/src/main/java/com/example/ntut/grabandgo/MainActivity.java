package com.example.ntut.grabandgo;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ntut.grabandgo.Restaurant_related.LoginActivity;
import com.example.ntut.grabandgo.Restaurant_related.RegisterActivity;
import com.example.ntut.grabandgo.Restaurant_related.RestValidateNotYetActivity;
import com.example.ntut.grabandgo.orders_daily.DailyOrdersActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class MainActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppIdCheckServlet";
    private final static String TAG = "MainActivity";
    private Button login, register;
    private AsyncTask IdCheckTask;
    private ProgressDialog progressDialog;
    private String username, password;
    private Boolean rest_validate;

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

    private void loginChangeToOrder() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        if (sharedPreferencesLogin.getBoolean("UsPaIsKeep", false)){    //如果有取出Boolean為true
            username = sharedPreferencesLogin.getString("user", "");
            password = sharedPreferencesLogin.getString("pass", "");
            rest_validate = sharedPreferencesLogin.getBoolean("rest_validate", false);

            //確認已紀錄的登入資訊是否正確
            String url = Common.URL + ServletName ;
            if (Common.networkConnected(MainActivity.this)) {
                IdCheckTask = new IdCheckTask().execute(url);
            } else {
                Common.showToast(this, R.string.msg_NoNetwork);
            }
        }
    }

    //確認已紀錄的登入資訊是否正確
    class IdCheckTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();       //Json要記得
            jsonObject.addProperty("username", username);   //將要送到伺服器的Key跟Value先放到jsonObject
            jsonObject.addProperty("password", password);
            try {
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG); //取得從伺服器回來的json字串
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            String message = joResult.get("loginCheckMessage").getAsString();

            return message;       //回傳String予onPostExecute()
        }

//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }

        //show result
        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            Log.d(TAG, "loginCheckMessage=" + message);
            if(message.equals("LoginInformationOK")){
                Intent intent;
                if (rest_validate) {
                    intent = new Intent(MainActivity.this, DailyOrdersActivity.class);
                    intent.putExtra("id", 1);
                } else {
                    intent = new Intent(MainActivity.this, RestValidateNotYetActivity.class);
                }
                startActivity(intent);
                progressDialog.cancel();
                finish();
            } else if(message.equals("LoginInformationError")){
                Common.showToast(MainActivity.this, "帳號密碼有誤，請重新登入．");
                progressDialog.cancel();
            }
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }
}
