package com.example.ntut.grabandgo.Financial_Analysis;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

//取得訂單收入相關資訊
class RevenueGetTask extends AsyncTask<String, Void, List<OrderItem>> {
    private static final String TAG = "RevenueGetTask";

    @Override
    protected List<OrderItem> doInBackground(String... params) {
        String url = params[0];
        String rest_name = params[1];
        String interval = params[2];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("param", "getOrderData");
        jsonObject.addProperty("rest_name", rest_name);
        jsonObject.addProperty("interval", interval);
        try {
            jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }

        Gson gson = new Gson();
        Type list = new TypeToken<List<OrderItem>>() {
        }.getType();
        Log.e(TAG, "list = " + list);

        return gson.fromJson(jsonIn, list);
    }
}
