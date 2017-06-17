package com.example.ntut.grabandgo.login_logout_register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_intraday.UnprocessedOrderActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = "LoginActivity";
    private String ServletName = "/AppStoreLoginServlet";
    private EditText etUsername, etPassword;
    private Button btLogin, btForget;
    private CheckBox checkRememberMe;
    private AsyncTask loginTask;
    private String rmMessage;

    //Remember Me
    private static final String PREFS_NAME="NamePWD";
    private SharedPreferences sharedPreferences=null;



    //機器人驗證API
    private GoogleApiClient mGoogleApiClient;

    // ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到 Google Services
    }

    // ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
    }

    // OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services 連線失敗
        // ConnectionResult 參數是連線失敗的資訊
    }



    //Async->開新的執行緒，執行完會自動把結果傳給主執行緒．
    class loginTask extends AsyncTask<String, Void, List<String>> {
                                    //第一個參數定doInBackground的參數資料類型
                                             //第二個參數定onProgressUpdate的參數資料類型
                                                        //第三個參數定onPostExecute的參數資料類型，
                                                        //同時也是doInBackground回傳的參數類型．

        //漏斗轉圈圈
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                jsonIn = getRemoteData(url, jsonObject.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            String message = joResult.get("loginMessage").getAsString();
            List<String> s = Arrays.asList(username, password, message);

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
            String p = Common.encryptString(s.get(1));
            p = Common.getMD5Endocing(p);
            String message = s.get(2);
            Log.d(TAG, "loginMessage=" + message);
            if(message.equals("LoginOK")){
                saveUserAndPass(u, p);
                Intent intent = new Intent(LoginActivity.this, UnprocessedOrderActivity.class);
                startActivity(intent);
            } else if (message.equals("UsernameOrPasswordError")){
                Common.showToast(LoginActivity.this, R.string.msg_UsernameOrPasswordError);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        getData(); 		//Remember Me//第二次進入的時候得到數據

        //機器人驗證API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogin = (Button) findViewById(R.id.btLogin);
        btForget = (Button) findViewById(R.id.btForget);
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


    private String getRemoteData(String url, String jsonOut) throws IOException {   //建立跟Server端的連結，把資料傳給Server，再等待回傳的資料．
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();   //放置要連線的物件然後建立連線
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));   //取得資料輸出串流(純文字)產生BufferedWriter物件
        bw.write(jsonOut);                              //把資料轉到Server
        //如果要Server讀取時用requesr.getParameter方式而非Gson，要改為
        //bw.write("param=category");   //key=value才可取得對應的value
        Log.d(TAG, "jsonOut: " + jsonOut);              //建議使用TAG讓我們方便在Android Studio看輸出資料
        bw.close();

        int responseCode = connection.getResponseCode();//輸出後會回復結果代碼

        if (responseCode == 200) {  //200->Success!!
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())); //取得資料輸入串流
            String line;
            while ((line = br.readLine()) != null) {    //把取得的資料一筆一筆讀取
                jsonIn.append(line);                    //放置到jsonIn(StreamBuilder)
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();                        //都寫完後就可以解除連結
        Log.d(TAG, "jsonIn: " + jsonIn);                //再看一下輸入資料
        return jsonIn.toString();
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
            if (Common.networkConnected(LoginActivity.this)) {       //主執行緒建立了loginTask物件 //呼叫execute方法,參數為URL&username&password&rmMessage
                loginTask = new loginTask().execute(url, username, password);//excute...loginTask的doInBackGround接到
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

    public void onForgetClick(View view) {
        Intent intent = new Intent(this, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    private void saveUserAndPass(String user, String pass) {
        sharedPreferences = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("UsPaIsKeep",true);
//        edit.putString("user",user);
//        edit.putString("pass",pass);      //Password於encryptString轉換時正常，但getMD5Endocing資料不同．
    }

}
