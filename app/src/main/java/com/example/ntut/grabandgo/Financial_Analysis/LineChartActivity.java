package com.example.ntut.grabandgo.Financial_Analysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ntut.grabandgo.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartActivity extends AppCompatActivity {
    private LineChart mChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_line_chart);
        mChart = (LineChart) findViewById(R.id.mChart);

    /* 使用 setData 的方法設定 LineData 物件*/
        mChart.setData(getLineData());
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
