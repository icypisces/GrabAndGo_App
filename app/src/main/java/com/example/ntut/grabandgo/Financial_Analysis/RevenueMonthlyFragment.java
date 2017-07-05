package com.example.ntut.grabandgo.Financial_Analysis;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.Restaurant_related.RegisterActivity;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RevenueMonthlyFragment extends BaseFragment {

    private static final String TAG = "RevenueMonthlyFragment";
    private Spinner spYearSelect, spMonthSelect;
    private Button btSelectMonth;
    private int today_year, today_month;
//    private LineChart monthlyLineChart;
    private final int months = 12;
    private CombinedChart mChart;

    //折線圖
    private LineChart lineChart;
    private float[] yData={10, 20, 30, 50, 60, 70, 20, 50, 50, 80, 75, 55};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_monthly,container,false);  //取得layout檔
        findView(view);
        setSpinners();



        return view;
    }

    private void findView(View view) {
        spYearSelect = (Spinner) view.findViewById(R.id.spYearSelect);
        spMonthSelect = (Spinner) view.findViewById(R.id.spMonthSelect);
        btSelectMonth = (Button) view.findViewById(R.id.btSelectMonth);
//        monthlyLineChart = (LineChart) view.findViewById(R.id.monthlyLineChart);
        mChart = (CombinedChart) view.findViewById(R.id.monthlyCombinedChart);
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


        mChart.setTouchEnabled(true); //是否可以觸摸


        mChart.setDrawGridBackground(true);//是否顯示表格顏色

//        mChart.setDragScaleEnable(true); //是否可以拖拽、縮放

        mChart.setBackgroundColor(Color.rgb(136, 244, 252));
        mChart.setGridBackgroundColor(Color.rgb(252, 174, 58));
        //表格顏色
        mChart.setNoDataText("加載中...");
        //沒有數據顯示的內容
        mChart.setAutoScaleMinMaxEnabled(true);//Y軸自動縮放範圍
        mChart.setDragEnabled(true);// 是否可以拖拽
        mChart.setScaleEnabled(true);// 是否可以縮放
        //二、設置顯示數據
        List<CandleEntry> candleEntries = new ArrayList<>();

        //圖表數據
        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(Color.GREEN);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(Color.RED);
        set.setIncreasingPaintStyle(Paint.Style.STROKE);
        set.setNeutralColor(Color.RED);
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.5f);

        set.setHighLightColor(Color.BLACK);

        set.setDrawValues(false);
        ArrayList<String> xVals = new ArrayList<>(); //x軸顯示的內容
        for (int i = 0; i < 10; i++) {
            xVals.add(String.valueOf(i));
        }
        CandleData candleData = new CandleData(xVals);
        candleData.addDataSet(set);
        CombinedData combinedData = new CombinedData(xVals);
        combinedData.setData(candleData);
        mChart.setData(combinedData);//當前螢幕會顯示所有的數據
        mChart.invalidate();


    }

    @Override
    protected void lazyLoad() {

    }


}
