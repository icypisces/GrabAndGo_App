package com.example.ntut.grabandgo.Restaurant_related;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RestInformationActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreProfileServlet";
    private final static String TAG = "RestInformationActivity";
    private ImageView ivRestLogo;
    private EditText etRestName, etRestType, etBranch,
            etOwner, etAddress, etPhone, etEmail, etUrl,
            etUsername, etPassword, etNewPassword, etNewPasswordConfirm;
    private TextInputLayout tilPassword, tilNewPassword, tilNewPasswordConfirm, tilAddress, tilPhone, tilEmail, tilUrl;
    private String username, password;
    private AsyncTask RestProfileGetTask, RestProfileUpdateTask;
    private ProgressDialog progressDialog;
    private LinearLayout linearLayout_userpass, linearLayout_newPassword, linearLayout_editButton;
    private Button btEditBegin, btConfirmPass, btEditSave;
    private int countPasswordError = 0;

    //Login
    private SharedPreferences sharedPreferencesLogin=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activity_rest_information);
        findViews();
        setUpToolBar();
        getLoginInformation();

        //取得餐廳資訊
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(RestInformationActivity.this)) {
            RestProfileGetTask = new RestInformationActivity.RestProfileGetTask().execute(url);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    private void findViews() {
        ivRestLogo = (ImageView) findViewById(R.id.ivRestLogo);
        etRestName = (EditText) findViewById(R.id.etRestName);
        etRestType = (EditText) findViewById(R.id.etRestType);
        etBranch = (EditText) findViewById(R.id.etBranch);
        etOwner = (EditText) findViewById(R.id.etOwner);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUrl = (EditText) findViewById(R.id.etUrl);

        btEditBegin = (Button) findViewById(R.id.btEditBegin);
        btEditSave = (Button) findViewById(R.id.btEditSave);
        linearLayout_userpass = (LinearLayout) findViewById(R.id.linearLayout_userpass);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btConfirmPass = (Button) findViewById(R.id.btConfirmPass);
        linearLayout_newPassword = (LinearLayout) findViewById(R.id.linearLayout_newPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etNewPasswordConfirm = (EditText) findViewById(R.id.etNewPasswordConfirm);
        linearLayout_editButton = (LinearLayout) findViewById(R.id.linearLayout_editButton);

        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilNewPassword = (TextInputLayout) findViewById(R.id.tilNewPassword);
        tilNewPasswordConfirm = (TextInputLayout) findViewById(R.id.tilNewPasswordConfirm);
        tilAddress = (TextInputLayout) findViewById(R.id.tilAddress);
        tilPhone = (TextInputLayout) findViewById(R.id.tilPhone);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilUrl = (TextInputLayout) findViewById(R.id.tilUrl);
    }

    private void getLoginInformation() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        username = sharedPreferencesLogin.getString("user", "");
        password = sharedPreferencesLogin.getString("pass", "");
    }

    //取得餐廳資訊
    class RestProfileGetTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RestInformationActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();       //Json要記得
            jsonObject.addProperty("param", "getProfile");    //將要送到伺服器的Key跟Value先放到jsonObject
            jsonObject.addProperty("username", username);
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
            List<String> s = null;
            if (message.equals("LoginInformationOK")) {
                String rest_name = joResult.get("rest_name").getAsString();
                String rest_type = joResult.get("rest_type").getAsString();
                String rest_branch = "";
                if (joResult.get("rest_branch") == null) {
                    rest_branch = "";
                } else {
                    rest_branch = joResult.get("rest_branch").getAsString();
                }
                String rest_owner = joResult.get("rest_owner").getAsString();
                String rest_address = joResult.get("rest_address").getAsString();
                String rest_phone = joResult.get("rest_phone").getAsString();
                String rest_email = joResult.get("rest_email").getAsString();
                String rest_url = "";
                if (joResult.get("rest_url") == null) {
                    rest_url = "";
                } else {
                    rest_url = joResult.get("rest_url").getAsString();
                }
                String logo = joResult.get("rest_logo").getAsString();

                s = Arrays.asList(message, rest_name, rest_type, rest_branch,
                        rest_owner, rest_address, rest_phone, rest_email, rest_url, logo);
            } else if (message.equals("LoginInformationError")) {
                s = Arrays.asList(message);
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
            String message = s.get(0);
            Log.d(TAG, "loginCheckMessage=" + message);
            if(message.equals("LoginInformationOK")){
                String rest_name = s.get(1);
                String rest_type = s.get(2);
                String rest_branch = s.get(3);
                String rest_owner = s.get(4);
                String rest_address = s.get(5);
                String rest_phone = s.get(6);
                String rest_email = s.get(7);
                String rest_url = s.get(8);
                String logo = s.get(9);
                setTextViewInformation(rest_name, rest_type, rest_branch, rest_owner,
                        rest_address, rest_phone, rest_email, rest_url, logo);
                progressDialog.cancel();
            } else if(message.equals("LoginInformationError")){
                Common.showToast(RestInformationActivity.this, "帳號密碼有誤，請重新登入．");
                Intent intent = new Intent(RestInformationActivity.this, LoginActivity.class);
                startActivity(intent);
                progressDialog.cancel();
                finish();
            }
        }
    }

    private void setTextViewInformation(String rest_name, String rest_type, String rest_branch,
                                        String rest_owner, String rest_address,
                                        String rest_phone, String rest_email, String rest_url,
                                        String logo) {
        etRestName.setText(rest_name);
        etRestType.setText(rest_type);
        etBranch.setText(rest_branch);
        etOwner.setText(rest_owner);
        etAddress.setText(rest_address);
        etPhone.setText(rest_phone);
        etEmail.setText(rest_email);
        etUrl.setText(rest_url);

        byte[] decodedString = Base64.decode(logo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivRestLogo.setImageBitmap(decodedByte);
    }


//----------------------------------------會員資料編輯相關----------------------------------------


    public void onProfileEditBegin(View view) {
        btEditBegin.setVisibility(View.GONE);
        btConfirmPass.setVisibility(View.VISIBLE);
        linearLayout_userpass.setVisibility(View.VISIBLE);
        etUsername.setText(username);
        etRestName.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etRestType.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etBranch.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etOwner.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etAddress.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etPhone.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etEmail.setBackgroundResource(R.drawable.edit_text_information_can_not);
        etUrl.setBackgroundResource(R.drawable.edit_text_information_can_not);
    }

    //確認密碼是否正確 - 開啟頁面抓取會員資料時已確認帳號密碼，故此處不再與Servlet確認．
    public void onConfirmPass(View view) {
        if (password.equals(etPassword.getText().toString())) {
            btConfirmPass.setVisibility(View.GONE);
            linearLayout_newPassword.setVisibility(View.VISIBLE);
            linearLayout_editButton.setVisibility(View.VISIBLE);
            etAddress.setBackgroundResource(R.drawable.edit_text_information);//可修改欄位轉換顏色
            etPhone.setBackgroundResource(R.drawable.edit_text_information);
            etEmail.setBackgroundResource(R.drawable.edit_text_information);
            etUrl.setBackgroundResource(R.drawable.edit_text_information);
            etAddress.setFocusableInTouchMode(true);    //可修改欄位設置為可修改狀態
            etPhone.setFocusableInTouchMode(true);
            etEmail.setFocusableInTouchMode(true);
            etUrl.setFocusableInTouchMode(true);
        } else {
            if (countPasswordError <= 3) {
                Common.showToast(this, "密碼錯誤，請重新輸入．");
                countPasswordError++;
            } else {
                Common.showToast(this, "密碼錯誤3次，請重新登入．");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public void onProfileEditCancel(View view) {
        Intent intent = new Intent(RestInformationActivity.this, RestInformationActivity.class);
        startActivity(intent);
    }


    public void onProfileEditSave(View view) {
        String newPassword = etNewPassword .getText().toString();
        String newPasswordConfirm = etNewPasswordConfirm .getText().toString();
        String address = etAddress .getText().toString();
        String phone = etPhone .getText().toString();
        String email = etEmail .getText().toString();
        String website = etUrl .getText().toString();

        //店家地址經緯度
        String latitude;
        String longitude;
        if(address == null || address.trim().length() == 0){
            latitude = "";
            longitude = "";
        } else {
            Geocoder geoCoder = new Geocoder(RestInformationActivity.this, Locale.getDefault());
            List<Address> addressLocation = null;
            try {
                addressLocation = geoCoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            latitude = String.valueOf(addressLocation.get(0).getLatitude());
            longitude = String.valueOf(addressLocation.get(0).getLongitude());
        }

        //更新會員資料
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(RestInformationActivity.this)) {
            RestProfileUpdateTask = new RestInformationActivity.RestProfileUpdateTask().
                    execute(url, newPassword, newPasswordConfirm, address, phone, email,
                            website, latitude, longitude);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }

    }

    //連接伺服器送出會員資料更新及取得回應
    class RestProfileUpdateTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RestInformationActivity.this);   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0].toString();
            String newPassword = params[1].toString();
            String newPasswordConfirm = params[2].toString();
            String address = params[3].toString();
            String phone = params[4].toString();
            String email = params[5].toString();
            String website = params[6].toString();
            String latitude = params[7].toString();
            String longitude = params[8].toString();

            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("param", "updateProfile");
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("newPassword", newPassword);
            jsonObject.addProperty("newPasswordConfirm", newPasswordConfirm);
            jsonObject.addProperty("address", address);
            jsonObject.addProperty("phone", phone);
            jsonObject.addProperty("email", email);
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
            List<String> s = null;
            String loginCheckMessage = joResult.get("loginCheckMessage").getAsString();
            Log.e(TAG, "loginCheckMessage = " + loginCheckMessage);
            if (loginCheckMessage.equals("LoginInformationError")) {
                String passwordError = joResult.get("passwordError").getAsString();
                s = Arrays.asList(loginCheckMessage, passwordError);
            } else if (loginCheckMessage.equals("LoginInformationOK")) {
                String UpdateProfileMessage = joResult.get("UpdateProfileMessage").getAsString();
                if (UpdateProfileMessage.equals("UpdateProfileOk")) {
                    String rest_name = joResult.get("rest_name").getAsString();
                    String rest_branch = "";
                    if (joResult.get("rest_branch") == null) {
                        rest_branch = "";
                    } else {
                        rest_branch = joResult.get("rest_branch").getAsString();
                    }
                    String logo = joResult.get("rest_logo").getAsString();
                    String validate = joResult.get("rest_validate").getAsString();
                    String rest_id = joResult.get("rest_id").getAsString();
                    if (newPassword != null && newPassword.trim().length() != 0) {
                        password = newPassword ;
                    }
                    s = Arrays.asList(loginCheckMessage, UpdateProfileMessage, username,
                            password, rest_name, rest_branch, logo, validate, rest_id);
                } else if (UpdateProfileMessage.equals("UpdateProfileError")) {
                    String _newPassword = joResult.get("newPassword").getAsString();
                    String _newPasswordConfirm = joResult.get("newPasswordConfirm").getAsString();
                    String _address = joResult.get("address").getAsString();
                    String _phone = joResult.get("phone").getAsString();
                    String _email = joResult.get("email").getAsString();
                    s = Arrays.asList(loginCheckMessage, UpdateProfileMessage, _newPassword,
                            _newPasswordConfirm, _address, _phone, _email);
                }
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
            String loginCheckMessage = s.get(0);
            Log.d(TAG, "loginCheckMessage=" + loginCheckMessage);
            if (loginCheckMessage.equals("LoginInformationError")) {
//                tilPassword.setErrorEnabled(true);
                tilPassword.setError(s.get(1));
                Common.showToast(RestInformationActivity.this, "密碼有誤，請重新輸入密碼．");
                progressDialog.cancel();
            } else if (loginCheckMessage.equals("LoginInformationOK")) {
                String UpdateProfileMessage = s.get(1);
                if (UpdateProfileMessage.equals("UpdateProfileOk")) {
                    String u = s.get(2);
                    String p = s.get(3);
                    String rest_name = s.get(4);
                    String rest_branch = s.get(5);
                    String logo = s.get(6);
                    String validate = s.get(7);
                    boolean rest_validate = Boolean.parseBoolean(validate);
                    String rest_id = s.get(8);
                    userLogin(u, p, rest_name, rest_branch, logo, rest_validate, rest_id);
                    Intent intent = new Intent(
                            RestInformationActivity.this, RestInformationActivity.class);
                    startActivity(intent);
                    progressDialog.cancel();
                    finish();
                } else if (UpdateProfileMessage.equals("UpdateProfileError")) {
                    SetErrorMessage(s);
                    progressDialog.cancel();
                }
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

    private void SetErrorMessage(List<String> s) {
        if (!s.get(2).equals("OK")) {
//            tilNewPassword.setErrorEnabled(true);
            tilNewPassword.setError(s.get(2));
        } else if (s.get(2).equals("OK")) {
            tilNewPassword.setError(null);
        }
        if (!s.get(3).equals("OK")) {
//            tilNewPasswordConfirm.setErrorEnabled(true);
            tilNewPasswordConfirm.setError(s.get(3));
        } else if (s.get(3).equals("OK")) {
            tilNewPasswordConfirm.setError(null);
        }
        if (!s.get(4).equals("OK")) {
//            tilAddress.setErrorEnabled(true);
            tilAddress.setError(s.get(4));
        } else if (s.get(4).equals("OK")) {
            tilAddress.setError(null);
        }
        if (!s.get(5).equals("OK")) {
//            tilPhone.setErrorEnabled(true);
            tilPhone.setError(s.get(5));
        } else if (s.get(5).equals("OK")) {
            tilPhone.setError(null);
        }
        if (!s.get(6).equals("OK")) {
//            tilEmail.setErrorEnabled(true);
            tilEmail.setError(s.get(6));
        } else if (s.get(6).equals("OK")) {
            tilEmail.setError(null);
        }
    }

}
