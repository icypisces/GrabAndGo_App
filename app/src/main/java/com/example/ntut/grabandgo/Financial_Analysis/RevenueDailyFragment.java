package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.BaseFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RevenueDailyFragment extends BaseFragment {

    private static final String TAG = "RevenueDailyFragment";
    private TextView tvDate, tvNoData;
    private Button btSelectDate;
    private EditText edRevenueTotal;
    private LinearLayout linearLayoutRevenueTotal;
    private int today_year, today_month, today_day;
    private PieChart dailyPieChart;
    private List<OrderItem> orderItemList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_daily,container,false);  //取得layout檔
        findView(view);

        return view;
    }

    private void findView(View view) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        edRevenueTotal = (EditText) view.findViewById(R.id.edRevenueTotal);
        linearLayoutRevenueTotal = (LinearLayout) view.findViewById(R.id.linearLayoutRevenueTotal);
        btSelectDate = (Button) view.findViewById(R.id.btSelectDate);
        dailyPieChart = (PieChart) view.findViewById(R.id.dailyPieChart);
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
                //DatePicker
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

                        Object[] xAndyData = getDateData(orderItemList);
                        displayPieChart((String[])xAndyData[0], (float[])xAndyData[1], (Integer) xAndyData[2]);
                    }

                }, today_year, today_month, today_day);
                dpd.show();
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderItemList = (List<OrderItem>) bundle.getSerializable("orderItemList");

            Object[] xAndyData = getDateData(orderItemList);
            displayPieChart((String[])xAndyData[0], (float[])xAndyData[1], (Integer) xAndyData[2]);
        }
    }

    private Object[] getDateData(List<OrderItem> orderItemList){
        List<OrderItem> orderItemListDaily = new ArrayList<>();
        String selectDate = (String) tvDate.getText();
        for (int i = 0; i < orderItemList.size(); i++) {
            String orderDate = orderItemList.get(i).getItem_note();
            if ( orderDate.equals(selectDate)) {
                orderItemListDaily.add(orderItemList.get(i));
            }
        }
        int size = orderItemListDaily.size();
        String[] xData = new String[size];
        float[] yData = new float[size];
        int RevenueTotalDaily = 0;
        for (int i = 0; i < orderItemListDaily.size(); i++) {
            xData[i] = orderItemListDaily.get(i).getItem_name();
            yData[i] = orderItemListDaily.get(i).getItem_price();
            RevenueTotalDaily += yData[i];
        }
        Object[] xAndyData = {xData, yData, RevenueTotalDaily};

        return xAndyData;
    }

    private void displayPieChart(String[] xData, float[] yData, int RevenueTotalDaily){
        if (xData.length != 0 && yData.length != 0) {
            //圓餅圖
            tvNoData.setVisibility(View.GONE);
            linearLayoutRevenueTotal.setVisibility(View.VISIBLE);
            edRevenueTotal.setText(Integer.toString(RevenueTotalDaily));
            dailyPieChart.setVisibility(View.VISIBLE);
            PieData mPieData = getPieData(xData, yData);
            showChart(dailyPieChart, mPieData);
        } else {
            dailyPieChart.setVisibility(View.GONE);
            linearLayoutRevenueTotal.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showChart(PieChart pieChart, PieData pieData) {

        pieChart.setHoleRadius(30f);                            //半徑
        pieChart.setTransparentCircleRadius(35f);               //半透明圈

        pieChart.setDescription("");

        pieChart.setDrawCenterText(true);                       //圓餅圖中間放置文字
        pieChart.setCenterText(getText(R.string.revenueDaily));               //圓餅圖中間的文字
        pieChart.setCenterTextSize(12f);

        pieChart.setDrawHoleEnabled(true);

        pieChart.setRotationAngle(90);                          //起始旋轉角度

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);                      //可以手動旋轉

        // display percentage values
        pieChart.setUsePercentValues(false);                     //顯示為百分比

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
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);      //下方中間顯示
        mLegend.setForm(Legend.LegendForm.CIRCLE);                      //設置比例圖形狀(default->方形)
        mLegend.setTextSize(12f);
        mLegend.setXEntrySpace(10f);
        mLegend.setYEntrySpace(10f);

        pieChart.animateXY(1000, 1000);                 //動畫
    }

    private PieData getPieData(String[] xData, float[] yData) {
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


        //y軸的集合                                         //顯示在比例圖上//(String) getText(R.string.revenueDaily)
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(0f);   //圓餅圖每塊距離

        ArrayList<Integer> colors = new ArrayList<Integer>();

        //圓餅圖顏色***
        colors.add(Color.rgb(136, 244, 252));
        colors.add(Color.rgb(240, 136, 252));
        colors.add(Color.rgb(252, 153, 120));
        colors.add(Color.rgb(252, 174, 58));
        colors.add(Color.rgb(184, 121, 252));
        colors.add(Color.rgb(240, 136, 252));
        colors.add(Color.rgb(252, 153, 120));
        colors.add(Color.rgb(240, 100, 252));
        colors.add(Color.rgb(230, 153, 120));
        colors.add(Color.rgb(240, 136, 200));
        colors.add(Color.rgb(252, 100, 120));


        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);   //被選中後...增加的長度(半徑)

        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setValueTextSize(10f);

        return pieData;
    }

    @Override
    protected void lazyLoad() {

    }

}
