package com.ex.group.folder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ex.group.approval.easy.util.RetrieveAppInfo;
import com.ex.group.approval.easy.util.StringUtil;
import com.ex.group.elecappmemo.ElecMemoAppWebViewActivity;
import com.ex.group.folder.dialog.CommonDialog_oneButton;
//import com.ex.group.folder.service.StoreJobServiece;
import com.ex.group.folder.dialog.CustomprogressDialog;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.RequestLauncherInfo;
import com.ex.group.folder.service.AppStateService;
import com.ex.group.folder.service.FirebaseIDService;
import com.ex.group.folder.service.FirebaseMessagingService;
import com.ex.group.folder.utility.AppInfo;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.ClientUtil;
import com.ex.group.folder.utility.CommonUtil;
import com.ex.group.folder.utility.Constants;
import com.ex.group.folder.utility.CustomVPN;
import com.ex.group.folder.utility.FolderUtil;
import com.ex.group.folder.utility.LogMaker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skt.pe.common.vpn.SGVPNConnection;
import com.sktelecom.ssm.lib.SSMLib;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ex.group.elecappmemo.Global.ELEC_URL;
import static com.ex.group.elecappmemo.Global.MEMO_URL;
import static com.ex.group.folder.service.FirebaseMessagingService.NOTI_ID;
import static com.ex.group.folder.utility.ClientUtil.APPNAME;
import static com.ex.group.folder.utility.ClientUtil.EX_STORE_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.SSM_APP;
import static com.ex.group.folder.utility.ClientUtil.SSM_INSTALLER_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.SSM_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.V3_APP;
import static com.ex.group.folder.utility.ClientUtil.V3_PACKAGE;

import static com.ex.group.folder.utility.CommonUtil.EssentialAppList;
import static com.ex.group.folder.utility.CommonUtil.checkNull;
import static com.ex.group.folder.utility.CommonUtil.clearApplicationData;
import static com.ex.group.folder.utility.CommonUtil.getVersionName;
import static com.ex.group.folder.utility.CommonUtil.isExistApp;
import static com.ex.group.folder.utility.FolderUtil.isRooted;
import static com.skt.pe.common.conf.Constants.Status.PERMISSION_CHECK;
import static com.sktelecom.ssm.lib.constants.SSMProtocolParam.LOGOUT;
import static com.sktelecom.ssm.remoteprotocols.ResultCode.OK;


public class IntroActivity extends BaseActivity {
    private static final int PERMISSION_CHECK = 9134;
    private static final int VPN_SERVICE_PERMISSION_GROUP = 9130;
    private static final int VPN_SERVICE_PERMISSION_ALLOW = 9131;
    private static final int VPN_SERVICE_PERMISSION_DNEY = 9132;
    private static final int APP_PERMISSION_RETURN = 9133;
    private static final int SSM_EXECUTE_CODE = 580;
    private boolean keepgoing = false;


    private ImageView image, image1;
    SharedPreferences userPref;
    String vpnId;
    String vpnPw;
    String token;
    String today;
    String fcmDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    long connectionTime;
    BluetoothAdapter bAdater;
    WifiManager wifi;
    int Wi_Fi;
    int bluetooth;

    CommonDialog_oneButton CDO;
    CommonDialog_oneButton commonDialog_oneButton;
    boolean rtn = false;
    public static SSMLib ssmLib;
    int ssmCode;


    int IMEI_CYCLE = -7;
    static String TAG = "=====IntroActivity=========";

    public String TAG(String str) {
        return TAG + "(" + str + ")==";
    }

    public String STORETAG = "◘◘◘◘◘ FROMSTORE@INTRO ◘◘◘◘◘";
    String mdmUrl = "https://mdm.ex.co.kr:52444/agent/setRegTargetDeviceInfoHphone.do";
    CustomprogressDialog cpd;

    CommonDialog_oneButton finishDialog;
    public void clearFinishDialog(){
        if(finishDialog!=null && finishDialog.isShowing()){
            finishDialog.dismiss();
            finishDialog =null;
        }
    }


    AnimationDrawable animatic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //job 등록
        image1 = (ImageView) findViewById(R.id.image1);
        image = (ImageView) findViewById(R.id.image);
        cpd = new CustomprogressDialog(IntroActivity.this, null);
        cpd.show();
        Intent intent = new Intent(ClientUtil.SGVPN_API);
        intent.setPackage(ClientUtil.SGN_PACKAGE);
        if (!bindService(intent, mConnection, BIND_AUTO_CREATE)) {
            Log.e(TAG, "service bind error");
            destroyConnection();
            finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                    "VPN 연결 실패", "모바일 업무망접속 앱이 설치 되지 않았습니다.\nex스토어에서 업무망접속 앱을 설치하시기 바랍니다.", false, finishListener2);
            finishDialog.show();
        }
        CustomVPN.getInstance().setIntent(intent);
    }


    public void authChk(){
        //2021-05-11 [EJY] App Data 삭제
        String appVerCurrent = checkNull(getVersionName(IntroActivity.this, getPackageName()));
        String appVerSharedPref = checkNull(getSharedString("APPVER"));

        //appVer 이 다르거나 설정이 안되어 있으면 clearApp 실행
        if(!appVerSharedPref.equals(appVerCurrent) || appVerSharedPref.equals("")) {
            clearApplicationData(IntroActivity.this);
            if(!getSharedString("FIRSTRUN").equals("N")) {
                clearSharedPref();
            }
        }

        /*if(permissionCheck()){
            checkUserRegisterState();
        }*/
        Log.d(TAG, "[EJY] authChk() - start ");


        Log.d(TAG,"token IntroActivity = "+FirebaseInstanceId.getInstance().getToken());

        FirebaseMessaging.getInstance().subscribeToTopic("testHoliday");
        //알람세팅에서 설정해놓은 알람 방식대로 topic 을 등록하거나 삭제 해준다.
        if (getSharedString("TOPIC_HOLIDAY").equals("Y")) {
            FirebaseMessaging.getInstance().subscribeToTopic("holiday");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("holiday");
        }
        if (getSharedString("TOPIC_BUSINESS").equals("Y")) {
            FirebaseMessaging.getInstance().subscribeToTopic("businessMessage");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("businessMessage");
        }
        if (getSharedString("TOPIC_MENU").equals("Y")) {
            FirebaseMessaging.getInstance().subscribeToTopic("menu");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("menu");
        }
        if (getSharedString("TOPIC_MENURESEARCH").equals("Y")) {
            FirebaseMessaging.getInstance().subscribeToTopic("menuResearch");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("menuResearch");
        }
        //2021.07 메모보고/전자결재 알림 on/off
        if(getSharedString("TOPIC_ELECAPP").equals("") || getSharedString("TOPIC_ELECAPP") == null ){
            setSharedString("TOPIC_ELECAPP","Y");

        }
        if(getSharedString("TOPIC_MEMOAPP").equals("") || getSharedString("TOPIC_MEMOAPP") == null ){
            setSharedString("TOPIC_MEMOAPP","Y");

        }
        onStoreCallback();

//        checkAppList();
        Log.d(TAG, "[EJY] authChk() - END ");
    }

    public static SGVPNConnection vpnConn;
    public static IBinder tempService = null;
    private IntroActivity.SgnServiceConnection mConnection = new IntroActivity.SgnServiceConnection();

    public void destroyConnection() {
        try {
            Log.e(TAG, "service bind unbindService");
            unbindService(mConnection);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy Exception : " + e);
        }

    }

    private class SgnServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("IntroActivity.onServiceConnected", "===================비정상 종료시 VPN 종료가 정상적으로 되지않아 추가===================");

            //바인드 객체는 사용 못합
            //CustomVPN.getInstance().setBinder(service);
            tempService = service;
            if (vpnConn == null) {
                vpnConn = SGVPNConnection.getInstance(service);
                Log.e(TAG, "##### VPN CustomVPN.getInstance() : " + vpnConn);
            }
            //컨넥션 인스턴스 객체를 전역으로 설정하여 다른 activity에서 사용가능하도록 설정
            //이유 : vpn 어플에 바인드하여 통신 할 경우 모바일오피스가 일시 정지에 빠지면서 onresum을 1번 더 호출하는 현상이 발생
            CustomVPN.getInstance().setVpnConn(vpnConn);

            Log.i("", "Service Connected");
            try {
                PermissionVPNCheck(PERMISSION_CHECK);
            } catch (RemoteException e) {
                Log.i("", "onServiceConnected PermissionVPNCheck exception " + e);
                e.printStackTrace();
            }

            Log.e("IntroActivity.onServiceConnected", "===================VPN 실행시 상태값===================" + vpnConn.getStatus());
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("SgnServiceConnection", "===================onServiceDisconnected===================");
        }//onServiceDisconnected
    }

    public void PermissionVPNCheck(int requestCode) throws RemoteException {
        Log.i("", "PermissionVPNCheck======== tempservice is " + vpnConn.getTempService());
        /*MobileApi objAidl = MobileApi.Stub.asInterface(tempService);*/
        Intent PermissionVPNCheck = vpnConn.getPermissionCheck();
        if (PermissionVPNCheck == null) {
            Log.i("", "PermissionVPNCheck is null");
            onActivityResult(requestCode, Activity.RESULT_OK, null);
        } else {
            Log.i("", "permissionchek is not null");
            startActivityForResult(PermissionVPNCheck, requestCode);
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
                //permissionCheck();
                prepareStartProfile(APP_PERMISSION_RETURN);
            } catch (RemoteException e) {
                System.out.println("ERR : " + e);
            }

        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == APP_PERMISSION_RETURN) {
            Log.i(TAG("에스지엔 PERMISSION"), "onActivityResult ... Activity.RESULT_CANCELED  START_PROFILE_EMBEDDED");
            //VPN-service 앱에서 사용할 권한 거부.. 앱 종료.
            Toast.makeText(IntroActivity.this, R.string.permission_grant, Toast.LENGTH_SHORT).show();
            setSharedString("LOGINSTATE", "LOGOUT");
            vpnConn.disconnection();
            finish();

        } else if (requestCode == PERMISSION_CHECK && resultCode == VPN_SERVICE_PERMISSION_DNEY) {
            Log.i(TAG("에스지엔 PERMISSION"), "onActivityResult ... PERMISSION_CHECK .. VPN_SERVICE_PERMISSION_DNEY ");
            //VPN service를 위한 권한 거부.. 앱 종료, 전화,저장공간.
            Toast.makeText(IntroActivity.this, R.string.permission_grant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + ClientUtil.SGN_PACKAGE));
            startActivity(intent);
            setSharedString("LOGINSTATE", "LOGOUT");
            vpnConn.disconnection();
            finish();
        } else if (requestCode == APP_PERMISSION_RETURN && resultCode == -1) {
            Log.e(TAG, "onActivityResult ... 모바일 오피스 .. 권한 체크 시작============= ");
            /*try {
                Log.e(TAG, "onResume ... startService(CustomVPN.getInstance().getIntent())============= ");
                startService(CustomVPN.getInstance().getIntent());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            vpnConn.disconnection();
            destroyConnection();
            permissionCheck();

            cpd.dismiss();
        }
    }//SgnServiceConnection

    // Package 설치여부 확인
    private void prepareStartProfile(int requestCode) throws RemoteException {

        Intent requestpermission = vpnConn.getService();
        Log.i(TAG("에스지엔 PERMISSION"), "prepareStartProfile : " + requestpermission);
        if (requestpermission == null) {
            Log.i(TAG("에스지엔 PERMISSION"), "==========prepareStartProfile is null");
            onActivityResult(requestCode, Activity.RESULT_OK, null);
        } else {
            Log.i(TAG("에스지엔 PERMISSION"), "==========prepareStartProfile is not  null requestCode" + requestCode );
            startActivityForResult(requestpermission, requestCode);
        }
    }

    public boolean vpnStartFlag = true;
    @Override
    protected void onResume() {
        super.onResume();
        if(vpnStartFlag){
            vpnStartFlag = false;
        }
    }

    public void checkUserRegisterState() {
        int networkStatus = CommonUtil.getMobileData(IntroActivity.this);
        Log.i(TAG, "networkStatus!!!!!" + networkStatus);
        Log.d(TAG, "[EJY] checkUserRegisterState() - networkStatus : "+networkStatus);

        if (networkStatus == -1 ) {
            clearFinishDialog();
            finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                    getString(R.string.hi_failed), getString(R.string.check_network), false, finishListener2);
            finishDialog.show();
        } else {
            String mac = CommonUtil.getMacAddr(IntroActivity.this);
            String mdn = CommonUtil.getMdn(IntroActivity.this);
            String imei = CommonUtil.getImei(IntroActivity.this);

            //IMEI 등록 to SSM 서버
            /**
             *   성공 : 002
             *   등록되지 않은 전화번호 :  003
             *   이미 등록된 휴대폰 정보 :  004
             */
            // 일주일에 한 번
            String authDate = getSharedString("AUTHDATE");
            Calendar aWeekAgoCal = GregorianCalendar.getInstance();
            Calendar authCal = GregorianCalendar.getInstance();

            Log.d(TAG, "[EJY] checkUserRegisterState() - authDate : "+authDate);
            Log.d(TAG, "[EJY] checkUserRegisterState() - aWeekAgoCal : "+aWeekAgoCal);
            Log.d(TAG, "[EJY] checkUserRegisterState() - authCal : "+authCal);

            aWeekAgoCal.add(aWeekAgoCal.DATE, IMEI_CYCLE);

            LogMaker.logStart();
            LogMaker.logmaking("인증날짜", authCal.getTime().toString());
            LogMaker.logmaking("단말 인증 필요", authCal.before(aWeekAgoCal));
            LogMaker.logEnd();

            try {
                if (!("").equals(authDate)) {
                    authCal.setTime(dateFormat.parse(authDate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("", "IntroActivity getPhoneState Step Test 1-1 = " + authDate + ":" + authCal.before(aWeekAgoCal));


            if (("").equals(authDate) || authCal.before(aWeekAgoCal))//인증만료일이 지났을 경우  일주일
            {
                String errorMessage = "";
                //errorMessage = CommonUtil.nullCheckParams(mdn, mac, imei);
                errorMessage = CommonUtil.nullCheckParams(mdn, mac, "1234");//안드로이드 10.0 대응 imei 미사용

                if (("").equals(errorMessage)) {    //mdn, mac, imei 검증


                    new preImeiAsyncTask().execute(mdn, mac, imei);

                } else {
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.alert), errorMessage, false, finishListener2);
                    finishDialog.show();
                }
            } else {


            }

        }
        Log.d(TAG, "[EJY] checkUserRegisterState() - END ");
    }

    public void currentStoreVersion() {
        String current_Version = "";

        try {
            PackageInfo pi;
            pi = getPackageManager().getPackageInfo(EX_STORE_PACKAGE, PackageManager.GET_META_DATA);
            current_Version = pi.versionName.toString();
            setSharedString("CURRENTSTOREVERSION", current_Version);
        } catch (Exception e) {
            Log.d(TAG, "[EJY] currentStoreVersion() - Exception ");
            e.printStackTrace();
        }
        Log.d(TAG, "[EJY] currentStoreVersion() - current_Version : "+current_Version);
    }

    public void checkssm() {
        Log.i(TAG, "===========checkSSM===========");

        ssmLib = new FolderUtil().initSSMLib(IntroActivity.this);

        String ssm_msg = "";//caution
        ssm_msg = FolderUtil.getMessage(IntroActivity.this, ssmLib.checkSSMValidation(), SSM_APP);
        Log.i(TAG, "checkSSM >>> check SSMValidation Code = " + ssm_msg + " check ssmCode" + ssmLib.checkSSMValidation());
        if (!("").equals(ssm_msg)) {    //SSM 비정상
            if (ssmLib.checkSSMValidation() == -1) {
                ssmLib.doingBind();
                LogMaker.logmaking("AFTER BINDING SSM", "");
                ssm_msg = FolderUtil.getMessage(IntroActivity.this, ssmLib.checkSSMValidation(), SSM_APP);
                checkV3();

            } else {
                clearFinishDialog();
                finishDialog = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.alert), ssm_msg, false, finishListener);
                finishDialog.show();

            }
        } else {

            checkV3();
        }
        Log.d(TAG, "[EJY] checkssm() - END ");
    }

    public void checkV3() {
        Log.i(TAG, "===========checkV3===========");
        //V3 앱 패키지명 : com.ahnlab.v3mobileenterprise
        if (ssmCode != OK) new FolderUtil().initSSMLib(IntroActivity.this);

        String v3_msg = "";
        v3_msg = FolderUtil.getMessage(IntroActivity.this, ssmLib.checkV3Validation(), V3_APP);    //V3 검사

        Log.i(TAG, "checkV3 >>> check V3Validation Code v3_msg = " + ssmLib.checkV3Validation());
        if (!("").equals(v3_msg)) {        //V3 기기 관리자 미등록 등 에러
            if (ssmLib.checkV3Validation() == -1) {

                Intent scanV3Intent = new Intent();
                scanV3Intent.setAction("com.sktelecom.ssm.action.ACTION_V3_SCAN_REQUEST");
                ssmLib.sendBroadcast(scanV3Intent);
                Log.i(TAG, "V3 sendBroadcast result=====>>>>" + ssmLib.sendBroadcast(scanV3Intent));
                Log.d("", "LoginActivity runAuthAction call location 3");
                Log.i(TAG, "V3 sendBroadcast result=====>>>>" + ssmLib.sendBroadcast(scanV3Intent));


                int ssmResult;


                image.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent loginIntent = new Intent(IntroActivity.this, LoginActivity.class);
                        animatic.stop();
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                        finish();
                    }
                }, 500);

                LogMaker.logmaking("AFTER BINDING V3", "");

            } else {
                clearFinishDialog();
                finishDialog = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.alert), v3_msg, false, V3ExecuteListener);
                finishDialog.show();
            }
        } else {    //V3 정상 설치
            Intent scanV3Intent = new Intent();
            scanV3Intent.setAction("com.sktelecom.ssm.action.ACTION_V3_SCAN_REQUEST");
            ssmLib.sendBroadcast(scanV3Intent);
            Log.i(TAG, "V3 sendBroadcast result=====>>>>" + ssmLib.sendBroadcast(scanV3Intent));
            Log.d("", "LoginActivity runAuthAction call location 3");
            Log.i(TAG, "V3 sendBroadcast result=====>>>>" + ssmLib.sendBroadcast(scanV3Intent));


            int ssmResult;


            image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent(IntroActivity.this, LoginActivity.class);
                    animatic.stop();
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }, 500);


        }
        Log.d(TAG, "[EJY] checkV3() - END ");
    }

    View.OnClickListener V3ExecuteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishDialog.dismiss();
            if (CommonUtil.isExistApp(IntroActivity.this, V3_PACKAGE)) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(V3_PACKAGE);
                startActivity(intent);
            }
        }
    };
    String curVersion = "";

    public void keepCreate() {
        Log.d(TAG, "[EJY] keepCreate() - start ");

        //Intro를 시작할때 스토어에서 시작한 부분이라면 메세지를 받아서 진행한다.

        ssmLib = SSMLib.getInstance(IntroActivity.this);
        if (ssmLib.checkSSMValidation() != OK) {
            ssmLib.initialize();
        }

        image1.setImageResource(R.drawable.anima);
        image1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        animatic = (AnimationDrawable) image1.getDrawable();

        image1.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
        image1.post(new Runnable() {
            @Override
            public void run() {
                animatic.start();
            }
        });
        userPref = getSharedPreferences("USERINFO", MODE_PRIVATE);
        vpnId = userPref.getString("USERID", "");
        vpnPw = userPref.getString("USERPWD", "");
        fcmDate = userPref.getString("FCMDATE", "");
        connectionTime = userPref.getLong("CONNECTIONTIME", 0);
        ssmCode = ssmLib.initialize();
        currentStoreVersion();

        //2021.07 메모보고 on TEST
        System.out.println("=========== userPref ==============  " + getSharedPreferences("USERINFO", MODE_PRIVATE) );

        Log.v("=============", "---------------------------------------OnCreate------------------------------------------------");


        bAdater = BluetoothAdapter.getDefaultAdapter();
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        bluetooth = BluetoothAdapter.STATE_OFF;
        if (bAdater != null) {
            if (bAdater.getState() == BluetoothAdapter.STATE_ON) {
                bluetooth = BluetoothAdapter.STATE_ON;
            }
        }

        Wi_Fi = ConnectivityManager.TYPE_MOBILE;
        if (wifi != null) {
            if (CommonUtil.getMobileData(IntroActivity.this) == ConnectivityManager.TYPE_WIFI) {
                Wi_Fi = ConnectivityManager.TYPE_WIFI;
                //와이파이 차단
                /*  image1.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi.setWifiEnabled(false);
                    }
                });
*/

            }
//            wifi.getWifiState();
        }


        SharedPreferences.Editor edit = userPref.edit();
        edit.putInt("BLUETOOTH", bluetooth);
        edit.putInt("WIFI", Wi_Fi);
        edit.apply();

        LogMaker.logStart();
        LogMaker.logmaking("vpnId", vpnId);
        LogMaker.logmaking("vpnPw", vpnPw);
        LogMaker.logmaking("fcmDate", fcmDate);
        LogMaker.logmaking("connectionTime", String.valueOf(connectionTime));
        LogMaker.logmaking("bluetooth", bluetooth);
        LogMaker.logmaking("Wi_Fi", Wi_Fi);
        LogMaker.logEnd();


        //dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = dateFormat.format(new Date());


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseApp.initializeApp(IntroActivity.this);
        token = FirebaseInstanceId.getInstance().getToken();
        LogMaker.logStart();
        Log.v("", "TOKEN :" + token);
        LogMaker.logEnd();


        /*boolean resultPermission = false;
        resultPermission = permissionCheck();

        if (resultPermission == true) {
            checkPhoneState();
        }*/

        Log.d(TAG, "[EJY] keepCreate() - END ");
    }

    public void getLauncherInfo() {
        Log.d(TAG, "[EJY] getLauncherInfo() - START ");

        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        final Call<RequestLauncherInfo> requestLauncherInfo = apiInterface.request_launcher_info();
        requestLauncherInfo.enqueue(new Callback<RequestLauncherInfo>() {
            @Override
            public void onResponse(Call<RequestLauncherInfo> call, Response<RequestLauncherInfo> response) {

                curVersion = response.body().getAppVer();
                Log.e(TAG, "launcher_version_current : " + getVersion());
                Log.e(TAG, "launcher_version_new     : " + curVersion);
                if (curVersion.equals(getVersion())) {
                    //패키지 매니저에서 이름이 일치하는 경우에는 그냥 넘어간다.
                    Log.e(TAG, "SUCC_version_current : " + getVersion());
                    Log.e(TAG, "SUCC_version_new     : " + curVersion);
                    keepgoing = true;
                    keepCreate();

                } else {   ///하지만 정보가 일치 하지 않으면 바로 다이얼로그 띄워서 설치 유도를 한다.
                    Log.e(TAG, "FAIL_version_current : " + getVersion());
                    Log.e(TAG, "FAIL_version_new     : " + curVersion);
                    commonDialog_oneButton = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.request_launcher_update_title),
                            getString(R.string.request_launcher_update_context), false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(EX_STORE_PACKAGE);
                            startActivity(intent);
                            commonDialog_oneButton.dismiss();
                            finish();
                        }
                    });
                    commonDialog_oneButton.show();


                }

            }

            @Override
            public void onFailure(Call<RequestLauncherInfo> call, Throwable t) {
                clearFinishDialog();
                finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                        getString(R.string.hi_failed), getString(R.string.check_network), false, finishListener2);
                finishDialog.show();

            }
        });
        Log.d(TAG, "[EJY] getLauncherInfo() - END ");

    }

    public void CheckEssentialApp() {
        Log.d(TAG, "[EJY] CheckEssentialApp() - START ");
        int length = EssentialAppList.length;
        Log.d(TAG, "[EJY] CheckEssentialApp() - length : "+length);
        boolean[] notInstalled = new boolean[length];
        for (int i = 0; i < length; i++) {
            if (!isExistApp(IntroActivity.this, EssentialAppList[i])) {
                LogMaker.logmaking(APPNAME[i], "UNINSTALLED");
                notInstalled[i] = false;
            } else {
                LogMaker.logmaking(APPNAME[i], "INSTALLED");
                notInstalled[i] = true;
            }
        }

        //■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
        //2020-10-26 v3 업데이트 처리 추가 notInstalled[3] 값 변경을 통하여 하단 gotoStore() 호출여부를 결정한다.
        String V3_Version = CommonUtil.getVersionName(IntroActivity.this, V3_PACKAGE);
        Log.d(TAG, "[EJY] appInfoList Size : "+appInfoList.size());

        for (int i=0; i< appInfoList.size(); i++){
            System.out.println("################# notinstalled3 = "+appInfoList.get(i).getAppNm()+" : "+appInfoList.get(i).getAppVer());

            if(V3_PACKAGE.equals(appInfoList.get(i).getPackageNm())){
                System.out.println("################# notinstalled3 = "+appInfoList.get(i).getAppVer()+" : "+V3_Version);
                if(!appInfoList.get(i).getAppVer().equals(V3_Version)){
                    notInstalled[3] = false;//true 면 스토어로 이동 유도
                }
            }
        }
        //■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
        System.out.println("################# notinstalled4 = "+notInstalled[3]);
        if (notInstalled[0] == false) {
            //ex 온라인 스토어로 이동한다.
            LogMaker.logmaking("GO_TO_ONLINE_STORE", "STORE");
            gotoWebStore();

        } else if (notInstalled[1] == true && notInstalled[2] == false) {

            //ssmInstaller로 넘어간다.
            LogMaker.logmaking("GO_TO_SSM_INSTALLER", "SSM");
            SSM_INSTALL();
        } else if (notInstalled[2] == false && notInstalled[1] == true) {
            //스토어 앱으로 이동한다.
            LogMaker.logmaking("GO_TO_STORE", "SSM_INSTALLER");
            gotoStore();
        } else if (notInstalled[1] == false && notInstalled[2] == false) {
            //스토어 앱으로 이동한다.
            LogMaker.logmaking("GO_TO_STORE", "SSM_INSTALLER");
            gotoStore();
        } else if (notInstalled[3] == false) {
            //스토어앱으로 이동한다.
            LogMaker.logmaking("GO_TO_STORE", "V3");
            gotoStore();

        } else if (notInstalled[4] == false) {
            //스토어앱으로 이동한다.
            LogMaker.logmaking("GO_TO_STORE", "VPN");
            gotoStore();
        } else {
            rootingCheck();
        }
        Log.d(TAG, "[EJY] CheckEssentialApp() - END ");
    }


    public void gotoStore() {
        Log.d(TAG, "[EJY] gotoStore() - START ");
        CDO = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.hi_failed), getString(R.string.intro_notInstalled_SSM_INSTALLER),
                false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CDO.dismiss();
                Intent intent = getPackageManager().getLaunchIntentForPackage(EX_STORE_PACKAGE);
                startActivity(intent);


            }
        });
        CDO.show();
        Log.d(TAG, "[EJY] gotoStore() - END ");
    }

    public void gotoWebStore() {
        Log.d(TAG, "[EJY] gotoWebStore() - START ");
        CDO = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.hi_failed), getString(R.string.intro_notInstalled_STORE),
                false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.ex.co.kr"));
                startActivity(intent);
                finish();
                CDO.dismiss();

            }
        });
        CDO.show();
        Log.d(TAG, "[EJY] gotoWebStore() - END ");
    }

    public void SSM_INSTALL() {
        Log.d(TAG, "[EJY] SSM_INSTALL() - START ");
        CDO = new CommonDialog_oneButton(IntroActivity.this, getString(R.string.hi_failed), getString(R.string.intro_notInstalled_SSM),
                false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(SSM_INSTALLER_PACKAGE);
                startActivityForResult(intent, SSM_EXECUTE_CODE);

                CDO.dismiss();
            }
        });
        CDO.show();
        Log.d(TAG, "[EJY] SSM_INSTALL() - END ");
    }

    public void checkPhoneState() {
        Log.d(TAG, "[EJY] checkPhoneState() - START ");
        String mac = CommonUtil.getMacAddr(IntroActivity.this);
        String mdn = CommonUtil.getMdn(IntroActivity.this);
        String imei = CommonUtil.getImei(IntroActivity.this);
        LogMaker.logStart();
        LogMaker.logmaking("mac", mac);
        LogMaker.logmaking("mdn", mdn);
        LogMaker.logmaking("imei", imei);
        LogMaker.logEnd();
        Log.d(TAG, "[EJY] checkPhoneState() - token : "+token);
        Log.d(TAG, "[EJY] checkPhoneState() - fcmDate : "+fcmDate);
        Log.d(TAG, "[EJY] checkPhoneState() - today : "+today);

//        if (token != null && !("").equals(token) && !fcmDate.equals(today)) {
        if (token != null && !("").equals(token)) {
            FirebaseIDService.sendToken(IntroActivity.this, token);
        }


        //IMEI 등록 to SSM 서버
        /**
         * @param result
         *   성공 : 002
         * , 등록되지 않은 전화번호 :  003
         * , 이미 등록된 휴대폰 정보 :  004
         */
        // 일주일에 한 번
        String authDate = getSharedString("AUTHDATE");
        Calendar aWeekAgoCal = GregorianCalendar.getInstance();
        Calendar authCal = GregorianCalendar.getInstance();

        aWeekAgoCal.add(aWeekAgoCal.DATE, IMEI_CYCLE);

        LogMaker.logStart();
        LogMaker.logmaking("인증날짜", authCal.getTime().toString());
        LogMaker.logmaking("단말 인증 필요", authCal.before(aWeekAgoCal));
        LogMaker.logEnd();
        try {
            if (!("").equals(authDate)) {
                authCal.setTime(dateFormat.parse(authDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("", "IntroActivity getPhoneState Step Test 1-1 = " + authDate + ":" + authCal.before(aWeekAgoCal));


        if (("").equals(authDate) || authCal.before(aWeekAgoCal))//인증만료일이 지났을 경우
        {
            String errorMessage = "";
            //errorMessage = CommonUtil.nullCheckParams(mdn, mac, imei);
            errorMessage = CommonUtil.nullCheckParams(mdn, mac, "1234");//안드로이드 10.0 대응 imei 미사용


            if (("").equals(errorMessage)) {    //mdn, mac, imei 검증
                int networkStatus = CommonUtil.getMobileData(IntroActivity.this);
                Log.i(TAG, "networkStatus!!!!!" + networkStatus);


                if (networkStatus == -1) {
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.hi_failed), getString(R.string.check_network), false, finishListener2);
                    finishDialog.show();

                } else {
                    new ImeiAsyncTask().execute(mdn, mac, imei);
                }
            } else {
                clearFinishDialog();
                finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                        getString(R.string.alert), errorMessage, false, finishListener2);
                finishDialog.show();
            }
        } else {
            checkAppList();
//            CheckEssentialApp();

        }
        Log.d(TAG, "[EJY] checkPhoneState() - END ");
    }

    public void rootingCheck() {
        //모든 필수앱 설치 완료
        Log.d(TAG, "[EJY] rootingCheck() - START ");
        LogMaker.logmaking("==========checkPhoneState", "==================");
        Log.d("", "IntroActivity getPhoneState Step Test 3");


        String ssm_msg = "";

        if (!isRooted()) {    //루팅 되지 않은 정상 단말

            LogMaker.logmaking("==========checkPhoneState", "==================");
            Log.d("", "IntroActivity getPhoneState Step Test 4");
            Log.i(TAG, "checkSSM >>> ssmCode = " + ssmCode);
            LogMaker.logEnd();
            LogMaker.logmaking("SSM", "ssmcheck");
            checkssm();

        } else {    //루팅 된 단말
            ssm_msg = getString(R.string.rooted);
            clearFinishDialog();
            finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                    getString(R.string.alert), ssm_msg, false, finishListener);
            finishDialog.show();
        }
        Log.d(TAG, "[EJY] rootingCheck() - END ");
    }

    public class preImeiAsyncTask extends AsyncTask<String, Void, String> {
        int CONN_TIMEOUT = 15;
        int CONN_TIMEOUT2 = 10;
        int READ_TIMEOUT = 15;
        String POST = "POST";

        ProgressDialog preImeiDialog;

        HttpURLConnection conn = null;
        URL url = null;
        int retry = 0;

        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "[EJY] preImeiAsyncTask >> onPreExecute()");
            super.onPreExecute();

            preImeiDialog = new ProgressDialog(IntroActivity.this);
            ((AlertDialog) preImeiDialog).setMessage(getResources().getString(R.string.dialog_wait));
            preImeiDialog.setCancelable(false);
            preImeiDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "[EJY] preImeiAsyncTask >> doInBackground()");
            String result = "";
            int retry = 0;
            String resultCode = "";
            try {
                if (params == null) {
                    throw new Exception("params is null");
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }

            try {
                //trustAllHosts();
                url = new URL(mdmUrl);

                conn = (HttpsURLConnection) url.openConnection();
                if (retry == 1) {
                    conn.setConnectTimeout(CONN_TIMEOUT * 1000);
                } else {
                    conn.setConnectTimeout(CONN_TIMEOUT2 * 1000);
                }


                conn.setReadTimeout(READ_TIMEOUT * 1000);
                conn.setRequestMethod(POST);
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject obj = new JSONObject();
                obj.put("hPhone", params[0]);
                obj.put("mac", params[1]);
                obj.put("imei", params[2]);
                obj.put("iosDeviceSerialNum", "");
                obj.put("companyAsset", "0");
                obj.put("osKind", "1");
                String ob = obj.toString();
                Log.v("Check OBJ", "" + ob);
                os = conn.getOutputStream();
                os.write(obj.toString().getBytes());
                Log.v("URL HTTP", "-->" + os);
                os.flush();

                String response;


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;

                    int nLength = 0;


                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();

                    response = new String(byteData);
                    LogMaker.logStart();
                    LogMaker.logmaking("response", response);
                    LogMaker.logEnd();
                    JSONObject responseJSON = new JSONObject(response);

                    String responseResult = responseJSON.getString("result");
                    resultCode = responseJSON.getString("resultCode");

                    Log.i(TAG, "DATA response = " + response);
                    Log.i(TAG, "responseResult = " + responseResult);
                    Log.i(TAG, "resultCode = " + resultCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "stack trace===>>> " + e.getMessage());
            }

            Log.d(TAG, "[EJY] preImeiAsyncTask >> doInBackground() - resultCode : "+resultCode);
            return resultCode;
        }

        /**
         * 성공 : 002
         * 등록되지 않은 전화번호 :  003
         * 이미 등록된 휴대폰 정보 :  004
         */

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "[EJY] preImeiAsyncTask >> onPostExecute() - result : "+result);
            Log.i(TAG, "=========onPostExecute=========");
            if (preImeiDialog != null && preImeiDialog.isShowing()) {
                preImeiDialog.dismiss();
            }
            Bundle bundle = new Bundle();

            if (("002").equals(result) || ("004").equals(result)) {

                bundle.putInt("msg", 0);
                bundle.putString("result", result);

//
            } else if (("003").equals(result)) {

                bundle.putInt("msg", -1);
                bundle.putString("result", result);

            } else {

                bundle.putInt("msg", 99);
                bundle.putString("result", result);
            }

            switch (bundle.getInt("msg")) {
                case -1:
                    Log.i(TAG, "DEVICE IS NOT REGISTERED... ");
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.hi_failed), getString(R.string.error_003), false, finishListener2);
                    finishDialog.show();
                    break;
                case 99:
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.hi_failed), getString(R.string.error_005), false, finishListener2);
                    finishDialog.show();
                    break;

                default:
                    break;
            }
        }
    }


    public class ImeiAsyncTask extends AsyncTask<String, Void, String> {
        int CONN_TIMEOUT = 15;
        int CONN_TIMEOUT2 = 10;
        int READ_TIMEOUT = 15;
        String POST = "POST";

        ProgressDialog imeiDialog;

        HttpsURLConnection conn = null;
        URL url = null;
        int retry = 0;

        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "[EJY] ImeiAsyncTask >> onPreExecute() ");
            // TODO Auto-generated method stub
            super.onPreExecute();

            imeiDialog = new ProgressDialog(IntroActivity.this);
            ((AlertDialog) imeiDialog).setMessage(getResources().getString(R.string.dialog_wait));
            imeiDialog.setCancelable(false);
            imeiDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "[EJY] ImeiAsyncTask >> doInBackground() ");
            // TODO Auto-generated method stub
            String result = "";
            int retry = 0;
            String resultCode = "";

            try {
                if (params == null) {
                    throw new Exception("params is null");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = e.getMessage();
            }

            try {
                //trustAllHosts();
                url = new URL(mdmUrl);

                conn = (HttpsURLConnection) url.openConnection();
                if (retry == 1) {
                    conn.setConnectTimeout(CONN_TIMEOUT * 1000);
                } else {
                    conn.setConnectTimeout(CONN_TIMEOUT2 * 1000);
                }


                conn.setReadTimeout(READ_TIMEOUT * 1000);
                conn.setRequestMethod(POST);
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject obj = new JSONObject();
                obj.put("hPhone", params[0]);
                obj.put("mac", params[1]);
                obj.put("imei", params[2]);
                obj.put("iosDeviceSerialNum", "");
                obj.put("companyAsset", "0");
                obj.put("osKind", "1");
                String ob = obj.toString();
                Log.v("Check OBJ", "" + ob);
                os = conn.getOutputStream();
                os.write(obj.toString().getBytes());
                Log.v("URL HTTP", "-->" + os);
                os.flush();

                String response;


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;

                    int nLength = 0;

                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();

                    response = new String(byteData);
                    LogMaker.logStart();
                    LogMaker.logmaking("response", response);
                    LogMaker.logEnd();
                    JSONObject responseJSON = new JSONObject(response);

                    String responseResult = responseJSON.getString("result");
                    resultCode = responseJSON.getString("resultCode");

                    Log.i(TAG, "DATA response = " + response);
                    Log.i(TAG, "responseResult = " + responseResult);
                    Log.i(TAG, "resultCode = " + resultCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "stack trace===>>> " + e.getMessage());
            }

            Log.d(TAG, "[EJY] ImeiAsyncTask >> doInBackground() - resultCode : "+resultCode);
            return resultCode;
        }


        /**
         * @param result 성공 : 002
         *               , 등록되지 않은 전화번호 :  003
         *               , 이미 등록된 휴대폰 정보 :  004
         */
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "[EJY] ImeiAsyncTask >> onPostExecute() - result : "+result);
            super.onPostExecute(result);

            Log.i(TAG, "=========onPostExecute=========");
            if (imeiDialog != null && imeiDialog.isShowing()) {
                imeiDialog.dismiss();
            }

            if (("002").equals(result) || ("004").equals(result)) {
                Message msg = imeiHandler.obtainMessage();
                msg.what = 0;
                msg.obj = result;
                imeiHandler.sendMessage(msg);
                //prefAuthInfo = getSharedPreferences("authInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();
                editor.putString("AUTHDATE", today);
                Log.i(TAG, " insert IMEI today============>>> " + today);
                editor.apply();
//				return;
            } else if (("003").equals(result)) {
                Message msg = imeiHandler.obtainMessage();
                msg.what = -1;
                msg.obj = result;
                imeiHandler.sendMessage(msg);
            } else {
                Message msg = imeiHandler.obtainMessage();
                msg.what = 99;
                msg.obj = result;
                imeiHandler.sendMessage(msg);
            }

        }

    }

    Handler imeiHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "[EJY] imeiHandler() - msg : "+msg);
//			imeiHandler.handleMessage(msg);
            switch (msg.what) {
                case 0:    //등록 성공
                    Log.i(TAG, "SUCCESS!!");
                    CheckEssentialApp();


                    break;
                case -1: //등록 실패(업무용 단말 등)
                    Log.i(TAG, "DEVICE IS NOT REGISTERED... ");
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.hi_failed), getString(R.string.error_003), false, preImeiDialogListener);
                    finishDialog.show();
                    break;

                case 99:
                    Log.i(TAG, "DEVICE REGISTERATION FAILED... ");
                    clearFinishDialog();
                    finishDialog = new CommonDialog_oneButton(IntroActivity.this,
                            getString(R.string.hi_failed), getString(R.string.error_005), false, preImeiDialogListener);
                    finishDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener preImeiDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishDialog.dismiss();
            finish();
        }
    };

    View.OnClickListener finishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(SSM_PACKAGE);
            startActivity(intent);
            finishDialog.dismiss();
            //finish();
        }
    };
    View.OnClickListener finishListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishDialog.dismiss();
            finish();
        }
    };

    public boolean permissionCheck() {
        Log.i(TAG, "=========permissionCheck========= FIRSTRUN : " + getSharedString("FIRSTRUN"));
        Log.i(TAG, "=========permissionCheck========= READ_PHONE_STATE : " + checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE));
        Log.i(TAG, "=========permissionCheck========= WRITE_EXTERNAL_STORAGE : " + checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
        Log.i(TAG, "=========permissionCheck========= READ_SMS : " + checkSelfPermission(android.Manifest.permission.READ_SMS));
        Log.i(TAG, "=========permissionCheck========= WRITE_CONTACTS : " + checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS));
        Log.i(TAG, "=========permissionCheck========= USE_FINGERPRINT : " + checkSelfPermission(android.Manifest.permission.USE_FINGERPRINT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(CommonUtil.permissions, CommonUtil.PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return false;
            } else {
                /*if ("".equals(getSharedString("FIRSTRUN"))) {
                    requestPermissions(CommonUtil.permissions, CommonUtil.PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    setSharedString("FIRSTRUN", "Y");
                }*/
                authChk();
                checkPhoneState();
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2:
                Log.v(TAG, "=\n\n=======================onRequestPermissionResult=======================");
                if (new CommonUtil().hasAllPermissionGranted(grantResults)) {
                    Log.v(TAG, "=\n\n=======================onRequestPermissionResult 권한 허용 완료=======================");
                    authChk();
                    checkPhoneState();
                } else {
                    Toast.makeText(IntroActivity.this, R.string.permission_grant, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:+" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(vpnConn != null)vpnConn.disconnection();
    }



    /*█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█*/
    /*█ Intent Meesage를 받고 난 후 로그인 상태에 따라서 실행 방법을 결정해 준다.                                                                                                                              █*/
    /*█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/

    public void onStoreCallback() {
        Log.d(TAG, "[EJY] onStoreCallback() - START ");

        getIntent();
        Log.e(STORETAG, "LOGINSTATE : " + getSharedString("LOGINSTATE"));
        if (getSharedString("LOGINSTATE").equals("LOGIN")) {  //LOGIN STATE를 판단해준다.
            keepgoing = false;
            Intent intentFromSTORE = getIntent();
            Log.e(STORETAG, "====" + intentFromSTORE.getStringExtra("packageName"));
            if (intentFromSTORE.getStringExtra("packageName") != null) {  //혹시 intent messeage가 null인지 확인한다.

                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.putExtra("packageName", intentFromSTORE.getExtras().getString("packageName"));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (intentFromSTORE.getStringExtra("fromNoti") != null) {
                Log.e(TAG("fromw noit : "), intentFromSTORE.getStringExtra("fromNoti"));
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    NotificationManager noman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    try {
                        noman.cancel(NOTI_ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getLauncherInfo();
                }
            }
        } else {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                NotificationManager noman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                ;
                try {
                    noman.cancel(NOTI_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getLauncherInfo();
            }
        }
        Log.d(TAG, "[EJY] onStoreCallback() - END ");
    }

    List<RetrieveAppInfo.Info> appInfoList = new ArrayList<RetrieveAppInfo.Info>();  //2021-03 [EJY] null 에러 적용
    ArrayList<AppInfo> appList = new ArrayList<AppInfo>();

    public void checkAppList(){
        APIInterface apiInterface;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Log.d(TAG, "[EJY] checkAppList() - appInfoList 1 : "+appInfoList);
        Log.d(TAG, "[EJY] checkAppList() - appList 1 : "+appList);
        appList.clear();

        Log.d(TAG, "[EJY] checkAppList() - appInfoList 2 : "+appInfoList);
        Log.d(TAG, "[EJY] checkAppList() - appList 2 : "+appList);
        if(appInfoList != null){
            appInfoList.clear();
        }

        //Log.d(TAG, "checkAppList() shared  - [ userId : "+CommonUtil.getPrefString(MainActivity.this, "userId")+" ], [ userNm : "+CommonUtil.getPrefString(MainActivity.this, "userNm")+" ], [ userType : "+CommonUtil.getPrefString(MainActivity.this, "userType")+" ], [ platformCd : "+CommonUtil.getPrefString(MainActivity.this, "platformCd")+" ], [ autoDownYn : "+CommonUtil.getPrefBoolean(MainActivity.this, "autoDownYn")+" ], [ deviceType : "+CommonUtil.getPrefString(MainActivity.this, "deviceType")+" ]");

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<RetrieveAppInfo> call = apiInterface.doGetRetrieveAppInfo(
                "11111111"
                , "EX"
                , "A"
                , "M"
        );
        //Call<RetrieveAppInfo> call = apiInterface.doGetRetrieveAppInfo("21603226", "EX", "A", "M");
        //Call<RetrieveAppInfo> call = apiInterface.doGetRetrieveAppInfo("112233aa", "ETC", "A", "M");
        call.enqueue(new Callback<RetrieveAppInfo>() {
            @Override
            public void onResponse(Call<RetrieveAppInfo> call, Response<RetrieveAppInfo> response) {
                Log.d(TAG, "checkAppList()~!");

                RetrieveAppInfo resource = response.body();
                String result = resource.result;
                String resultMsg = resource.resultMsg;
                String storeVer = resource.storeVer;
                appInfoList = resource.appInfoList;

                Log.d(TAG, "checkAppList() - Response [ result : "+result+" ], [ resultMsg : "+resultMsg+" ]");
                Log.d(TAG, "checkAppList() - Response [ storeVer : "+storeVer+" ]");

                if(null == result){

                }

                if(result.equals("1000") && resultMsg.equals("OK")){
                    Log.d(TAG, "checkAppList() - appInfoList Size : "+ appInfoList.size());
                    for(int i=0; i<appInfoList.size() ;i++) {
                        Log.d(TAG, "checkAppList() - Response [ appinfo : "+appInfoList.get(i).getPackageNm()+" ]["+appInfoList.get(i).getAppVer()+"]");
                    }
                }else{
                    Log.d(TAG, "checkAppList() - 통신실패");
                }
                Log.d(TAG, "checkAppList() - appList Size : "+appList.size());

                //setAppList(appList);
                CheckEssentialApp();
            }

            @Override
            public void onFailure(Call<RetrieveAppInfo> call, Throwable t) {
                Log.d(TAG, "checkAppList() - onFailure");
                call.cancel();
                CheckEssentialApp();
            }
        });

    }

    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*█                                                                                                                                                                                                  █*/
    /*▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀*/


}