package com.example.ntut.grabandgo.orders_daily;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ntut.grabandgo.BaseFragment;
import com.example.ntut.grabandgo.R;

public class UnprocessedOrderFragment extends BaseFragment {

    private static final String TAG = "UnprocessedOrderFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_unprocessed,container,false);  //取得layout檔
        return view;
    }


    @Override
    protected void lazyLoad() {

    }
}
