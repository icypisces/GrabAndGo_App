package com.example.ntut.grabandgo.Financial_Analysis;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RevenueMonthlyFragment extends BaseFragment {

    private static final String TAG = "RevenueMonthlyFragment";
    private Spinner spYearSelect, spMonthSelect;
    private Button btSelectMonth;
    private EditText edRevenueTotal;
    private int today_year, today_month;
    private final int months = 12;
    private LineChart monthlyLineChart;
    private String selectMonth;
    private List<OrderItem> orderItemList = null;

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
        edRevenueTotal = (EditText) view.findViewById(R.id.edRevenueTotal);
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
                Object[] xAndyData = getDateData(orderItemList);
                edRevenueTotal.setText(Integer.toString((int)xAndyData[1]));
                try {
                    monthlyLineChart.setData(getLineData(
                            (Map<Integer, Float>) xAndyData[0], (String) xAndyData[2]));    //( (X,Y) , selectMonth )
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                monthlyLineChart.animateX(1000);
                monthlyLineChart.notifyDataSetChanged();
                monthlyLineChart.invalidate();
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderItemList = (List<OrderItem>) bundle.getSerializable("orderItemList");

            Object[] xAndyData = getDateData(orderItemList);
            edRevenueTotal.setText(Integer.toString((int)xAndyData[1]));
            try {
                monthlyLineChart.setData(getLineData(
                        (Map<Integer, Float>) xAndyData[0], (String) xAndyData[2]));    //( (X,Y) , selectMonth )
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthlyLineChart.animateX(1000);
            monthlyLineChart.notifyDataSetChanged();
            monthlyLineChart.invalidate();
        }
    }

    private Object[] getDateData(List<OrderItem> orderItemList){
        List<OrderItem> orderItemListMonthly = new ArrayList<>();
        selectMonth = spYearSelect.getSelectedItem().toString().trim() + "-"
                + spMonthSelect.getSelectedItem().toString().trim();
        for (int i = 0; i < orderItemList.size(); i++) {
            String[] notes = orderItemList.get(i).getItem_note().split("\\/");
            String orderMonth = notes[1];
            if ( orderMonth.equals(selectMonth)) {
                orderItemListMonthly.add(orderItemList.get(i));
            }
        }
        int size = orderItemListMonthly.size();
        int[] xData = new int[size];  //日期
        float[] yData = new float[size];    //銷售金額
        int RevenueTotalMonthly = 0;
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            xData[i] = Integer.parseInt(orderItemList.get(i).getItem_note().split("\\/")[0]);//有收入的日期
            yData[i] = orderItemListMonthly.get(i).getItem_price();         //該日期收入
            map.put(xData[i], yData[i]);
            RevenueTotalMonthly += yData[i];                                //累加收入
        }
        Object[] xAndyData = {map, RevenueTotalMonthly, selectMonth};
        return xAndyData;
    }

    /* 將 DataSet 資料整理好後，回傳 LineData */
    private LineData getLineData(Map<Integer, Float> map, String selectMonth) throws ParseException {
        //取得查詢當月的日數
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(selectMonth + "-1");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int DATA_COUNT = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // LineDataSet(List<Entry> 資料點集合, String 類別名稱)
        LineDataSet dataSetA = new LineDataSet( getChartData(DATA_COUNT, map), selectMonth);
        dataSetA.setColor(Color.rgb(252, 190, 128));
        dataSetA.setLineWidth(2f);
        dataSetA.setDrawCircleHole(true);
        dataSetA.setCircleColor(Color.rgb(255, 156, 58));       //設置空心圓顏色
        dataSetA.setCircleRadius(10f);                           //設置空心圓半徑
        dataSetA.setCircleColorHole(Color.rgb(255, 255, 255));   //設置內圓顏色
        dataSetA.setCircleRadius(5f);                            //設置內圓半徑
        dataSetA.setDrawFilled(true);                            //線的陰影

//        LineDataSet dataSetB = new LineDataSet( getChartData(DATA_COUNT, 2), "B");

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);
 //       dataSets.add(dataSetB);

        // LineData(List<String> Xvals座標標籤, List<Dataset> 資料集)
        LineData data = new LineData(getLabels(DATA_COUNT), dataSets);

        return data;
    }

    /* 取得 List<Entry> 的資料給 DataSet */
    private List<Entry> getChartData(int count, Map<Integer, Float> map){
        int index_date = 0;
        List<Entry> chartData = new ArrayList<>();
        for(int i=0; i<count; i++){
            // Entry(value 數值, index 位置)
            //日期 = index位置+1
            index_date = (i+1);
            if (map.get(index_date) == null) {
                chartData.add(new Entry( 0, i));
            } else {
                chartData.add(new Entry( map.get(index_date), i));
            }
        }
        return chartData;
    }

    /* 取得 XVals Labels 給 LineData */
    private List<String> getLabels(int count){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<count;i++) {
            chartLabels.add((i+1) + "日");
        }
        return chartLabels;
    }

    @Override
    protected void lazyLoad() {

    }
}
