package com.example.ntut.grabandgo.login_logout_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_intraday.UnprocessedOrderActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private String ServletName = "/AppStoreRegisterServlet";
    private final static String TAG = "RegisterActivity";
    public EditText etUsername, etPassword, etPasswordConfirm, etStoreName,
            etBranch, etAddress, etPhone, etEmail, etOwner, etWebsite;
    public Spinner spRestType;
    public Button btRegister;
    private AsyncTask RegisterTask, RestTypeTask;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();

        String url = Common.URL + ServletName ;
        //取得餐廳類別選項
        if (Common.networkConnected(RegisterActivity.this)) {
            RegisterTask = new RegisterTask().execute(url);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        etStoreName = (EditText) findViewById(R.id.etStoreName);
        etBranch = (EditText) findViewById(R.id.etBranch);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etOwner = (EditText) findViewById(R.id.etOwner);
        etWebsite = (EditText) findViewById(R.id.etWebsite);
        spRestType = (Spinner) findViewById(R.id.spRestType);
        btRegister = (Button) findViewById(R.id.btRegister);
    }



    //取得餐廳類別資訊
    class RestTypeTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();       //Json要記得
            jsonObject.addProperty("param", "category");    //將要送到伺服器的Key跟Value先放到jsonObject
            try {
                jsonIn = getRemoteData(url, jsonObject.toString()); //getRemoteData請詳Line179，得到一個json字串．
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();                             //用Gson
            Type listType = new TypeToken<List<String>>() {     //解析回List<String>
            }.getType();

            return gson.fromJson(jsonIn, listType);             //回傳json字串跟List<String>
        }

        @Override
        protected void onPostExecute(List<String> items) {      //items->從doInBackground傳來的回傳結果
            ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                    android.R.layout.simple_list_item_1, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRestType.setAdapter(adapter);
            progressDialog.cancel();
        }
    }



    public void onSubmitClick(View view) {
        String username = etUsername .getText().toString();
        String password = etPassword .getText().toString();
        String passwordConfirm = etPasswordConfirm .getText().toString();
        String storeName = etStoreName .getText().toString();
        String restType = (String)spRestType.getSelectedItem();
        String branch = etBranch .getText().toString();
        String address = etAddress .getText().toString();
        String phone = etPhone .getText().toString();
        String email = etEmail .getText().toString();
        String owner = etOwner .getText().toString();
        String website = etWebsite .getText().toString();
        String url = Common.URL + ServletName ;


        //店家地址經緯度
        Geocoder geoCoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        List<Address> addressLocation = null;
        try {
            addressLocation = geoCoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String latitude = String.valueOf(addressLocation.get(0).getLatitude());
        String longitude = String.valueOf(addressLocation.get(0).getLongitude());


        if (Common.networkConnected(RegisterActivity.this)) {
            RegisterTask = new RegisterTask().execute(url, username, password, passwordConfirm,
                    storeName, restType, branch, address, phone, email, owner, website, latitude, longitude);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
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



    class RegisterTask extends AsyncTask<String, Void, List<String>> {
        //第一個參數定doInBackground的參數資料類型
        //第二個參數定onProgressUpdate的參數資料類型
        //第三個參數定onPostExecute的參數資料類型，
        //同時也是doInBackground回傳的參數類型．

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0].toString();
            String username = params[1].toString();
            String password = params[2].toString();
            String passwordConfirm = params[3].toString();
            String storeName = params[4].toString();
            String restType = params[5].toString();
            String branch = params[6].toString();
            String address = params[7].toString();
            String phone = params[8].toString();
            String email = params[9].toString();
            String owner = params[10].toString();
            String website = params[11].toString();
            String latitude = params[12].toString();
            String longitude = params[13].toString();

            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("passwordConfirm", passwordConfirm);
            jsonObject.addProperty("storeName", storeName);
            jsonObject.addProperty("restType", restType);
            jsonObject.addProperty("branch", branch);
            jsonObject.addProperty("address", address);
            jsonObject.addProperty("phone", phone);
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("owner", owner);
            jsonObject.addProperty("website", website);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);

            try {
                jsonIn = getRemoteData(url, jsonObject.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
//            String message = joResult.get("loginMessage").getAsString();
//            List<String> s = Arrays.asList(username, password, message);

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
                Intent intent = new Intent(RegisterActivity.this, UnprocessedOrderActivity.class);
                startActivity(intent);
            } else if (message.equals("UsernameOrPasswordError")){
                Common.showToast(RegisterActivity.this, R.string.msg_UsernameOrPasswordError);
            }
        }

    }




}
