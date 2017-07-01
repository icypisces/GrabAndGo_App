package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.orders_daily.BaseFragment;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class RevenueDailyFragment extends BaseFragment{

    private static final String TAG = "RevenueDailyFragment";
    private String ServletName = "/AppRevenueDailyServlet";
    private TextView tvDate;
    private Button btSelectDate;
    private int today_year, today_month, today_day;
    private String rest_name;
    private AsyncTask OrderDateTask;
    private ProgressDialog progressDialog;

    //Login
    private SharedPreferences sharedPreferencesLogin = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.financial_fragment_revenue_daily,container,false);  //取得layout檔
        findView(view);
        getRestaurantName();



        return view;
    }

    private void findView(View view) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        btSelectDate = (Button) view.findViewById(R.id.btSelectDate);
    }

    private void getRestaurantName() {
        sharedPreferencesLogin = getActivity().getSharedPreferences(Common.getUsPass(),MODE_PRIVATE);
        rest_name = sharedPreferencesLogin.getString("rest_name", "");
    }

    @Override
    protected void lazyLoad() {

    }
    private View.OnClickListener onSelectDateClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            DatePickerFragment datePickerFragment = new DatePickerFragment();
//            datePickerFragment.show(getFragmentManager(), "date_picker");
            // 設定初始日期 - 當天
            final Calendar today = Calendar.getInstance();
            today_year = today.get(Calendar.YEAR);
            today_month = today.get(Calendar.MONTH);
            today_day = today.get(Calendar.DAY_OF_MONTH);

            //DatePicker
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    tvDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }
            }, today_year, today_month, today_day);
            dpd.show();
        };

    };

//    public void onSelectDateClick(View view) {
//
//        DatePickerFragment datePickerFragment = new DatePickerFragment();
//        datePickerFragment.show(getFragmentManager(), "date_picker");

//        //設定初始日期 - 當天
//        final Calendar today = Calendar.getInstance();
//        today_year = today.get(Calendar.YEAR);
//        today_month = today.get(Calendar.MONTH);
//        today_day = today.get(Calendar.DAY_OF_MONTH);
//
//        //DatePicker
//        DatePickerDialog dpd = new DatePickerDialog(RevenueDailyFragment.this, new DatePickerDialog.OnDateSetListener(){
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                tvDate.setText(year + "-" + ( month + 1 ) + "-" + dayOfMonth);
//            }
//        }, today_year, today_month, today_day);
//        dpd.show();
//    }
//
//
//    @Override
//    public void getData(String data) {
//        tvDate.setText(data);
//    }
}
