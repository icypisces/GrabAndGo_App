package com.example.ntut.grabandgo.Financial_Analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;

public class SalesChartsMonthlyFragment extends BaseFragment {

    private static final String TAG = "RevenueYearlyFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_sales_charts_monthly,container,false);  //取得layout檔
        return view;
    }

    @Override
    protected void lazyLoad() {

    }
}
