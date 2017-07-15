package com.example.ntut.grabandgo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ntut.grabandgo.orders_daily.DailyOrdersActivity;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class OrderService extends Service {
    private static final String TAG = "OrderService";
    private static final String SERVER_URI =
            "ws://10.0.2.2:8080/_Grab_Go/AppStoreWebSocketServer/"; // + rest_id
    private SharedPreferences sharedPreferencesLogin = null;
    private String rest_id;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;

    public static WebSocketClient client;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private boolean isSaveInService = true;
    private boolean isConn = true;
    private NotificationCompat.Builder notifyBuilder;
    private Vibrator vibrator;
    private MyThread myThread;
    private Receiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //讓程式不會因為手機休眠而停擺
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        wakeLock.acquire();                             //PARTIAL -> CPU支援，鍵盤和畫面不支援

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter intentFilter = new IntentFilter(); //攔截器
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);

        this.myThread = new MyThread();
        this.myThread.start();

        getRestaurantName();

        URI uri = null;
        try {
            uri = new URI(SERVER_URI + rest_id);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        OrderWebsocketClient orderWebsocketClient = new OrderWebsocketClient(uri);
        orderWebsocketClient.connect();
    }

    private void getRestaurantName() {
        sharedPreferencesLogin = getSharedPreferences(Common.getUsPass(), MODE_PRIVATE);
        rest_id = sharedPreferencesLogin.getString("rest_id", "");
    }

    //WebSocket
    class OrderWebsocketClient extends WebSocketClient {
        private static final String TAG = "OrderWebsocketClient";

        public OrderWebsocketClient(URI serverUri) {
            super(serverUri, new Draft_6455());
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "onOpen: " + handshakedata.toString());
        }

        @Override
        public void onMessage(String message) {
            openVibrator();
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//
//                }
//            });
            Intent intent = new Intent(OrderService.this, DailyOrdersActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("message", message);
            startActivity(intent);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            //顯示關閉原因
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            //顯示Exception訊息
            Log.d(TAG, "onError: " + ex.toString());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        getRestaurantName();
        WebSocketImpl.DEBUG = true;
        //解決IPV6的問題
//        System.setProperty("java.net.preferIPv6Addresses", "false");
//        System.setProperty("java.net.preferIPv4Stack", "true");



        return START_STICKY;    //回傳START_STICKY可以保證再次建立新的Service時仍會呼叫onStartCommand
    }

    private void connectClient() {

    }


    private void openVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 開啟 停止 開啟
        vibrator.vibrate(pattern, -1);           //重複兩次上面的pattern 如果只想震動一次，index設為-1
    }

    @Override
    public void onDestroy() {
        //可以加結束時想做的事
        wakeLock.release(); //Service執行結束時就釋放wakeLock
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");！
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class MyThread extends  Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.toString();
                }
            }
        }
    }



}

//android websocket的使用
//http://blog.h5min.cn/MINGZHNGLEI/article/details/53463683

// android 建立长连接的方法
//http://blog.h5min.cn/mingzhnglei/article/details/53464001

//Not able to call runOnUiThread in a thread from inside of a service [duplicate]
//https://stackoverflow.com/questions/18948251/not-able-to-call-runonuithread-in-a-thread-from-inside-of-a-service

//Websocket Server as Service on Android Tablet
//https://stackoverflow.com/questions/27231700/websocket-server-as-service-on-android-tablet

//TooTallNate/Java-WebSocket
//https://github.com/TooTallNate/Java-WebSocket

//android application with phonegap that run websocket in background service
//https://stackoverflow.com/questions/24449444/android-application-with-phonegap-that-run-websocket-in-background-service

//開機時自動啟動Activity跟Service
//http://rx1226.pixnet.net/blog/post/235016962-%5Bandroid%5D-3-1-%E5%9C%A8%E9%96%8B%E6%A9%9F%E6%99%82%E8%87%AA%E5%8B%95%E5%95%9F%E5%8B%95activity%E5%92%8Cservice

//開發 API 指南 App Components 服務
//https://developer.android.com/guide/components/services.html

//How to establish a WebSocket connection in android service?
//https://stackoverflow.com/questions/31476344/how-to-establish-a-websocket-connection-in-android-service

//使用Websocket實現消息推送
//http://www.itread01.com/articles/1476688582.html

//Android中脫離WebView使用WebSocket
//https://read01.com/JPaaG.html

//How to connect to web-socket server from android app
//https://stackoverflow.com/questions/34655411/how-to-connect-to-web-socket-server-from-android-app

//USING WEBSOCKETS IN NATIVE IOS AND ANDROID APPS
//https://www.varvet.com/blog/using-websockets-in-native-ios-and-android-apps/