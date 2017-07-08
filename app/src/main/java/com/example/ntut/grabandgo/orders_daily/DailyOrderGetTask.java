package com.example.ntut.grabandgo.orders_daily;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

//取得今日訂單(含未來訂單)
class DailyOrderGetTask extends AsyncTask<String, Void, List<Order>> {
    private static final String TAG = "HistoryOrderGetTask";

    @Override
    protected List<Order> doInBackground(String... params) {
        String url = params[0];
        String rest_id = params[1];
        String status = params[2];
        String param = params[3];
//        String customer = params[4];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("param", param);
        jsonObject.addProperty("rest_id", rest_id);
        jsonObject.addProperty("status", status);
//        jsonObject.addProperty("customer", customer);
        try {
            jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }

        Gson gson = new Gson();
        Type list = new TypeToken<List<Order>>() {
        }.getType();
        Log.e(TAG, "list = " + list);

        return gson.fromJson(jsonIn, list);
    }
}
