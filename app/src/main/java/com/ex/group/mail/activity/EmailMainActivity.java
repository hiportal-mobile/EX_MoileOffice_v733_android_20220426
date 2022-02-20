package com.ex.group.mail.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.approval.easy.activity.ApprovalMainActivity;
import com.ex.group.board.activity.BoardListActivity;
import com.ex.group.elecappmemo.DialogActivity;
import com.ex.group.elecappmemo.ElecMemoAppWebViewActivity;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.CommonUtil;
import com.ex.group.mail.data.EmailDatabaseHelper;
import com.ex.group.mail.data.EmailMainListData;
import com.ex.group.mail.data.UserListData;
import com.ex.group.mail.service.EmailMainThread;
import com.ex.group.mail.service.EmailReceiveThread;
import com.ex.group.mail.util.EmailClientUtil;
import com.ex.group.mail.widget.MainDocListAdapter;
import com.ex.group.mail.widget.MainListAdapter;
import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ex.group.folder.R;

import static com.ex.group.elecappmemo.Global.ELEC_URL;

/**
 * 메일함 목록 화면
 * 
 * @author sjsun5318
 *
 */
public class EmailMainActivity extends SKTActivity implements OnClickListener, OnMenuItemClickListener {
	String TAG = EmailMainActivity.class.getSimpleName();
	static Context thisContext;
	private EmailMainListData[] m_MainListDatas = null; // 메인 편지함 목록
	private EmailMainListData[] m_MainDocDatas = null; // 메인 문서함 목록
	private EmailMainListData[] mainListDatas = null;
	private EmailMainListData[] mainDocListDatas = null;
	private MainListAdapter adapter = null; // 메인 편지함 adapter
	private MainDocListAdapter doc_adapter = null; // 메인 문서함 adapter
	private Intent intent = null;
	private TextView title = null;
	private TextView lastupdate = null;
	private ListView list = null; // 편지함 리스트
	private ListView doc_list = null; // 문서함 리스트
	private Menu mMenu = null;
	private LinearLayout emptyMailLayout = null;
	private LinearLayout emptyDocLayout = null;
	EmailAddJobDialog jobDialog;
	
	ArrayList<UserListData> userList = null;
	
	boolean dialogCheck = false;	
	
	private 	AlertDialog.Builder dialog = null;

	private Handler mHandler = new Handler(new android.os.Handler.Callback() {
		/**
		 * EmailMainThread 에서 넘어오는 handling
		 */
		@Override
		public boolean handleMessage(Message msg) {

			if (msg.what == EmailClientUtil.START_RUNABLE) {
				setUpdateStart();
				if (mMenu != null) {
					mMenu.findItem(R.id.MENUREFRESH).setEnabled(false);
				}
			} else if (msg.what == EmailClientUtil.END_RUNABLE) {
				EmailDatabaseHelper helper = new EmailDatabaseHelper(EmailMainActivity.this);
				try {
					// 현재 시간 가져온다.
					String up = helper.getBoxUpdate(EmailClientUtil.id, EmailClientUtil.companyCd);
					setUpdateEnd();

					if (!StringUtil.isNull(up)) {
						lastupdate.setText(up);
					}
					// 메일함(폴더) 데이타 조회 id=>로그인 아이디
//					EmailMainListData[] mainListDatas = helper.getMainTableData(EmailClientUtil.id, EmailClientUtil.companyCd); // 편지함 리스트 조회
//					EmailMainListData[] mainDocListDatas = helper.getMainTableDocData(EmailClientUtil.id, EmailClientUtil.companyCd); // 문서함 리스트 조회

					mainListDatas = helper.getMainTableData(EmailClientUtil.id, EmailClientUtil.companyCd); // 편지함 리스트 조회
					mainDocListDatas = helper.getMainTableDocData(	EmailClientUtil.id, EmailClientUtil.companyCd); // 문서함 리스트 조회
//					mainListDatas = null;		//test												
					if(adapter != null){
						adapter.clear();
					}
					if(doc_adapter != null){
						doc_adapter.clear();
					}
					
					

					setUI(mainListDatas);
					setDocUI(mainDocListDatas);

					EmailClientUtil.mainUpdate = false;
					if (mMenu != null) {
						mMenu.findItem(R.id.MENUREFRESH).setEnabled(true);
					}

				} finally {
					helper.close();
				}
			} else if (msg.what == EmailClientUtil.ADDRESS) {
				Log.d(TAG, "handlerMessage = "	+ EmailClientUtil.ADDRESS);
				Log.d(TAG, "handlerMessage = " + (String) msg.obj);
				Log.d(TAG, "handlerMessage = "	+ EmailClientUtil.empNm);

				EmailClientUtil.empNm = (String) msg.obj;
				EmailClientUtil.setMail(EmailMainActivity.this,	EmailClientUtil.id, EmailClientUtil.empNm);
				title.setText(EmailClientUtil.empNm);
				
				//겸직자인 경우 겸직정보 가져오기 위한 통신
				if(EmailClientUtil.emailAddress.length() > 8){
					new Action(EmailClientUtil.COMMON_MAIL_GETUSERINFO).execute("");
//					checkEmailAddr();
//					checkJob();
//					showJobDialog();
				}
				else{
					
				}
			}
			else if (msg.what == EmailClientUtil.UESRINFO) {
				Log.i(TAG, "==========GETUSERINFO handler===========");
			} 
			else if (msg.what == EmailClientUtil.NO_TABLE_ERROR) {
				EmailClientUtil.mainUpdate = false;
				
			} 
			else if (msg.what == EmailClientUtil.SKT_EXCEPTION) {
				EmailClientUtil.mainUpdate = false;
				EmailMainThread.setRunning(EmailClientUtil.END_RUNABLE);
				SKTException e = (SKTException) msg.obj;

				onCommonError(e);
			}
			return false;
		}
	});
/*	public XMLData getXmlData(String primitive) throws SKTException{
		
		Parameters params = new Parameters(primitive);
		
		if(EmailClientUtil.COMMON_MAIL_GETUSERINFO.equals(primitive)){
			params.put("userID", EmailClientUtil.emailAddress.split("_")[0]);
		}
		
		Controller controller = new Controller(EmailMainActivity.this);
		return controller.request(params, false, Environ.FILE_SERVICE);
	}*/
	
/*	public void showJobDialog(){
			jobDialog = new EmailAddJobDialog(thisContext, EmailClientUtil.nedmsID.split("_"));
			jobDialog.setCancelable(false);
			jobDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					Toast.makeText(getApplicationContext(), "선택 한 부서명 : "+jobDialog.getJob()+"", Toast.LENGTH_LONG).show();
					if (!EmailClientUtil.mainUpdate && !EmailReceiveThread.getRunning()) {
						EmailClientUtil.mainUpdate = true;
						// 메일함(폴더) 가져오는 스레드
//						EmailMainThread thread = new EmailMainThread(thisContext, mHandler);
//						thread.start();
					}
				}
			});
			jobDialog.show();
			dialogCheck = true;
	}*/
	
	public void showJobDialog(ArrayList<UserListData> userList){
			jobDialog = new EmailAddJobDialog(thisContext, userList);
			jobDialog.setCancelable(false);
			jobDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
				
					if (!EmailClientUtil.mainUpdate && !EmailReceiveThread.getRunning()) {
						EmailClientUtil.mainUpdate = true;
						//PRIVATE BOX 가져오기
						// 메일함(폴더) 가져오는 스레드
						EmailClientUtil.nedmsID = jobDialog.getJob();
						EmailMainThread thread = new EmailMainThread(thisContext, mHandler);
						thread.start();
						Toast.makeText(getApplicationContext(), "선택 한 팀 : "+jobDialog.getDeptNm(), Toast.LENGTH_LONG).show();
					}
				}
			});
			jobDialog.show();
	}


	private void setUpdateStart() {
		findViewById(R.id.update_progress).setVisibility(View.VISIBLE);
		findViewById(R.id.updateBtn).setVisibility(View.GONE);
		emptyDocLayout.setVisibility(View.VISIBLE);
	}

	private void setUpdateEnd() {
		findViewById(R.id.update_progress).setVisibility(View.GONE);
		findViewById(R.id.updateBtn).setVisibility(View.VISIBLE);
		emptyDocLayout.setVisibility(View.GONE);
	}

	/**
	 * onCreate 메소드
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreateX(Bundle savedInstanceState) {
		Log.d(TAG, "*** onCreateX() ");
		Log.i(TAG, "onCreate");
		thisContext = this;
		EnvironManager.setNeedEncPwd(true);
		EnvironManager.setTestEncPwd("FMH4T1+J2dgF/nOBXE4i+A==");
		EnvironManager.setTestCompany("E10");
		EnvironManager.setTestMdn("01071118162");
		EmailClientUtil.LOGMODE = true;

		try{
			EmailClientUtil.companyCd = StringUtil.isNull(SKTUtil.getCheckedCompanyCd(this)) ? EnvironManager.getTestCompanyCd() : SKTUtil.getCheckedCompanyCd(this);
			EmailClientUtil.mdn = StringUtil.isNull(SKTUtil.getMdn(this)) ? "mdn"	: SKTUtil.getMdn(this);
			Map<String, String> map = SKTUtil.getGMPAuth(this);
			// login 처리
//			 EmailClientUtil.id = "18401827"; //겸직자 테스트 //"19203920"; //"19203920"; //테스트
//			 EmailClientUtil.id = "21603226"; //겸직자 테스트 //"19203920"; //"19203920"; //테스트
			// 위해 주석 map.get(AuthData.ID_ID); 19516018 한도영, 19203920 김기형
//			Log.i("EMAILMAIN", "ID_ID=================================="+map.get(AuthData.ID_ID));
			
			//20160617 테스트 주석
			EmailClientUtil.id = map.get(AuthData.ID_ID);
			Log.d(TAG, "sj   EmailClientUtil.id =  "	+ EmailClientUtil.id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		EmailMainThread.setRunning(EmailClientUtil.END_RUNABLE);		 //사용자 정보(아이디, 이름)
		EmailReceiveThread.setRunning(null, EmailClientUtil.END_RUNABLE);//메일함
		
//		checkJob();
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			dialog = new AlertDialog.Builder(EmailMainActivity.this);
		}
		else{
			dialog = new AlertDialog.Builder(EmailMainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		}
		
		if(EmailClientUtil.id.length()>8){
			dialog.setMessage(getResources().getString(R.string.mail_notAvailable)).setCancelable(false).setNeutralButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					moveTaskToBack(true);
					finish();
					return;
				}
			});
			
			AlertDialog alert = dialog.create();
			alert.setTitle("경고");
			alert.show();
		}else{
			checkEmailAddr();
		}
		
		Log.d(TAG, "onCreate sj   EmailClientUtil.id =  " + EmailClientUtil.id);
		
		findViewById(R.id.updateBtn).setOnClickListener(this);
		findViewById(R.id.sendmail).setOnClickListener(this);
		title = (TextView) findViewById(R.id.settitle);
		lastupdate = (TextView) findViewById(R.id.lastupdate);
		list = (ListView) findViewById(R.id.MAINLISTVIEW);
		doc_list = (ListView) findViewById(R.id.MAINDOCVIEW);
		emptyMailLayout = (LinearLayout) findViewById(R.id.emptyMailLayout);
		emptyDocLayout = (LinearLayout) findViewById(R.id.emptyDocLayout);

//		
	}

	/**
	 * 액션처리후 메일함 리스트 셋팅
	 * 
	 * @param xmlData
	 */
	private void setUI(EmailMainListData[] data) {
		if(data != null){
			if (!StringUtil.isNull(EmailClientUtil.empNm)) {
				// emptyMailLayout.setVisibility(View.GONE);
				title.setText(EmailClientUtil.empNm);
			}
			for (int a = 0; a < data.length; a++) {
				adapter.add(data[a]);
			}
		}
		else{
			alertServerConnection();
		}
	
	}
	/** 서버에서 Data를 받아오지 못 했을 경우 **/
	public void alertServerConnection(){
		dialog.setMessage(getResources().getString(R.string.mail_serverNotAvaliable)).setCancelable(false).setNeutralButton("확인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				moveTaskToBack(true);
				finish();
				return;
			}
		});
		
		AlertDialog alert = dialog.create();
		alert.setTitle("경고");
		alert.show();
	}
	/**
	 * 액션처리후 문서함 리스트 셋팅
	 * 
	 * @param xmlData
	 */
	private void setDocUI(EmailMainListData[] data) {
		if(data != null){
			if (!StringUtil.isNull(EmailClientUtil.empNm)) {
				emptyDocLayout.setVisibility(View.GONE);
				title.setText(EmailClientUtil.empNm);
			}
			for (int a = 0; a < data.length; a++) {
				doc_adapter.add(data[a]);
			}
		}
		else{
			alertServerConnection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skt.pe.common.activity.SKTActivity#onAction(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	protected XMLData onAction(String primitive, String... args) throws SKTException {
		Log.d(TAG, "*** onAction() - primitive : "+primitive);
		Log.d(TAG, "*** onAction() - args : "+args);
		 if(EmailClientUtil.COMMON_MAIL_GETADDRESS.equals(primitive)){
			Parameters params = new Parameters(primitive);
			params.put("empId", EmailClientUtil.id);
			params.put("empName", EmailClientUtil.empNm);
			params.put("companyCd", EmailClientUtil.companyCd);
			params.put("lang", "ko");
			
			Controller controller = new Controller(EmailMainActivity.this);
			return controller.request(params, false, Environ.FILE_SERVICE);
		 }
		else if(EmailClientUtil.COMMON_MAIL_GETUSERINFO.equals(primitive)){
			Parameters params = new Parameters(primitive);
			params.put("userID", EmailClientUtil.emailAddress.split("_")[0]);
			
			Controller controller = new Controller(EmailMainActivity.this);
			return controller.request(params, false, Environ.FILE_SERVICE);
		}
		else{
			return onAction(primitive, args);
		}
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skt.pe.common.activity.SKTActivity#onActionPost(java.lang.String,
	 * com.skt.pe.common.service.XMLData,
	 * com.skt.pe.common.exception.SKTException)
	 */
	@Override
	protected void onActionPost(String primitive, XMLData result, SKTException e) {
		try{

			if(EmailClientUtil.COMMON_MAIL_GETADDRESS.equals(primitive)){
				EmailClientUtil.emailAddress = result.get("emailAddress");
				EmailClientUtil.empNm = result.get("empNm");
				
				EmailClientUtil.setMail(EmailMainActivity.this,	EmailClientUtil.id, EmailClientUtil.empNm);
				title.setText(EmailClientUtil.empNm);
				if(EmailClientUtil.emailAddress.length() > 8){
					new Action(EmailClientUtil.COMMON_MAIL_GETUSERINFO).execute("");
				}
				else{
					EmailClientUtil.nedmsID = EmailClientUtil.emailAddress;
					EmailMainThread thread = new EmailMainThread(thisContext, mHandler);
					thread.start();
//					Toast.makeText(getApplicationContext(), "선택 한 사용자 아이디 : "+EmailClientUtil.nedmsID+"", Toast.LENGTH_LONG).show();
				}
				
				
			}
			else if(EmailClientUtil.COMMON_MAIL_GETUSERINFO.equals(primitive)){
				 userList = new ArrayList<UserListData>();
//				 Log.i(TAG, "result====== "+result);
				 Log.i(TAG, "info=========="+result.get("info"));
				 XMLData childUserList = result.getChild("otherinfo");
				childUserList.setList("userList");
				Log.i(TAG, "otherinfo : "+childUserList.get("userList"));
				
				Document doc =  convertStringToDocument(childUserList.get("userList"));
				NodeList nList = doc.getElementsByTagName("user");
//				Log.i(TAG, "xml size : "+nList.getLength());
				
				for(int i=0; i<nList.getLength(); i++){
					UserListData data = new UserListData();
					Node node = nList.item(i);
					if(node.getNodeType() == Node.ELEMENT_NODE){
						Element element = (Element)node;
						 
						NodeList idList= element.getElementsByTagName("id");
						Element idElmnt = (Element) idList.item(0);
						Node id = idElmnt.getFirstChild();
						data.setId(id.getNodeValue());
						
						
						NodeList nameList= element.getElementsByTagName("name");
						Element nameElmnt = (Element) nameList.item(0);
						Node name = nameElmnt.getFirstChild();
						data.setName(name.getNodeValue());
						
						NodeList rankNameList= element.getElementsByTagName("rankName");
						Element rankElmnt = (Element) rankNameList.item(0);
						Node rank = rankElmnt.getFirstChild();
						data.setRankName(rank.getNodeValue());
							
						NodeList deptList= element.getElementsByTagName("deptName");
						Element deptElmnt = (Element) deptList.item(0);
						Node dept = deptElmnt.getFirstChild();
						data.setDeptName(dept.getNodeValue());
						
					/*	Log.i(TAG, "id : "+data.getId());
						Log.i(TAG, "name : "+data.getName());
						Log.i(TAG, "rankName : "+data.getRankName());
						Log.i(TAG, "deptName : "+data.getDeptName());*/
						
						userList.add(data);
					}
					
				}
				
				showJobDialog(userList);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skt.pe.common.activity.SKTActivity#onActionPre(java.lang.String)
	 */
	@Override
	protected int onActionPre(String primitive) {
		return Action.SERVICE_RETRIEVING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skt.pe.common.activity.SKTActivity#onActivityResultX(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResultX(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "*** onActivityResultX() ");
	}

	/**
	 * onStart 메소드
	 */
	@Override
	protected void onStartX() {
		Log.d(TAG, "sj   onStart() " + EmailMainThread.getRunning());
		Log.i(TAG, "onStart");
			/** 업무용 계정이 아닐 경우 **/
			if(EmailClientUtil.id.length() > 8){
				
				dialog.setMessage(getResources().getString(R.string.mail_notAvailable)).setCancelable(false).setNeutralButton("확인", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						moveTaskToBack(true);
						finish();
						return;
					}
				});
				
				AlertDialog alert = dialog.create();
				alert.setTitle("경고");
				alert.show();
			}
			
		else if(EmailClientUtil.id.length() == 8){
			adapter = new MainListAdapter(this, R.layout.mail_box_item);
			doc_adapter = new MainDocListAdapter(this, R.layout.mail_box_item);

			EmailDatabaseHelper helper = new EmailDatabaseHelper(EmailMainActivity.this);
			try {
				if (EmailMainThread.getRunning()) {
					EmailMainThread.runningThread.interrupt();
				}
				Log.d("xxxxxxxxxxx", "sj   aa" + EmailMainThread.getRunning());
				String up = helper.getBoxUpdate(EmailClientUtil.id, EmailClientUtil.companyCd);
				if (StringUtil.isNull(up)) {
					lastupdate.setText("");
				} else {
					lastupdate.setText(up);
				}

				title.setText(EmailClientUtil.empNm);

				m_MainListDatas = helper.getMainTableData(EmailClientUtil.id, EmailClientUtil.companyCd);
				m_MainDocDatas = helper.getMainTableDocData(EmailClientUtil.id, EmailClientUtil.companyCd);

			} catch (Exception e) {
				Log.e(TAG, "error : EmailMainActivity = " + e.toString());
			} finally {
				helper.close();
			}

			if (m_MainListDatas != null) {
				setUI(m_MainListDatas);
			} 
			if (m_MainDocDatas != null) {
				setDocUI(m_MainDocDatas);
			}

			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if (EmailMainThread.getRunning()) {
						EmailMainThread.runningThread.interrupt();
					}
					Log.i("", "paramIntMail : " + paramInt);
//					Log.i(TAG, "list click =======/ne-mail address : "+EmailClientUtil.emailAddress);
					intent = new Intent(getApplicationContext(), EmailReceiveActivity.class);
					intent.putExtra("mainData", adapter.getItem(paramInt));
					startActivity(intent);
				}
			});

			doc_list.setAdapter(doc_adapter);
			doc_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> paramAdapterView,
                                        View paramView, int paramInt, long paramLong) {

					if (EmailMainThread.getRunning()) {
						EmailMainThread.runningThread.interrupt();
					}
					int no = paramInt;
					Log.i(TAG, "doc_list no : "+no);
					Log.i(TAG, "doc_list click =======/ne-mail address : "+ EmailClientUtil.emailAddress);
					intent = new Intent(getApplicationContext(), EmailReceiveActivity.class);
					intent.putExtra("mainData", doc_adapter.getItem(no));
					startActivity(intent);
				}
			});

			Log.d(TAG, "sj   aa" + EmailClientUtil.mainUpdate + EmailReceiveThread.getRunning());

			/*if (!EmailClientUtil.mainUpdate && !EmailReceiveThread.getRunning()) {
				EmailClientUtil.mainUpdate = true;
				// 메일함(폴더) 가져오는 스레드
				EmailMainThread thread = new EmailMainThread(this, mHandler);
				thread.start();
			}*/
		
		}
	}

	/**
	 * 접속자  정보 확인(겸직자 구분)
	 */
	private void checkEmailAddr() {
		// if(StringUtil.isNull(EmailClientUtil.empNm)) {
//		EmailAddressRunnable addr = new EmailAddressRunnable(this, mHandler, EmailClientUtil.id, EmailClientUtil.companyCd);
//		new Thread(addr).start();
		Log.d("VARIABLE_FIND","imei check = "+CommonUtil.getImei(EmailMainActivity.this));
		new Action(EmailClientUtil.COMMON_MAIL_GETADDRESS).execute("");

		/*new Action2(EmailClientUtil.COMMON_MAIL_GETADDRESS, "http://128.200.121.68:9000/emp_ex/service.pe?authKey=a66781f684b2726ce3ac9769739172a72bbe1744\n" +
				"&companyCd=EX\n" +
				"&encPwd=vHkMvAbFGx2vZ8Q6fCEhPqtSQPD16Fb26RCRCiWhwYBTjy2KZsnyJLHMJEwxTIiTXbyGUAlUes2e5fSoI01hQGlfKbpZCV13Ho%2BoKIRsS2M%3D\n" +
				"&mdn=01063822504\n" +
				"&appId=null\n" +
				"&appVer=5.0.4\n" +
				"&lang=ko\n" +
				"&groupCd=EX\n" +
				"&primitive=COMMON_MAILNEW_GETADDRESS\n" +
				"&empName=\n" +
				"&empId=20609710").execute("");*/
		
		// }
	}


	/**
	 * 옵션 메뉴 생성
	 * 
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (mMenu == null) {
			inflater.inflate(R.menu.mail_foldermenu, menu);
			this.mMenu = menu;

		}
		if (EmailClientUtil.mainUpdate) {
			menu.findItem(R.id.MENUREFRESH).setEnabled(false);
		}

		return true;
	}

	/**
	 * 옵션 메뉴 선택 이벤트 헨들러<br>
	 * - 업데이트 - 편지쓰기 - 서명설정
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.MENUWRITE) {
			intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("aaa", "aaa");
			intent.putExtra("refer", "main");
			startActivity(intent);
		} else if (item.getItemId() == R.id.MENUREFRESH) {
			
			checkEmailAddr();
//			checkJob();
			
			EmailMainThread thread = new EmailMainThread(this, mHandler);
			thread.start();
			EmailClientUtil.mainUpdate = true;
			// }else if(item.getItemId()== R.id.MENU_SIGN_SETING){
			// EmailDialog di = new EmailDialog(this);
			// di.show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 편지함 목록 선택시 이벤트 핸들러(리스트 이동)
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	/*
	 * public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
	 * arg3) { if(EmailMainThread.getRunning()) {
	 * EmailMainThread.runningThread.interrupt(); } intent = new
	 * Intent(this,EmailReceiveActivity.class); intent.putExtra("mainData",
	 * adapter.getItem(arg2)); startActivity(intent); }
	 */

	/**
	 * 설정할 레이아웃을 리턴한다.
	 */
	@Override
	protected int assignLayout() {
		return R.layout.mail_box;
	}

	/**
	 * onClick 이벤트 핸들러<br>
	 * - Update 버튼
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.updateBtn:
			if (EmailClientUtil.mainUpdate) {
				return;
			} else {
				
				
				checkEmailAddr();
//				checkJob();
				
				EmailMainThread thread = new EmailMainThread(this, mHandler);
				thread.start();
				EmailClientUtil.mainUpdate = true;
			}
			break;
		case R.id.sendmail:
			intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("aaa", "aaa");
			intent.putExtra("refer", "main");
			startActivity(intent);
			break;
	/*	case R.id.job1:
			toggleBtn((Button)radioBtn[0]);
			break;
		case R.id.job2:
			toggleBtn((Button)radioBtn[1]);
			break;
		case R.id.check1:
			toggleBtn((Button)v);
			break;
		case R.id.check2:
			toggleBtn((Button)v);
			break;
		case R.id.btn_jobConfirm:
			Toast.makeText(EmailMainActivity.this, "확인", Toast.LENGTH_LONG).show();
			break;*/

		default:
			break;
		}
		
	}
	/*public void toggleBtn(Button v){
		radioBtn[0].setBackgroundResource(R.drawable.radio_off);
		radioBtn[1].setBackgroundResource(R.drawable.radio_off);
		v.setBackgroundResource(R.drawable.radio_on);
		
	}*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skt.pe.common.activity.SKTActivity#OnAuthDone()
	 */
	/*public void OnAuthDone() {
		blocking(-1);
		onCreateX(null);
	}*/

	/**
	 * 특정에러 코드 핸들링<br>
	 * 
	 * @param ex
	 */
	@Override
	public void onCommonError(SKTException ex) {
		super.onCommonError(ex);
		if (ex != null) {
			if ("5404".equals(ex.getErrCode())) {
				ex.alert(this, new DialogButton(0) {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
			} else if (Constants.Status.E_NOT_CONNECT_SSLVPN_ID.equals(ex
					.getErrCode())) {
				SKTDialog dlg = new SKTDialog(this);
				dlg.getDialog("확인", Resource._E072_ko, new DialogButton(0) {
					public void onClick(DialogInterface arg0, int arg1) {
						SKTUtil.closeApp(EmailMainActivity.this);
					}
				}).show();
			} else if (Constants.Status.E_NOT_INSTALL_SSLVPN_ID.equals(ex
					.getErrCode())) {
				SKTDialog dlg = new SKTDialog(this);
				dlg.getDialog("확인", Resource._E071_ko, new DialogButton(0) {
					public void onClick(DialogInterface arg0, int arg1) {
						SKTUtil.closeApp(EmailMainActivity.this);
					}
				}).show();
			} /*else if ("7002".equals(ex
					.getErrCode())) {
				Intent intent = new Intent(EmailMainActivity.this, DialogActivity.class);
				intent.putExtra("title", "야호");
				intent.putExtra("content", "핸드폰 번호 등록이 필요하오니, 헬프테스크를 통하여 요청해주세요. (모바일오피스 - 설치/등록 - 사용자등록) ");
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}*/
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		// TODO Auto-generated method stub

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("xxxxxxxxxxx", "sj   onDestroy() " + EmailMainThread.getRunning());

		EmailDatabaseHelper helper = new EmailDatabaseHelper(
				EmailMainActivity.this);
		helper.dropTables();
		helper.close();

		super.onDestroy();
//		finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Log.d("xxxxxxxxxxx",
				"sj   onBackPressed() " + EmailMainThread.getRunning());
		if (EmailMainThread.getRunning()) {
			EmailMainThread.runningThread.interrupt();
			EmailMainThread.setRunning(EmailClientUtil.END_RUNABLE);
			EmailClientUtil.mainUpdate = false;
		}
		if (EmailReceiveThread.getRunning()) {
			EmailReceiveThread.runningThread.interrupt();
			EmailReceiveThread.setRunning(EmailClientUtil.END_RUNABLE);
		}
		super.onBackPressed();
//		goElecMemo();
	}

	public void goElecMemo() {
		Intent intent = new Intent(EmailMainActivity.this, ElecMemoAppWebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("hybridUrl", ELEC_URL);
		startActivity(intent);
		finish();
	}

	// Handler 를 이용하여 처리하기
	private boolean m_Flag = false;
	Handler back_Handler = new Handler(new Callback() {
		public boolean handleMessage(Message msg) {
			if (msg.what == 0) {
				m_Flag = false;
			}
			return true;
		}
	});

	// 뒤로가기버튼설정
	/*public boolean onKeyDown(int KeyCode, KeyEvent event) {
		super.onKeyDown(KeyCode, event);
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (KeyCode) {
			case KeyEvent.KEYCODE_BACK: // 뒤로 키와 같은 기능을 한다.
				if (false) {
					Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
					// 버튼클릭시 true
					m_Flag = true;
					// Handler 호출 (2초 이후 back_Check 값 false)
					back_Handler.sendEmptyMessageDelayed(0, 2000);
					return false;
				} else {

					try {
//						SKTUtil.runApp(this, "com.ex.group.folder");
					} catch (Exception e) {
						e.printStackTrace();
					}

//					moveTaskToBack(true);
					finish();
					return true;
				}
			}
			return false;
		}
		return false;
	}*/
	
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();  
        } 
        return null;
    }
	  
    private static String getTagValue(String sTag, Element eElement) {
    	 NodeList nlList = ((Document) eElement).getElementsByTagName(sTag).item(0).getChildNodes();
    	 
    	        Node nValue = (Node) nlList.item(0);
    	 
    	 return nValue.getNodeValue();
    	  }
	/*private void dialogJobList(String[] args){
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(EmailMainActivity.this);
		dialog.setTitle(R.string.addJob);
		dialog.setSingleChoiceItems(args, -1, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
			
			
		});
		dialog.setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Toast.makeText(EmailMainActivity.this, , duration)
			}
		});
		
		dialog.create().show();
		
		
	}*/


	//테스트 test
	public class Action2 extends AsyncTask<String, Void, String> {
		// --------------------------------------------------------------------------------------------
		// #region 공통코드 정보 수신
		// 진행 상태 Progressbar
//        ProgressDialog progressDialog;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteOutputStream = null;

		String jsonObject = null;

		String primitive = "";
		String url = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		// primitive 를 통해 작업 내용 구분짓는다, URL을 구분짓는다.
		public Action2(String primitive, String url) {
			this.primitive = primitive;
			this.url = url;
		}


		@Override
		protected String doInBackground(String... arg0) {

			try {
				StringBuffer body = new StringBuffer();
				body.append(url);
                /*
                appendUrl = "&encversion=2";
                body.append(appendUrl);
                */

				URL url = new URL(body.toString());//운영
				Log.i(TAG, TAG + "URL : = " + body.toString());
				String strBody = body.toString();

				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setRequestProperty("Cache-Control", "no-cache");
				 conn.setDoOutput(true);
				conn.setDoInput(true);

				int responseCode = conn.getResponseCode();
				//Log.d(TAG, TAG + " ACTION responsecode  " + responseCode + "----" + conn.getResponseMessage());
				if (responseCode == HttpURLConnection.HTTP_OK) {

					inputStream = conn.getInputStream();
					byteOutputStream = new ByteArrayOutputStream();
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					int nLength = 0;

					while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
						byteOutputStream.write(byteBuffer, 0, nLength);
					}
					byteData = byteOutputStream.toByteArray();
					String response = new String(byteData, "euc-kr");
					//Log.d("", TAG + "responseData  = " + response);

					if (response == null || response.equals("")) {
						Log.e(TAG, TAG + "Response is NULL!!");
					} else {
						if (response.trim().equals("")) {
							Log.e(TAG, TAG + "Response is NULL!!");
						}
					}

					/**
					 * thkang
					 * 불필요 코드
					 Map<String, List<String>> headers = conn.getHeaderFields();
					 Iterator<String> it = headers.keySet().iterator();
					 while (it.hasNext()) {
					 String key = it.next();
					 List<String> values = headers.get(key);
					 StringBuffer sb = new StringBuffer();
					 for (int i = 0; i < values.size(); i++) {
					 sb.append(";" + values.get(i));
					 }
					 }
					 */

					jsonObject = response;
				}

			} catch (IOException e) {
				e.printStackTrace();
				jsonObject = "";
				Log.e("", "에러");

			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();

					} catch (IOException e) {
						e.printStackTrace();
						Log.e("", "에러");
					}
				}

				if (byteOutputStream != null) {
					try {
						byteOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("", "에러");
					}
				}

				if (conn != null) {
					conn.disconnect();
				}
			}
			return jsonObject;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			//Log.d("",TAG+"onProgressUpdate!!");
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG,"onactionpost result = " + result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			//Log.d("",TAG+"onCancelled!!");
		}

		@Override
		protected void onCancelled(String s) {
			super.onCancelled(s);
			//Log.d("",TAG+"onCancelled String s!!");
		}
	}
}