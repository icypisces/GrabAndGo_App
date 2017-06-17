package com.example.ntut.grabandgo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Common {
    public static final String KEY = "KittySnoopyMicky";
    // Android官方模擬器連結本機web server可以直接使用 http://10.0.2.2
//	public static String URL = "http://192.168.19.27:8080/TextToJson_Web/SearchServlet";
    public final static String URL = "http://10.0.2.2:8080/_Grab_Go/";
    //10.0.2.2會自動找模擬器在的"主要作業系統"，自動轉址到同硬體設備的網址．
    //使用條件：1. 使用模擬器而非手機，且
    //　　　　　2. 連線到同一硬體設備，且有裝設WebServer．
    //如果使用手機當模擬器，連線條件：
    //　　　　　1. 同一個區域網路且同一個區域網段．
    //　　　　　2. 連線至WebServer所在之IP(192.168.XXX.XXX)．

    private final static String US_PASS = "UsAndPass.txt";

    public static String getUsPass() {
        return US_PASS;
    }


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



    public static String getMD5Endocing(String message) {
        final StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            byte[] digest = md.digest();

            for (int i = 0; i < digest.length; ++i) {
                final byte b = digest[i];
                final int value = (b & 0x7F) + (b < 0 ? 128 : 0);
                buffer.append(value < 16 ? "0" : "");
                buffer.append(Integer.toHexString(value));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }

    public static String encryptString(String message) {
        //  DES : Data Encryption Standard, 一種對稱式加密演算法。
        //           美國聯邦政府於1976年定為聯邦資料處理標準(FIPS)，它的
        //           金鑰則必須是7個位元組、加密區塊(Cipher Block)固定為8個位元組。
        //           DES目前已被視為是一種不安全的演算法。
        //  AES : Advanced Encryption Standard, 一種對稱式加密演算法。
        //           (美國聯邦政府於2001年納入FIPS 140-2標準)，此種演算法
        //           的Cipher Block固定為16個位元組。金鑰則必須是16個位元組、
        //           24個位元組或32個位元組(即128個位元、192個位元或256個位元)。
        //  ECB : Electronic CookBook, 一種資料的加密方式，這種加密方式採取
        //           每個區塊(如8個或16個位元組)獨立加密，即加密任ㄧ區塊時與其它區塊
        //           無關。獨立壓縮有優點也有缺點。
        //           優點為可以由多個處理器來平行處理ㄧ個很大的資料。缺點為如果資料
        //           的內容有重複出現的部分，而且重複資料的長度剛好與加密區塊等長，
        //           則這些重複出現的部分經過加密後會出現相同的結果。
        //  PKCS5Padding: 如果要加密的資料不是8個(如DES加密演算法)或
        //           16個(如AES加密演算法)位元組的整數倍，則必須在欲加密資料的
        //           尾端加入若干個位元組來湊成整數倍。PKCS5Padding是一種
        //           補足不足位元組的方法。
        String encryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedString = Base64.encodeToString(cipher.doFinal(message.getBytes()), 16);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

}
