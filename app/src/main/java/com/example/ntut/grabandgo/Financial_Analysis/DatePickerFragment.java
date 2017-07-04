package com.example.ntut.grabandgo.Financial_Analysis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private String date;

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        //設定初始日期 - 當天
//        final Calendar today = Calendar.getInstance();
//        int today_year = today.get(Calendar.YEAR);
//        int today_month = today.get(Calendar.MONTH);
//        int today_day = today.get(Calendar.DAY_OF_MONTH);
//        return new DatePickerDialog(getActivity(), this, today_year, today_month, today_day);
//    }                                              //this連結到onDateSet方法...OnDateSetListener一定要Override的方法

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = year + "-" + ( month + 1 ) + "-" + dayOfMonth;
//        tvDate.setText(year + "-" + ( month + 1 ) + "-" + dayOfMonth);
    }
}
