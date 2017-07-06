package com.example.ntut.grabandgo.Financial_Analysis;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesChartsMonthlyFragment extends BaseFragment {

    private static final String TAG = "SalesChartsMonthlyFragment";
    private Spinner spYearSelect, spMonthSelect;
    private Button btSelectMonth;
    private EditText edLimitCount;
    private int today_year, today_month;
    private int limitCount;
    private HorizontalBarChart monthlyHorizontalBarChart;
    private String selectMonth;
    private List<OrderItem> salesChartsList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_sales_charts_monthly,container,false);  //取得layout檔
        findView(view);
        setSpinners();
        return view;
    }

    private void findView(View view) {
        spYearSelect = (Spinner) view.findViewById(R.id.spYearSelect);
        spMonthSelect = (Spinner) view.findViewById(R.id.spMonthSelect);
        btSelectMonth = (Button) view.findViewById(R.id.btSelectMonth);
        edLimitCount = (EditText) view.findViewById(R.id.edLimitCount);
        monthlyHorizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.monthlyHorizontalBarChart);
    }

    private void setSpinners() {
        // 設定初始日期 - 當天
        final Calendar today = Calendar.getInstance();
        today_year = today.get(Calendar.YEAR);
        today_month = today.get(Calendar.MONTH);

        final int yearMin = 2000;
        List<String> yearSelect = new ArrayList<>();
        for (int i=today_year; i>=yearMin; i--) {
            yearSelect.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, yearSelect);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearSelect.setAdapter(adapterYear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spYearSelect.setDropDownVerticalOffset(10);
        }

        final int monthMin = 1;
        final int monthMax = 12;
        List<String> monthSelect = new ArrayList<>();
        for (int i=monthMin; i<=monthMax; i++) {
            monthSelect.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, monthSelect);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonthSelect.setAdapter(adapterMonth);
        spMonthSelect.setSelection(today_month);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMonth = spYearSelect.getSelectedItem().toString().trim() + "-"
                        + spMonthSelect.getSelectedItem().toString().trim();
                Object[] dateData = getDateData(salesChartsList);
                try {
                    monthlyHorizontalBarChart.setData(getHorizontalBarData(
                            (float[])dateData[0], (int)dateData[1], (String) dateData[2]));    //( 金額 , 顯示排行數量, selectMonth )
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                monthlyHorizontalBarChart.animateX(1000);
                monthlyHorizontalBarChart.notifyDataSetChanged();
                monthlyHorizontalBarChart.invalidate();
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesChartsList = (List<OrderItem>) bundle.getSerializable("salesChartsList");
            edLimitCount.setText(String.valueOf(10));   //預設顯示排行數量為10
            Object[] dateData = getDateData(salesChartsList);
            try {
                monthlyHorizontalBarChart.setData(getHorizontalBarData(
                        (float[])dateData[0], (int)dateData[1], (String) dateData[2]));    //( 金額 , 顯示排行數量, selectMonth )
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthlyHorizontalBarChart.animateX(1000);
            monthlyHorizontalBarChart.notifyDataSetChanged();
            monthlyHorizontalBarChart.invalidate();
        }
    }

    private Object[] getDateData(List<OrderItem> orderItemList){
        List<OrderItem> salesChartsListMonthly = new ArrayList<>();
        selectMonth = spYearSelect.getSelectedItem().toString().trim() + "-"
                + spMonthSelect.getSelectedItem().toString().trim();
        for (int i = 0; i < orderItemList.size(); i++) {
            String orderMonth = orderItemList.get(i).getItem_note();
            if ( orderMonth.equals(selectMonth)) {
                salesChartsListMonthly.add(orderItemList.get(i));
            }
        }
        int size = salesChartsListMonthly.size();
        limitCount = Integer.parseInt(edLimitCount.getText().toString());
        float[] yData = new float[size];    //銷售金額
        int showCount = Math.min(limitCount, size);
        for (int i = 0; i < showCount; i++) {
            yData[i] = salesChartsListMonthly.get(i).getItem_price();       //該商品當月收入
        }
        Object[] dateData = {yData, showCount, selectMonth};
        return dateData;
    }

    /* 將 DataSet 資料整理好後，回傳 HorizontalBarChart */
    private BarData getHorizontalBarData(float[] yData, int showCount, String selectMonth) throws ParseException {

        // BarDataSet(List<Entry> 資料點集合, String 類別名稱)
        BarDataSet dataSet = new BarDataSet( getChartData(showCount, yData), selectMonth);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // BarData(List<String> Xvals座標標籤, List<Dataset> 資料集)
        BarData data = new BarData(getLabels(showCount), dataSets);

        return data;
    }

    /* 取得 List<Entry> 的資料給 DataSet */
    private List<BarEntry> getChartData(int count, float[] yData){
        int index_month = 0;
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i<count; i++){
            // Entry(value 數值, index 位置)
            chartData.add(new BarEntry( yData[i], i));
        }
        return chartData;
    }

    /* 取得 XVals Labels 給 BarData */
    private List<String> getLabels(int count){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<count;i++) {
            chartLabels.add((i+1) + "月");
        }
        return chartLabels;
    }

    @Override
    protected void lazyLoad() {

    }
}
