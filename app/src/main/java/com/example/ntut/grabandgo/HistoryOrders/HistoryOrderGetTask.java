package com.example.ntut.grabandgo.HistoryOrders;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.Order;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

//取得歷史訂單
class HistoryOrderGetTask extends AsyncTask<String, Void, List<Order>> {
    private static final String TAG = "HistoryOrderGetTask";

    @Override
    protected List<Order> doInBackground(String... params) {
        String url = params[0];
        String rest_id = params[1];
        String selectMonth = params[2];
        String searchPhone = params[3];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("param", "HistoryOrder");
        jsonObject.addProperty("rest_id", rest_id);
        jsonObject.addProperty("selectMonth", selectMonth);
        jsonObject.addProperty("searchPhone", searchPhone);
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
