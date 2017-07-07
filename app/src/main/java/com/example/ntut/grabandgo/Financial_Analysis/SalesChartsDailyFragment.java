package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SalesChartsDailyFragment extends BaseFragment {

    private static final String TAG = "SalesChartsDailyFragment";
    private TextView tvDate, tvNoData;
    private Button btSelectDate;
    private EditText etLimitCount;
    private int today_year, today_month, today_day;
    private int limitCount;
    private final int limitCount_default = 10;
    private String selectDate;
    private HorizontalBarChart dailyHorizontalBarChart;
    private List<OrderItem> salesChartsList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_sales_charts_daily,container,false);  //取得layout檔
        findView(view);
        return view;
    }

    private void findView(View view) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        btSelectDate = (Button) view.findViewById(R.id.btSelectDate);
        etLimitCount = (EditText) view.findViewById(R.id.etLimitCount);
        dailyHorizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.dailyHorizontalBarChart);
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
                        Object[] dateData = getDateData(salesChartsList);
                        displayHorizontalBarChart(dateData);
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etLimitCount.getWindowToken(), 0);
                    }

                }, today_year, today_month, today_day);
                dpd.show();
            }
        });

        //取得自Activity送來的資料，並依日期取得資料及產生圓餅圖．
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesChartsList = (List<OrderItem>) bundle.getSerializable("salesChartsList");
            etLimitCount.setText(String.valueOf(10));   //預設顯示排行數量為10
            Object[] dateData = getDateData(salesChartsList);
            displayHorizontalBarChart(dateData);
        }


        
    }

    private Object[] getDateData(List<OrderItem> orderItemList){
        List<OrderItem> salesChartsListMonthly = new ArrayList<>();
        selectDate = (String) tvDate.getText();
        for (int i = 0; i < orderItemList.size(); i++) {
            String orderMonth = orderItemList.get(i).getItem_note();
            if ( orderMonth.equals(selectDate)) {
                salesChartsListMonthly.add(orderItemList.get(i));
            }
        }
        int size = salesChartsListMonthly.size();
        limitCount = Integer.parseInt(etLimitCount.getText().toString());
        float[] yData = new float[size];                                    //銷售金額
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
        Object[] dateData = {yData, showCount, selectDate};
        return dateData;
    }

    private void displayHorizontalBarChart (Object[] dateData) {
        dailyHorizontalBarChart.setDescription("");
        dailyHorizontalBarChart.getAxisLeft().setEnabled(true);
        //X軸
        XAxis xAxis = dailyHorizontalBarChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);          // X軸位置...左方

        if ((int)dateData[1] != 0) {
            tvNoData.setVisibility(View.GONE);
            dailyHorizontalBarChart.setVisibility(View.VISIBLE);
            try {
                dailyHorizontalBarChart.setData(getHorizontalBarData(
                        (float[])dateData[0], (int)dateData[1], (String) dateData[2]));    //( 金額 , 顯示排行數量, selectMonth )
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dailyHorizontalBarChart.animateX(1000);
            dailyHorizontalBarChart.notifyDataSetChanged();
            dailyHorizontalBarChart.invalidate();
        } else {
            tvNoData.setVisibility(View.VISIBLE);
            dailyHorizontalBarChart.setVisibility(View.GONE);
        }
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

    @Override
    protected void lazyLoad() {

    }
}
