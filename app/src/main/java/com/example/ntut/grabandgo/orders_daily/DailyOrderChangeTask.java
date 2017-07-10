package com.example.ntut.grabandgo.orders_daily;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

//今日訂單的相關修改
class DailyOrderChangeTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "DailyOrderChangeTask";

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String ord_id = params[1];
        String param = params[2];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("param", param);
        jsonObject.addProperty("ord_id", ord_id);
        try {
            jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }

        Gson gson = new Gson();
        JsonObject joResult = gson.fromJson(jsonIn.toString(),
                JsonObject.class);
        String changeResult = joResult.get("changeResult").getAsString();

        return changeResult;
    }
}
