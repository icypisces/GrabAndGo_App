package com.example.ntut.grabandgo.Financial_Analysis;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevenueYearlyFragment extends BaseFragment {

    private static final String TAG = "RevenueYearlyFragment";
    private Spinner spYearSelect;
    private Button btSelectYear;
    private EditText edRevenueTotal;
    private int today_year;
    private BarChart yearlyBarChart;
    private String selectYear;
    private List<OrderItem> orderItemList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_yearly,container,false);  //取得layout檔
        findView(view);
        setSpinners();
        return view;
    }

    private void findView(View view) {
        spYearSelect = (Spinner) view.findViewById(R.id.spYearSelect);
        btSelectYear = (Button) view.findViewById(R.id.btSelectYear);
        edRevenueTotal = (EditText) view.findViewById(R.id.edRevenueTotal);
        yearlyBarChart = (BarChart) view.findViewById(R.id.yearlyBarChart);
    }

    private void setSpinners() {
        // 設定初始日期 - 當天
        final Calendar today = Calendar.getInstance();
        today_year = today.get(Calendar.YEAR);

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btSelectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectYear = spYearSelect.getSelectedItem().toString().trim();
                Object[] xAndyData = getDateData(orderItemList);
                displayBarChart(xAndyData);
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderItemList = (List<OrderItem>) bundle.getSerializable("orderItemList");
            Object[] xAndyData = getDateData(orderItemList);
            displayBarChart(xAndyData);
        }
    }

    private void displayBarChart (Object[] xAndyData) {
        yearlyBarChart.setDescription("");
        edRevenueTotal.setText(Integer.toString((int)xAndyData[1]));
        try {
            yearlyBarChart.setData(getBarData(
                    (Map<Integer, Float>) xAndyData[0], (String) xAndyData[2]));    //( (X,Y) , selectMonth )
        } catch (ParseException e) {
            e.printStackTrace();
        }
        yearlyBarChart.animateX(1000);
        yearlyBarChart.notifyDataSetChanged();
        yearlyBarChart.invalidate();
    }

    private Object[] getDateData(List<OrderItem> orderItemList){
        List<OrderItem> orderItemListYearly = new ArrayList<>();
        selectYear = spYearSelect.getSelectedItem().toString().trim();
        for (int i = 0; i < orderItemList.size(); i++) {
            String[] notes = orderItemList.get(i).getItem_note().split("\\/");
            String orderYear = notes[1];
            if ( orderYear.equals(selectYear)) {
                orderItemListYearly.add(orderItemList.get(i));
            }
        }
        int size = orderItemListYearly.size();
        int[] xData = new int[size];        //月份
        float[] yData = new float[size];    //銷售金額
        int RevenueTotalYearly = 0;
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            xData[i] = Integer.parseInt(orderItemList.get(i).getItem_note().split("\\/")[0]);//有收入的月份
            yData[i] = orderItemListYearly.get(i).getItem_price();         //該月份收入
            map.put(xData[i], yData[i]);
            RevenueTotalYearly += yData[i];                                //累加收入
        }
        Object[] xAndyData = {map, RevenueTotalYearly, selectYear};
        return xAndyData;
    }

    /* 將 DataSet 資料整理好後，回傳 BarData */
    private BarData getBarData(Map<Integer, Float> map, String selectYear) throws ParseException {
        final int DATA_COUNT = 12;

        // BarDataSet(List<Entry> 資料點集合, String 類別名稱)
        BarDataSet dataSet = new BarDataSet( getChartData(DATA_COUNT, map), selectYear);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // BarData(List<String> Xvals座標標籤, List<Dataset> 資料集)
        BarData data = new BarData(getLabels(DATA_COUNT), dataSets);

        return data;
    }

    /* 取得 List<Entry> 的資料給 DataSet */
    private List<BarEntry> getChartData(int count, Map<Integer, Float> map){
        int index_month = 0;
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i<count; i++){
            // Entry(value 數值, index 位置)
            //日期 = index位置+1
            index_month = (i+1);
            if (map.get(index_month) == null) {
                chartData.add(new BarEntry( 0, i));
            } else {
                chartData.add(new BarEntry( map.get(index_month), i));
            }
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
