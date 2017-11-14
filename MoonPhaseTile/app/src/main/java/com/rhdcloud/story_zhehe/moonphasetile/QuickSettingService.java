package com.rhdcloud.story_zhehe.moonphasetile;

import android.service.quicksettings.TileService;
//import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.Calendar;
import java.util.Locale;


//import android.text.format.DateFormat;

//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.io.InputStream;
//import java.io.ByteArrayOutputStream;
//import android.widget.Toast;
import android.os.Handler;
import android.graphics.drawable.Icon;
import android.content.pm.ApplicationInfo;
import java.lang.ref.WeakReference;


/**
 * Created by Zhehe on 2017/1/18.
 */

public class QuickSettingService extends TileService {
    //private static final String LOG_TAG = "MyActivity";
    //private static boolean temp=false;
    private static String day="";
    private int lunarday;
    private String name;
    private String result;
    SimpleDateFormat formatter = new SimpleDateFormat ("dd",Locale.US);
    private CalendarUtil cal=new CalendarUtil();
    //当用户从Edit栏添加到快速设定中调用
    @Override
    public void onTileAdded() {
        //Log.d(LOG_TAG, "onTileAdded");
        UpdateMoonPhase();
    }
    //当用户从快速设定栏中移除的时候调用
    @Override
    public void onTileRemoved() {
        //Log.d(LOG_TAG, "onTileRemoved");
    }
    // 点击的时候
    @Override
    public void onClick() {
        //Log.d(LOG_TAG, "onClick");
        UpdateMoonPhase();
    }
    // 打开下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    //在TleAdded之后会调用一次
    @Override
    public void onStartListening () {
        //Log.d(LOG_TAG, "onStartListening");

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String temp = formatter.format(curDate);
        if(!temp.equals(day)){
            UpdateMoonPhase();
            day=temp;
        }
        /*if(!temp){
            temp=true;
            Log.d(LOG_TAG, "test");
        }*/

    }
    // 关闭下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    // 在onTileRemoved移除之前也会调用移除
    @Override
    public void onStopListening () {
        //Log.d(LOG_TAG, "onStopListening");
    }
    /*
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:

                    String name;
                    if(result.equals("null"))
                    {
                        result="Moon";
                        name="moon";
                    }
                    else{
                        name="moon_"+result.toLowerCase().replaceAll("\\s","_");
                    }
                    //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                    getQsTile().setLabel(result);
                    ApplicationInfo appInfo = getApplicationInfo();
                    int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
                    getQsTile().setIcon(Icon.createWithResource(getApplicationContext(),resID));
                    getQsTile().updateTile();
                    break;

                default:
                    break;
            }
        }
    }
    */
    public void UpdateMoonPhase(){
    /*
        new Thread() {//创建子线程进行网络访问的操作
            public void run() {
                try {
                    result = GetMoonPhaseJson();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    result="null";
                    e.printStackTrace();
                }
            }
        }.start();
    */
        new Thread() {//创建子线程进行网络访问的操作
            public void run() {
                try {
                    lunarday=cal.GetLunarDay();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {

                }
            }
        }.start();

    }
    private static class MyHandler extends Handler {
        //注意下面的“PopupActivity”类是MyHandler类所在的外部类，即所在的activity
        WeakReference<QuickSettingService> mActivity;
        MyHandler(QuickSettingService activity) {
            mActivity = new WeakReference<QuickSettingService>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            QuickSettingService theActivity = mActivity.get();
            if(theActivity!=null){
                switch (msg.what) {
                    case 0:

                        if ((theActivity.lunarday == 30) || (theActivity.lunarday == 1))
                            theActivity.result = "New Moon";
                        else if ((theActivity.lunarday > 1) && (theActivity.lunarday < 7))
                            theActivity.result = "Waxing Crescent";
                        else if ((theActivity.lunarday == 7) || (theActivity.lunarday == 8))
                            theActivity.result = "First Quarter";
                        else if ((theActivity.lunarday > 8) && (theActivity.lunarday < 15))
                            theActivity.result = "Waxing Gibbous";
                        else if ((theActivity.lunarday == 15) || (theActivity.lunarday == 16))
                            theActivity.result = "Full Moon";
                        else if ((theActivity.lunarday > 16) && (theActivity.lunarday < 22))
                            theActivity.result = "Waning Gibbous";
                        else if ((theActivity.lunarday == 22) || (theActivity.lunarday == 23))
                            theActivity.result = "3rd Quarter";
                        else theActivity.result = "Waning Crescent";
                        if (theActivity.lunarday == -1) {
                            theActivity.result = "Moon";
                            theActivity.name = "moon";
                        } else theActivity.name = "moon_" + theActivity.result.toLowerCase().replaceAll("\\s", "_");
                        //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                        theActivity.getQsTile().setLabel(theActivity.result);
                        ApplicationInfo appInfo = theActivity.getApplicationInfo();
                        int resID = theActivity.getResources().getIdentifier(theActivity.name, "drawable", appInfo.packageName);
                        theActivity.getQsTile().setIcon(Icon.createWithResource(theActivity.getApplicationContext(), resID));
                        theActivity.getQsTile().updateTile();
                        break;

                    default:
                        break;
                }
            }
        }
    };

        MyHandler handler = new MyHandler(this) {

    };

    /*
    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }
    private String GetMoonPhaseJson() throws Exception{
        String path="http://farmsense-prod.apigee.net/v1/moonphases/?d="+Integer.toString((int) (System.currentTimeMillis() / 1000));
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            String json = new String(data); // 把字符串组转换成字符串
            json = json.replaceAll("^.*\"Phase\":\"([0-9a-zA-Z\\s]+)\".*$", "$1");
            return json;
        }else{
            return "null";
        }
    }
    */
}
