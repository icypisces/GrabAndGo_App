package com.example.ntut.grabandgo.Restaurant_related;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.DailyOrdersActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RestValidateNotYetActivity extends NavigationDrawerSetup {
    private String ServletName = "/AppStoreResendEmailServlet";
    private final static String TAG = "RestValidateNotYetActivity";

    private Button reSendEmail;
    private String username, password, rest_id;
    private AsyncTask ResendEmailTask;
    private ProgressDialog progressDialog;

    //Login
    private SharedPreferences sharedPreferencesLogin=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activity_validate_not_yet);
        setUpToolBar();
        findViews();
        getLoginInformation();
    }

    private void findViews() {
        reSendEmail = (Button) findViewById(R.id.reSendEmail);
    }

    private void getLoginInformation() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        username = sharedPreferencesLogin.getString("user", "");
        password = sharedPreferencesLogin.getString("pass", "");
        rest_id = sharedPreferencesLogin.getString("rest_id", "");
    }

    public void btResendEmail(View view) {
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(RestValidateNotYetActivity.this)) {
            ResendEmailTask = new ResendEmailTask().execute(url, username, password);
            Log.d(TAG, "username=" + username + ", password=" + password);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    class ResendEmailTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RestValidateNotYetActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
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

            Gson gson = new Gson();
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            String resendEmail = joResult.get("resendEmail").getAsString();

            return resendEmail;
        }

        //show result
        @Override
        protected void onPostExecute(String resendEmail) {
            super.onPostExecute(resendEmail);
            if (resendEmail.equals("1")) {
                Intent intent = new Intent(RestValidateNotYetActivity.this, SendEmailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", "resendEmail");
                intent.putExtras(bundle);
                startActivity(intent);
                progressDialog.cancel();
                finish();
            } else if (resendEmail.equals("-1")) {
                Common.showToast(RestValidateNotYetActivity.this, R.string.sendEailFail);
            }
        }
    }
}
