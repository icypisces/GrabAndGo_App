package com.example.ntut.grabandgo.Financial_Analysis;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;


public class RevenueMonthlyFragment extends BaseFragment {

    private static final String TAG = "RevenueMonthlyFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_monthly,container,false);  //取得layout檔


        return view;
    }

    @Override
    protected void lazyLoad() {

    }

}
