package com.ex.group.folder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import com.ex.group.folder.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ex.group.folder.utility.CommonUtil.checkNull;

/**
 * Created by JSP 2018.12.08
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    //토픽 주제를 6가지로 나눠서 작성 하였음

    SimpleDateFormat dt = new SimpleDateFormat("aa hh:mm");
    String today;
    private final String MENU = "/topics/menu";                     //본사 식단
    private final String MENURESEARCH = "/topics/menuResearch";       //연구 식단
    private final String BUSINESSMSG = "/topics/businessMessage";     //경영메세지
    private final String HOLIDAY = "/topics/holiday";            //경조사
    private final String ELECAPP = "elecapp";
    private final String MEMOAPP = "memo";
//    private final String ELECAPP = "/topics/elecapp";
//    private final String MEMOAPP = "/topics/memoapp";

    private final String TESTHOLIDAY = "/topics/testHoliday";   ///테스트를 위한 토픽


   //안드로이드  오레오 이상에서 알림채널을 등록하기 위한 속성값
    public static final int NOTI_ID = 1;
    final String CHANNEL_ID = " FCM_CHANNEL";
    final String CHANNEL_NAME = "모바일오피스 알림서비스";
    final int IMPORTANCE_HIGH = NotificationManager.IMPORTANCE_HIGH;
    final int IMPORTANCE_LOW = NotificationManager.IMPORTANCE_NONE;


    final static String TAG = "FirebaseMessage";

    final static String TAG(String str) {
        return TAG + "[" + str + "]";
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("JSJ","onMessageReceived() ");
        Log.d("JSJ","onMessageReceived() - getFrom : "+remoteMessage.getFrom().toString());
        Log.d("JSJ","onMessageReceived() - getData : "+remoteMessage.getData());
//        String topic = remoteMessage.getFrom();
        String topic = remoteMessage.getData().get("topic");
//        topic = MEMOAPP;

//        Log.d(TAG, "onMessageReceived() - TOPIC_ELECAPP : "+getState("TOPIC_ELECAPP"));
        Log.d(TAG, "onMessageReceived() - TOPIC_MEMOAPP : "+getSharedString("TOPIC_MEMOAPP"));

        Log.d("JSJ","JSJ step 1 : topic1 : " + checkNull(topic));
        Log.d("JSJ","JSJ step 1 : topic1 : " + topic);
        Log.d("JSJ","JSJ step 1 : topic2 : " + remoteMessage.toString());
//        Log.d("JSJ","JSJ step 1 : topic3 : " + checkNull(remoteMessage.getNotification().toString()));
        Log.d("JSJ","JSJ step 1 : topic4 : " + remoteMessage.getData().toString());
//        Log.d("JSJ","JSJ step 1 : topic5 : " + remoteMessage.getNotification().getBody());
//        Log.d("JSJ","JSJ step 1 : topic6 : " + remoteMessage.getNotification().getTitle());

        Log.e(TAG, "msg.getdata====>>" + remoteMessage.getData());
        Log.e(TAG, "msg.getdata====>>" + remoteMessage.getData().isEmpty());
        Log.e(TAG, "msg ONE   ==" + remoteMessage.getData().get("msg1"));
        Log.e(TAG, "msg TWO   ==" + remoteMessage.getData().get("msg2"));
        Log.e(TAG, "msg THREE ==" + remoteMessage.getData().get("msg3"));
        Log.e(TAG, "msg FOUR ==" + remoteMessage.getData().get("msg4"));
        Log.e(TAG, "msg FIVE==" + remoteMessage.getData().get("msg5"));
        Log.e(TAG, "msg SIX ==" + remoteMessage.getData().get("msg6"));

        Log.d(TAG, "");

        today = dt.format(new Date());
        Log.e(TAG, "remoteMessage.getData().size()====>>" + remoteMessage.getData().size());
        boolean data = remoteMessage.getData().size() > 0 ? true : false;//데이터 유무 확인

        if (isBanSetted() && isBanTime()) {
            if (isBanTime()) {
                Log.e(TAG("MESSAGE  "), ": YOU SETTED BANTIME 'YES' AND NOW IT's BAMTIME");
            }
        } else {
            Log.d("JSJ","JSJ step 2");

            if (isBanSetted() && !isBanTime()) {
                Log.e(TAG("MESSAGE  "), ": YOU SETTED BANTIME 'YES' BUT BANTIME is NOT NOW");
            } else {
                Log.e(TAG("MESSAGE  "), ": YOU DIDNT SET BANTIME");
            }
            Log.d("JSJ","JSJ step 3");
            Log.d("JSJ","JSJ step 3 - data : "+data);
            Log.d("JSJ","JSJ step 3 - topic : "+topic);
            if (data) {
                switch (topic) {

                    case ELECAPP:
                        Log.d("JSJ","JSJ step 4");
                        if(getSharedString("TOPIC_ELECAPP").equals("Y")) {
                            noti_elec(remoteMessage);
                        }
                        break;

                    case MEMOAPP:
                        Log.d("JSJ","JSJ step 5");
                        if(getSharedString("TOPIC_MEMOAPP").equals("Y")) {
                            noti_memo(remoteMessage);
                        }
                        break;

                    case MENU:
                        noti_menu(remoteMessage);
                        break;

                    case MENURESEARCH:
                        noti_menuResearch(remoteMessage);
                        break;

                    case BUSINESSMSG:
                        noti_businessMsg(remoteMessage);
                        break;

                    case HOLIDAY:
                        noti_holiday(remoteMessage);
                        break;

                    case TESTHOLIDAY:
                        noti_holiday(remoteMessage);
                        break;
                    default:
                        Log.d("JSJ", "default");
//                        String pushTitle = checkNull(remoteMessage.getData().get("title"));
//                        if(pushTitle.equals("")) {
//                            pushTitle = remoteMessage.getNotification().getTitle();
//                        }
//                        Log.d("JSJ", "default - pushTitle : "+pushTitle);
////                if(remoteMessage.getNotification().getTitle().contains("메모")){
//                        if(pushTitle.contains("메모")){
//                            topic = MEMOAPP;
//                            noti_memo(remoteMessage);
//                        }else{
//                            topic = ELECAPP;
//                            noti_elec(remoteMessage);
//                        }
                        break;

                }



            }else{
//                topic = remoteMessage.getData()
                Log.d("JSJ","JSJ step else " + remoteMessage.getData().get("topic"));

                String pushTitle = checkNull(remoteMessage.getData().get("title"));
                if(pushTitle.equals("")) {
                    pushTitle = remoteMessage.getNotification().getTitle();
                }
//                if(remoteMessage.getNotification().getTitle().contains("메모")){
                if(pushTitle.contains("메모")){
                    topic = MEMOAPP;
                }else{
                    topic = ELECAPP;
                }


                switch (topic) {
                    case ELECAPP:
                        Log.d("JSJ", "JSJ step 4");
                        noti_elec(remoteMessage);
                        break;

                    case MEMOAPP:
                        Log.d("JSJ", "JSJ step 5");
                        noti_memo(remoteMessage);
                        break;
                }

            }

        }

    }

    private void noti_memo(RemoteMessage rmm) {
        Log.d("JSJ","JSJ noti_memo called");
        Bundle bun = new Bundle();
        bun.putString("TOPIC", MEMOAPP);

//        String title = checkNull(rmm.getNotification().getTitle());
//        String body = checkNull(rmm.getNotification().getBody());
        String title = rmm.getData().get("title");
        String body = rmm.getData().get("body");
        if(checkNull(title).equals("")) {
            title = checkNull(rmm.getNotification().getTitle());
        }
        if(checkNull(body).equals("")) {
            body = checkNull(rmm.getNotification().getBody());
        }

        Log.d("JSJ","JSJ noti_memo called - title : "+title);
        Log.d("JSJ","JSJ noti_memo called - body : "+body);

        bun.putString("title",title);
        bun.putString("body",body);

        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", title+"\n"+body);
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        bun.putString("msg5", rmm.getData().get("msg5"));
        bun.putString("msg6", rmm.getData().get("msg6"));

        sendNotification(bun);
    }

    private void noti_elec(RemoteMessage rmm) {

        Log.d("JSJ","JSJ noti_elec called");

        Bundle bun = new Bundle();
        bun.putString("TOPIC", ELECAPP);

//        String title = checkNull(rmm.getNotification().getTitle());
//        String body = checkNull(rmm.getNotification().getBody());
        String title = rmm.getData().get("title");
        String body = rmm.getData().get("body");
        if(checkNull(title).equals("")) {
            title = checkNull(rmm.getNotification().getTitle());
        }
        if(checkNull(body).equals("")) {
            body = checkNull(rmm.getNotification().getBody());
        }

        Log.d("JSJ","JSJ noti_elec called - title : "+title);
        Log.d("JSJ","JSJ noti_elec called - body : "+body);

        bun.putString("title",title);
        bun.putString("body",body);
        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", title+"\n"+body);
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        bun.putString("msg5", rmm.getData().get("msg5"));
        bun.putString("msg6", rmm.getData().get("msg6"));
        sendNotification(bun);
    }


    private void noti_menu(RemoteMessage rmm) {
        Bundle bun = new Bundle();
        bun.putString("TOPIC", MENU);

        String title = rmm.getData().get("title");
        String body = rmm.getData().get("body");
        if(checkNull(title).equals("")) {
            title = checkNull(rmm.getNotification().getTitle());
        }
        if(checkNull(body).equals("")) {
            body = checkNull(rmm.getNotification().getBody());
        }

        bun.putString("title",title);
        bun.putString("body",body);
        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", rmm.getData().get("msg2"));
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        bun.putString("msg5", rmm.getData().get("msg5"));
        bun.putString("msg6", rmm.getData().get("msg6"));
        sendNotification(bun);
    }

    private void noti_menuResearch(RemoteMessage rmm) {
        Bundle bun = new Bundle();
        bun.putString("TOPIC", MENURESEARCH);
        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", rmm.getData().get("msg2"));
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        bun.putString("msg5", rmm.getData().get("msg5"));
        bun.putString("msg6", rmm.getData().get("msg6"));
        sendNotification(bun);
    }

    private void noti_businessMsg(RemoteMessage rmm) {
        Bundle bun = new Bundle();
        bun.putString("TOPIC", BUSINESSMSG);
        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", rmm.getData().get("msg2"));
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        sendNotification(bun);
    }

    private void noti_holiday(RemoteMessage rmm) {
        Bundle bun = new Bundle();
        bun.putString("TOPIC", HOLIDAY);
        bun.putString("msg1", rmm.getData().get("msg1"));
        bun.putString("msg2", rmm.getData().get("msg2"));
        bun.putString("msg3", rmm.getData().get("msg3"));
        bun.putString("msg4", rmm.getData().get("msg4"));
        sendNotification(bun);
    }

    private int SetAppIcon() {
        if (Build.MANUFACTURER.toLowerCase().equals("samsung") || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.app_icon_launcher;
        } else {
            return R.drawable.app_icon_launcher_trans5;
        }
    }

    private void sendNotification(Bundle bundle) {
        Log.d(TAG, "");
        Log.d("JSJ","sendNotification - bundle : "+bundle);
        Intent intent2Menu = new Intent(getApplicationContext(), com.ex.group.folder.MenuActivity.class);
        PendingIntent menuPending = PendingIntent.getActivity(this, 0, intent2Menu, PendingIntent.FLAG_ONE_SHOT);

        Intent intent2Launcher = new Intent(getApplicationContext(), com.ex.group.folder.IntroActivity.class);
        intent2Launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2Launcher.putExtra("fromNoti", "Y");
        PendingIntent mainPending = PendingIntent.getActivity(this, 0 /* Request code */, intent2Launcher, PendingIntent.FLAG_ONE_SHOT);


        /** 첫번째 !!!
         * NotificationManager 선언 및 초기화를 해준다.* */
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //커스텀 뷰를 활용 한다.
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notificationpushview);
        contentView.setImageViewResource(R.id.icon, R.drawable.app_icon_launcher);

        RemoteViews menuView = new RemoteViews(getPackageName(), R.layout.notificationpushmenuview);
        menuView.setImageViewResource(R.id.icon, R.drawable.app_icon_launcher);




        /* 두번째 !!!
         * 안드로이드 O 를 기준으로 알림 채널을 등록해준다.
         * 오레오 이 후 버전에서부터는 알림 설정을 해주지 않으면 NOTIFICATION이 울리지 않는다 유념해야한다.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{1, 1000});
            mChannel.setShowBadge(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(mChannel);


            Notification.Builder builder;


            Log.d("JSJ","JSJ sendNotification  msg bundle " + bundle.getString("TOPIC"));
            switch (bundle.getString("TOPIC")) {

                case ELECAPP:
                    contentView.setOnClickPendingIntent(R.id.more, menuPending);
                    contentView.setTextViewText(R.id.msg_title, "MobileOffice - 전자결재");
                    contentView.setTextViewText(R.id.msg_time, today);
                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    contentView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg5, bundle.getString("msg5"));
                    contentView.setTextViewText(R.id.msg6, bundle.getString("msg6"));


                    builder = new Notification.Builder(getBaseContext(), CHANNEL_ID)
                            .setContentTitle("MobileOffice - 전자결재")
                            .setContentText(bundle.getString("title")+"\n"+bundle.getString("body"))
                            .setSmallIcon(SetAppIcon())
                            .setAutoCancel(true)
                            .setChannelId(CHANNEL_ID)
                            .setColor(Color.parseColor("#8866aa"))
                            .setContentIntent(mainPending)
                            .setCustomBigContentView(contentView);
                    break;

                case MEMOAPP:
                    contentView.setOnClickPendingIntent(R.id.more, menuPending);
                    contentView.setTextViewText(R.id.msg_title, "MobileOffice - 메모보고");
                    contentView.setTextViewText(R.id.msg_time, today);
                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    contentView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg5, bundle.getString("msg5"));
                    contentView.setTextViewText(R.id.msg6, bundle.getString("msg6"));


                    builder = new Notification.Builder(getBaseContext(), CHANNEL_ID)
                            .setContentTitle("MobileOffice - 메모보고")
                            .setContentText(bundle.getString("title")+"\n"+bundle.getString("body"))
                            .setSmallIcon(SetAppIcon())
                            .setAutoCancel(true)
                            .setChannelId(CHANNEL_ID)
                            .setColor(Color.parseColor("#8866aa"))
                            .setContentIntent(mainPending)
                            .setCustomBigContentView(contentView);
                    break;

                case MENU:
                    menuView.setOnClickPendingIntent(R.id.more, menuPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 오늘의식단(본사)");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg6"));

                    builder = new Notification.Builder(getBaseContext(), CHANNEL_ID)
                            .setContentTitle("MobileOffice - 오늘의식단(본사)")
                            .setSmallIcon(SetAppIcon())
                            .setAutoCancel(true)
                            .setChannelId(CHANNEL_ID)
                            .setColor(Color.parseColor("#8866aa"))
                            .setCustomBigContentView(menuView);
                    break;

                case MENURESEARCH:
                    menuView.setOnClickPendingIntent(R.id.more, menuPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 오늘의식단(도로교통연구원)");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg6"));

                    builder = new Notification.Builder(getBaseContext(), CHANNEL_ID)
                            .setSmallIcon(SetAppIcon())
                            .setContentTitle("MobileOffice - 오늘의식단(도로교통연구원)")
                            .setColor(Color.parseColor("#8866aa"))
                            .setAutoCancel(false)
                            .setCustomBigContentView(menuView);

                    break;

                case BUSINESSMSG:
                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, "작성자:" + bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg4, "작성일시:" + bundle.getString("msg3"));
                    builder = new Notification.Builder(this, CHANNEL_ID)
                            .setSmallIcon(SetAppIcon())
                            .setContentTitle("MobileOffice - " + bundle.getString("msg1"))
                            .setColor(Color.parseColor("#8866aa"))
                            .setStyle(new Notification.BigTextStyle())
                            .setAutoCancel(true).setContentIntent(mainPending)
                            .setCustomBigContentView(contentView);
                    break;

                case HOLIDAY:
                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, "작성자:" + bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg4, "작성일시:" + bundle.getString("msg3"));
                    builder = new Notification.Builder(this, CHANNEL_ID)
                            .setSmallIcon(SetAppIcon())
                            .setContentTitle("MobileOffice - " + bundle.getString("msg1"))
                            .setColor(Color.parseColor("#8866aa"))
                            .setStyle(new Notification.BigTextStyle())
                            .setAutoCancel(true).setContentIntent(mainPending)
                            .setCustomBigContentView(contentView);

                    break;

                default:
                    builder = new Notification.Builder(this, CHANNEL_ID)
                            .setSmallIcon(SetAppIcon())
                            .setStyle(new Notification.BigTextStyle())
                            .setAutoCancel(true).setContentIntent(mainPending)
                            .setCustomContentView(contentView);
                    break;
            }

            notificationManager.notify(NOTI_ID, builder.build());
        } else {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification.Builder notification;
            notification = new Notification.Builder(FirebaseMessagingService.this)
                    .setSmallIcon(SetAppIcon())
                    .setVibrate(new long[]{1, 1000});
            switch (bundle.getString("TOPIC")) {

                case ELECAPP:
                    menuView.setOnClickPendingIntent(R.id.more, mainPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 전자결재");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("title"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("body"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg6"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle("MobileOffice - 전자결재")
                                .setCustomBigContentView(menuView);
                    } else {

                        notification.setContentTitle("MobileOffice - 전자결재")
                                .setContentText(bundle.getString("body"))
                                .setContentIntent(menuPending);
                    }


                    break;

                case MEMOAPP:
                    menuView.setOnClickPendingIntent(R.id.more, mainPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 메모보고");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("title"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("body"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg6"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle("MobileOffice - 메모보고")
                                .setCustomBigContentView(menuView);
                    } else {
                        notification.setContentTitle("MobileOffice - 메모보고")
                                .setContentText(bundle.getString("body"))
                                .setContentIntent(menuPending);
                    }
                    break;
                case MENU:
                    menuView.setOnClickPendingIntent(R.id.more, mainPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 오늘의식단(본사)");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg6"));


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle("MobileOffice - 오늘의식단(본사)")
                                .setCustomBigContentView(menuView);
                    } else {

                        notification.setContentTitle("MobileOffice - 오늘의식단(본사)")
                                .setContentText(bundle.getString("msg2"))
                                .setContentIntent(menuPending);
                    }


                    break;

                case MENURESEARCH:

                    menuView.setOnClickPendingIntent(R.id.more, menuPending);
                    menuView.setTextViewText(R.id.msg_title, "MobileOffice - 오늘의식단(교통연구원)");
                    menuView.setTextViewText(R.id.msg_time, today);
                    menuView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    menuView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    menuView.setTextViewText(R.id.msg3, bundle.getString("msg3"));
                    menuView.setTextViewText(R.id.msg4, bundle.getString("msg4"));
                    menuView.setTextViewText(R.id.msg6, bundle.getString("msg5"));
                    menuView.setTextViewText(R.id.msg5, bundle.getString("msg6"));


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle("MobileOffice - 오늘의식단(교통연구원)")
                                .setCustomBigContentView(menuView);
                    } else {

                        notification
                                .setContentTitle("MobileOffice - 오늘의식단(교통연구원)")
                                .setContentText(bundle.getString("msg2"))
                                .setContentIntent(menuPending);
                    }

                    break;

                case BUSINESSMSG:

                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, "작성자:" + bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg4, "작성일시:" + bundle.getString("msg3"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle(bundle.getString("msg1"))
                                .setContentIntent(mainPending)
                                .setCustomBigContentView(contentView);
                    } else {
                        notification.setContentTitle(bundle.getString("msg1"))
                                .setContentIntent(mainPending).setContent(contentView);
                    }

                    break;

                case HOLIDAY:

                    contentView.setTextViewText(R.id.msg1, bundle.getString("msg1"));
                    contentView.setTextViewText(R.id.msg2, bundle.getString("msg2"));
                    contentView.setTextViewText(R.id.msg3, "작성자:" + bundle.getString("msg4"));
                    contentView.setTextViewText(R.id.msg4, "작성일시:" + bundle.getString("msg3"));


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notification.setContentTitle(bundle.getString("msg1"))
                                .setContentIntent(mainPending)
                                .setCustomBigContentView(contentView);
                    } else {

                        notification.setContentTitle(bundle.getString("msg1"))
                                .setContentIntent(mainPending).setContent(contentView);
                    }


                    break;

                default:
                    notification = new Notification.Builder(FirebaseMessagingService.this)
                            .setSmallIcon(SetAppIcon())
                            .setVibrate(new long[]{100, 100, 100, 300, 100, 500});
                    break;

            }


            //Issue the notification
            mNotificationManager.notify(NOTI_ID, notification.build());


        }

        /*
         * 세번째 !!!
         * NOtification에 설정값들을 적용 시켜준다.
         * */


    }

    public String getSharedString(String name) {
        SharedPreferences user = getBaseContext().getSharedPreferences("USERINFO", MODE_PRIVATE);
        if (!user.contains(name)) {
            setSharedString(name, "");
        }
        final String value = user.getString(name, "");
        Log.e(TAG("GET_SHARED" + name), value);
        return value;
    }

    public void setSharedString(String name, String value) {
        SharedPreferences user = getBaseContext().getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString(name, value);
        Log.e(TAG("SET_SHARED" + name), value);
        edit.commit();

    }

    private boolean isBanTime() {

        if (!getSharedString("START_HOUR").equals("")) {
            String sh;
            if (getSharedString("START_HOUR").length() == 1) {
                sh = "0" + getSharedString("START_HOUR");
            } else {
                sh = getSharedString("START_HOUR");
            }
            String sm;
            if (getSharedString("START_MINUTE").length() == 1) {
                sm = "0" + getSharedString("START_MINUTE");
            } else {
                sm = getSharedString("START_MINUTE");
            }
            String eh;
            if (getSharedString("END_HOUR").length() == 1) {
                eh = "0" + getSharedString("END_HOUR");
            } else {
                eh = getSharedString("END_HOUR");
            }
            String em;
            if (getSharedString("END_MINUTE").length() == 1) {
                em = "0" + getSharedString("END_MINUTE");
            } else {
                em = getSharedString("END_MINUTE");
            }
            String start = sh + sm;
            String end = eh + em;
            String current;
            SimpleDateFormat dateFormat = new SimpleDateFormat("kkmm");
            current = dateFormat.format(new Date()).toString();

            int startTime = Integer.parseInt(start);
            int endTime = Integer.parseInt(end);
            int currentTime = Integer.parseInt(current);
            Log.e(TAG("START"),":"+start);
            Log.e(TAG("END"),":"+end);
            Log.e(TAG("CURRENT"),":"+current);

            if (startTime < endTime && startTime < currentTime && currentTime < endTime) {
                /*
                 * 조건 1 시작시간보다 종료시간이 크다
                 * 조건 2 시작시간보다 현재시간이 크다
                 * 조건 3 죵료시간보다 현재시간이 작다
                 * */
                return true;
            } else if ((startTime > endTime) && (startTime < currentTime || currentTime < endTime)) {
                /*
                 * 조건 1 시작시간 보다 종료시간이 작다.
                 * 조건 2 시작시간 보다 현재시간이 크다. 혹은 종료시간 보다 현재시간이 작다.
                 * */
                return true;

            } else {

                return false;
            }


        } else {
            return false;
        }


    }

    private boolean isBanSetted() {
        if (getSharedString("MESSAGE_BANTIME").equals("Y")) {
            /*조건 1  MESSAGE_BANTIME 이 Y 로 설정 되어있다.*/

            return true;
        } else {
            return false;
        }


    }

    /*public boolean getState(String str){
        if(getSharedString(str).equals("Y")){
            return true;
        }else{
            return false;
        }
    }*/

}
