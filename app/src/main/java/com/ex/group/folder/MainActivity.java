package com.ex.group.folder;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Dimension;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ex.group.approval.easy.activity.ApprovalMainActivity;
import com.ex.group.board.activity.BoardListActivity;
import com.ex.group.board.data.UserInfo;
import com.ex.group.board.util.Global;
import com.ex.group.elecappmemo.ElecMemoAppWebViewActivity;
import com.ex.group.elecappmemo.NoticeMemoDialog;
import com.ex.group.folder.categoryList.AppDiffCallback;
import com.ex.group.folder.categoryList.AppdataList;
import com.ex.group.folder.dialog.CommonDialog;
import com.ex.group.folder.dialog.CommonDialog_oneButton;
import com.ex.group.folder.dialog.CustomprogressDialog;
import com.ex.group.folder.dragNdropHelper.OnStartDragListener;
import com.ex.group.folder.dragNdropHelper.TouchAdapter;
import com.ex.group.folder.dragNdropHelper.TouchCallback;
import com.ex.group.folder.dragNdropHelper.TouchViewHolder;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.ElecMemoAlarmInfo;
import com.ex.group.folder.retrofitclient.pojo.MemoNoticePopInfo;
import com.ex.group.folder.retrofitclient.pojo.RequestInitInfo;
import com.ex.group.folder.retrofitclient.pojo.RequestMailCount;
import com.ex.group.folder.retrofitclient.pojo.StackRunningAppLog;
import com.ex.group.folder.service.AppLoginLogoutState;
import com.ex.group.folder.service.AppStateService;
import com.ex.group.folder.utility.APPINFODB;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.ClientUtil;
import com.ex.group.folder.utility.CommonUtil;
import com.ex.group.folder.utility.Constants;
import com.ex.group.folder.utility.LogMaker;
import com.ex.group.mail.activity.EmailMainActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.skt.pe.common.vpn.SGVPNConnection;
import com.sktelecom.ssm.lib.SSMLib;
import com.sktelecom.ssm.lib.constants.SSMProtocolParam;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.ex.group.elecappmemo.Global.ALARM_LIST_URL;
import static com.ex.group.folder.utility.ClientUtil.EX_STORE_PACKAGE;
import static com.ex.group.folder.utility.CommonUtil.checkNull;
import static com.ex.group.elecappmemo.Global.ELEC_URL;
import static com.ex.group.elecappmemo.Global.MEMO_URL;
import static com.ex.group.folder.utility.CommonUtil.fontSizeForTablet;
import static com.ex.group.folder.utility.CommonUtil.getDeviceId;
import static com.ex.group.folder.utility.CommonUtil.getDeviceModel;
import static com.ex.group.folder.utility.CommonUtil.getDeviceOs;
import static com.ex.group.folder.utility.CommonUtil.getIsPhone;
import static com.ex.group.folder.utility.CommonUtil.getIsTablet;
import static com.ex.group.folder.utility.CommonUtil.getPrefString;
import static com.ex.group.folder.utility.CommonUtil.getScreenSize;
import static com.ex.group.folder.utility.CommonUtil.getVersionName;
import static com.skt.pe.common.conf.Constants.Status.PERMISSION_CHECK;
import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGIN;
import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGOUT;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnStartDragListener,View.OnTouchListener {
    public boolean mvpnstate = true;
    public ArrayList<AppdataList> dragList_ = new ArrayList<>();

    static Context mContext;
    public APPINFODB appInfoDb;
    public AppStateService myAppStateService;
    public boolean dragMotion=false;

    public static Activity contextMain;
    private final String INSTALLED = "1";
    private final String UNINSTALLED = "0";
    private final String NEED_UPDATE = "2";


    private final String BOARD_PKGNM = "com.ex.group.board";
    private final String MAIL_PKGNM = "com.ex.group.mail";
    private final String EASYAP_PKGNM = "com.ex.group.approval.easy";
    private final String SEARCHMAN_PKGNM = "com.ex.group.addressbook";
    private final String ELEC_PKGNM = "com.ex.elecapp";
    private final String MEMO_PKGNM = "com.ex.memo";

    SpannableStringBuilder stringBuilder;
    WifiManager wifi;
    BluetoothAdapter bAdapter;

    public LinearLayout coachmark_layout;

    int Wi_Fi;
    int bluetooth;

    SSMLib ssmLib;
    int ssmCode;
    Handler handler;

    SharedPreferences userPref;
    private static String VPNpackageName;

    private String unReadMailcount, approvalCount;
    private String searchURL;
    private String VPNHybridURL;
    public TimerTask ConnectionTimmerTask;
    public Timer timer;

    static int counter = 0;
    public static final int CONNECTIONTIME = 15;

    private String TAG(String msg) {
        return TAG + "==(" + msg + ")==";
    }

    private static String TAG = " ◘◘◘◘◘◘  MainActivity  ◘◘◘◘◘◘  ";
    private static String STORETAG = "◘◘◘◘◘ FROMSTORE@MAIN ◘◘◘◘◘";
    public CustomprogressDialog cpd;
    public CustomprogressDialog cpd_parsing;
    public CustomprogressDialog cpd_store;
    public CommonDialog commonDialog;
    public CommonDialog_oneButton commonDialog_oneButton;
    public CommonDialog_oneButton notiDialog;

    public boolean refresh = false;

    DrawerLayout drawer;
    LinearLayout linearLayout_content_main;
    LinearLayout button_bottom_store, button_bottom_trafficstate, button_bottom_diet, button_bottom_topic;
    NavigationView navigationView;
    ImageView button_setting, button_alarm, drawer_header_logout, button_out, iv_calendar;
    TextView text_name, text_id;
    TextView text_date;
    TextView elecmemo_alarmcnt;
    TextView tv_bottom_store, tv_bottom_chart, tv_bottom_news, tv_bottom_menu;
    Calendar time = GregorianCalendar.getInstance();
    DateFormat dateFormat;
    String today;
    ImageView main_bgbg;

    long connectionTime;

    //RecyclerView Call
    public static ArrayList<AppdataList> SampleData;
    public static ArrayList<AppdataList> CopyData;

    public static ArrayList<AppdataList> appList_onVpn;//JSJ VPN 사용 앱 목록 저장용 리스트(스마트정비관리 강제로 제거됨)
    public static ArrayList<AppdataList> appList_offVpn;//JSJ 인터넷 사용 앱 목록 저장용 리스트(스마트정비관리 강제로 추가됨)

    RecyclerView main_recycler_gridview, main_recycler_gridview2;
    MainCategoryRecyclerGridViewAdapter app_adapter;
    MainCategoryRecyclerGridViewAdapter app_adapter2;

    public SwipeRefreshLayout main_swipe_refresh_layout;
    LinearLayout refresh_layout;

//    private int fontSizeForTablet = 25;

    public void setView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        button_setting = (ImageView) findViewById(R.id.button_setting);
        button_alarm = (ImageView) findViewById(R.id.button_alarm);
        button_out = findViewById(R.id.button_out);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
       /* navigationView.getMenu().add(0, Menu.FIRST + 1, Menu.FIRST + 1, "버전정보  - v" + getVersion() + "(최신)").setIcon(R.drawable.menu_icon_info);
        navigationView.getMenu().add(0, Menu.FIRST + 2, Menu.FIRST + 2, "매뉴얼").setIcon(R.drawable.menu_icon_help).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.ex.co.kr/mobile/binary/manual/mobileoffice_manual.pdf"));
                        startActivity(intent);

                        return false;
                    }
                }
        );*/
        navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setTitle(navigationView.getMenu().getItem(2).getSubMenu().getItem(0).getTitle()+"- v" + getVersion() + "(최신)");

        View headerView = navigationView.getHeaderView(0);
        drawer_header_logout = (ImageView) headerView.findViewById(R.id.drawer_header_logout);

        linearLayout_content_main = (LinearLayout) findViewById(R.id.linearLayout_content_main);

        button_bottom_store = (LinearLayout) findViewById(R.id.button_bottom_store);
        button_bottom_trafficstate = (LinearLayout) findViewById(R.id.button_bottom_trafficstate);
        button_bottom_topic = (LinearLayout) findViewById(R.id.button_bottom_topic);
        button_bottom_diet = (LinearLayout) findViewById(R.id.button_bottom_diet);

        tv_bottom_store = (TextView) findViewById(R.id.tv_bottom_store);
        tv_bottom_chart = (TextView) findViewById(R.id.tv_bottom_chart);
        tv_bottom_news = (TextView) findViewById(R.id.tv_bottom_news);
        tv_bottom_menu = (TextView) findViewById(R.id.tv_bottom_menu);

        text_name = (TextView) findViewById(R.id.main_textview_user_welcome);
        text_id = (TextView) findViewById(R.id.main_textview_user_welcome_id);
        text_date = (TextView) findViewById(R.id.main_textview_date);
        iv_calendar = (ImageView) findViewById(R.id.iv_calendar);

        elecmemo_alarmcnt = (TextView) findViewById(R.id.elecmemo_alarmcnt);
        time.getTime().toString();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd-EEE");
        today = dateFormat.format(new Date());

        text_date.setText(today + "요일");
        text_name.setText(getSharedString("USERNAME"));
        text_id.setText(getSharedString("USERID"));
        Log.d(TAG, "[EJY] fontSize 0 : "+getIsTablet(MainActivity.this));
        Log.d(TAG, "[EJY] fontSize 1 : "+getScreenSize(MainActivity.this) + 300);
        //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
        if(getIsTablet(MainActivity.this)) {
            tv_bottom_store.setTextSize(Dimension.SP, fontSizeForTablet);
            tv_bottom_chart.setTextSize(Dimension.SP, fontSizeForTablet);
            tv_bottom_news.setTextSize(Dimension.SP, fontSizeForTablet);
            tv_bottom_menu.setTextSize(Dimension.SP, fontSizeForTablet);

            text_name.setTextSize(Dimension.SP, fontSizeForTablet);
            text_id.setTextSize(Dimension.SP, fontSizeForTablet);
            text_date.setTextSize(Dimension.SP, fontSizeForTablet);
            Log.d(TAG, "[EJY] fontSize : "+fontSizeForTablet);
            Log.d(TAG, "[EJY] fontSize 2 : "+getScreenSize(MainActivity.this) + 300);
            text_id.setWidth(getScreenSize(MainActivity.this) + 300);
            Log.d(TAG, "[EJY] fontSize 3 : "+text_id.getWidth());

            LinearLayout.LayoutParams text_id_params = (LinearLayout.LayoutParams) text_id.getLayoutParams();
            text_id_params.width = fontSizeForTablet + 150;

            //달력 이미지 조정
            LinearLayout.LayoutParams iv_calendar_params = (LinearLayout.LayoutParams) iv_calendar.getLayoutParams();
            iv_calendar_params.width = fontSizeForTablet + 30;
            iv_calendar_params.height = fontSizeForTablet + 30;
        }

        //RecyclerView Setting/Initiating
        // For adapterSetting
        SampleData = new ArrayList<AppdataList>();
        CopyData = new ArrayList<AppdataList>();
        appList_onVpn = new ArrayList<AppdataList>();
        appList_offVpn = new ArrayList<AppdataList>();

        main_recycler_gridview = (RecyclerView) findViewById(R.id.main_recycler_gridview);
        main_recycler_gridview2 = (RecyclerView) findViewById(R.id.main_recycler_gridview2);

        app_adapter = new MainCategoryRecyclerGridViewAdapter(this, appList_onVpn, true);
//        main_recycler_gridview.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false));//JSJ rewrite
        main_recycler_gridview.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));//JSJ rewrite
        main_swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);

        //2021.07 swipe 새로고침 민감도 수정 700 -> 300
        main_swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimary, R.color.board_colorAccent);
        //main_swipe_refresh_layout.setDistanceToTriggerSync(700);
        main_swipe_refresh_layout.setDistanceToTriggerSync(300);

        app_adapter2 = new MainCategoryRecyclerGridViewAdapter(this, appList_offVpn, false);
        main_recycler_gridview2.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));//JSJ rewrite

    }


    public void onClick() {

        /*로그아웃 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        drawer_header_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                    vibrate(100);
                    mvpnstate = false;
                    String content;
                    commonDialog = new CommonDialog(MainActivity.this, getString(R.string.Exit), getString(R.string.dialog_logout), false,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //접속 종료
                                    vpnConn.disconnection();
                                    destroyConnection();
                                    ssmLib.setLoginStatus(LOGOUT);
                                    commonDialog.dismiss();

                                    //로그인 화면 이동
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    setSharedString("LOGINSTATE", "LOGOUT");

                                    finish();
                                }
                            }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                        }
                    });
                    commonDialog.show();
                } else {

                    commonDialog = new CommonDialog(MainActivity.this, getString(R.string.Exit), getString(R.string.dialog_logout), false,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    vpnConn.disconnection();
                                    destroyConnection();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    setSharedString("LOGINSTATE", "LOGOUT");
                                    commonDialog.dismiss();
                                    finish();

                                }
                            }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                        }
                    });
                    commonDialog.show();
                }
            }
        });


        /*설정 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(15);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        /*알람 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        button_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ElecMemoAppWebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*intent.putExtra("hybridUrl", ALARM_LIST_URL+"?userId="+getSharedString("USERID")
                        +"&userNm="+getSharedString("USERNAME")
                        +"&dptcd="+getSharedString("USERDEPARTURECODE"));*/
                intent.putExtra("hybridUrl", ALARM_LIST_URL);
                MainActivity.this.startActivityForResult(intent, 7777);

            }
        });

        button_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cpd != null) {
                        if (cpd.isShowing()) {
                            cpd.setCancelable(true);
                            cpd.dismiss();
                            cpd = null;
                        }
                    }
                    commonDialog = new CommonDialog(MainActivity.this, getString(R.string.Exit), "모바일오피스를 종료하시겠습니까?", false,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                            /*if (mVpnReceiver != null) {
                                unregisterReceiver(mVpnReceiver);
                            }*/
                                    ssmLib.setLoginStatus(LOGOUT);
                                    mvpnstate = false;
                                    vpnConn.disconnection();
                                    destroyConnection();

                                    commonDialog.dismiss();
                                    setSharedString("LOGINSTATE", "LOGOUT");
                                    finish();
                                }
                            }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                        }
                    });
                    commonDialog.show();

                } catch (Exception e) {
                }
            }
        });

        /*스토어 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        //2021-03 [EJY] 스토어 버튼 클릭 이벤트 추가
        button_bottom_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //2021-03-31 [EJY] VPN 연결 끊기
            disVpnConn();

            try {
                Intent intent = new Intent(EX_STORE_PACKAGE + ".LAUNCH_MAIN");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(EX_STORE_PACKAGE);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", getSharedString("USERID"));
                startActivity(intent);
            }

            }
        });
        /*교통/영업현황 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        button_bottom_trafficstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CurrentTrafficBusinessStateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        /*주요뉴스 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        button_bottom_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TopicActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        /*식단 버튼 클릭 ◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘◘*/
        button_bottom_diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void currentStoreVersion() {
        String current_Version;

        try {
            PackageInfo pi;
            pi = getPackageManager().getPackageInfo(EX_STORE_PACKAGE, PackageManager.GET_META_DATA);
            current_Version = pi.versionName.toString();
            setSharedString("CURRENTSTOREVERSION", current_Version);
            Log.e(TAG, "current store version " + current_Version);

        } catch (Exception e) {
        }
    }

    public void onSwipeToTop() {
        main_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reNewDBINFO( app_adapter.getAppdataLists());
                currentStoreVersion();

                main_swipe_refresh_layout.setDistanceToTriggerSync(1500);
                main_swipe_refresh_layout.setRefreshing(false);

                getDownloadableApp();
                getInitInfo();
                SampleData.clear();
                CopyData.clear();

                if (lottie_Refresh != null) {
                    refresh_layout.setVisibility(View.GONE);
                    lottie_Refresh.cancelAnimation();
                    lottie_Refresh = null;
                }

                main_swipe_refresh_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        main_swipe_refresh_layout.setDistanceToTriggerSync(700);
                    }
                }, 3000);
            }
        });
    }

    public void vibrate(int i) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(i);
    }

    /*boolean isAppstateConn = false;
    ServiceConnection AppstateConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            AppStateService.LocalBinder mb = (AppStateService.LocalBinder) service;
            myAppStateService = mb.getService();
            isAppstateConn = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(getApplicationContext(),
                    "서비스 연결 해제",
                    Toast.LENGTH_LONG).show();
            isAppstateConn = false;
        }
    };*/

    /*private void subscribeTestTopic(){
        Log.e(TAG("FCMTEST"),"정상적으로 등록되었습니다.");
        FirebaseMessaging.getInstance().subscribeToTopic("testHoliday");
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate()");

        Log.d(TAG, "[EJY] ***** onCreate() - getDeviceId : "+getDeviceId(MainActivity.this));
        Log.d(TAG, "[EJY] ***** onCreate() - getDeviceModel : "+getDeviceModel());
        Log.d(TAG, "[EJY] ***** onCreate() - getDeviceOs : "+getDeviceOs());
        Log.d(TAG, "[EJY] ***** onCreate() - getIsPhone : "+getIsPhone(MainActivity.this));
        Log.d(TAG, "[EJY] ***** onCreate() - getIsTablet : "+getIsTablet(MainActivity.this));
        Log.d(TAG, "[EJY] ***** onCreate() - isTablet : "+isTablet());



        super.onCreate(savedInstanceState);
        dragMotion=false;

        Intent intent = new Intent(MainActivity.this, AppLoginLogoutState.class);
        startService(intent);

        setContentView(R.layout.activity_main);

        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Log.d("token","token refreshed : "+FirebaseInstanceId.getInstance().getToken());

        //job 등록
        contextMain = MainActivity.this;
        setSharedString("LOGINSTATE", "LOGIN");
        Log.e("LOGINSTATE", "LOGIN");

        //App 리스트를 가져오고 저장하는 DB이다.
        appInfoDb = new APPINFODB(getBaseContext());

        if (getSharedString("FIRSTRUN").equals("Y")) {
            coachmark_layout = (LinearLayout) findViewById(R.id.coachmark_layout);
            coachmark_layout.setVisibility(View.VISIBLE);
            coachmark_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            final ImageView coachmark_skip = (ImageView) findViewById(R.id.coachmark_skip);
            final LinearLayout coachmark_icon = (findViewById(R.id.coachmark_icon));
            final ImageView coachmark_icon_explain = (ImageView) findViewById(R.id.coachmark_icon_explain);
            final LinearLayout coachmark_bottomm = (LinearLayout) findViewById(R.id.coachmark_bottomm);
            final RelativeLayout coachmark_middle = (RelativeLayout) findViewById(R.id.coachmark_middle);
            final ImageView coachmark_icon_anima = (ImageView) findViewById(R.id.coachmark_icon_anima);
            final AnimationDrawable anima;
            coachmark_icon_anima.setImageResource(R.drawable.coachmark_anima);
            coachmark_icon_anima.setScaleType(ImageView.ScaleType.FIT_CENTER);
            anima = (AnimationDrawable) coachmark_icon_anima.getDrawable();
            coachmark_icon_anima.postDelayed(new Runnable() {
                @Override
                public void run() {
                    anima.start();
                }
            }, 1000);

            coachmark_icon_anima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrate(30);

                    coachmark_icon.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    anima.stop();
                                    coachmark_icon_anima.setVisibility(View.GONE);

                                    coachmark_bottomm.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            coachmark_bottomm.setVisibility(View.GONE);
                                            coachmark_middle.setVisibility(View.VISIBLE);

                                        }
                                    }, 2000);

                                    coachmark_icon_anima.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            coachmark_skip.setVisibility(View.VISIBLE);

                                        }
                                    }, 4000);
                                }
                            }, 250
                    );
                    coachmark_icon.setVisibility(View.VISIBLE);
                    coachmark_icon_explain.setVisibility(View.VISIBLE);
                }
            });
            coachmark_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coachmark_layout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    coachmark_layout.setVisibility(View.GONE);
                    setSharedString("FIRSTRUN", "N");

                    //2021-04-21 [EJY] 메모보고 공지사항 메인페이지에서 표출
                    getNoticePopInfo();
                    cpd_parsing.show();
                }
            });

            //2021-05-11 [EJY] AppVer 저장
            setSharedString("APPVER", getVersionName(MainActivity.this, getPackageName()));

        } else {
            //2021-04-21 [EJY] 메모보고 공지사항 메인페이지에서 표출
            getNoticePopInfo();
        }

        Log.e(TAG, "AT INIT");
        Log.e(TAG, "==MEMORY==\n\n" + "CURRENTMEMORY : " + Runtime.getRuntime().totalMemory() + "\nMAXCURRENT : " + Runtime.getRuntime().maxMemory() + "\nAFFORDABLE : " + Runtime.getRuntime().freeMemory());
        setView();
        ssmLib = SSMLib.getInstance(MainActivity.this);
        //ssmLib.setLoginStatus(LOGIN);
        userPref = getSharedPreferences("USERINFO", MODE_PRIVATE);


        Wi_Fi = userPref.getInt("WIFI", ConnectivityManager.TYPE_MOBILE);
        bluetooth = userPref.getInt("BLUETOOTH", BluetoothAdapter.STATE_OFF);
        mContext = getBaseContext();
        currentStoreVersion();
        getDownloadableApp();
        getAlarmCnt();
        getInitInfo();//앱목록 조회 http://store.ex.co.kr/mobilerelay/retrieveInitInfo.do?userId=22071435&userType=EX&platformCd=A&deviceType=M
        //getAppInfoFromServer();
        setTouchHelper(app_adapter);

        SampleData.clear();
        CopyData.clear();
        onSwipeToTop();

        //2021.07 메모보고 & 전자결재 알림뱃지

        connectionTime = getSharedlong("CONNECTIONTIME");
        onClick();
        Log.e(TAG, "AT LAST");
        Log.e(TAG, "==MEMORY==\n\n" + "CURRENTMEMORY : " + Runtime.getRuntime().totalMemory() + "\nMAXCURRENT : " + Runtime.getRuntime().maxMemory() + "\nAFFORDABLE : " + Runtime.getRuntime().freeMemory());

    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Intent intent1 = intent;

        if (intent1.getStringExtra("packageName") != null) {
            cpd_store = new CustomprogressDialog(MainActivity.this, "준비중입니다.");
            cpd_store.show();
            drawer.postDelayed(new Runnable() {
                @Override
                public void run() {
                cpd_store.dismiss();

                Log.e(STORETAG, "SUCCESS! pacakgeName is " + intent1.getStringExtra("packageName"));
                /*wifi.setWifiEnabled(false);*/
                //VPN RECEIVER가 등록돼있지 않다면 진입
                if (mVpnReceiver == null) {
                    IntentFilter intentFilter = new IntentFilter(ClientUtil.SGVPN_STATUS);
                    registerReceiver(mVpnReceiver, intentFilter);
                }

                if (vpnConn == null) {
                    logmaking("START VPN", "vpn instance is null!!!!");
                    vpnConn = SGVPNConnection.getInstance(tempService);
                }

                if (vpnConn.getStatus() != Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                    Log.e(STORETAG, "VPN IS NOT CONNECTED TRYINGTOCONNENTING");
                    VPNpackageName = intent1.getStringExtra("packageName");
                    start_vpn();
                } else {
                    Log.e(STORETAG, "VPN IS CONNECTED");
                    try {
                        Intent intent2 = new Intent(intent1.getStringExtra("packageName") + ".LAUNCH_MAIN");
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.putExtra("userId", getSharedString("USERID"));
                        startActivity(intent2);
                        Log.e(STORETAG, "Launching Package " + intent1.getStringExtra("packageName"));
                    } catch (Exception e) {
                        Intent intent2 = getPackageManager().getLaunchIntentForPackage(intent1.getStringExtra("packageName").toString());
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.putExtra("userId", getSharedString("USERID"));
                        startActivity(intent2);
                        Log.e(STORETAG, "LaunchIntentForPackage " + intent1.getStringExtra("packageName"));
                    }
                }
                }
            }, 3000);

        } else if (intent1.getStringExtra("finishApp") != null) {
            if (intent1.getStringExtra("finishApp").equals("Y")) {

                try {
                    //vpnConn.disconnection();
                    destroyConnection();
                    finishAndRemoveTask(MainActivity.this);
                } catch (Exception e) {
                }
            }
        } else {
            Log.e(STORETAG, "FAILED BECAUSE MESSAGE IS NULL");
        }

    }

    LottieAnimationView lottie_Refresh;

    @Override
    protected void onResume() {
        super.onResume();

        netWorkStateCheck();
        if (cpd != null) {
            cpd.dismiss();
            cpd = null;
        }
        /*▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀*/

        mvpnstate = true;
        Log.v(TAG, "===onResume===");

        //2021.07 메모보고/전자결재/메일 현황체크
        getElecMemocount(CopyData);
        getMailcount(CopyData);
        getAlarmCnt();  //알림 카운트 조회

        Intent intent = new Intent(ClientUtil.SGVPN_API);
        intent.setPackage(ClientUtil.SGN_PACKAGE);

        if (!bindService(intent, mConnection, BIND_AUTO_CREATE)) {
            Log.i(TAG, "service bind error");
        } else {
            Log.i(TAG, "service binding");
            try {
                startService(intent);
                Log.i(TAG, "startservice");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "==========static showProgress visible ==========" + "START VPN");
        Log.v(TAG, "===registerVPNRECEIVER===");
        IntentFilter intentFilter = new IntentFilter(ClientUtil.SGVPN_STATUS);
        registerReceiver(mVpnReceiver, intentFilter);
        Log.i(TAG, "==========static showProgress visible ==========" + "REGISTER RECEIVER");


        //vpn상태가 접속상태가 아니라면 textView를 없애준다.
        if (vpnConn == null) {
            text_name.setText(getSharedString("USERNAME"));
        } else if (vpnConn != null && vpnConn.getStatus() != Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
            text_name.setText(getSharedString("USERNAME"));
        }
        IntentFilter intfil = new IntentFilter("com.ex.group.store.REFRESH_APP");
        registerReceiver(AppRefreshReceiver, intfil);

        // 2021.07 swipe 새로고침 민감도
        if (refresh == true) {

            refresh_layout = (LinearLayout) findViewById(R.id.refresh_layout);

            lottie_Refresh = (LottieAnimationView) findViewById(R.id.main_refresh_lottie_button);
            refresh_layout.setVisibility(View.VISIBLE);
            lottie_Refresh.setColorFilter((R.color.white));
            //lottie_Refresh.setAnimation("scrolldown.json");
            //2021.07 새로고침 이미지 수정
            //lottie_Refresh.setAnimation("main_hand_swipe_up_gesture.json");
            //lottie_Refresh.setAnimation("bell.json");
            //lottie_Refresh.setAnimation("main_swipe.json");
            lottie_Refresh.setAnimation("main_arrow_down.json");
            lottie_Refresh.setRepeatCount(100);
            lottie_Refresh.playAnimation();
            refresh = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        reNewDBINFO( app_adapter.getAppdataLists());
        Log.v(TAG, "unRegistermVpnReceiver");
        if (cpd != null) {
            progressState(false, "비정상종료", 99);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "unRegistermVpnReceiver");
        try {
            if (mVpnReceiver != null) {
                // unregisterReceiver(mVpnReceiver);
            }
            if (cpd != null) {
                progressState(false, "비정상종료", 99);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG("onActivityResult"),"resultCode:"+resultCode+"  requestCode: "+ requestCode);
        Log.e(TAG("onActivityResult"),"data:"+data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_CHECK && resultCode == VPN_SERVICE_PERMISSION_ALLOW) {
            Log.i(TAG("에스지엔 PERMISSION"), "onActivityResult ... PERMISSION_CHECK .. VPN_SERVICE_PERMISSION_ALLOW ");
            try {
                prepareStartProfile(APP_PERMISSION_RETURN);
            } catch (RemoteException e) {
                System.out.println("ERR : " + e);
            }

        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == APP_PERMISSION_RETURN) {
            Log.i(TAG("에스지엔 PERMISSION"), "onActivityResult ... Activity.RESULT_CANCELED  START_PROFILE_EMBEDDED");
            //VPN-service 앱에서 사용할 권한 거부.. 앱 종료.
            Toast.makeText(MainActivity.this, R.string.permission_grant, Toast.LENGTH_SHORT).show();
            setSharedString("LOGINSTATE", "LOGOUT");
            vpnConn.disconnection();
            destroyConnection();
            finish();

        } else if (requestCode == PERMISSION_CHECK && resultCode == VPN_SERVICE_PERMISSION_DNEY) {
            Log.i(TAG("에스지엔 PERMISSION"), "onActivityResult ... PERMISSION_CHECK .. VPN_SERVICE_PERMISSION_DNEY ");
            //VPN service를 위한 권한 거부.. 앱 종료, 전화,저장공간.
            Toast.makeText(MainActivity.this, R.string.permission_grant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + ClientUtil.SGN_PACKAGE));
            startActivity(intent);
            setSharedString("LOGINSTATE", "LOGOUT");
            vpnConn.disconnection();
            destroyConnection();
            finish();

        }else if(/*resultCode == RESULT_OK && */requestCode == 7777){
            String app = "";
            if(data != null) {
                app = checkNull(data.getStringExtra("app"));
            } else {
                app = "N";
            }
            Log.d(TAG, "AlarmList - app : "+app);
//            getAlarmCnt();
//            String app = checkNull(data.getStringExtra("app"));
            if (app.equals("E")) {
                VPNpackageName = ELEC_PKGNM;
            }else if (app.equals("M")) {
                VPNpackageName = MEMO_PKGNM;
            } else {
                System.out.println("-------------------------------------------- 알림카운트  3");
                getAlarmCnt();
                return;
            }

            if (mVpnReceiver == null) {
                IntentFilter intentFilter = new IntentFilter(ClientUtil.SGVPN_STATUS);
                registerReceiver(mVpnReceiver, intentFilter);
            }

            //VPNCONNECTION STATUS가 접속상태 ?  VPN 접속 : 앱실행
            if (vpnConn.getStatus() != Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                start_vpn();
            } else if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                if (app.equals("E")) {
                    Intent intent = new Intent(MainActivity.this, ElecMemoAppWebViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("hybridUrl", ELEC_URL);
                    startActivity(intent);
                } else if (app.equals("M")) {
                    Intent intent = new Intent(MainActivity.this, ElecMemoAppWebViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("hybridUrl", MEMO_URL);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.v(TAG, "  ====onBackPressed KEY DOWN====");
                drawer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (drawer.isDrawerOpen(GravityCompat.END)) {
                            drawer.closeDrawer(GravityCompat.END);
                        } else {
                            try {
                                if (cpd != null) {
                                    if (cpd.isShowing()) {
                                        cpd.setCancelable(true);
                                        cpd.dismiss();
                                        cpd = null;
                                    }
                                }
                                commonDialog = new CommonDialog(MainActivity.this, getString(R.string.Exit), "모바일오피스를 종료하시겠습니까?", false,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                            /*if (mVpnReceiver != null) {
                                unregisterReceiver(mVpnReceiver);
                            }*/
                                                ssmLib.setLoginStatus(LOGOUT);
                                                mvpnstate = false;
                                                vpnConn.disconnection();
                                                destroyConnection();

                                                commonDialog.dismiss();
                                                setSharedString("LOGINSTATE", "LOGOUT");
                                                finish();
                                            }
                                        }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        commonDialog.dismiss();
                                    }
                                });
                                commonDialog.show();
                            } catch (Exception e) {
                            }
                        }
                    }
                });

        }//switch end
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
    }


    // DragListener를 이용해서 drag & drop을 구현하는 구간이다.
    // 여기서 선언한다 모든것들을....

    private ItemTouchHelper mItemTouchHelper;
    public void setTouchHelper(MainCategoryRecyclerGridViewAdapter adapter) {
        ItemTouchHelper.Callback callback = new TouchCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(main_recycler_gridview);
    }

    @Override
    public void onstartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.easy_login_setting) {
            Intent intent = new Intent(MainActivity.this, EasyLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if(id ==R.id.alarm_setting){
            Intent intent = new Intent(MainActivity.this, AlarmSettingActivity.class);
            startActivity(intent);

        }else if(id ==R.id.m1){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.ex.co.kr/mobile/binary/manual/mobileoffice_manual.pdf"));
            startActivity(intent);
        }
       /* else if(id ==R.id.m2){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.ex.co.kr/mobile/binary/manual/mobileoffice_manual.pdf"));
            startActivity(intent);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void getMailcount(final ArrayList<AppdataList> copydata) {
        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);

        final Call<RequestMailCount> requestMailCountCall = apiInterface.request_unread_mail_count(getSharedString("USERID"), "A");
        requestMailCountCall.enqueue(new Callback<RequestMailCount>() {
            @Override
            public void onResponse(Call<RequestMailCount> call, Response<RequestMailCount> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMsg;
                String approvalCnt = response.body().approvalCnt;
                String mailCnt = response.body().mailCnt;
                AppdataList app;
                LogMaker.logmaking("result", result);
                LogMaker.logmaking("resultMsg", resultMsg);
                LogMaker.logmaking("approvalCnt", approvalCnt);
                LogMaker.logmaking("mailCnt", mailCnt);
                unReadMailcount = mailCnt;
                approvalCount = approvalCnt;
                Log.v(TAG, "SAMPLEDATASIZE at GETMAILCOUNT: [" + copydata.size() + "]");
                for (int i = 0; i < copydata.size(); i++) {
                    if (copydata.get(i).getPackageNm().equals(EASYAP_PKGNM)) {
                        app = copydata.get(i);
                        app.setUnCheckedCount(approvalCount);
                        Log.v(TAG, "UNREADMAILCOUNT  : [" + app.getUnCheckedCount() + "]");
                        copydata.set(i, app);
                    } else if (copydata.get(i).getPackageNm().equals(MAIL_PKGNM)) {
                        copydata.get(i).setUnCheckedCount(mailCnt);
                        app = copydata.get(i);
                        app.setUnCheckedCount(unReadMailcount);
                        Log.v(TAG, "UNREADMAILCOUNT  : [" + app.getUnCheckedCount() + "]");
                        copydata.set(i, app);
                        Log.v(TAG, "COPYVALUE  : [" + copydata.get(i).getUnCheckedCount() + "]");
                    }

                    /*else if(copydata.get(i).getPackageNm().equals(ELEC_PKGNM)) {
                        app = copydata.get(i);
                        app.setUnCheckedCount(unReadMailcount);
                        copydata.set(i, app);
                    }*/
                }
//                app_adapter.updateAppdataList(copydata);

                appList_onVpn.clear();
                appList_offVpn.clear();
                for (int i = 0; i < copydata.size(); i++) {
                    if("스마트정비관리".equals(copydata.get(i).getAppNm())) {
                        appList_offVpn.add(copydata.get(i));
                    }else{
                        if("Y".equals(copydata.get(i).getEssentialYn())){
                            appList_onVpn.add(copydata.get(i));
                            Log.d("copydata","copydata addon = " + copydata.get(i).getAppNm());
                        }else{
                            appList_offVpn.add(copydata.get(i));
                            Log.d("copydata","copydata addoff = " + copydata.get(i).getAppNm());
                        }
                    }

                }
                Log.d("copydata","copydata = " + appList_onVpn.size());
                app_adapter.updateAppdataList(copydata, true);
                app_adapter2.updateAppdataList(copydata, false);
            }

            @Override
            public void onFailure(Call<RequestMailCount> call, Throwable t) {
                cpd_parsing.dismiss();

            }
        });

    }

    //2021-04-19 [EJY] 전자결재/메모보고 대기 카운트 조회
    public void getElecMemocount(final ArrayList<AppdataList> copydata) {
        Log.d(TAG, "[EJY] getElecMemocount() ");
        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);

        final Call<ElecMemoAlarmInfo> requestElecWaitCountCall = apiInterface.request_elec_wait_count(getSharedString("USERID"));
        requestElecWaitCountCall.enqueue(new Callback<ElecMemoAlarmInfo>() {
            @Override
            public void onResponse(Call<ElecMemoAlarmInfo> call, Response<ElecMemoAlarmInfo> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMessage;
                String elecCnt = response.body().elecCnt;

                AppdataList app;
                LogMaker.logmaking("result", result);
                LogMaker.logmaking("resultMsg", resultMsg);
                LogMaker.logmaking("elecCnt", elecCnt);
                Log.v(TAG, "SAMPLEDATASIZE at GETM ELEC COUNT : [" + copydata.size() + "]");
                for (int i = 0; i < copydata.size(); i++) {
                    if (copydata.get(i).getPackageNm().equals(ELEC_PKGNM)) {
                        app = copydata.get(i);
                        app.setUnCheckedCount(elecCnt);
                        Log.v(TAG, "UNREAD ELEC COUNT  : [" + app.getUnCheckedCount() + "]");
                        copydata.set(i, app);
                    }
                }

                getMailcount(copydata);
            }

            @Override
            public void onFailure(Call<ElecMemoAlarmInfo> call, Throwable t) {
                cpd_parsing.dismiss();

            }
        });

        final Call<ElecMemoAlarmInfo> requestMemoWaitCountCall = apiInterface.request_memo_wait_count(getSharedString("USERID"));
        requestMemoWaitCountCall.enqueue(new Callback<ElecMemoAlarmInfo>() {
            @Override
            public void onResponse(Call<ElecMemoAlarmInfo> call, Response<ElecMemoAlarmInfo> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMessage;
                String memoCnt = response.body().memoCnt;
                AppdataList app;
                LogMaker.logmaking("result", result);
                LogMaker.logmaking("resultMsg", resultMsg);
                LogMaker.logmaking("memoCnt", memoCnt);
                Log.v(TAG, "SAMPLEDATASIZE at GET MEMO COUNT : [" + copydata.size() + "]");
                for (int i = 0; i < copydata.size(); i++) {
                    if (copydata.get(i).getPackageNm().equals(MEMO_PKGNM)) {
                        app = copydata.get(i);
                        app.setUnCheckedCount(memoCnt);
                        Log.v(TAG, "UNREAD MEMO COUNT  : [" + app.getUnCheckedCount() + "]");
                        copydata.set(i, app);
                    }
                }
            }

            @Override
            public void onFailure(Call<ElecMemoAlarmInfo> call, Throwable t) {
                notiDialog = new CommonDialog_oneButton(MainActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                notiDialog.show();

                if(cpd_parsing!=null && cpd_parsing.isShowing()){
                    cpd_parsing.dismiss();
                }
            }
        });

    }


    public class RecyclerViewDecoration extends RecyclerView.ItemDecoration{
        private final int divHeight;
        public RecyclerViewDecoration(int divHeight){
            this.divHeight = divHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top=divHeight;
        }
    }

    public void stackRunningAppLog(String AppName) {
        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        final Call<StackRunningAppLog> stackRunningAppLogCall = apiInterface.stack_running_app_info(
                getSharedString("USERID"), Build.MODEL, CommonUtil.getMdn(MainActivity.this), AppName, Build.MANUFACTURER, getSharedString("USERTYPE")
        );
        stackRunningAppLogCall.enqueue(new Callback<StackRunningAppLog>() {
            @Override
            public void onResponse(Call<StackRunningAppLog> call, Response<StackRunningAppLog> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMsg;
                Log.v(TAG, "(STACKRUNNINGAPPLOG - resultMsg) : " + resultMsg);
            }

            @Override
            public void onFailure(Call<StackRunningAppLog> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction()==MotionEvent.ACTION_UP){dragMotion=true;}
        else {}
        return false;
    }

    public enum VS {
        PACKAGENAME,
        APPID,
        APPNAME,
        SERVER_APPVER,
        CURRENT_APPVER,
        HYBRIDYN,
        ICONURL,
        VPNYN,
        GROUPSORT,
        HYBRIDURL,
        APPTYPE,
        APPSORT,
        TEXT
    }

    /*
     *앱 목록 조회
     */
    public void getInitInfo() {
        APIInterface apiInterface;
        //apiInterface = APIClient.getClient().create(APIInterface.class);//운영
        apiInterface = APIClient.getClientTest().create(APIInterface.class);//테스트 test
        //cpd = new CustomprogressDialog(MainActivity.this);
        //cpd.show();
        final String deviceType = (isTablet()) ? "T" : "M";

        Log.d("getInitInfo", "getInitInfo = "+getSharedString("USERID"));
        Log.d("getInitInfo", "getInitInfo = "+getSharedString("USERTYPE"));
        Log.d("getInitInfo", "getInitInfo = "+deviceType);

        final Call<RequestInitInfo> requestInitInfoCall = apiInterface.request_initial_info(getSharedString("USERID"), getSharedString("USERTYPE"), "A", deviceType);//운영
//        final Call<RequestInitInfo> requestInitInfoCall = apiInterface.request_initial_info_test(getSharedString("USERID"), getSharedString("USERTYPE"), "A", deviceType);//테스트 test
        requestInitInfoCall.enqueue(new Callback<RequestInitInfo>() {
            @Override
            public void onResponse(Call<RequestInitInfo> call, Response<RequestInitInfo> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMsg;
                String StoreVersion = response.body().StoreVer;
                logStart();
                logmaking("result", result);
                logmaking("resultMsg", resultMsg);
                setSharedString("NEWSTOREVERSION", StoreVersion);
                logEnd();

                List<RequestInitInfo.AppInfoList> appInfoList = response.body().appInfoList;
                for (RequestInitInfo.AppInfoList appInfoList1 : appInfoList) {
                    Log.d("JSJ","JSJ appinfolist1 check " + appInfoList1.getPackageNm());

                    if (appInfoList1.getAppNm().equals("직원검색"))
                        searchURL = appInfoList1.getHybridUrl();
                    setSharedString("MEMBERHYBRIDURL", searchURL);
                }

                ArrayList<RequestInitInfo.AppInfoList> treeSet = new ArrayList<RequestInitInfo.AppInfoList>();
                if (!getSharedString("DRAGMODE").equals("Y")) {
                    for (int i = 0; i < appInfoList.size(); i++) {
                        switch (String.valueOf(isExistApp(appInfoList.get(i).getPackageNm(), appInfoList.get(i).getAppVer()))) {
                            case INSTALLED:
                                appInfoList.get(i).setNeedUpdate("N");
                                treeSet.add(appInfoList.get(i));
                                Log.e(TAG, "appname" + appInfoList.get(i).getAppNm());
                                break;
                            case UNINSTALLED:
                                break;
                            case NEED_UPDATE:
                                appInfoList.get(i).setNeedUpdate("Y");
                                treeSet.add(appInfoList.get(i));
                                break;
                            default:
                                break;
                        }
                    }

                } else {
                    List<RequestInitInfo.AppInfoList> appDownList = new ArrayList<>();

                    for (int i = 0; i < downLoadableAppList.size(); i++) {

                        for (int j = 0; j < appInfoList.size(); j++) {
                            RequestInitInfo.AppInfoList list = new RequestInitInfo.AppInfoList();
                            list = appInfoList.get(j);
                            int count = 0;

                            if (downLoadableAppList.get(i).getPackageNm().equals(list.getPackageNm())) {
                                if (downLoadableAppList.get(i).getAppVer().equals(list.getAppVer())) {
                                    list.setNeedUpdate("N");
                                    appDownList.add(list);

                                } else if (downLoadableAppList.get(i).getAppVer().equals("0")) {
//                                    if (count == 0) {
//                                        count++;
//                                        list.setNeedUpdate("N");
//                                        appDownList.add(list);
//                                    }
                                    list.setNeedUpdate("N");
                                    appDownList.add(list);
                                } else {
                                    list.setNeedUpdate("Y");
                                    appDownList.add(list);
                                }
                                Log.e(TAG("1146 " + i + " " + j), list.getAppNm());
                            }
                        }
                    }

                    for (int i = 0; i < appDownList.size(); i++) {
                        Log.d("JSJ","JSJ downloadAppList tree [" + appDownList.get(i).getAppNm()+"]");
                        treeSet.add(appDownList.get(i));
                    }
                }

                Iterator it = treeSet.iterator();

                //하단 코드는 iterator 로 순차적으로 긁어오는 것 App의 정보 저장
                while (it.hasNext()) {
                    RequestInitInfo.AppInfoList app = (RequestInitInfo.AppInfoList) it.next();

                    Log.d("JSJ","JSJ appname check " + app.getAppNm());
                    if (app.getAppType().equals("N") || app.getAppType().equals("M")) {
                        AppdataList CD = new AppdataList();
                        CD.setNeedUpdate(app.getNeedUpdate());
                        CD.setFixed(false);
                        CD.setPackageNm(app.getPackageNm());
                        CD.setDrawableIcon(drawableicon(app.getPackageNm()));
                        CD.setAppId(app.getAppId());
                        CD.setAppVer(app.getAppVer());
                        CD.setAppNm(app.getAppNm());
                        if("게시판".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_board2);
                            CD.setDrawableIcon3(R.drawable.icon_board3);
                        }else if("사내메일".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_mail2);
                            CD.setDrawableIcon3(R.drawable.icon_mail3);
                        }else if("메모보고".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_memo2);
                            CD.setDrawableIcon3(R.drawable.icon_memo3);
                        }else if("전자결재".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_stamp);
                            CD.setDrawableIcon3(R.drawable.icon_approve3);
                        }else if("직원검색".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_member2);
                            CD.setDrawableIcon3(R.drawable.icon_member3);
                        }else if("외출휴가".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_break2);
                            CD.setDrawableIcon3(R.drawable.icon_break3);
                        }else if("감사정보".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_inspection2);
                            CD.setDrawableIcon3(R.drawable.icon_inspect3);//해당 아이콘 없음 (감사정보)
                        }else if("재난관리앱".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_disaster2);
                            CD.setDrawableIcon3(R.drawable.icon_disaster3);
                        }else if("국회업무".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_assembly2);
                            CD.setDrawableIcon3(R.drawable.icon_assembly3);
                        }else if("법무정보".equals(app.getAppNm())){
                            CD.setDrawableIcon2(R.drawable.icon_law2);
                            CD.setDrawableIcon3(R.drawable.icon_law3);
                        }else if("스마트정비관리".equals(app.getAppNm())) {
                            app.setVpnYn("N");//강제로 VPN아래 목록으로 설정.
                            CD.setDrawableIcon2(R.drawable.icon_repair2);
                            CD.setDrawableIcon3(R.drawable.icon_repair3);
                        }else if("exBus".equals(app.getAppNm())) {
                            CD.setDrawableIcon2(R.drawable.icon_bus2);
                            CD.setDrawableIcon3(R.drawable.icon_bus2);//해당 아이콘 없음 (exBus)
                        }else if("터널암판정(검측)".equals(app.getAppNm())) {
                            CD.setDrawableIcon2(R.drawable.icon_tunnel2);
                            CD.setDrawableIcon3(R.drawable.icon_tunnel2);//해당 아이콘 없음 (터널암판정(검측))
                        }else if("구조물점검".equals(app.getAppNm())) {
                            CD.setDrawableIcon2(R.drawable.icon_structure);
                            CD.setDrawableIcon3(R.drawable.icon_structure);//해당 아이콘 없음 (구조물점검)
                        }else if("ex상황속보".equals(app.getAppNm())) {
                            CD.setDrawableIcon2(R.drawable.icon_exflash2);
                            CD.setDrawableIcon3(R.drawable.icon_exflash2);//해당 아이콘 없음 (ex상황속보)
                        }

                        CD.setIconUrl(app.getIconUrl());
                        CD.setHybridYn(app.getHybridYn());
                        CD.setHybridUrl(app.getHybridUrl());
                        CD.setInstallUrl(app.getInstallUrl());
                        CD.setEssentialYn(app.getVpnYn());
                        CD.setEssentilaInstCd(app.getAppType());
                        CD.setUnCheckedCount("");
                        SampleData.add(CD);
                        CopyData.add(CD);
                        Log.d("JSJ","JSJ downloadAppList fulllist [" + app.getAppNm()+"]");
                        if("Y".equals(app.getVpnYn())){
                            appList_onVpn.add(CD);
                        }else{
                            appList_offVpn.add(CD);
                        }

//                        Log.e(TAG, "[EJY] -->" + app.getAppNm());
                    }
                }

                fixedAppSetting();
                app_adapter.updateAppdataList(SampleData, true);//recyclerView 1 update 업데이트
                app_adapter2.updateAppdataList(SampleData, false);//recyclerView 2 update 업데이트

                logmaking("StartMakingGRIDRECyclerView", "gogo");
                main_recycler_gridview.setAdapter(app_adapter);
                main_recycler_gridview2.setAdapter(app_adapter2);
//                main_recycler_gridview.addItemDecoration(new RecyclerViewDecoration(100));
//                main_recycler_gridview2.addItemDecoration(new RecyclerViewDecoration(100));

                Log.v(TAG, "SAMPLEDATASIZE : [" + SampleData.size() + "]");

                if (getSharedString("USERTYPE").equals("EX")){    //loginAction 에서 가져온 usertype이 EX 일 경우에만 FIXXED APP 을 설정한다.
                    //2021-04-19 [EJY]
                    //getMailcount(CopyData);
                    getElecMemocount(CopyData);
                }

                List<RequestInitInfo.NoticeList> noticeList = response.body().noticeList;
                for (RequestInitInfo.NoticeList notilist : noticeList) {
                    logmaking("noticeType", notilist.getNoticeType());
                    logmaking("noticeID", notilist.getNoticeId());
                    logmaking("noticeTitle", notilist.getNoticeTitle());
                    logmaking("noticeContent", notilist.getNoticeContent());

                    notiDialog = new CommonDialog_oneButton(MainActivity.this, notilist.getNoticeTitle(), notilist.getNoticeContent(), false, null);
                    notiDialog.show();
                }
            }

            @Override
            public void onFailure(Call<RequestInitInfo> call, Throwable t) {
                Log.d(TAG, "onFailure() - call : "+call.toString());
                notiDialog = new CommonDialog_oneButton(MainActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                notiDialog.show();

                if(cpd_parsing!=null && cpd_parsing.isShowing()){
                    cpd_parsing.dismiss();
                }
            }
        });
    }

    /*전자결재, 메모보고 알림 카운트 조회*/
    public void getAlarmCnt() {
        System.out.println("-------------------------------------------- 알림카운트  ");
        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        //cpd = new CustomprogressDialog(MainActivity.this);
        //cpd.show();
        final String deviceType = (isTablet()) ? "T" : "M";

        Log.d("getInitInfo", "getInitInfo getAlarmCnt = [USERID : "+getSharedString("USERID")+"][USERTYPE : "+getSharedString("USERTYPE")+"][deviceType : "+deviceType+"]");

        final Call<ElecMemoAlarmInfo> requestInitInfoCall = apiInterface.request_elecmemo_alarm_count(getSharedString("USERID"));
//        final Call<ElecMemoAlarmInfo> requestInitInfoCall = apiInterface.request_elecmemo_alarm_count("22071435");
        requestInitInfoCall.enqueue(new Callback<ElecMemoAlarmInfo>() {
            @Override
            public void onResponse(Call<ElecMemoAlarmInfo> call, Response<ElecMemoAlarmInfo> response) {
                String result = response.body().result;
                String cnt = response.body().cnt;
                String resultMsg = response.body().resultMessage;

                if("0".equals(cnt)){
                    elecmemo_alarmcnt.setVisibility(View.GONE);
                }else{
                    if(cnt.length() == 1 ){
                        elecmemo_alarmcnt.setText(" "+cnt+" ");
                    }else if(cnt.length() == 2 ){
                        elecmemo_alarmcnt.setText(" "+cnt);
                    }else{
                        elecmemo_alarmcnt.setText(cnt);
                    }
                }

                Log.d("getAlarmCnt","getAlarmCnt cnt == "+cnt);
                Log.d("getAlarmCnt","getAlarmCnt result== "+result);
                Log.d("getAlarmCnt","resultMsg result== "+resultMsg);
            }

            @Override
            public void onFailure(Call<ElecMemoAlarmInfo> call, Throwable t) {
                notiDialog = new CommonDialog_oneButton(MainActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                notiDialog.show();

                if(cpd_parsing!=null && cpd_parsing.isShowing()){
                    cpd_parsing.dismiss();
                }
            }
        });
    }

    public int drawableicon(String pn) {
        int rt;
        switch (pn) {

            case ("com.ex.group.elec"):  //전자결재
                rt = R.drawable.app_icon_searchman_selecter;
                break;
            case ("com.ex.group.memo"):  //메모보고
                rt = R.drawable.app_icon_searchman_selecter;
                break;
            case ("com.ex.group.addressbook"):  //직원 검색
                rt = R.drawable.app_icon_searchman_selecter;
                break;
            case ("com.ex.group.board"):  //게시판
                rt = R.drawable.app_icon_board_selecter;
                break;
            case ("com.ex.group.mail"):  //사내메일
                rt = R.drawable.app_icon_mail;
                break;
            case ("com.ex.group.approval.easy"):  //간이결제
                rt = R.drawable.app_icon_ok_selecter;
                break;
            case ("com.ex.group.km"):  //법무정보
                rt = R.drawable.app_icon_law_selecter;
                break;
            case ("com.ex.group.audit"):  //감사정보
                rt = R.drawable.app_icon_inspection_selecter;
                break;
            case ("com.ex.group.aw"):  //국회정보
                rt = R.drawable.app_icon_assembly_selecter;
                break;
            case ("com.ex.hsms"):  //모바일사면
                rt = R.drawable.app_icon_slope_selecter;
                break;
            case ("com.ex.safeservice"):  //안전점검
                rt = R.drawable.app_icon_safe_selecter;
                break;
            case ("com.ex.construct.hicon"):  //건설관리앱
                rt = R.drawable.app_icon_build_selecter;
                break;
            case ("com.ex.rdems"):  //재난관리앱
                rt = R.drawable.app_icon_disaster_selecter;
                break;
            case ("com.ex.smartmm"):  //스마트정비관리
                rt = R.drawable.app_icon_repair_selecter;
                break;
            case ("com.ex.pw"):  //체납차량관리
                rt = R.drawable.app_icon_car_selecter;
                break;
            case ("com.ex.group.store"):
                rt = R.drawable.app_icon_store_selecter;
                break;
            case ("com.ex.bus"):
                rt = R.drawable.app_icon_exbus_selecter;
                break;
            case ("com.ex.newsflash"):
                rt = R.drawable.app_icon_newsflash_selecter;
                break;
            default:
                rt = 0;
        }
        return rt;
    }

    //고정된 앱들 표출 리스트에 추가하는 부분 (이전버전 스토어만 등록되고있었고, 현재 전자결재 메모보고 추가 되었지만 서버에서 내려주는 정보로 처리가 가능하여 해당 메소드안에서 추가되는 앱은 없음)
    public void fixedAppSetting() { // 5종 App 설치
        String[] appNm = {"외출휴가", "게시판", "사내메일", "직원검색", "전자결재", "메모보고", "스토어"};
        String[] HybridYn = {"N", "N", "N", "Y", "N", "N", "N"};
        String[] SEARCHURL = {"", "", "", "", "", "", ""};
        String[] essentialInstCd = {"M", "M", "M", "M" ,"M", "M", ""};
        String[] appId = {"MOGP000006", "MIGP000003", "MOGP000001", "MOGP000004","MOGP000047","MOGP000048",  "MOCR000003"};
        String[] essentialYn = {"Y", "Y", "Y", "Y", "Y", "Y", "N"};
        String[] packageNm = {"외출휴가", "게시판", "사내메일", "직원검색", "전자결재", "메모보고", EX_STORE_PACKAGE};
        int[] drawable = {R.drawable.app_icon_ok_selecter, R.drawable.app_icon_board_selecter, R.drawable.app_icon_mail_selecter, R.drawable.app_icon_searchman_selecter, R.drawable.app_icon_searchman_selecter, R.drawable.app_icon_searchman_selecter, R.drawable.app_icon_store_selecter};

        /*for (int i = 4 ; i<6; i++){
            Log.e(TAG, "App FOR ETC ");
            AppdataList CD = new AppdataList();

            if (getSharedString("CURRENTSTOREVERSION").equals(getSharedString("NEWSTOREVERSION"))) {
                CD.setNeedUpdate("N");
            } else {
                CD.setNeedUpdate("Y");
            }

            CD.setFixed(true);
            CD.setAppNm(appNm[i]);
            CD.setHybridYn(HybridYn[i]);
            CD.setHybridUrl(SEARCHURL[i]);
            CD.setDrawableIcon(drawable[i]);
            CD.setEssentilaInstCd(essentialInstCd[i]);
            CD.setEssentialYn(essentialYn[i]);
            CD.setUnCheckedCount("");
            CD.setPackageNm(packageNm[i]);
            CD.setAppId(appId[i]);
            if("게시판".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_board2);
                CD.setDrawableIcon3(R.drawable.icon_board3);
            }else if("사내메일".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_mail2);
                CD.setDrawableIcon3(R.drawable.icon_mail3);
            }else if("메모보고".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_memo2);
                CD.setDrawableIcon3(R.drawable.icon_memo3);
            }else if("전자결재".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_approve2);
                CD.setDrawableIcon3(R.drawable.icon_approve3);
            }else if("직원검색".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_member2);
                CD.setDrawableIcon3(R.drawable.icon_member3);
            }else if("외출휴가".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_break2);
                CD.setDrawableIcon3(R.drawable.icon_break3);
            }else if("감사정보".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_inspection2);
                CD.setDrawableIcon3(R.drawable.icon_inspection2);//해당 아이콘 없음 (감사정보)
            }else if("재난관리앱".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_disaster2);
                CD.setDrawableIcon3(R.drawable.icon_disaster3);
            }else if("국회업무".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_assembly2);
                CD.setDrawableIcon3(R.drawable.icon_assembly3);
            }else if("법무정보".equals(appNm[i])){
                CD.setDrawableIcon2(R.drawable.icon_law2);
                CD.setDrawableIcon3(R.drawable.icon_law3);
            }else if("스마트정비관리".equals(appNm[i])) {
                CD.setDrawableIcon2(R.drawable.icon_repair2);
                CD.setDrawableIcon3(R.drawable.icon_repair3);
            }else if("exBus".equals(appNm[i])) {
                CD.setDrawableIcon2(R.drawable.icon_bus2);
                CD.setDrawableIcon3(R.drawable.icon_bus2);//해당 아이콘 없음 (exBus)
            }else if("터널암판정(검측)".equals(appNm[i])) {
                CD.setDrawableIcon2(R.drawable.icon_tunnel2);
                CD.setDrawableIcon3(R.drawable.icon_tunnel2);//해당 아이콘 없음 (터널암판정(검측))
            }else if("ex상황속보".equals(appNm[i])) {
                CD.setDrawableIcon2(R.drawable.icon_exflash2);
                CD.setDrawableIcon3(R.drawable.icon_exflash2);//해당 아이콘 없음 (ex상황속보)
            }
            SampleData.add(CD);
            CopyData.add(CD);
        }*/

    }

    public boolean netWorkStateCheck() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wi_fi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobile.isConnected() || wi_fi.isConnected()) {
            return true;

        } else {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConnectivityManager man = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mo.isConnected() || wi.isConnected()) {
                return true;
            }else{
                notiDialog = new CommonDialog_oneButton(MainActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notiDialog.dismiss();
                        Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        startActivity(intent);
                        notiDialog.dismiss();
                    }
                });
                notiDialog.show();
            }
            return false;
        }
    }

    public void start_vpn() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wi_fi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        System.out.println("------------------------------------------------------vpn 1----------------------------");

        //2021.07 vpn TEST
        /*if (wi_fi.isConnected() || !mobile.isConnected() ) {

            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            commonDialog1m = new CommonDialog(MainActivity.this, "", getString(R.string.wifi_network1), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                            startActivity(intent);

                            Log.v(TAG, "Progressshowing");
                            commonDialog1m.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonDialog1m.dismiss();
                        }
                    });
            commonDialog1m.show();

        }else if(!wi_fi.isConnected() && !mobile.isConnected()){
            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            commonDialog1m = new CommonDialog(MainActivity.this, "", getString(R.string.wifi_network2), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);

                            Log.v(TAG, "Progressshowing");
                            commonDialog1m.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonDialog1m.dismiss();
                        }
                    });
            commonDialog1m.show();

        }else{*/
            ssmLib.setLoginStatus(LOGIN);
            logmaking("START VPN", "");

            if (vpnConn == null) {
                logmaking("START VPN", "vpn instance is null!!!!");
                vpnConn = SGVPNConnection.getInstance(tempService);
            }
            logmaking("IS CONNECTEd", "?");
            connection();

            logmaking("IS CONNECTEd", " Umm. Idon't Know well");
        //}
    }

    private final int APP_PERMISSION_RETURN = 9133;
    private final int VPN_SERVICE_PERMISSION_GROUP = 9130;
    private final int VPN_SERVICE_PERMISSION_ALLOW = 9131;
    private final int VPN_SERVICE_PERMISSION_DNEY = 9132;

    public static SGVPNConnection vpnConn;
    public static IBinder tempService = null;
    private SgnServiceConnection mConnection = new SgnServiceConnection();
    private class SgnServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("", "===================onServiceConnected===================");

            tempService = service;

            if (vpnConn == null)
            {vpnConn = SGVPNConnection.getInstance(service);}

            Log.i("", "Service Connected");
            try {
                PermissionCheck(PERMISSION_CHECK);
            } catch (RemoteException e) {
                Log.i("", "onServiceConnected permissionCheck exception " + e);
                e.printStackTrace();
            }
            if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
//                stringBuilder = new SpannableStringBuilder(getSharedString("USERNAME") + " - 업무망접속중");
                stringBuilder = new SpannableStringBuilder(getSharedString("USERNAME") );
                stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8800ff")), getSharedString("USERNAME").length(),
                        stringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                text_name.setText(stringBuilder);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("SgnServiceConnection", "===================onServiceDisconnected===================");
            System.out.println("Service Disconnected");
            tempService = null;
            if (cpd != null && cpd.isShowing()) {
                progressState(false, "비정상종료", 99);
                text_name.setText(getSharedString(getSharedString("USERNAME")));
                mvpnstate = true;
                Toast.makeText(MainActivity.this, getString(R.string.vpn_unusual_finish), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClientUtil.SGVPN_API);
                intent.setPackage(ClientUtil.SGN_PACKAGE);
                if (!bindService(intent, mConnection, BIND_AUTO_CREATE)) {
                    Log.i(TAG, "service bind error");
                } else {
                    try {
                        startService(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }//if else
            }//if
        }//onServiceDisconnected

    }//SgnServiceConnection

    public void PermissionCheck(int requestCode) throws RemoteException {
        Log.i("", "PermissionCheck======== tempservice is " + vpnConn.getTempService());
        /*MobileApi objAidl = MobileApi.Stub.asInterface(tempService);*/
        Intent permissioncheck = vpnConn.getPermissionCheck();
        if (permissioncheck == null) {
            Log.i("", "permissionchek is null");
            onActivityResult(requestCode, Activity.RESULT_OK, null);
        } else {
            Log.i("", "permissionchek is not null");
            startActivityForResult(permissioncheck, requestCode);
        }
    }

    public CommonDialog commonDialog1m;
    public void connection() {
        //     Toast.makeText(LoginActivity.this, encrypted.toString(), Toast.LENGTH_SHORT).show();
        int status = vpnConn.getStatus();
        logmaking("VPNConn.getStatus", vpnConn.getStatus());

        if (CommonUtil.getMobileData(MainActivity.this) == TYPE_WIFI) {        // Wi-Fi 켜져 있을 경우
            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            commonDialog1m = new CommonDialog(MainActivity.this, "", getString(R.string.wifi_network), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonDialog1m.dismiss();
                            wifi.setWifiEnabled(false);

                            drawer.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    preConnection();
                                }
                            }, 2000);

                            Log.v(TAG, "Progressshowing");
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonDialog1m.dismiss();
                        }
                    });
            commonDialog1m.show();

        } else {            //	Wi-Fi OFF LTE
//                edit.putInt("Wi-Fi", 0);		//Wi-Fi 켜져있으면  1, 꺼져있으면 0 저장
            if (status == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
            } else {
                preConnection();
            }
        }
    }

    public void preConnection() {
        progressState(true, getString(R.string.vpn_wait_message), 99);
        //cpd.show();

        if (!("").equals(getSharedString("USERID")) && !("").equals(getSharedString("USERPWD"))) {
            Log.e(TAG, getSharedString("USERPWD"));
            vpnConn.connection(getSharedString("USERID"), getSharedString("USERPWD"));
        }
        //vpnConn.connection("test01", "test01");
    }

    // Package 설치여부 확인
    private void prepareStartProfile(int requestCode) throws RemoteException {

        Intent requestpermission = vpnConn.getService();
        Log.i(TAG("에스지엔 PERMISSION"), "requestpermission : " + requestpermission);
        if (requestpermission == null) {
            Log.i(TAG("에스지엔 PERMISSION"), "==========requestpermission is null");
            onActivityResult(requestCode, Activity.RESULT_OK, null);
        } else {
            Log.i(TAG("에스지엔 PERMISSION"), "==========requestpermission is not  null");
            startActivityForResult(requestpermission, requestCode);
        }
    }

    private List<AppdataList> downLoadableAppList = new ArrayList<>();
    public void getDownloadableApp() {
        downLoadableAppList.clear();

        main_recycler_gridview.post(new Runnable() {
            @Override
            public void run() {
                cpd_parsing = new CustomprogressDialog(MainActivity.this, "잠시만 기다려 주세요");
                cpd_parsing.show();

            }
        });

        ContentResolver contentResolver;
        contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                Uri.parse("content://com.ex.store.provider.userappinfo/TBNM"),
                null,
                null, null, null);

        //5개의 고정된 앱
        String[] appNm = {"외출휴가", "게시판", "사내메일", "직원검색", "전자결재", "메모보고", "스토어"};
        String[] appId = {"MOGP000006", "MIGP000003", "MOGP000001", "MOGP000004", "MOGP000047", "MOGP000048", "MOCR000003"};
        String[] packageNm = {EASYAP_PKGNM, BOARD_PKGNM, MAIL_PKGNM, SEARCHMAN_PKGNM, ELEC_PKGNM, MEMO_PKGNM, EX_STORE_PACKAGE};
        String[] appver = {"0", "0", "0", "0", "0", "0", "0"};

        for (int i = 0; i < appNm.length; i++) {
            AppdataList appdataList = new AppdataList();
            appdataList.setAppId(appId[i]);
            appdataList.setAppNm(appNm[i]);
            appdataList.setPackageNm(packageNm[i]);
            appdataList.setAppVer(appver[i]);
            downLoadableAppList.add(appdataList);
            Log.e(TAG, "==Downloadable===" + appdataList.getAppNm());
        }

        while (cursor.moveToNext()) {
            if (/*cursor.getInt(6) != 0*/true) {
                AppdataList appdataList = new AppdataList();
                appdataList.setAppId(cursor.getString(1));
                appdataList.setAppNm(cursor.getString(2));
                appdataList.setPackageNm(cursor.getString(3));
                appdataList.setAppVer(cursor.getString(4));
                //appdataList.setAppCondition(cursor.getInt(6));
                downLoadableAppList.add(appdataList);
                Log.e(TAG, "==Downloadable===" + appdataList.getAppNm());
            }
        }

        if (getSharedString("DRAGMODE").equals("Y")) {
            Log.e(TAG("DEBUGGING==downloadableAppSize"), ": " + downLoadableAppList.size());
            APPINFODB appinfodb = new APPINFODB(getBaseContext());
            List<AppdataList> draggedList = new ArrayList<>();
            if (appinfodb.getAppInfoList().size() == 0) {
                //만약에 db가 비워져있다면 그냥 프로파이더의 내용을 바탕으로 판단 고고링
                Log.e(TAG, "==Downloadable===리스트가 없음");
            } else {
                draggedList = appinfodb.getAppInfoList();
                Log.e(TAG("downLodableAppList size drag"), ": " + draggedList.size());
                //TODO:#1 draggedList  -> provider   =  App의 삭제 목록을 찾아낸다.

                List<String> Deletelist = new ArrayList<>();
                for (int i = 0; i < draggedList.size(); i++) {
                    Log.e(TAG("DEBUGGING==draggedList.size"), ": " + draggedList.size());
                    boolean count = false;
                    //true : 앱 존재
                    //false : 앱 삭제
                    for (int j = 0; j < downLoadableAppList.size(); j++) {
                        if (draggedList.get(i).getAppNm().equals(downLoadableAppList.get(j).getAppNm())) {
                            Log.e(TAG("비교 시작_빼기_MATCHING"), draggedList.get(i).getAppNm() + " : " + downLoadableAppList.get(j).getAppNm());
                            draggedList.get(i).setAppVer(downLoadableAppList.get(j).getAppVer());
                            count = true;     //같은 값이 존재 하면 true 로 변경
                        }
                    }
                    if (count == false) {
                        //count 값이 false 일때 해당 index를 저장한다.
                        Deletelist.add(draggedList.get(i).getAppNm());
                    }
                }
                Log.e(TAG("비교 시작_빼기SIZE"), " : size " + Deletelist.size());

                for (int del = 0; del < Deletelist.size(); del++) {
                    // 인트형 어레이리스트에 저장된 값중 해당 하는  list를 날려버린다.
                    Log.e(TAG("비교 시작_빼기_DELETING"), Deletelist.get(del).toString() + " : size " + Deletelist.size());

                    for (Iterator<AppdataList> it = draggedList.iterator();it.hasNext();){
                        String val = it.next().getAppNm();
                        if(val.equals(Deletelist.get(del))){
                            it.remove();
                        }
                    }
                }


                //TODO:#2 provider  -> draggedList   =  App의 추가 목록을 찾아 낸다.
                List<Integer> Addlist = new ArrayList<>();
                for (int i = 0; i < downLoadableAppList.size(); i++) {
                    int count = 0;
                    //   true:
                    //  false:
                    for (int j = 0; j < draggedList.size(); j++) {

                        if (!downLoadableAppList.get(i).getAppNm().equals(draggedList.get(j).getAppNm())) {
                            //같은 값이 존재 하면 기존에 있던 앱과 같음
                            //같은 값이 없으면 추가 해줘야함 고로 같은 값이 있을때
                            //true로 변경 해줘야한다.
                            count ++;
                            Log.e(TAG("비교 시작_더하기(NOPE)"), downLoadableAppList.get(i).getAppNm() + " : " + draggedList.get(j).getAppNm()+"count : "+count);
                            // 같은 값이 존재 하면 기존에 있던 앱과 같음. 같은 값이 없으면 추가 해줘야함 고로 같은 값이 있을때  true로 변경 해줘야한다.
                        } else {
                            Log.e(TAG("비교 시작_더하기(MATCHING)"), downLoadableAppList.get(i).getAppNm() + " : " + draggedList.get(j).getAppNm());
                        }

                        if (count==draggedList.size()) {
                            //count 값 이 false 일때 해당 index를 저장한다.
                            Addlist.add(i);
                        }
                    }
                }

                Log.e(TAG("비교 시작_더하기"), String.valueOf(Addlist.size()));

                for (int add = 0; add < Addlist.size(); add++) {
                    Log.e(TAG("DEBUGGING==addListwhat"), ": " + Addlist.get(add));
                    // 인트형 어레이리스트에 저장된 값중 해당 하는 list를 추가해준다.
                    draggedList.add(downLoadableAppList.get(Addlist.get(add)));
                    Log.e(TAG("비교 시작_더하기(really)"), String.valueOf(downLoadableAppList.get(add).getAppNm()));

                }

                //TODO:#3 마지막으로 가공된 draggedList를 downloaddableAppList에 넣어 준다.
                downLoadableAppList.clear();
                for (int a = 0; a < draggedList.size(); a++) {
                    downLoadableAppList.add(draggedList.get(a));

                }
                for (int zz = 0; zz < downLoadableAppList.size(); zz++) {
                    Log.e(TAG("비교 시작_최종리스"), ": " + downLoadableAppList.get(zz).getAppNm());
                }
            }

            appinfodb.close();
        }
    }

    public String isExistApp(String packageName, String appVersion) {
        String state = UNINSTALLED;
        Log.d(TAG,"[EJY] isExistApp() - downLoadableAppList.size : "+ downLoadableAppList.size());
        for (AppdataList app : downLoadableAppList) {
//            Log.d(TAG,"[EJY] isExistApp() - downloadAppList check ["+app.getAppNm() +"] "+ app.getPackageNm());
            Log.d("JSJ","JSJ downloadAppList check "+packageName +":"+ app.getPackageNm());
            if (packageName.equals(app.getPackageNm())) {
                if (appVersion.equals(app.getAppVer())) {
                    Log.e(TAG, "is INSTALLED" + app.getAppNm());
                    return INSTALLED;
                } else if (app.getAppVer().equals("0")) {
                    Log.v(TAG, "[Target MUST is INSTALLED][ " + packageName + "---" + appVersion + "]   vs  [" + app.getPackageNm() + "] : [" + app.getAppVer() + "]");
                    return INSTALLED;
                } else {
                    Log.v(TAG, "[Target UPDATE][ " + packageName + "---" + appVersion + "]   vs  [" + app.getPackageNm() + "] : [" + app.getAppVer() + "]");
                    return NEED_UPDATE;
                }
            }
        }
        Log.v(TAG, "STATE   : " + state);
        return state;

    }

    public void destroyConnection() {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy Exception : " + e);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(MainActivity.this, AppStateService.class);
                stopService(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, AppStateService.class);
                stopService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "onDestroy Exception : " + e);
        }

        try {
            if (mVpnReceiver != null) {
                {   unregisterReceiver(mVpnReceiver);  }

                vpnConn.disconnection();
                ssmLib.setLoginStatus(LOGOUT);
                if (mVpnReceiver != null) {
                    unregisterReceiver(mVpnReceiver);
                }
            }
            if (cpd != null) {
                progressState(false, "비정상종료", 99);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "====onDestroy");
        setSharedString("LOGINSTATE", "LOGOUT");

        if (vpnConn != null) {
            Log.e(TAG, "vpnConn is Activating");
            vpnConn.getInstance(tempService);
            vpnConn.disconnection();
            vpnConn = null;
        } else {
            Log.e(TAG, "vpnConn is null");
        }
        try {
            unregisterReceiver(mVpnReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(AppRefreshReceiver);
        } catch (Exception e) {
        }
        try {
            unbindService(mConnection);
        } catch (Exception e) {
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(MainActivity.this, AppStateService.class);
                stopService(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, AppStateService.class);
                stopService(intent);
            }
        } catch (Exception e) {
        }
    }

    Runnable runner = new Runnable() {
        @Override
        public void run() {
            int result;
            result = ssmLib.setLoginStatus(SSMProtocolParam.LOGOUT);
            Log.i(TAG, "SSM LOGIN STATUS result : " + result);
            Log.i(TAG, "ssm LOGOUT wifi : " + Wi_Fi);
            Log.i(TAG, "ssm LOGOUT bluetooth : " + bluetooth);

            if (Wi_Fi == ConnectivityManager.TYPE_WIFI) {
                if (wifi != null) {
                    Log.i(TAG, "Wi_Fi ON!!!!");
                    wifi.setWifiEnabled(true);
                }
            }
            if (bluetooth == BluetoothAdapter.STATE_ON) {
                if (bAdapter != null) {
                    Log.i(TAG, "Bluetotoh ON!!");
                    bAdapter.enable();
                }
            }
        }
    };

    /*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

    //   mainActivity 의 category의 그리드뷰 리사이클러뷰의 어댑터 이다.//그룹웨어 현장업무, 행정업무, 사내편의에 들어갈 내용을 뿌려준다.

      ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/
    public class MainCategoryRecyclerGridViewAdapter extends RecyclerView.Adapter<MainCategoryRecyclerGridViewAdapter.ItemRowHolder> implements TouchAdapter {

        public ArrayList<AppdataList> appdataLists;
        private Context context;
        boolean setAppVpn = false;

        public MainCategoryRecyclerGridViewAdapter(Context context, ArrayList<AppdataList> appdataLists) {
            this.context = context;
            this.appdataLists = appdataLists;
        }
        public MainCategoryRecyclerGridViewAdapter(Context context, ArrayList<AppdataList> appdataLists, boolean setAppVpn) {//JSJ
            this.context = context;
            this.appdataLists = appdataLists;
            this.setAppVpn = setAppVpn;
            Log.d("appdataLists","appdataLists in adapter size = " + appdataLists.size());
        }

        public ArrayList<AppdataList> getAppdataLists() {
            notifyDataSetChanged();
            Log.e(TAG, "[EJY] getAppdataLists() - appdataLists.size : "+String.valueOf(appdataLists.size()));
            ArrayList<AppdataList> getList = new ArrayList<>();
            for (int i = 0; i < appdataLists.size(); i++) {
                getList.add(appdataLists.get(i));
            }
            return getList;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            if (cpd_parsing != null) {
                if (cpd_parsing.isShowing()) {
                    cpd_parsing.dismiss();
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(ItemRowHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_app_list_layout, null);

            Resources resources = getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();

            float width = metrics.widthPixels;
            float height = metrics.heightPixels;
            width = main_recycler_gridview.getWidth();
            height = main_recycler_gridview.getHeight();

////            float BasicRatio = 640f / 360f;
//            float BasicRatio =1.81f;
//            float myScreenRatio = height / width;
//            float ratioGap = myScreenRatio - BasicRatio;
//            float myScreenheightToBasicRatio = height * (1 - (ratioGap / 2f));
//            float iconheight = myScreenheightToBasicRatio * (104f / 640f);
//            Log.d(TAG, "width : " + width + "\nheight : " + height + "\nBasicRatio: " + BasicRatio + "\nmyScreenRation: " + myScreenRatio +
//                    "\nratioGap : " + ratioGap + "\nmyScreenheightToBasicRatio: " + myScreenheightToBasicRatio + "\niconheight: " + iconheight);
//            contentView.setLayoutParams(new LinearLayout.LayoutParams((int)width/4, (int) iconheight));
            contentView.setLayoutParams(new LinearLayout.LayoutParams((int)width/4, (int) height/3));

            ItemRowHolder myHolder = new ItemRowHolder(contentView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(final ItemRowHolder holder, final int position) {
            Log.d(TAG, "[EJY] onBindViewHolder() ");
            final AppdataList list = appdataLists.get(position);
            Log.d(TAG, "[EJY] onBindViewHolder() - VPN -> list :: "+list.getAppNm()+"/ "+list.getAppVer()+" / "+list.getUnCheckedCount());

            //new DownloadImageTask(holder.appicon).execute(appdataLists.get(i).getIconUrl());
            /*if (list.getDrawableicon() == 0) {
                try {
                    holder.appicon.setImageDrawable(context.getPackageManager().getApplicationIcon(list.getPackageNm()));

                } catch (Exception e) {
                }
            } else {
                holder.appicon.setImageResource(list.getDrawableicon());
            }*/

            holder.appicon.setImageResource(list.getDrawableicon2());
            holder.appicon_name.setText(list.getAppNm());

            //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
            if(getIsTablet(context)) {
                holder.appicon_name.setTextSize(Dimension.SP, fontSizeForTablet);
            }
            Log.d(TAG, "[EJY] onBindViewHolder() 2 ");
            if(setAppVpn){
                holder.ll_bordersetting.setBackground(getResources().getDrawable(R.drawable.main_recycler_border));
            }else if(!setAppVpn){
                holder.ll_bordersetting.setBackground(getResources().getDrawable(R.drawable.main_recycler_border2));
            }

            try {
                if (!list.getUnCheckedCount().equals("") && !list.getUnCheckedCount().equals("0")) {
                    holder.appicon_count.setVisibility(View.VISIBLE);
                    holder.appicon_count.setVisibility(View.VISIBLE);

                    Log.v(TAG, "[EJY] onBindViewHolder() - getUnChekcedCount [" + list.getUnCheckedCount() + "]");
                    holder.appicon_count.setText(list.getUnCheckedCount());
                    //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
                    if(getIsTablet(context)) {
                        holder.appicon_count.setTextSize(Dimension.SP, fontSizeForTablet);
                        holder.appicon_count.setWidth(fontSizeForTablet + 10);
                        holder.appicon_count.setHeight(fontSizeForTablet + 10);
                    }
                } else {
                    holder.appicon_count.setVisibility(View.GONE);
                }
            } catch (Exception e) {
            }

            //VPN 여부 표시
            if (list.getEssentialYn().equals("Y")) {
//                holder.appicon_state_vpn_layout.setVisibility(View.VISIBLE);//2021-03-09 감독 요청으로 vpn표시 제거
                holder.appicon_state_vpn_layout.setVisibility(View.GONE);
            } else {
                holder.appicon_state_vpn_layout.setVisibility(View.GONE);
            }

            //업데이트 여부 표시 및 업데이트 여부에 따른 스토어 실행
            if (list.getNeedUpdate().equals("Y")) {
                Log.e(TAG, "[EJY] : THIS APPPLICATION NEEDS UPDATE ");
                holder.appicon_state_update_layout.setVisibility(View.VISIBLE);

                holder.appicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //2021-05-10 [EJY] 스마트정비관리인 경우 VPN 끊기 제거
                        if(!list.getAppNm().equals("스마트정비관리")) {
                            //2021-03-31 [EJY] VPN 연결 끊기
                            disVpnConn();
                        }

                        try {
                            Intent intent = new Intent(EX_STORE_PACKAGE + ".LAUNCH_MAIN");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                    }

                });
            } else {
                holder.appicon_state_update_layout.setVisibility(View.GONE);
                holder.click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "[EJY] position : "+position);
                        Log.d(TAG, "[EJY] getAdapterPosition : "+holder.getAdapterPosition());

                        if (netWorkStateCheck()) {
                            vibrate(15);
                            stackRunningAppLog(list.getAppId());
//                            holder.click.setBackgroundColor(getResources().getColor(R.color.gray));
//                            holder.appicon.setClickable(true); //[EJY!!!] 원래 false
//                            view.setBackgroundColor(Color.GRAY);
                            holder.appicon.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.appicon.setClickable(true);
                                }
                            }, 1500);

                            //VPN 접속여부 CASE : YES
                            try {

                                if (list.getEssentialYn().equals("Y")) {
                                    Log.d(TAG, "[EJY] onBindViewHolder() - VPN 접속여부 CASE : YES ");
                                    //VPN RECEIVER가 등록돼있지 않다면 진입
                                    if (mVpnReceiver == null) {
                                        IntentFilter intentFilter = new IntentFilter(ClientUtil.SGVPN_STATUS);
                                        registerReceiver(mVpnReceiver, intentFilter);
                                    }

                                    //VPNCONNECTION STATUS가 접속상태 ?  VPN 접속 : 앱실행
                                    if (vpnConn.getStatus() != Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                                        VPNpackageName = list.getPackageNm();
                                        VPNHybridURL = list.getHybridUrl();
                                        start_vpn();

                                    } else if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                                        //기존 FIXED APP  에대해서는 하드코딩돼있음
                                        if (list.getAppNm().equals("외출휴가")) {
                                            Intent intent = new Intent(context, ApprovalMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        } else if (list.getAppNm().equals("게시판")) {
                                            Intent intent = new Intent(context, BoardListActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        } else if (list.getAppNm().equals("사내메일")) {
                                            Intent intent = new Intent(context, EmailMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        } else if (list.getAppNm().equals("전자결재")) {
                                            Intent intent = new Intent(context, ElecMemoAppWebViewActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("hybridUrl", ELEC_URL);
                                            context.startActivity(intent);
                                        } else if (list.getAppNm().equals("메모보고")) {
                                            Intent intent = new Intent(context, ElecMemoAppWebViewActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("hybridUrl", MEMO_URL);
                                            context.startActivity(intent);
                                        } else if (list.getAppNm().equals("직원검색")) {
                                            Intent intent = new Intent(context, WebViewActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("hybridUrl", searchURL + getSharedString("USERDEPARTURECODE"));
                                            Log.v("hybridSelect", "   :::    " + list.getHybridUrl());
                                            context.startActivity(intent);
                                            //FIXED APP 이 아닌 경우에는 하이브리드 여부를 판단해준다.
                                        } else {
                                            Log.d(TAG, "[EJY] onBindViewHolder() - YES [ HybridYn : "+list.getHybridYn()+" ] "+list.getPackageNm());
                                            if (list.getHybridYn().equals("N")) {
                                                try {
                                                    Intent intent = new Intent(list.getPackageNm().toString() + ".LAUNCH_MAIN");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("userId", getSharedString("USERID"));
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    Intent intent = getPackageManager().getLaunchIntentForPackage(list.getPackageNm().toString());
                                                    intent.putExtra("userId", getSharedString("USERID"));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Intent intent = new Intent(context, WebViewActivity.class);
                                                intent.putExtra("hybridUrl", searchURL + getSharedString("USERDEPARTURECODE"));
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                Log.v("hybridSelect", "   :::    " + list.getHybridUrl());
                                                context.startActivity(intent);
                                            }
                                        }
                                    }

                                } else {//VPN 접속여부 CASE : NO
                                    Log.d(TAG, "[EJY] onBindViewHolder() - VPN 접속여부 CASE : NO ");
                                    if (list.getAppNm().equals("외출휴가")) {
                                        Intent intent = new Intent(context, ApprovalMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } else if (list.getAppNm().equals("게시판")) {
                                        Intent intent = new Intent(context, BoardListActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } else if (list.getAppNm().equals("사내메일")) {
                                        Intent intent = new Intent(context, EmailMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } else if (list.getAppNm().equals("직원검색")) {
                                        Intent intent = new Intent(context, WebViewActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("hybridUrl", searchURL + getSharedString("USERDEPARTURECODE"));
                                        Log.v("hybridSelect", "   :::    " + list.getHybridUrl());
                                        context.startActivity(intent);
                                    } else {
                                        Log.d(TAG, "[EJY] onBindViewHolder() - NO [ HybridYn : "+list.getHybridYn()+" ] "+list.getPackageNm());
                                        if (list.getHybridYn().equals("N")) {

                                            //2021-05-10 [EJY] 스마트정비관리인 경우 VPN 끊기 제거
                                            if(!list.getAppNm().equals("스마트정비관리")) {
                                                //2021-03-31 [EJY] VPN 연결 끊기
                                                disVpnConn();
                                            }

                                            try {
                                                Intent intent = new Intent(list.getPackageNm().toString() + ".LAUNCH_MAIN");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                Intent intent = getPackageManager().getLaunchIntentForPackage(list.getPackageNm().toString());
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("userId", getSharedString("USERID"));
                                                startActivity(intent);
                                            }
                                        } else {
                                            Intent intent = new Intent(context, WebViewActivity.class);
                                            intent.putExtra("hybridUrl", searchURL + getSharedString("USERDEPARTURECODE"));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            Log.v("hybridSelect", "   :::    " + list.getHybridUrl());
                                            context.startActivity(intent);
                                        }

                                    }

                                    throw new Exception();
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                });

            }
            //하이브리드 앱 상태 여부에 따라서 onClickListener 걸어주기
        }

        @Override
        public int getItemCount() {
            int size = appdataLists.size();
            return (null != appdataLists ? appdataLists.size() : 0);
        }

        @Override
        public boolean onItemMoved(int fromPosition, int toPosition) {
            return false;
        }

        @Override
        public boolean onItemMove(final int fromPosition, final int toPosition ,int state) {
            final int from = fromPosition;
            final int to = toPosition;
            setSharedString("DRAGMODE","Y");

            if(from>to){
                appdataLists.add(toPosition,appdataLists.get(from));
                notifyItemMoved(from,to);
                appdataLists.remove(from+1);
            }else if(to > from){
                appdataLists.add(toPosition+1,appdataLists.get(from));
                notifyItemMoved(from,to);
                appdataLists.remove(from);
            }
            //Collections.swap(appdataLists, from, to);
            return false;
        }

        @Override
        public void onItemDismiss(int position) {
            appdataLists.remove(position);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder implements TouchViewHolder {
            protected LinearLayout click;
            protected LinearLayout ll_bordersetting;
            protected ImageView appicon;
            protected ImageView appicon_state_vpn;
            protected ImageView appicon_state_update;
            protected TextView appicon_count;
            protected TextView appicon_name;
            protected LinearLayout appicon_state_update_layout;
            protected LinearLayout appicon_state_vpn_layout;

            public ItemRowHolder(View view) {
                super(view);
                this.ll_bordersetting = (LinearLayout) view.findViewById(R.id.ll_bordersetting);
                this.click = (LinearLayout) view.findViewById(R.id.click);
                this.appicon = (ImageView) view.findViewById(R.id.appicon);
                this.appicon_state_vpn = (ImageView) view.findViewById(R.id.appicon_state_vpn);
                this.appicon_state_update = (ImageView) view.findViewById(R.id.appicon_state_update);
                this.appicon_count = (TextView) view.findViewById(R.id.appicon_count);
                this.appicon_name = (TextView) view.findViewById(R.id.main_app_name);
                this.appicon_state_update_layout = (LinearLayout) view.findViewById(R.id.appicon_state_update_layout);
                this.appicon_state_vpn_layout = (LinearLayout) view.findViewById(R.id.appicon_state_vpn_layout);
            }

            @Override
            public void onItemSelected() {
                vibrate(60);
            }

            @Override
            public void onItemClear() {
            }

            @Override
            public void onItemDropped() {
                // notifyDataSetChanged();
            }
        }


        public void updateAppdataList(ArrayList<AppdataList> newList) {
            final AppDiffCallback diffCallback = new AppDiffCallback(this.appdataLists, newList);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

            this.appdataLists.clear();
            this.appdataLists.addAll(newList);

            diffResult.dispatchUpdatesTo(this);
        }

        /* RecyclerView 목록 업데이트 */
        public void updateAppdataList(ArrayList<AppdataList> newList, boolean isVpn) {
            Log.d("updateAppdataList","updateAppdataList list size = " + newList.size());
            final AppDiffCallback diffCallback = new AppDiffCallback(this.appdataLists, newList);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

            this.appdataLists.clear();
            Log.d(TAG,"updateAppdataList() appdataLists : " + appdataLists);
            for (int i=0; i<newList.size(); i++){
                if(isVpn){
                    if("Y".equals(newList.get(i).getEssentialYn())) {
                        this.appdataLists.add(newList.get(i));
                    }
                }else{
                    if("N".equals(newList.get(i).getEssentialYn())) {
                        this.appdataLists.add(newList.get(i));
                    }
                }
            }

            diffResult.dispatchUpdatesTo(this);
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }
    }

    public void reNewDBINFO(ArrayList<AppdataList> appdataLists) {
        APPINFODB appInfoListFromDB = new APPINFODB(getBaseContext());
        Log.e(TAG("IS DRAGGED? : "),getSharedString("DRAGMODE"));
        if (appInfoListFromDB.getAppInfoList().size() == 0) {//테이블 사이즈 가 0일때
            try {
                Log.e(TAG("\nONINSERT ROW"), "\nINSERTING ROW START");
                for (int i = 0; i < appdataLists.size(); i++) {

                    appInfoListFromDB.INSERT_APPITEM(
                            appdataLists.get(i).getPackageNm(),
                            appdataLists.get(i).getAppId(),
                            appdataLists.get(i).getAppNm(),
                            appdataLists.get(i).getAppVer());
                }
            } catch (Exception e) {
                Log.e(TAG("\nONINSERT ROW"), "\nINSERTING ROW FAILDED");
            }
        } else {
            //테이블 사이즈가 0이 아닐때
            try {
                Log.e(TAG("\nONDElETE TABLE"), "\nDELETING TABLE START");
                appInfoListFromDB.DROPTABLE();

            } catch (Exception e) {
            }

            try {
                if (appInfoListFromDB.getAppInfoList().size() == 0) {
                    Log.e(TAG("\nDELETING TABLE IS SUCCES NOW ONINSERT ROW"), "\nINSERTING ROW START");
                    for (int i = 0; i < appdataLists.size(); i++) {
                        try {
                            appInfoListFromDB.INSERT_APPITEM(
                                    appdataLists.get(i).getPackageNm(),
                                    appdataLists.get(i).getAppId(),
                                    appdataLists.get(i).getAppNm(),
                                    appdataLists.get(i).getAppVer());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG("\nONINSERT ROW"), "\nINSERTING ROW FAILDED");
            }

        }

        appInfoListFromDB.close();
    }


    private BroadcastReceiver AppRefreshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refresh = true;
        }
    };


    /*█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█*/
    /*█             VPN broadCastReceiver                                                                                                                                                                █*/
    /*█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    //SGVPN Receiver
    private BroadcastReceiver mVpnReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressState(true, "", 0);
            int value = intent.getIntExtra("STATUS", 0);
            Log.i(TAG, "==========SGVPNBroadcastReceiver========== getAction ============" + intent.getAction() + "[VALUE] : "
                    + value +
                    "-" + intent.getStringExtra("DETAILSTATUS"));

            if (intent.getAction().equals("com.sgvpn.vpnservice.STATUS")) {
                int service_status = intent.getIntExtra("STATUS", 0);

                if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                    Log.e(TAG, "[STATUS at BR]-[ status : " + vpnConn.getStatus() + " ]");

                    progressState(false, intent.getStringExtra("DETAILSTATUS"), intent.getIntExtra("STATUS", 0));
//                    stringBuilder = new SpannableStringBuilder(getSharedString("USERNAME") + " - 업무망접속중");
                    stringBuilder = new SpannableStringBuilder(getSharedString("USERNAME"));
                    stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8800ff")), getSharedString("USERNAME").length(),
                            stringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                } else {
                    progressState(false, intent.getStringExtra("DETAILSTATUS"), vpnConn.getStatus());
                }
            }
        }


    };


    public void progressState(final boolean visible, final String vpnState, final int vpnCode) {
        Log.d(TAG, "[EJY] progressState() - visible : "+visible);
        Log.d(TAG, "[EJY] progressState() - vpnState : "+vpnState);
        Log.d(TAG, "[EJY] progressState() - vpnCode : "+vpnCode);
        final int vpnStateCode = vpnCode;

        drawer.post(new Runnable() {
            @Override
            public void run() {
                if (cpd == null & vpnCode != Constants.Status.Connection_N_Status.LEVEL_DISCONNECTED_DONE.ordinal()) {
                    cpd = new CustomprogressDialog(MainActivity.this, null);
                    cpd.setCancelable(false);
                    cpd.setContent(vpnState);
                    if ((commonDialog_oneButton != null && commonDialog_oneButton.isShowing())) {
                        commonDialog_oneButton.dismiss();
                        commonDialog_oneButton = null;
                    } else if (commonDialog != null && commonDialog.isShowing()) {
                        commonDialog.dismiss();
                        commonDialog = null;
                    }
                    try {
                        try {
                            cpd.show();
                        } catch (Exception e) {
                        }
                        counter = 0;
                        ConnectionTimmerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Log.e(TAG, "ConnectionTimeCount : " + String.valueOf(counter));

                                if (counter == CONNECTIONTIME) {
                                    if (cpd != null) {
                                        cpd.dismiss();
                                        cpd = null;
                                        timer.cancel();
                                        timer.purge();
                                        ConnectionTimmerTask.cancel();
                                    }
                                    if (vpnConn == null) {
                                        Toast.makeText(MainActivity.this, "접속제한 시간을 초과하였습니다. \n로그아웃 이후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                    if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                                        Intent intent = new Intent(MainActivity.this, AppStateService.class);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            intent.putExtra("start", "start");
                                            startService(intent);
                                            Log.e("MainActivity ---", "STARTFOREGROUNDSERVICE");
                                        } else {
                                            startService(intent);
                                        }

                                    } else {
                                        vpnConn.disconnection();
                                        try {
                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this,
                                                    getString(R.string.connectingFailed), getString(R.string.connectionTimeExcess), true,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            destroyConnection();
                                                            onResume();
                                                        }
                                                    });
                                            commonDialog_oneButton.show();
                                        } catch (Exception e) {
                                        }
                                    }

                                }
                                counter++;
                            }
                        };
                        timer = new Timer();
                        timer.schedule(ConnectionTimmerTask, 0, 1000);
                    } catch (Exception e) {
                    }
                }

                if (visible) {
                    //do Nothing
                } else {
                    if (vpnCode == 99) {
                        if (cpd != null) {
                            cpd.dismiss();
                            cpd = null;
                            timer.cancel();
                            timer.purge();
                            ConnectionTimmerTask.cancel();
                        }
                    }

                    if ((vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal() && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal())) {
                        Log.e(TAG, "[STATUS at THREAD INVISIBLE ]-[ status : " + vpnConn.getStatus() + " ]");

                        if (stringBuilder != null) {
                            text_name.setText(stringBuilder);
                        }

                        text_name.post(new Runnable() {
                            @Override
                            public void run() {
                                if (cpd != null) {
                                    cpd.dismiss();
                                    cpd = null;
                                    timer.cancel();
                                    timer.purge();
                                    ConnectionTimmerTask.cancel();
                                }
                                Log.e(TAG, "[STATUS at THREAD INVISIBLE ]-[ status : " + vpnConn.getStatus() + "  ] \nCONGRAT! VPN IS CONNECTED");
                                Log.d("VPNPACKAGENAME", "VPNPACKAGENAME = " + VPNpackageName);
                                if (VPNpackageName.equals(EASYAP_PKGNM)) {

                                    Intent i = new Intent(MainActivity.this, ApprovalMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);

                                } else if (VPNpackageName.equals(BOARD_PKGNM)) {
                                    Intent i = new Intent(MainActivity.this, BoardListActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                } else if (VPNpackageName.equals(MAIL_PKGNM)) {
                                    Intent i = new Intent(MainActivity.this, EmailMainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                } else if (VPNpackageName.equals(ELEC_PKGNM) || VPNpackageName.equals("전자결재")) {
                                    Intent intent = new Intent(MainActivity.this, ElecMemoAppWebViewActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("app", "E");
                                    intent.putExtra("hybridUrl", ELEC_URL);
                                    MainActivity.this.startActivity(intent);
                                } else if (VPNpackageName.equals(MEMO_PKGNM) || VPNpackageName.equals("메모보고")) {
                                    Intent intent = new Intent(MainActivity.this, ElecMemoAppWebViewActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("hybridUrl", MEMO_URL);
                                    intent.putExtra("app", "M");
                                    MainActivity.this.startActivity(intent);
                                } else if (VPNpackageName.equals(SEARCHMAN_PKGNM)) {
                                    Intent i = new Intent(MainActivity.this, WebViewActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putExtra("hybridUrl", searchURL + getSharedString("USERDEPARTURECODE"));
                                    Log.v("hybridSelect", "   :::    " + VPNHybridURL);
                                    startActivity(i);
                                } else {

                                    Log.v("packageName", ":::: 3 " + VPNpackageName);
                                    try {
                                        Intent i = new Intent(VPNpackageName.toString() + ".LAUNCH_MAIN");
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.putExtra("userId", getSharedString("USERID"));
                                        startActivity(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent(MainActivity.this, AppStateService.class);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.putExtra("start", "start");
                                    startService(intent);
                                    Log.e("MainActivity ---", "STARTFOREGROUNDSERVICE");
                                } else {
                                    startService(intent);
                                }
                            }
                        });
                    } else {
                        if (vpnConn != null) {
                            {
                                Log.e(TAG, "[STATUS at THREAD INVISIBLE ]-[ status : " + vpnConn.getStatus() + " ]");
                                text_name.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.i(TAG, "static progressDialog is showing");
                                        // Log.e(TAG, "=====[VPN_STATE] : [" + vpnStateCode + "] === [VPNCONN_STATE] : [" + vpnConn.getStatus() + "]=====");

                                        if (commonDialog_oneButton == null
                                                /*&& vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_DUP_LOGIN.ordinal()*/
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_DUP_LOGIN.ordinal()) {
                                            //중복 로그인 상황일때의 브로드캐스트 리시버 다이얼로그
                                            //[STATEMESSAGE  : LEVERL_DUP_LOGIN ] [STATECODE  :  6]
                                            if (cpd != null) {
                                                cpd.dismiss();
                                                cpd = null;
                                                timer.cancel();
                                                timer.purge();
                                                ConnectionTimmerTask.cancel();
                                            }

                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this, getString(R.string.connectingFailed), vpnState, false,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            commonDialog_oneButton = null;
                                                            vpnConn.disconnection();

                                                            ssmLib = SSMLib.getInstance(MainActivity.this);
                                                            ssmLib.setLoginStatus(LOGOUT);
                                                        }
                                                    });
                                            commonDialog_oneButton.show();

                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_AUTH_FAILED.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_AUTH_FAILED.ordinal()) {
                                            //    인증정보 실패했을때
                                            //    [ STATEMESSAGE   :  LEVEL_AUTH_FAILED ]  [STATECODE  :  3 ]

                                            if (cpd != null) {
                                                cpd.dismiss();
                                                cpd = null;
                                                timer.cancel();
                                                timer.purge();
                                                ConnectionTimmerTask.cancel();
                                            }

                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this, getString(R.string.authenticationFailed), vpnState, false,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            commonDialog_oneButton = null;
                                                        }
                                                    });
                                            commonDialog_oneButton.show();

                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_CRITICAL_ITEMS_NOT_FOUNTD.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CRITICAL_ITEMS_NOT_FOUNTD.ordinal()) {
                                            //PARAMETER 값이 부족할때
                                            //[STATEMESSAGE  :  LEVEL_CRITICAL_ITEMS_NOT_FOUND]   [STATECODE  :  ]

                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_CONNECTING.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTING.ordinal()) {

                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_NOLEVEL.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_NOLEVEL.ordinal()) {

                                            if (cpd != null) {
                                                cpd.dismiss();
                                                cpd = null;
                                                timer.cancel();
                                                timer.purge();
                                                ConnectionTimmerTask.cancel();
                                            }

                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this, getString(R.string.connectionFailed), vpnState, false,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            commonDialog_oneButton = null;
                                                        }
                                                    });
                                            commonDialog_oneButton.show();

                                            //   네트워크 감지 불가일때의 브로드캐스트 리시버 다이얼로그 [ STATEMESSAGE   :  LEVEL_NONETWORK ]  [STATECODE  :  7 ]
                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_NONETWORK.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_NONETWORK.ordinal()){

                                            if (cpd != null) {
                                                cpd.dismiss();
                                                cpd = null;
                                                timer.cancel();
                                                timer.purge();
                                                ConnectionTimmerTask.cancel();
                                            }

                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this, getString(R.string.networkTransition_title), getString(R.string.networkTransition_text), false,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            commonDialog_oneButton = null;
                                                            ssmLib.setLoginStatus(LOGOUT);
                                                            vpnConn.disconnection();
                                                            //vpnConn=null;

                                                            ssmLib = SSMLib.getInstance(MainActivity.this);
                                                            ssmLib.setLoginStatus(LOGOUT);
                                                            drawer.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if(netWorkStateCheck()){
                                                                        start_vpn();}
                                                                }
                                                            }, 1500);
                                                        }
                                                    });
                                            commonDialog_oneButton.show();

                                        } else if (commonDialog_oneButton == null
                                                && vpnStateCode == Constants.Status.Connection_N_Status.LEVEL_DISCONNECTED_DONE.ordinal()
                                                && vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_DISCONNECTED_DONE.ordinal()
                                                && contextMain != null){
                                            //   네트워크 감지 불가일때의 브로드캐스트 리시버 다이얼로그
                                            //   [ STATEMESSAGE   :  LEVEL_NONETWORK ]  [STATECODE  :  7 ]

                                            if (cpd != null) {
                                                cpd.dismiss();
                                                cpd = null;
                                                timer.cancel();
                                                timer.purge();
                                                ConnectionTimmerTask.cancel();
                                            }

                                            commonDialog_oneButton = new CommonDialog_oneButton(MainActivity.this, getString(R.string.connectingFail_title), getString(R.string.connectingFail_text), false,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            commonDialog_oneButton.dismiss();
                                                            commonDialog_oneButton = null;
                                                            ssmLib.setLoginStatus(LOGOUT);
                                                            mvpnstate = false;
                                                            vpnConn.disconnection();
                                                            vpnConn.disconnection();
                                                            destroyConnection();
                                                            onResume();
                                                        }
                                                    });

                                            try {
                                                commonDialog_oneButton.show();
                                            } catch (Exception e) {
                                            }

                                        } else {
                                            Log.e(TAG, String.valueOf(vpnConn.getStatus()));
                                            if (vpnConn.getStatus() == Constants.Status.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                                                if (cpd != null) {
                                                    cpd.dismiss();
                                                    cpd = null;
                                                    timer.cancel();
                                                    timer.purge();
                                                    ConnectionTimmerTask.cancel();
                                                }
                                            }
                                        }

                                    }
                                }, 0);
                            }
                        }
                    }

                }
            }
        });


    }

    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀*/


    /*█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█*/
    /*█ Measure ScreenSize to                                                                                                                                                                            █*/
    /*█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    public Boolean isTablet() {
        Display dispaly = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        dispaly.getMetrics(displayMetrics);

        double WidthToInchs = displayMetrics.widthPixels / (double) displayMetrics.densityDpi;
        double HeightToInchs = displayMetrics.heightPixels / (double) displayMetrics.densityDpi;
        double screenSize = Math.sqrt(Math.pow(WidthToInchs, 2) + Math.pow(HeightToInchs, 2));
        Log.d(TAG, "isTablet ?  -->" + screenSize);

        return (screenSize >= 7.0);
    }

    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀*/


    //2021-03-31 [EJY] VPN 연결 끊기 메소드 생성
    public void disVpnConn() {
        ssmLib.setLoginStatus(LOGOUT);
        mvpnstate = false;
        vpnConn.disconnection();
        destroyConnection();

        if(commonDialog != null) {
            commonDialog.dismiss();
        }

//        setSharedString("LOGINSTATE", "LOGOUT");
//        finish();

    }


    //2021-04-21 [EJY] 메모보고 공지사항 메인페이지에서 표출
    public void getNoticePopInfo() {
        Log.d(TAG, "[EJY] getNoticePopInfo() ");

        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);

        final Call<MemoNoticePopInfo> requestNoticePopupCall = apiInterface.request_notice_popup_info(getSharedString("USERID"));
        requestNoticePopupCall.enqueue(new Callback<MemoNoticePopInfo>() {

            @Override
            public void onResponse(Call<MemoNoticePopInfo> call, Response<MemoNoticePopInfo> response) {
                Log.d(TAG, "[EJY] getNoticePopInfo() - response.body : "+response.body());
                String result = response.body().result;
                String resultMessage = response.body().resultMessage;
                List<MemoNoticePopInfo.NoticeData> noticeData = response.body().data;

                Log.d(TAG, "[EJY] getNoticePopInfo() - result : "+result);
                Log.d(TAG, "[EJY] getNoticePopInfo() - resultMessage : "+resultMessage);
                Log.d(TAG, "[EJY] getNoticePopInfo() - noticeData : "+noticeData.size());

                if(cpd_parsing!=null && cpd_parsing.isShowing()){
                    cpd_parsing.dismiss();
                }
                if(noticeData.size() > 0) {
                    for(int i=0; i<noticeData.size(); i++) {
                        Log.d(TAG, "[EJY] getNoticePopInfo() - result : "+noticeData.get(i).getNOTC_MATR_TITL_NM()+" / "+noticeData.get(i).getMEMO_NOTC_MATR_SEQ());
                        Log.d(TAG, "[EJY] getNoticePopInfo() - shared : "+getPrefString(MainActivity.this, "CHECKED_NOTC_"+noticeData.get(i).getMEMO_NOTC_MATR_SEQ()));
                        Log.d(TAG, "[EJY] getNoticePopInfo() - getATTFL_SEQ : "+noticeData.get(i).getATTFL_SEQ());
                        String checkedPop = getPrefString(MainActivity.this, "CHECKED_NOTC_"+noticeData.get(i).getMEMO_NOTC_MATR_SEQ());
                        if(!checkedPop.equals("Y")) {  //다시보지않기 체크하지 않았을 경우에만 표시
                            NoticeMemoDialog noticeMemoDialog = new NoticeMemoDialog(MainActivity.this);
                            noticeMemoDialog.show();
                            noticeMemoDialog.setTextView(noticeData.get(i).getMEMO_NOTC_MATR_SEQ(), noticeData.get(i).getNOTC_CLSS_CD(), noticeData.get(i).getNOTC_MATR_TITL_NM(), noticeData.get(i).getNOTC_MATR_CTNT());
//                            noticeMemoDialog.setAttflInfo(checkNull(noticeData.get(i).getATTFL_SEQ()));  //첨부파일 부분 제거 - 웹에서 공지사항 등록 시에도 첨부파일이 제외됨
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MemoNoticePopInfo> call, Throwable t) {
                if(cpd_parsing!=null && cpd_parsing.isShowing()){
                    cpd_parsing.dismiss();
                }
            }
        });
    }


}


