package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class RevenueDailyFragment extends BaseFragment{

    private static final String TAG = "RevenueDailyFragment";
    private String ServletName = "/AppRevenueDailyServlet";
    private TextView tvDate;
    private Button btSelectDate;
    private int today_year, today_month, today_day;
    private PieChart dailyPieChart;
    private String rest_name;
    private int prod_id;
    private String item_name;
    private int item_price;
    private int item_amout;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;

    //圓餅圖測試用***
    private float[] yData = {5, 10, 15, 20, 25};
    private String[] xData = {"aaa", "bbb", "ccc", "DDD", "EEE"};

    /*Activity傳遞資料予Fragment測試中*/
    public void setOrderItem(OrderItem orderItem){
        orderItem.setProd_id(prod_id);
        orderItem.setItem_name(item_name);
        orderItem.setItem_price(item_price);
        orderItem.setItem_amout(item_amout);
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_daily,container,false);  //取得layout檔
        findView(view);
        getRestaurantName();

        //圓餅圖
        PieData mPieData = getPieData();
        showChart(dailyPieChart, mPieData);

        return view;
    }

    private void findView(View view) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        btSelectDate = (Button) view.findViewById(R.id.btSelectDate);
        dailyPieChart = (PieChart) view.findViewById(R.id.dailyPieChart);
    }

    private void getRestaurantName() {
        sharedPreferencesLogin = getActivity().getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        rest_name = sharedPreferencesLogin.getString("rest_name", "");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 設定初始日期 - 當天
        final Calendar today = Calendar.getInstance();
        today_year = today.get(Calendar.YEAR);
        today_month = today.get(Calendar.MONTH);
        today_day = today.get(Calendar.DAY_OF_MONTH);

        tvDate.setText(today_year + "-" + (today_month + 1) + "-" + today_day);

        btSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "date_picker");
//                // 設定初始日期 - 當天
//                final Calendar today = Calendar.getInstance();
//                today_year = today.get(Calendar.YEAR);
//                today_month = today.get(Calendar.MONTH);
//                today_day = today.get(Calendar.DAY_OF_MONTH);

                //DatePicker
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, today_year, today_month, today_day);
                dpd.show();
            }
        });

//************************************連線(待補)****************************************




    }


    private void showChart(PieChart pieChart, PieData pieData) {
//        pieChart.setHoleColorTransparent(true);

        pieChart.setHoleRadius(40f);                            //半徑
        pieChart.setTransparentCircleRadius(45f);               //半透明圈
        //pieChart.setHoleRadius(0)                             //實心圓

        pieChart.setDescription("");

        // pieChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true);                       //圓餅圖中間放置文字
        pieChart.setCenterText(getText(R.string.revenueDaily)); //圓餅圖中間的文字
        pieChart.setCenterTextSize(16f);

        pieChart.setDrawHoleEnabled(true);

        pieChart.setRotationAngle(90);                          //起始旋轉角度

        // draws the corresponding description value into the slice
        // pieChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);                      //可以手動旋轉

        // display percentage values
        pieChart.setUsePercentValues(true);                     //顯示為百分比
        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
//      pieChart.setOnChartValueSelectedListener(this);
        // pieChart.setTouchEnabled(false);

//      pieChart.setOnAnimationListener(this);

        //設置數據
        pieChart.setData(pieData);

        // undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();                          //設置比例圖
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //下方中間顯示
        mLegend.setForm(Legend.LegendForm.CIRCLE);                      //設置比例圖形狀(default->方形)
        mLegend.setTextSize(16f);
        mLegend.setXEntrySpace(10f);
        mLegend.setYEntrySpace(10f);

        pieChart.animateXY(1000, 1000);                 //動畫
    }

    private PieData getPieData() {
        //xValues用來表示每個區塊的內容
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++) {
            xValues.add(xData[i]);
        }

        //yValues用來表示封裝每個區塊的實際數據
        ArrayList<Entry> yValues = new ArrayList<Entry>();

        for (int i=0; i<yData.length; i++) {
            yValues.add(new Entry(yData[i], i));
        }


        //y軸的集合                                         //顯示在比例圖上
        PieDataSet pieDataSet = new PieDataSet(yValues, (String) getText(R.string.revenueDaily));
        pieDataSet.setSliceSpace(0f);   //圓餅圖每塊距離

        ArrayList<Integer> colors = new ArrayList<Integer>();

        //圓餅圖顏色***
        colors.add(Color.rgb(136, 244, 252));
        colors.add(Color.rgb(240, 136, 252));
        colors.add(Color.rgb(252, 153, 120));
        colors.add(Color.rgb(252, 174, 58));
        colors.add(Color.rgb(184, 121, 252));


        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);   //被選中後...增加的長度(半徑)

        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setValueTextSize(24f);

        return pieData;
    }

    @Override
    protected void lazyLoad() {

    }

}
