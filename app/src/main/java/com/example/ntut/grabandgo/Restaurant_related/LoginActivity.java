package com.example.ntut.grabandgo.Restaurant_related;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.OrderService;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.DailyOrdersActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends NavigationDrawerSetup
//        implements GoogleApiClient.ConnectionCallbacks,   //機器人驗證的方法過期，待處理
//        GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = "LoginActivity";
    private String ServletName = "/AppStoreLoginServlet";
    private EditText etUsername, etPassword;
    private Button btLogin;
    private CheckBox checkRememberMe;
    private AsyncTask LoginTask;
    private ProgressDialog progressDialog;

    //Remember Me
    private static final String PREFS_NAME="NamePWD";
    private SharedPreferences sharedPreferences=null;

    //Login
    private SharedPreferences sharedPreferencesLogin=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activity_login);
        findViews();
        getData(); 		//Remember Me//第二次進入的時候得到數據
        setUpToolBar();
    }

    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogin = (Button) findViewById(R.id.btLogin);
        checkRememberMe = (CheckBox) findViewById(R.id.checkRememberMe);
    }

    //Remember Me
    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        //判斷之前是否存儲過密碼
        if(sharedPreferences.getBoolean("isKeep",true)){
            etUsername.setText(sharedPreferences.getString("user",""));
            etPassword.setText(sharedPreferences.getString("pass",""));
            checkRememberMe.setChecked(true);
        } else {
            etUsername.setText("");
            etPassword.setText("");
            checkRememberMe.setChecked(false);
        }
    }


    public void onSubmitClick(View view) {
        String username = etUsername .getText().toString();
        String password = etPassword .getText().toString();
        String url = Common.URL + ServletName ;

        if (username == null || username.trim().length() <= 0) {
            Common.showToast(this, R.string.msg_enterUsername);
        } else if (password == null || password.trim().length() <= 0) {
            Common.showToast(this, R.string.msg_enterPassword);
        } else {
            if (Common.networkConnected(LoginActivity.this)) {       //主執行緒建立了LoginTask物件 //呼叫execute方法,參數為URL&username&password&rmMessage
                LoginTask = new LoginTask().execute(url, username, password);//excute...LoginTask的doInBackGround接到
                Log.d(TAG, "username=" + username + ", password=" + password);
            } else {
                Common.showToast(this, R.string.msg_NoNetwork);
            }
        }

        if(checkRememberMe.isChecked()){
            //如果checkbook被選中
            sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isKeep",true);
            edit.putString("user",username);
            edit.putString("pass",password);
            edit.commit();
        } else {
            sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isKeep",false);
            edit.putString("user","");
            edit.putString("pass","");
            edit.commit();
        }
    }


    //Async->開新的執行緒，執行完會自動把結果傳給主執行緒．
    class LoginTask extends AsyncTask<String, Void, List<String>> {
        //第一個參數定doInBackground的參數資料類型
        //第二個參數定onProgressUpdate的參數資料類型
        //第三個參數定onPostExecute的參數資料類型，
        //同時也是doInBackground回傳的參數類型．

        //漏斗轉圈圈
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0].toString();
            String username = params[1].toString();
            String password = params[2].toString();
            Log.d(TAG, "username=" + username + ", password=" + password);
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("password", password);

            try {
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            String message = joResult.get("loginMessage").getAsString();
            List<String> s = null;
            if(message.equals("LoginOK")){
                String rest_name = joResult.get("rest_name").getAsString();
                String rest_branch = joResult.get("rest_branch").getAsString();
                String logo = joResult.get("rest_logo").getAsString();
                String validate = joResult.get("rest_validate").getAsString();
                String rest_id = joResult.get("rest_id").getAsString();
                s = Arrays.asList(username, password, message,
                        rest_name, rest_branch, logo, validate, rest_id);
            } else if (message.equals("UsernameOrPasswordError")){
                s = Arrays.asList(username, password, message);
            }


            return s;       //回傳List<String>予onPostExecute()
        }

//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }

        //show result
        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);
            String u = s.get(0);
            String p = s.get(1);
    //        p = Common.getMD5Endocing(Common.encryptString(p));//Password於encryptString轉換時正常，但getMD5Endocing資料不同．
            String message = s.get(2);
            Log.d(TAG, "loginMessage=" + message);
            if(message.equals("LoginOK")){
                String rest_name = s.get(3);
                String rest_branch = s.get(4);
                String logo = s.get(5);
                String validate = s.get(6);
                boolean rest_validate = Boolean.parseBoolean(validate);
                String rest_id = s.get(7);
                userLogin(u, p, rest_name, rest_branch, logo, rest_validate, rest_id);
                Intent intent;
                if (rest_validate) {
                    intent = new Intent(LoginActivity.this, DailyOrdersActivity.class);
                    intent.putExtra("id", 1);
                } else {
                    intent = new Intent(LoginActivity.this, RestValidateNotYetActivity.class);
                }
                startActivity(intent);

                //重新開啟Service
                Intent serviceIntent = new Intent(LoginActivity.this, OrderService.class);
                startService(serviceIntent);

                progressDialog.cancel();
                finish();
            } else if (message.equals("UsernameOrPasswordError")){
                etUsername.setText(u);
                etPassword.setText(p);
                Common.showToast(LoginActivity.this, R.string.msg_UsernameOrPasswordError);
                progressDialog.cancel();
            }
        }
    }

    private void userLogin(String user, String pass, String rest_name,
                           String rest_branch, String logo, boolean rest_validate, String rest_id) {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferencesLogin.edit();
        edit.clear();
        edit.putBoolean("UsPaIsKeep",true);
        edit.putString("user",user);
        edit.putString("pass",pass);
        edit.putString("rest_name",rest_name);
        edit.putString("rest_branch",rest_branch);
        edit.putString("logo",logo);
        edit.putBoolean("rest_validate",rest_validate);
        edit.putString("rest_id",rest_id);
        edit.commit();
    }


    @Override
    protected void onPause() {
        if (LoginTask != null) {
            LoginTask.cancel(true);
            LoginTask = null;
        }

        super.onPause();
    }
}
