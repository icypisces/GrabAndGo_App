package com.example.ntut.grabandgo;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    //Fragment當前狀態是否可見
    protected boolean isVisible;

    //控制加載
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) { //是否為可見狀態
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    //延遲加載，設定為abstract(子類必須重寫此方法)
    protected abstract void lazyLoad();
}
