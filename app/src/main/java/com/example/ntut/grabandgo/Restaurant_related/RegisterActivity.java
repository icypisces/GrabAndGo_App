package com.example.ntut.grabandgo.Restaurant_related;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreRegisterServlet";
    private final static String TAG = "RegisterActivity";
    public EditText etUsername, etPassword, etPasswordConfirm, etStoreName,
            etBranch, etAddress, etPhone, etEmail, etOwner, etWebsite;
    TextInputLayout tilUsername, tilPassword, tilPasswordConfirm, tilStoreName,
            tilBranch, tilAddress, tilPhone, tilEmail, tilOwner, tilWebsite;
    public Spinner spRestType;
    public Button btRegister;
    private AsyncTask RegisterTask, RestTypeTask;
    private ProgressDialog progressDialog;

    //Login
    private SharedPreferences sharedPreferencesLogin=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        setUpToolBar();

        String url = Common.URL + ServletName ;
        //取得餐廳類別選項
        if (Common.networkConnected(RegisterActivity.this)) {
            RestTypeTask = new RestTypeTask().execute(url);
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
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilPasswordConfirm = (TextInputLayout) findViewById(R.id.tilPasswordConfirm);
        tilStoreName = (TextInputLayout) findViewById(R.id.tilStoreName);
        tilBranch = (TextInputLayout) findViewById(R.id.tilBranch);
        tilAddress = (TextInputLayout) findViewById(R.id.tilAddress);
        tilPhone = (TextInputLayout) findViewById(R.id.tilPhone);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilOwner = (TextInputLayout) findViewById(R.id.tilOwner);
        tilWebsite = (TextInputLayout) findViewById(R.id.tilWebsite);
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
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG); //取得從伺服器回來的json字串
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
        String restType = spRestType.getSelectedItem().toString().trim();
        String branch = etBranch .getText().toString();
        String address = etAddress .getText().toString();
        String phone = etPhone .getText().toString();
        String email = etEmail .getText().toString();
        String owner = etOwner .getText().toString();
        String website = etWebsite .getText().toString();
        String url = Common.URL + ServletName ;


        //店家地址經緯度
        String latitude;
        String longitude;
        if(address == null || address.trim().length() == 0){
            latitude = "";
            longitude = "";
        } else {
            Geocoder geoCoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
            List<Address> addressLocation = null;
            try {
                addressLocation = geoCoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            latitude = String.valueOf(addressLocation.get(0).getLatitude());
            longitude = String.valueOf(addressLocation.get(0).getLongitude());
        }



        if (Common.networkConnected(RegisterActivity.this)) {
            RegisterTask = new RegisterTask().execute(url, username, password, passwordConfirm,
                    storeName, restType, branch, address, phone, email, owner, website, latitude, longitude);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }

    }


    //連接伺服器送出註冊資料及取得回應
    class RegisterTask extends AsyncTask<String, Void, List<String>> {
        //第一個參數定doInBackground的參數資料類型
        //第二個參數定onProgressUpdate的參數資料類型
        //第三個參數定onPostExecute的參數資料類型，
        //同時也是doInBackground回傳的參數類型．

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
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
            jsonObject.addProperty("param", "signUp");
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
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            String message = joResult.get("RegisterMessage").getAsString();
            List<String> s = null;
            if (message.equals("RegisterOk")) {
               s = Arrays.asList(username, password, message);
            } else if (message.equals("RegisterError")) {
                String _username = joResult.get("username").getAsString();
                String _password = joResult.get("password").getAsString();
                String _passwordConfirm = joResult.get("passwordConfirm").getAsString();
                String _storeName = joResult.get("storeName").getAsString();
                String _address = joResult.get("address").getAsString();
                String _phone = joResult.get("phone").getAsString();
                String _email = joResult.get("email").getAsString();
                String _owner = joResult.get("owner").getAsString();
                s = Arrays.asList(username, password, message, _username, _password, _passwordConfirm,
                        _storeName, _address, _phone, _email, _owner);
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
//            p = Common.getMD5Endocing(Common.encryptString(p));//Password於encryptString轉換時正常，但getMD5Endocing資料不同．
            String message = s.get(2);
            Log.d(TAG, "RegisterMessage=" + message);
            if(message.equals("RegisterOk")){
                Intent intent = new Intent(RegisterActivity.this, SendEmailActivity.class);
                startActivity(intent);
                userLogin(u, p);
            } else if(message.equals("RegisterError")){
                SetErrorMessage(s);
            }
            progressDialog.cancel();
            finish();
        }

    }

    private void userLogin(String user, String pass) {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferencesLogin.edit();
        edit.clear();
        edit.putBoolean("UsPaIsKeep",true);
        edit.putString("user",user);
        edit.putString("pass",pass);
        edit.commit();
    }

    private void SetErrorMessage(List<String> s) {
        if (!s.get(3).equals("OK")) {
            tilUsername.setErrorEnabled(true);
            tilUsername.setError(s.get(3));
        } else if (s.get(3).equals("OK")) {
            tilUsername.setError(null);
        }
        if (!s.get(4).equals("OK")) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError(s.get(4));
        } else if (s.get(4).equals("OK")) {
            tilPassword.setError(null);
        }
        if (!s.get(5).equals("OK")) {
            tilPasswordConfirm.setErrorEnabled(true);
            tilPasswordConfirm.setError(s.get(5));
        } else if (s.get(5).equals("OK")) {
            tilPasswordConfirm.setError(null);
        }
        if (!s.get(6).equals("OK")) {
            tilStoreName.setErrorEnabled(true);
            tilStoreName.setError(s.get(6));
        } else if (s.get(6).equals("OK")) {
            tilStoreName.setError(null);
        }
        if (!s.get(7).equals("OK")) {
            tilAddress.setErrorEnabled(true);
            tilAddress.setError(s.get(7));
        } else if (s.get(7).equals("OK")) {
            tilAddress.setError(null);
        }
        if (!s.get(8).equals("OK")) {
            tilPhone.setErrorEnabled(true);
            tilPhone.setError(s.get(8));
        } else if (s.get(8).equals("OK")) {
            tilPhone.setError(null);
        }
        if (!s.get(9).equals("OK")) {
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(s.get(9));
        } else if (s.get(9).equals("OK")) {
            tilEmail.setError(null);
        }
        if (!s.get(10).equals("OK")) {
            tilOwner.setErrorEnabled(true);
            tilOwner.setError(s.get(10));
        } else if (s.get(10).equals("OK")) {
            tilOwner.setError(null);
        }
    }


    @Override
    protected void onPause() {
        if (RestTypeTask != null) {
            RestTypeTask.cancel(true);
            RestTypeTask = null;
        }

        if (RegisterTask != null) {
            RegisterTask.cancel(true);
            RegisterTask = null;
        }

        super.onPause();
    }

}
