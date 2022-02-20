package com.ex.group.elecappmemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.ex.group.approval.easy.activity.ApprovalMainActivity;
import com.ex.group.board.activity.BoardListActivity;
import com.ex.group.board.data.UserInfo;
import com.ex.group.board.util.Global;
import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.dialog.CustomprogressDialog;
import com.ex.group.mail.activity.EmailMainActivity;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.util.StringUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;

//2021-03-30 [EJY] 상단 메뉴바 제거 전 ElecMemoAppWebViewActivity
public class ElecMemoAppWebViewActivity_bak extends BaseWebViewActivity implements View.OnClickListener {
    String TAG = "ElecMemoAppWebViewActivity";
    final String ELEC_URL = "http://128.200.121.68:9000/elecapp";
    String hybridUrl = "http://128.200.121.68:9000/elecapp/";
    String parentDeptCode;

    WebView webView;

    private boolean flag = false;
    Handler backHandler;

    CustomprogressDialog progress;

    LinearLayout ll_tabbar;

    LinearLayout btn_board;
    LinearLayout btn_mail;
    LinearLayout btn_elecapp;
    LinearLayout btn_memoapp;
    LinearLayout btn_vacation;
    LinearLayout btn_more;
//    TextView btn_moremenu;

    ValueCallback mFilePathCallback;//파일첨부시 사용

    Context context = ElecMemoAppWebViewActivity_bak.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webelec_view_bak);
        activity = ElecMemoAppWebViewActivity_bak.this;

        Log.i(TAG, "WebViewActivity onCreate");
        final Intent intent = getIntent();
        Log.i(TAG, "[EJY] onCreate() - intent : "+intent);
        hybridUrl = intent.getStringExtra("hybridUrl");
        Log.i(TAG, "[EJY] onCreate() - hybridUrl : "+hybridUrl);

        UserInfo.mdn = StringUtil.isNull(SKTUtil.getMdn(this)) ? "mdn" : SKTUtil.getMdn(ElecMemoAppWebViewActivity_bak.this);
        String urlParam = "userId="+getSharedString("USERID")+"&userNm="+getSharedString("USERNAME")+"&dptcd="+getSharedString("USERDEPARTURECODE")+"&phoneNo="+UserInfo.mdn;
        if(hybridUrl.endsWith("?")) {
            hybridUrl = hybridUrl+urlParam;
        }

        Log.i(TAG, "[EJY] onCreate() - hybridUrl : "+hybridUrl);

        progress = new CustomprogressDialog(ElecMemoAppWebViewActivity_bak.this,null);
        progress.show();

		/*		Uri uri = Uri.parse(hybridUrl);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(it);*/

        backHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==0){
                    flag = false;
                }
            }
        };


        ll_tabbar = findViewById(R.id.ll_tabbar);

        btn_board = findViewById(R.id.btn_board);
        btn_mail = findViewById(R.id.btn_mail);
        btn_elecapp = findViewById(R.id.btn_elecapp);
        btn_memoapp = findViewById(R.id.btn_memoapp);
        btn_vacation = findViewById(R.id.btn_vacation);
        btn_more = findViewById(R.id.btn_more);
//        TextView btn_moremenu = (TextView) findViewById(R.id.btn_moremenu);

        btn_board.setOnClickListener(this);
        btn_mail.setOnClickListener(this);
        btn_elecapp.setOnClickListener(this);
        btn_memoapp.setOnClickListener(this);
        btn_vacation.setOnClickListener(this);
//        btn_more.setOnClickListener(this);
//        btn_moremenu.setOnClickListener(this);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(ElecMemoAppWebViewActivity_bak.this, MenuMoreActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.animation_slide_in_right,R.anim.animation_slide_out_right);

//                showMenu("");
            }
        });

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(hybridUrl);
        if(hybridUrl.contains("mobileAlarmList")){
            ll_tabbar.setVisibility(View.GONE);
        }else if(hybridUrl.contains("elecapp")){
            btn_elecapp.setSelected(true);
            btn_elecapp.setBackgroundColor(getResources().getColor(R.color.red));
        }else if(hybridUrl.contains("memo")){
            btn_memoapp.setSelected(true);
            btn_memoapp.setBackgroundColor(getResources().getColor(R.color.red));
        }

        WebSettings webSettings = webView.getSettings();
        webView.clearHistory();
        webView.clearCache(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        webView.addJavascriptInterface(new ElecMemoJsInterface(ElecMemoAppWebViewActivity_bak.this, webView), "ElecMemoAppWebViewActivity");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result){
                new AlertDialog.Builder(ElecMemoAppWebViewActivity_bak.this).setTitle("알림").setMessage(message).setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                result.confirm();

                            }
                        }).setCancelable(false).create().show();
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(ElecMemoAppWebViewActivity_bak.this).setTitle("알림").setMessage(message).setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                result.confirm();
                            }
                        }).setCancelable(false).create().show();
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                startActivityForResult(intent, 0);
                return true;
            }
        });



        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // url 주소에 해당하는 웹페이지를 로딩
//
                if (url.startsWith("tel:") || url.startsWith("sms:")|| url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
//				        view.reload();
                    return true;
                }
                view.loadUrl(url);
                return true;// true를 리턴하면 WebView는 해당 URL을 렌더하지 않는다.
//			        return false;
//			        return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                // 페이지 로딩시 호출된다.
                Log.i(TAG, "onPageFinished");
                super.onPageFinished(view, url);
                if(progress.isShowing()){
                    try{
                   progress.dismiss();
                    }catch (Exception e){}
                    }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 페이지 요청이 시작될 경우 호출된다.
                super.onPageStarted(view, url, favicon);
                try {
                    progress.show();
                }catch (Exception e){}
            }



            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                // 키를 오버로딩한것인데 주로 웹페이지를 뒤,앞 등으로 이동하게 한다.
                // 왼쪽키를 누르게 되면 뒤로, 오른쪽 키는 앞으로 가게 한다.
                int keyCode = event.getKeyCode();
//                Log.i(TAG, "shouldOverrideKeyEvent"+" pressed keycode BACK!!");
//                Log.i(TAG, "webView.canGoBack ?? "+ webView.canGoBack());
//                goMainActivity();
//                if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && webView.canGoBack()) {
//                    webView.goBack();
////                    return true;
//                }else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && webView.canGoForward()) {
////                    webView.goForward();
//                    goMainActivity();
////                    return true;
//                }
                return true;
//			        return super.shouldOverrideKeyEvent(view, event);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                switch (errorCode) {
                    case ERROR_AUTHENTICATION:
                    case ERROR_BAD_URL:
                    case ERROR_CONNECT:
//                case ERROR_FAILED_SSL_HANDSHAKE:
                    case ERROR_FILE:
                    case ERROR_FILE_NOT_FOUND:
                    case ERROR_HOST_LOOKUP:
                    case ERROR_IO:
                    case ERROR_PROXY_AUTHENTICATION:
                    case ERROR_REDIRECT_LOOP:
                    case ERROR_TIMEOUT:
                    case ERROR_TOO_MANY_REQUESTS:
                    case ERROR_UNKNOWN:
                    case ERROR_UNSUPPORTED_AUTH_SCHEME:
                        //case ERROR_UNSUPPORTED_SCHEME:
                        webView.loadUrl("about:blank");

                        ElecMemoDialog dialog = new ElecMemoDialog(ElecMemoAppWebViewActivity_bak.this, "알림", "통신이 원활하지 않습니다. 다시 시도해주세요.", false, null);
                        dialog.show();

                        /*final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                        final LayoutInflater layoutInflater = getLayoutInflater();

                        View customMessage = layoutInflater.inflate(R.layout.layout_dialog, null);
                        final Button btn_confirm = (Button) customMessage.findViewById(R.id.btn_confirm);
                        final TextView dialog_content = (TextView)customMessage.findViewById(R.id.dialog_content);
                        dialog_content.setText("통신이 원활하지 않습니다. 다시 시도해주세요.");
                        btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //앱 종료
                                ElecMemoAppWebViewActivity.this.finishAffinity();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.setContentView(customMessage);
                        dialog.show();

                        break;*/
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() - resultCode : "+String.valueOf(resultCode));
        Log.d(TAG, "onActivityResult() - requestCode : "+String.valueOf(requestCode));
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }else{
                mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
            }
            mFilePathCallback = null;
        }else{
            mFilePathCallback.onReceiveValue(null);
        }
    }

    public class ElecMemoJsInterface {
//        final String TAG = "ElecMemoAppWebJsInterface";
        Context mContext;
        WebView webView;

        public ElecMemoJsInterface(Context context, WebView webView) {
            this.mContext = context;
            this.webView = webView;
        }

        @JavascriptInterface
        public void goMain() {
            Log.d(TAG, "goMain()");
            goMainActivity();
        }


        @JavascriptInterface
            public void goAttach(String url, String fileName) {
//            goAttachurl = M_2_1.hwp
//            goAttachname = /Mobile_Inter/himoffice/upload/memo/2021/02/24/M_2_1.hwp

            Log.d(TAG,"goAttach() - url : " + url);
            Log.d(TAG,"goAttach() - fileName : " + fileName);

            /* 전자결재 테스트 */
//            개발서버 => http://172.16.90.12:8090/brmroot/cache/ofc/2021/02/23/BCC679636AC105A0C68346762000F204E_1614042076726_.hwp
//            운영서버 => http://172.16.164.14:8090/brmroot/cache/ofc/2021/03/24/B5EAF052AAC10A5120F816B6E0056EC47_1616496231722_.hwp
//            운영서버되는거servelt => url = "http://nedms.ex.co.kr:8090/servlet/com.nanum.xf.servlet.file.StreamDownloadServlet?filePath=/MAIL08/brmStorage/server/brmroot/cache/ofc/2021/03/25/B678FF6B1AC10A5120F816B6E00779221_1616645197189_.hwp";
//            운영서버되는거servelt => fileName = "B678FF6B1AC10A5120F816B6E00779221_1616645197189_.hwp";
            /* 메모보고 테스트 */
//            \\\\192.168.53.10\\Mobile_EXDOC\\Doc\\docuzen_converted\\memo\\2021\\02\\24\\M_3_1.hwp

//            url = "";
//            fileName = "";

            final String docUrl = url;
            final String docFileName = fileName;

            Log.d(TAG,"goAttach() - docUrl : " + docUrl);
            Log.d(TAG,"goAttach() - docFileName : " + docFileName);

            DocuzenViewLauncher(ElecMemoAppWebViewActivity_bak.this, docFileName, docUrl);
        }


        @JavascriptInterface
        public void goToApp(String app){
            Log.d(TAG, "goToApp() - app : "+app);
            if("E".equals(app)){
                Intent intent = new Intent();
                intent.putExtra("app", "E");
                setResult(RESULT_OK, intent);
                finish();
//
            }else if("M".equals(app)){

                Intent intent = new Intent();
                intent.putExtra("app", "M");
                setResult(RESULT_OK, intent);
                finish();

//                Intent intent = new Intent(ElecMemoAppWebViewActivity.this, ElecMemoAppWebViewActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("hybridUrl", "http://128.200.121.68:9000/memo/init.do?userId="+getSharedString("USERID")//운영
//                        +"&userNm="+getSharedString("USERNAME")
//                        +"&dptcd="+getSharedString("USERDEPARTURECODE"));
//                ElecMemoAppWebViewActivity.this.startActivity(intent);

//                ll_tabbar.setVisibility(View.GONE);
//                webView.loadUrl("http://128.200.121.68:9000/memo/init.do?userId="+getSharedString("USERID")//운영
//                        +"&userNm="+getSharedString("USERNAME")
//                        +"&dptcd="+getSharedString("USERDEPARTURECODE"));

//                "http://128.200.121.68:9000/elecapp/init.do?userId="+getSharedString("USERID")
//                        +"&userNm="+getSharedString("USERNAME")
//                        +"&dptcd="+getSharedString("USERDEPARTURECODE")
//                "http://128.200.121.68:9000/memo/init.do?userId="+getSharedString("USERID")//운영
//                        +"&userNm="+getSharedString("USERNAME")
//                        +"&dptcd="+getSharedString("USERDEPARTURECODE")
            }
        }

        //jsp 소스코딩 예제
        //예제 : goAttach('http://hiportal.ex.co.kr/hiportal/board/BBS_1/2021/02/23/20210223135434_73285.hwp', '레드휘슬 참여 경로 및 작성 예시.hwp' )

        /*function goAttach(filePath, fileName){
            if(isiOSMobile == "Y"){
                JSONObject obj1 = {'filename':'aaa.hwp', 'url' : 'http://123.jsp'};
                window.webkit.messageHandlers.goAttach.postMessage(obj1.toString());
            }else{
                window.ElecMemoAppWebViewActivity.goAttach(filePath, fileName);	//성공javascriptInterface
            }
        }*/

    }


        @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_board:
                intent = new Intent(ElecMemoAppWebViewActivity_bak.this, BoardListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ElecMemoAppWebViewActivity_bak.this.startActivity(intent);
                finish();
                break;
            case R.id.btn_mail:
                intent = new Intent(ElecMemoAppWebViewActivity_bak.this, EmailMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ElecMemoAppWebViewActivity_bak.this.startActivity(intent);
                finish();
                break;
            case R.id.btn_elecapp:
                intent = new Intent(ElecMemoAppWebViewActivity_bak.this, ElecMemoAppWebViewActivity_bak.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*intent.putExtra("hybridUrl", "http://128.200.121.68:9000/elecapp/init.do?userId="+getSharedString("USERID")
                                                                                                +"&userNm="+getSharedString("USERNAME")
                                                                                                +"&dptcd="+getSharedString("USERDEPARTURECODE"));*/
                intent.putExtra("hybridUrl", "http://128.200.121.68:9000/elecapp/init.do?");
                ElecMemoAppWebViewActivity_bak.this.startActivity(intent);
                finish();
                break;
            case R.id.btn_memoapp:
                intent = new Intent(ElecMemoAppWebViewActivity_bak.this, ElecMemoAppWebViewActivity_bak.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("hybridUrl", "http://128.200.121.68:9000/memo/init.do?userId=21501229"//테스트 test
//                        +"&userNm=안현"
//                        +"&dptcd=N02145");
                /*intent.putExtra("hybridUrl", "http://128.200.121.68:9000/memo/init.do?userId="+getSharedString("USERID")//운영
                                                                                                +"&userNm="+getSharedString("USERNAME")
                                                                                                +"&dptcd="+getSharedString("USERDEPARTURECODE"));*/
                intent.putExtra("hybridUrl", "http://128.200.121.68:9000/memo/init.do?");
                ElecMemoAppWebViewActivity_bak.this.startActivity(intent);
                finish();
                break;
            case R.id.btn_vacation:
               /* intent = new Intent(ElecMemoAppWebViewActivity.this, WebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("hybridUrl", getSharedString("hybridUrl"));
                ElecMemoAppWebViewActivity.this.startActivity(intent);*/

                intent = new Intent(ElecMemoAppWebViewActivity_bak.this, ApprovalMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ElecMemoAppWebViewActivity_bak.this.startActivity(intent);
                finish();
                break;
            case R.id.btn_moremenu:

                Log.d(TAG, "appList_onVpn : "+MainActivity.appList_onVpn.size());
                for(int i=0; i< MainActivity.appList_onVpn.size(); i++) {
                    Log.d(TAG, "appList_onVpn : [ "+i+" ] "+MainActivity.appList_onVpn.get(i).getAppNm()+" : "+MainActivity.appList_onVpn.get(i).getDrawableicon3() );
                }

//                showMenu("");
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        webView.saveState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        webView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown() - webView.getUrl() : "+ webView.getUrl());

        if(keyCode == KeyEvent.KEYCODE_BACK ){
            Log.i(TAG, "onKeyDown() - keycode back");
            webView.loadUrl("javascript:f_goBackBtn()");
            return false;
        }else{
            Log.i(TAG, "onKeyDown() - keycode back else");
            return  super.onKeyDown(keyCode, event);
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }*/

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    // 문서뷰어 연동
    public static void DocuzenViewLauncher(Activity activity, String file_name, String file_url) {
        Intent intent = new Intent(activity, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);
        Log.i("파일명", file_name);
        Log.i("파일패스", file_url);
        try{

            String ext = file_name.substring(file_name.lastIndexOf("."));
            long tmp = System.currentTimeMillis();

            String tmpName = String.valueOf(tmp) + ext;

            Log.d("AUDIT", "################## ext : "+ext);
            Log.d("AUDIT", "################## tmpName : "+tmpName);

            String temp = Global.DZCSURLPREFIX;
            //temp += "fileName=" + URLEncoder.encode(tmpName, "utf-8") + "&filePath=" + URLEncoder.encode(file_url, "utf-8");
            temp += "fileName=" + URLEncoder.encode(tmpName, "euc-kr") + "&filePath=" + URLEncoder.encode(file_url, "euc-kr");

            Log.d("AUDIT", "################## temp : "+temp);
            Log.d("AUDIT", "################## baseDzcsUrl : "+Global.DZCSURL);


            Log.d("AUDIT", "################## baseDzcsUrl : "+Global.DZCSURL);
            Log.d("AUDIT", "################## URL : "+temp);

            intent.putExtra("baseDzcsUrl", Global.DZCSURL);
            intent.putExtra("URL", temp);
//				intent.putExtra("URL", "toiphoneapp://callDocumentFunction?fileName=1533889680636.hwp&filePath=http://eai.ex.co.kr:8088/jsp/com/gt/DownLoadTest.jsp?filename=pi/MSDS고시최종(20080110).hwp");
//

            Log.d("AUDIT", "################## Decode : "+ URLDecoder.decode(temp, "utf-8"));
            activity.startActivity(intent);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void goMainActivity() {
        Log.d(TAG, "goMainActivity()");
        Intent intent = new Intent(ElecMemoAppWebViewActivity_bak.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("app", "N");
        Log.d(TAG, "goMainActivity() - i : "+intent.getStringExtra("app"));
//            startActivityForResult(intent, RESULT_OK);
//            startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }
}
