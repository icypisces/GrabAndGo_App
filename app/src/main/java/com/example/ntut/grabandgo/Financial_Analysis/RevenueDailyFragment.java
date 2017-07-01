package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RevenueDailyFragment extends BaseFragment {

    private static final String TAG = "RevenueDailyFragment";
    private String ServletName = "/AppRevenueDailyServlet";
    private Spinner spSelectDate;
    private String rest_name;
    private AsyncTask OrderDateTask;
    private ProgressDialog progressDialog;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_daily,container,false);  //取得layout檔
        findView(view);
        getRestaurantName();

        String url = Common.URL + ServletName ;
        //取得訂單日期
        if (Common.networkConnected(getActivity())) {
            OrderDateTask = new OrderDateTask().execute(url);
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        return view;
    }

    private void findView(View view) {
        spSelectDate = (Spinner) view.findViewById(R.id.spSelectDate);
    }

    private void getRestaurantName() {
        sharedPreferencesLogin = getActivity().getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        rest_name = sharedPreferencesLogin.getString("rest_name", "");
    }

    @Override
    protected void lazyLoad() {

    }

    //取得訂單日期
    class OrderDateTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());   //progressDialog -> 執行時的轉圈圈圖示
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();       //Json要記得
            jsonObject.addProperty("param", "revernueDate");    //將要送到伺服器的Key跟Value先放到jsonObject
            jsonObject.addProperty("rest_name", rest_name);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSelectDate.setAdapter(adapter);
            progressDialog.cancel();
        }
    }
}
