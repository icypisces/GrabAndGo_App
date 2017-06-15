package com.example.ntut.grabandgo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Common {

    // Android官方模擬器連結本機web server可以直接使用 http://10.0.2.2
//	public static String URL = "http://192.168.19.27:8080/TextToJson_Web/SearchServlet";
    public final static String URL = "http://10.0.2.2:8080/_Grab_Go/";
    //10.0.2.2會自動找模擬器在的"主要作業系統"，自動轉址到同硬體設備的網址．
    //使用條件：1. 使用模擬器而非手機，且
    //　　　　　2. 連線到同一硬體設備，且有裝設WebServer．
    //如果使用手機當模擬器，連線條件：
    //　　　　　1. 同一個區域網路且同一個區域網段．
    //　　　　　2. 連線至WebServer所在之IP(192.168.XXX.XXX)．


    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
