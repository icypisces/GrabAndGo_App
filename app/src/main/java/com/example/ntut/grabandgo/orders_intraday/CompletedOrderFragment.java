package com.example.ntut.grabandgo.orders_intraday;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ntut.grabandgo.R;

public class CompletedOrderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //inflate(int resource, ViewGroup root, boolean attachToRoot)
        View view = inflater.inflate(R.layout.order_fragment_completed, container, false);  //取得layout檔
        return view;
    }
}
