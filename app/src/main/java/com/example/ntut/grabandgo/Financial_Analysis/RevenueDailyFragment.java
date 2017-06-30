package com.example.ntut.grabandgo.Financial_Analysis;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;

public class RevenueDailyFragment extends BaseFragment {

    private static final String TAG = "RevenueDailyFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_daily,container,false);  //取得layout檔
        return view;
    }

    @Override
    protected void lazyLoad() {

    }
}
