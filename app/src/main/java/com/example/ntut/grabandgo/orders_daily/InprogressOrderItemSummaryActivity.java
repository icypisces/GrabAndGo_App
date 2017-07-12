package com.example.ntut.grabandgo.orders_daily;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class InprogressOrderItemSummaryActivity extends NavigationDrawerSetup {
    private final static String TAG = "InprogressOrderItemSummaryActivity";
    private String ServletName = "/AppStoreOrderDailyServlet";

    private Button btBack;
    private TableLayout tlOrderDetail;

    private SharedPreferences sharedPreferencesLogin=null;
    private String rest_id;

    private AsyncTask ItemSummaryTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_inprogress_item_summary);
        setUpToolBar();
        findViews();
        getRestaurantName();
        getItemSummaryFromServlet();

    }

    private void findViews() {
        btBack = (Button) findViewById(R.id.btBack);
        tlOrderDetail = (TableLayout) findViewById(R.id.tlOrderDetail);
    }

//-------------------------------連線至伺服器取得ItemSummary資料---------------------------------
    private void getRestaurantName() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(), MODE_PRIVATE);
        rest_id = sharedPreferencesLogin.getString("rest_id", "");
    }

    private void getItemSummaryFromServlet() {
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(InprogressOrderItemSummaryActivity.this)) {
            ItemSummaryTask = new ItemSummaryTask().execute(url, TAG, rest_id);
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    class ItemSummaryTask extends AsyncTask<String, Void, List<List<String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InprogressOrderItemSummaryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<List<String>> doInBackground(String... params) {
            String url = params[0].toString();
            String param = params[1].toString();
            String rest_id = params[2].toString();
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("param", param);
            jsonObject.addProperty("rest_id", rest_id);

            try {
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();    //用Gson
            Type itemSummary = new TypeToken<List<List<String>>>() {
            }.getType();
            Log.e(TAG, "itemSummary = " + itemSummary);

            return gson.fromJson(jsonIn, itemSummary);
        }

        @Override
        protected void onPostExecute(List<List<String>> itemSummary) {
            super.onPostExecute(itemSummary);
            String prodIdPre = "";  //上一列prod_id
            int countAmount = 0;    //目前之prod_id的個數統計
            int count = 0;          //列數計算
            for (int i=0; i<itemSummary.size(); i++) {
                List<String> itemDetail = itemSummary.get(i);
                String prod_id = itemDetail.get(0);
                String item_name = itemDetail.get(1);
                String ord_id = itemDetail.get(2);
                String ord_pickuptime = itemDetail.get(3);
                String item_amount = itemDetail.get(4);
                String item_note = "";
                if (itemDetail.get(5) == null || itemDetail.get(5).equals("null")) {
                    item_note = "";
                } else {
                    item_note = itemDetail.get(5);
                }


                if ( i > 0) {      //如果本列非第一列，且prod_id與上列相同
                    //分隔線 - 細
                    View line = new View(InprogressOrderItemSummaryActivity.this);
                    line.setBackgroundColor(Color.rgb(193, 193, 193));
                    tlOrderDetail.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                    if (!prod_id.equals(prodIdPre)) {
                        //subtotal列
                        TableRow tableRowLast = new TableRow(InprogressOrderItemSummaryActivity.this);
                        String[] textViewsTotal = {"", getResources().getString(R.string.subTotal), "", "", String.valueOf(countAmount), ""};
                        for (int j = 0; j < textViewsTotal.length; j++) {
                            TextView textView = new TextView(InprogressOrderItemSummaryActivity.this);
                            textView.setText(textViewsTotal[j]);
                            textView.setPadding(10, 0, 10, 0);
                            textView.setMaxWidth(75);
                            textView.setSingleLine(false);
                            tableRowLast.addView(textView, j);  //j是編號
                        }
                        tlOrderDetail.addView(tableRowLast);
                        //分隔線 - 粗
                        View lineHeavy = new View(InprogressOrderItemSummaryActivity.this);
                        lineHeavy.setBackgroundColor(Color.rgb(107, 106, 106));
                        tlOrderDetail.addView(lineHeavy, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 7));
                        //countAmount歸零
                        countAmount = 0;
                    }
                }

                TableRow tableRow = new TableRow(InprogressOrderItemSummaryActivity.this);
                String[] textViews = {prod_id, item_name, ord_id, ord_pickuptime,
                                    item_amount, item_note};

                for (int l = 0; l < textViews.length; l++) {
                    TextView textView = new TextView(InprogressOrderItemSummaryActivity.this);
                    textView.setText(textViews[l]);
                    textView.setPadding(10, 0, 10, 0);
                    textView.setMaxWidth(70);
                    textView.setSingleLine(false);
                    tableRow.addView(textView, l);
                }
                tlOrderDetail.addView(tableRow);
                prodIdPre = prod_id;
                countAmount += Integer.parseInt(item_amount);
                count ++;

                //最後一項prod的小計
                if (count == itemSummary.size()) {
                    //分隔線 - 細
                    View line = new View(InprogressOrderItemSummaryActivity.this);
                    line.setBackgroundColor(Color.rgb(107, 106, 106));
                    tlOrderDetail.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                    //subtotal列
                    TableRow tableRowLast = new TableRow(InprogressOrderItemSummaryActivity.this);
                    String[] textViewsTotal = {"", getResources().getString(R.string.subTotal), "", "", String.valueOf(countAmount), ""};
                    for (int j = 0; j < textViewsTotal.length; j++) {
                        TextView textView = new TextView(InprogressOrderItemSummaryActivity.this);
                        textView.setText(textViewsTotal[j]);
                        textView.setPadding(10, 0, 10, 0);
                        textView.setMaxWidth(75);
                        textView.setSingleLine(false);
                        tableRowLast.addView(textView, j);  //j是編號
                    }
                    tlOrderDetail.addView(tableRowLast);
                    //分隔線 - 粗
                    View lineHeavy = new View(InprogressOrderItemSummaryActivity.this);
                    lineHeavy.setBackgroundColor(Color.rgb(107, 106, 106));
                    tlOrderDetail.addView(lineHeavy, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 7));
                }
            }
            progressDialog.cancel();
        }
    }

    public void onBackClick(View view) {
        finish();
    }
}
