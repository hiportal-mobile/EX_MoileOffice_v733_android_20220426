package com.ex.group.folder.service;

/*
 *                                                                            -CREATED BY JSP 2018.11
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.dialog.CommonDialog_oneButton;
import com.ex.group.folder.utility.ClientUtil;
import com.skt.pe.common.vpn.SGVPNConnection;
import com.sktelecom.ssm.lib.SSMLib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static com.ex.group.folder.utility.ClientUtil.SGN_PACKAGE;
import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGIN;
import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGOUT;

public class AppStateService extends Service {

    public static  TimerTask saveVpnSErvice;
    public static Timer secondTimer;
    private final IBinder mBinder = new LocalBinder();



    public static SGVPNConnection vpnConn;
    public static IBinder tempService = null;
    private SgnServiceConnection mConnection;
    private class SgnServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("", "===================onServiceConnected===================");
            tempService = iBinder;
            if(vpnConn == null)
            {
                vpnConn = SGVPNConnection.getInstance(tempService);
                Log.i("","Service Connected");
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("SgnServiceConnection","==========onServiceDisconneted=============");
            System.out.println("Service Disconneted");
            tempService = null;

            Intent intent  = new Intent(ClientUtil.SGVPN_API);
            intent.setPackage(ClientUtil.SGN_PACKAGE);
            if(!bindService(intent, mConnection,BIND_AUTO_CREATE)){
                Log.i(TAG,"servic binding error");

            }else{
                try{
                    startService(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
    public class LocalBinder extends Binder {
        public AppStateService getService(){
            return AppStateService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG("onBind"),"BINDSUCCESS");

        return mBinder;

    }


    private String TAG="===APPSTATESERVICE===";
    private String TAG(String Name){
        String tag ="===APPSTATESERVICE===";
        return tag+"["+Name+"]";
    }

    private int SetAppIcon() {
        if (Build.MANUFACTURER.toLowerCase().equals("samsung") || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.app_icon_launcher;
        } else {
            return R.drawable.app_icon_launcher_trans5;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //  서비스 시작 시 에 포그라운드로 전환 한다.  startService는 MainActivity에서 VPN 연결 성공시 호출한다.
        //  포그라운드로 전환 될 때 노티피케이션을 띄워 VPN 을 백그라운드에서도 죽일 수 있게 만들었다.


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            Notification notification;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("channel_vpn","VPN 서비스", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{30,30,30});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

           Notification.Builder mBuilder;
            PendingIntent mPendingIntent;
            mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class).putExtra("finishApp","Y").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT);




            mBuilder = new Notification.Builder(getBaseContext(), "channel_vpn").setContentTitle("title")
                    .setContentText("body").setSmallIcon(SetAppIcon()).setAutoCancel(true).setContentIntent(mPendingIntent);

            RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.notificationview);

            contentView.setImageViewResource(R.id.icon,R.drawable.app_icon_launcher);
            SpannableStringBuilder stringBuilder;
            stringBuilder = new SpannableStringBuilder("모바일오피스" + " - 업무망 연결됨");
            /*stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 7,
                    stringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
            contentView.setTextViewText(R.id.appName,stringBuilder);
            contentView.setOnClickPendingIntent(R.id.vpn_disconnet_button,mPendingIntent);
            mBuilder.setContent(contentView);


            notification = new Notification.Builder(AppStateService.this)
                    .setSmallIcon(SetAppIcon())
                    .setChannelId("channel_vpn")
                    .setContentTitle("title")
                    .setContentText("body").setAutoCancel(true)
                    .build();
            notification.contentView =contentView;




            startForeground(1,notification);
        }else {
            Notification notification;
            PendingIntent mPendingIntent;
            mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class).putExtra("finishApp","Y").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.notificationview);

            contentView.setImageViewResource(R.id.icon,R.drawable.app_icon_launcher);
            SpannableStringBuilder stringBuilder;
            stringBuilder = new SpannableStringBuilder("모바일오피스" + " - 업무망 연결됨");
            /*stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 7,
                    stringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
            contentView.setTextViewText(R.id.appName,stringBuilder);
            contentView.setOnClickPendingIntent(R.id.vpn_disconnet_button,mPendingIntent);



            notification = new Notification.Builder(AppStateService.this)
                    .setSmallIcon(SetAppIcon())
                    .setContentTitle("title")
                    .setContentText("body").setAutoCancel(true)
                    .build();
            notification.contentView =contentView;
            startForeground(1,notification);




        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {


        super.onCreate();







        Log.e(TAG("onCreate"),"SERVICECREATED");


    }

  @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        /*
        int TRIM_MEMORY_BACKGROUND = 40;
        int TRIM_MEMORY_COMPLETE = 80;
        int TRIM_MEMORY_MODERATE = 60;
        int TRIM_MEMORY_RUNNING_CRITICAL = 15;
        int TRIM_MEMORY_RUNNING_LOW = 10;
        int TRIM_MEMORY_RUNNING_MODERATE = 5;
        int TRIM_MEMORY_UI_HIDDEN = 20;*/
        Log.e(TAG("onTrimMemory"), String.valueOf(level));
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_UI_HIDDEN]");
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_RUNNING_MODERATE]");

                break;
            case TRIM_MEMORY_RUNNING_LOW:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_RUNNING_LOW]");

                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_RUNNING_CRITICAL]");
                break;
            case TRIM_MEMORY_BACKGROUND:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_BACKGROUND]");
                break;
            case TRIM_MEMORY_MODERATE:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_MODERATE]");
                break;
            case TRIM_MEMORY_COMPLETE:
                Log.e(TAG("onTrimMemory"), "STATE [TRIM_MEMORY_COMPLETE]");
                break;
        }




        if (level > TRIM_MEMORY_COMPLETE) {

            if (MainActivity.vpnConn != null) {

                MainActivity.vpnConn.disconnection();
                SSMLib ssmLib = SSMLib.getInstance(getBaseContext());
                ssmLib.setLoginStatus(LOGOUT);
                Log.e(TAG("onTrimMemmory"), "VPN 종료 성공");
                MainActivity.vpnConn.disconnection();

            }else{
                mConnection = new SgnServiceConnection();
                Intent intent  = new Intent(ClientUtil.SGVPN_API);
                intent.setPackage(ClientUtil.SGN_PACKAGE);
                bindService(intent, mConnection,BIND_AUTO_CREATE);
                if(!bindService(intent,mConnection,BIND_AUTO_CREATE)){
                    Log.i(TAG,"Service bind error");
                }

                vpnConn = SGVPNConnection.getInstance(tempService);
                Log.e(TAG,"new VPN Connection");

                vpnConn.disconnection();
            }

                SSMLib ssmLib = SSMLib.getInstance(getBaseContext());
                ssmLib.setLoginStatus(LOGOUT);
                Log.e(TAG("onTrimMemmory"), "VPN 종료 재시도 성공");
                SharedPreferences user = getSharedPreferences("USERINFO", MODE_PRIVATE);
                SharedPreferences.Editor edit = user.edit();
                edit.putString("LOGINSTATE", "LOGOUT");
                edit.commit();
                Log.e(TAG("LOGINSTATE"), "LOGOUT");

            }







        }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG("onTaskRemoved"), "rootIntent"+rootIntent);


        if (MainActivity.vpnConn != null) {

            MainActivity.vpnConn.disconnection();
            SSMLib ssmLib = SSMLib.getInstance(getBaseContext());
            ssmLib.setLoginStatus(LOGOUT);

            Log.e(TAG("onTaskRemoved"), "VPN 종료 성공");
        }
        else{
            Log.e(TAG("onTaskRemoved"), "VPN 종료 재시도");
            mConnection = new SgnServiceConnection();
            Intent intent  = new Intent(ClientUtil.SGVPN_API);
            intent.setPackage(ClientUtil.SGN_PACKAGE);
            bindService(intent, mConnection,BIND_AUTO_CREATE);
            if(!bindService(intent,mConnection,BIND_AUTO_CREATE)){
                Log.i(TAG,"Service bind error");
            }

            vpnConn = SGVPNConnection.getInstance(tempService);
            Log.e(TAG,"new VPN Connection  Status : "+String.valueOf(vpnConn.getStatus()));

            vpnConn.disconnection();

            Log.e(TAG("onTaskRemoved"), "VPN 종료 재시도 성공");
            unbindService(mConnection);
        }




        SharedPreferences user = getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString("LOGINSTATE", "LOGOUT");
        edit.commit();
        Log.e(TAG("LOGINSTATE"), "LOGOUT");

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            stopForeground(true);
            stopSelf();}

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        Log.e(TAG("onDestroy"), "");


        if (MainActivity.vpnConn != null) {

            MainActivity.vpnConn.disconnection();
            SSMLib ssmLib = SSMLib.getInstance(getBaseContext());
            ssmLib.setLoginStatus(LOGOUT);
            Log.e(TAG("onDestroy"), "VPN 종료 성공");

        }
        else{
            mConnection = new SgnServiceConnection();
            Intent intent  = new Intent(ClientUtil.SGVPN_API);
            intent.setPackage(ClientUtil.SGN_PACKAGE);
            try {
                if (!bindService(intent, mConnection, BIND_AUTO_CREATE)) {
                    Log.i(TAG, "Service bind error");
                }

                vpnConn = SGVPNConnection.getInstance(tempService);
                Log.e(TAG, "new VPN Connection");

                vpnConn.disconnection();
            }catch (Exception e){}

            unbindService(mConnection);

        }




        SharedPreferences user = getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString("LOGINSTATE", "LOGOUT");
        edit.commit();
        Log.e(TAG("LOGINSTATE"), "LOGOUT");

    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            stopForeground(true);
            stopSelf();}

        Log.e(TAG("onUnBIND"),"");
        return super.onUnbind(intent);
    }

    public void AidleNetwork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wi_fi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobile.isConnected() || wi_fi.isConnected()) {
            try {


           Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",boolean.class);
           setMobileDataEnabledMethod.setAccessible(false);
           setMobileDataEnabledMethod.invoke(manager,false);}catch (Exception e){e.printStackTrace();}
        } else {

        }

    }
    private void setMobileDataEnabled(Context context, boolean enabled) throws Exception {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }


}
