package com.example.ntut.grabandgo.Financial_Analysis;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
    private EditText etLimitCount;
    private TextView tvNoData;
    private int today_year, today_month;
    private int limitCount;
    private final int limitCount_default = 10;
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
        etLimitCount = (EditText) view.findViewById(R.id.etLimitCount);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
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
                displayHorizontalBarChart(dateData);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etLimitCount.getWindowToken(), 0);
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesChartsList = (List<OrderItem>) bundle.getSerializable("salesChartsList");
            etLimitCount.setText(String.valueOf(limitCount_default));   //預設顯示排行數量為10
            Object[] dateData = getDateData(salesChartsList);
            displayHorizontalBarChart(dateData);
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
        limitCount = Integer.parseInt(etLimitCount.getText().toString());
        float[] yData = new float[size];                        //銷售金額
        if ( limitCount == 0 && size > 0 ) {                    //如果輸入的顯示個數為0但銷售商品項目不為0
            limitCount = Math.min(limitCount_default, size);    //將顯示個數設定為 " 銷售商品項目和預設顯示個數的較小值 "
        }
        int showCount = Math.min(limitCount, size);             //將選擇的顯示個數與實際商品數取小值
        if ( limitCount > showCount && showCount > 0) {
            Common.showToast(getActivity(), R.string.keyInLimitCountOver);
        } else if ( limitCount == 0  && showCount > 0) {
            limitCount = Math.min(size, limitCount_default);
            showCount = limitCount;
        }
        etLimitCount.setText(String.valueOf(showCount));
        for (int i = 0; i < showCount; i++) {
            yData[i] = salesChartsListMonthly.get(i).getItem_price();       //該商品當月收入
        }
        Object[] dateData = {yData, showCount, selectMonth};
        return dateData;
    }

    /* 將 DataSet 資料整理好後，回傳 HorizontalBarChart */
    private BarData getHorizontalBarData(float[] yData, int showCount, String selectMonth) throws ParseException {
        monthlyHorizontalBarChart.getAxisLeft().setEnabled(true);
        //X軸
        XAxis xAxis = monthlyHorizontalBarChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);          // X軸位置...左方

        // BarDataSet(List<Entry> 資料點集合, String 類別名稱)
        BarDataSet dataSet = new BarDataSet( getChartData(showCount, yData), selectMonth);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // BarData(List<String> Xvals座標標籤, List<Dataset> 資料集)
        BarData data = new BarData(getLabels(showCount), dataSets);

        return data;
    }

    /* 取得 List<BarEntry> 的資料給 DataSet */
    private List<BarEntry> getChartData(int count, float[] yData){
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i<count; i++){
            // Entry(value 數值, index 位置)
            chartData.add(new BarEntry( yData[(count-i-1)], i));
        }
        return chartData;
    }

    /* 取得 XVals Labels 給 BarData */
    private List<String> getLabels(int count){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<count;i++) {
            chartLabels.add((count-i) + "");
        }
        return chartLabels;
    }

    private void displayHorizontalBarChart (Object[] dateData) {
        if ((int)dateData[1] != 0) {
            tvNoData.setVisibility(View.GONE);
            monthlyHorizontalBarChart.setVisibility(View.VISIBLE);
            try {
                monthlyHorizontalBarChart.setData(getHorizontalBarData(
                        (float[])dateData[0], (int)dateData[1], (String) dateData[2]));    //( 金額 , 顯示排行數量, selectMonth )
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthlyHorizontalBarChart.animateX(1000);
            monthlyHorizontalBarChart.notifyDataSetChanged();
            monthlyHorizontalBarChart.invalidate();
        } else {
            tvNoData.setVisibility(View.VISIBLE);
            monthlyHorizontalBarChart.setVisibility(View.GONE);
        }
    }

    @Override
    protected void lazyLoad() {

    }
}
