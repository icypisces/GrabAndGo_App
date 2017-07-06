package com.example.ntut.grabandgo.Financial_Analysis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ntut.grabandgo.MainActivity;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.example.ntut.grabandgo.orders_daily.DailyOrdersActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RevenueMonthlyFragment extends BaseFragment {

    private static final String TAG = "RevenueMonthlyFragment";
    private Spinner spYearSelect, spMonthSelect;
    private Button btSelectMonth;
    private int today_year, today_month;
    private final int months = 12;
    private LineChart monthlyLineChart;
    private String selectMonth;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_monthly,container,false);  //取得layout檔

        findView(view);
        setSpinners();

        monthlyLineChart.setData(getLineData());
        monthlyLineChart.notifyDataSetChanged();
        monthlyLineChart.invalidate();


        return view;
    }

    private void findView(View view) {
        spYearSelect = (Spinner) view.findViewById(R.id.spYearSelect);
        spMonthSelect = (Spinner) view.findViewById(R.id.spMonthSelect);
        btSelectMonth = (Button) view.findViewById(R.id.btSelectMonth);
        monthlyLineChart = (LineChart) view.findViewById(R.id.monthlyLineChart);
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
            }
        });

    }

    @Override
    protected void lazyLoad() {

    }

    /* 將 DataSet 資料整理好後，回傳 LineData */
    private LineData getLineData(){
        final int DATA_COUNT = 5;  //資料數固定為 5 筆

        // LineDataSet(List<Entry> 資料點集合, String 類別名稱)
        LineDataSet dataSetA = new LineDataSet( getChartData(DATA_COUNT, 1), "A");
        LineDataSet dataSetB = new LineDataSet( getChartData(DATA_COUNT, 2), "B");

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);
        dataSets.add(dataSetB);

        // LineData(List<String> Xvals座標標籤, List<Dataset> 資料集)
        LineData data = new LineData(getLabels(DATA_COUNT), dataSets);

        return data;

    }

    /* 取得 List<Entry> 的資料給 DataSet */
    private List<Entry> getChartData(int count, int ratio){

        List<Entry> chartData = new ArrayList<>();
        for(int i=0;i<count;i++){
            // Entry(value 數值, index 位置)
            chartData.add(new Entry( i*2*ratio, i));
        }
        return chartData;
    }

    /* 取得 XVals Labels 給 LineData */
    private List<String> getLabels(int count){

        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<count;i++) {
            chartLabels.add("X" + i);
        }
        return chartLabels;
    }



}
