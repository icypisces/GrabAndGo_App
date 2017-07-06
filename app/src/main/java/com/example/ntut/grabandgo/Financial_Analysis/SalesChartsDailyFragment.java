package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.NavigationDrawerSetup;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SalesChartsDailyFragment extends BaseFragment {

    private static final String TAG = "SalesChartsDailyFragment";
    private TextView tvDate, tvNoData;
    private Button btSelectDate;
    private EditText edRevenueTotal;
    private LinearLayout linearLayoutRevenueTotal;
    private int today_year, today_month, today_day;
    private HorizontalBarChart dailyHorizontalBarChart;
    private List<OrderItem> orderItemList = null;

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
        edRevenueTotal = (EditText) view.findViewById(R.id.edRevenueTotal);
        linearLayoutRevenueTotal = (LinearLayout) view.findViewById(R.id.linearLayoutRevenueTotal);
        btSelectDate = (Button) view.findViewById(R.id.btSelectDate);
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




                    }

                }, today_year, today_month, today_day);
                dpd.show();
            }
        });


        
    }

    @Override
    protected void lazyLoad() {

    }
}
