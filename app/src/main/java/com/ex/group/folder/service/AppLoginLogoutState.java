package com.ex.group.folder.service;

/*
 *                                                                            -CREATED BY JSP 2018.11
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.utility.ClientUtil;
import com.skt.pe.common.vpn.SGVPNConnection;
import com.sktelecom.ssm.lib.SSMLib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGOUT;

public class AppLoginLogoutState extends Service {


    private String TAG = "===APPLOGINSTATUS===";

    private String TAG(String Name) {

        return TAG + "[" + Name + "]";
    }


    @Override
    public void onCreate() {


        super.onCreate();

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG("onTaskRemoved"), "rootIntent" + rootIntent);
        SharedPreferences user = getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString("LOGINSTATE", "LOGOUT");
        edit.commit();
        Log.e(TAG("LOGINSTATE"), "LOGOUT");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        Log.e(TAG("onDestroy"), "");

        SharedPreferences user = getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString("LOGINSTATE", "LOGOUT");
        edit.commit();
        Log.e(TAG("LOGINSTATE"), "LOGOUT");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
