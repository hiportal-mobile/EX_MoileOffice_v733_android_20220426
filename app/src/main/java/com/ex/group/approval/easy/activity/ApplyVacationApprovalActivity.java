package com.ex.group.approval.easy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ex.group.approval.easy.addressbook.data.GuntaeCdVo;
import com.ex.group.approval.easy.constant.ApprovalConstant;
import com.ex.group.approval.easy.dialog.WheelDatePickerDialogHelper;
import com.ex.group.approval.easy.dialog.WheelGuntaeDialog;
import com.ex.group.approval.easy.dialog.WheelGuntaeDialogActivity;
import com.ex.group.approval.easy.dialog.ifaces.PEDialogInterface;
import com.ex.group.approval.easy.domain.VocCode;
import com.ex.group.approval.easy.domain.VocCodeTree;
import com.ex.group.approval.easy.primitive.DraftPrimitive;
import com.ex.group.approval.easy.primitive.VacCodePrimitive;
import com.ex.group.approval.easy.util.ActivityLauncher;
import com.ex.group.folder.R;
import com.google.gson.JsonObject;
import com.skt.pe.common.activity.ifaces.CommonUI;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.ds.Tree;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.primitive.Primitive;
import com.skt.pe.common.primitive.util.PrimitiveList;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApplyVacationApprovalActivity extends ApprovalCommonActivity implements OnClickListener, CommonUI {
	private final String TAG = "ApplyVacati";



	private Button applicationDate = null;
	private Button fromDate = null;
	private Button untilDate = null;
	private Button selectGuntaeCode = null;
	private Button selectExceptDate = null;
//	private DraftFormPrimitive dformPrim = null;
	private DraftPrimitive draftPrim = new DraftPrimitive();
	private VacCodePrimitive vcPrim = null;

	TextView apply_vacation_textview_term = null;

	// 2015-03-05 Join 추가 시작
	private TextView tvExceptDate1 = null;		// 총 휴가일수 (제외일수까지 포함한 값 = 단순히 휴가시작일 - 휴가종료일 만 계산된 값)
	private TextView tvExceptDate2 = null;		// 제외일수
	// 2015-03-05 Join 추가 끝

	private boolean[] exclusiveDateBackup = null;
	private int term = 0;

	private final static String activityTitle = "휴가신청서";

	ArrayList<GuntaeCdVo> guntaeList1 = new ArrayList<>();


	//휴가 신청 정보 [입력]단계
	String S_EMP_ID = "";//사원번호ex)21212121
	String S_ATTEND_APPL_YMD = "";//신청일자ex)20190527
	String S_ATTEND_CD = "";//근태유형ex)
	String S_ATTEND_NM = "";//근태유형 명
	String LIMIT_D_CNT = "";//휴가가능일수
	String S_ATTEND_END_YMD = "";//근태종료일ex)20190527
	String S_CONTECT_TXT = "";//연락처ex)
	String S_NOTE = "";//용무ex)
	String S_ATTEND_EXCE_YMD_LIST = "";//제외일 ex)20190102,20190102
	String S_ANL_END_YM = "";//연차종료년월 ex)201905


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected int assignLayout() {
		return R.layout.easy_apply_vacation;
	}

	@Override
    public void onCreateX(Bundle savedInstanceState) {
		setSubTitle(activityTitle);
		Intent intent = getIntent();
		vcPrim = (VacCodePrimitive) intent.getSerializableExtra(ApprovalConstant.IntentArg.PRIMITIVE);
		draftPrim.setDraftKind(DraftPrimitive.DraftKind.VACATIION);
		initUI();
		getHolEmpInfo();
		//getAttendList("1", "22000");//근태코드정보 조회
    }

	//근태 코드 정보 조회
	public void getHolEmpInfo(){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			params.put("S_API_URL", "/searchHolGoingEmpInfo");
			params.put("S_APP_URL", "/searchHolGoingEmpInfo");
			params.put("S_EMP_ID", ""+SKTUtil.getGMPAuth(ApplyVacationApprovalActivity.this).get(AuthData.ID_ID));

		/*	//필수파라미터
			Map<String, String> gmpAuth = SKTUtil.getGMPAuthPwd(ApplyVacationApprovalActivity.this);
			params.put("authKey", URLEncoder.encode((String)gmpAuth.get("AUTHKEY")) + "&");
			params.put("companyCd", URLEncoder.encode((String)gmpAuth.get("COMPANY_CD")) + "&");
			params.put("encPwd", URLEncoder.encode((String)gmpAuth.get("ENC_PWD")) + "&");*/

			Environ environ = EnvironManager.getEnviron(ApplyVacationApprovalActivity.this);
			String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort()+environ.FILE_ATTACHMENT+"?"+params.toString()+"&"+environ.getEnvironParam();

			new JsonAction(COMMON_SEARCHHOLGOINGEMPINFO, url, this).execute();

		}catch(Exception e){
			e.printStackTrace();
		}
	}


    //근태 코드 정보 조회
    public void getAttendList(String S_ATTEND_CLASS, String S_PARREND_ATTEND_CD){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			params.put("S_API_URL", "/searchAttendList");
			params.put("S_APP_URL", "/searchAttendList");
			params.put("S_ATTEND_CLASS", "1");
			params.put("S_PARREND_ATTEND_CD", "22200");
			params.put("S_EMP_ID", ""+SKTUtil.getGMPAuth(ApplyVacationApprovalActivity.this).get(AuthData.ID_ID));

		/*	//필수파라미터
			Map<String, String> gmpAuth = SKTUtil.getGMPAuthPwd(ApplyVacationApprovalActivity.this);
			params.put("authKey", URLEncoder.encode((String)gmpAuth.get("AUTHKEY")) + "&");
			params.put("companyCd", URLEncoder.encode((String)gmpAuth.get("COMPANY_CD")) + "&");
			params.put("encPwd", URLEncoder.encode((String)gmpAuth.get("ENC_PWD")) + "&");*/

			Environ environ = EnvironManager.getEnviron(ApplyVacationApprovalActivity.this);
			String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort()+environ.FILE_ATTACHMENT+"?"+params.toString()+"&"+environ.getEnvironParam();

			new JsonAction(COMMON_SEARCHATTENDLIST, url, this).execute();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	String sendApplyUrl = "";
	//휴가 신청 정보
	public void goSearchHolGoingCheck(){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			//휴가 신청 정보 [입력]단계
			SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatYYYYMM = new SimpleDateFormat("yyyyMMdd");


			params.put("S_API_URL", "/searchHolGoingCheck");
			params.put("S_APP_URL", "/searchHolGoingCheck");
			params.put("S_EMP_ID", ""+SKTUtil.getGMPAuth(ApplyVacationApprovalActivity.this).get(AuthData.ID_ID));
			params.put("S_ATTEND_APPL_YMD", formatYYYYMMDD.format(draftPrim.getTargetDate()));
			params.put("S_ATTEND_CD", S_ATTEND_CD);
			params.put("S_ATTEND_STA_YMD",formatYYYYMMDD.format(draftPrim.getFromDate()) );
			params.put("S_ATTEND_END_YM", formatYYYYMMDD.format(draftPrim.getUntilDate()));
			params.put("S_ATTEND_END_YMD", formatYYYYMMDD.format(draftPrim.getUntilDate()));
			params.put("S_CONTACT_TXT", draftPrim.getTelNum());
			params.put("S_NOTE", draftPrim.getDescript());
			params.put("S_ATTEND_EXCE_YMD_LIST", S_ATTEND_EXCE_YMD_LIST);
			params.put("S_ANL_END_YM", S_ANL_END_YM);

			Environ environ = EnvironManager.getEnviron(ApplyVacationApprovalActivity.this);
			String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort()+environ.FILE_ATTACHMENT+"?"+params.toString()+"&"+environ.getEnvironParam();
			draftPrim.setUrl(url);
			new JsonAction(COMMON_SEARCHHOLGOINGCHECK, url, this).execute();

		}catch(Exception e){
			//테스트 test
//			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);//테스트 test
//			alertDialog.setTitle("에러");//테스트 test
//			alertDialog.setMessage(""+e.toString());//테스트 test
//			alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {//테스트 test
//				@Override//테스트 test
//				public void onClick(DialogInterface dialogInterface, int i) {//테스트 test
//					dialogInterface.dismiss();//테스트 test
//				}
//			});//테스트 test
//			alertDialog.setCancelable(true);//테스트 test
//			alertDialog.show();//테스트 test

			e.printStackTrace();
		}
	}


	String[] ATTEND_CD_LIST ;//근태코드
	String[] ATTEND_NM_LIST;//근태명
	String[] LIMIT_D_CNT_LIST;//한도일수
	String[] CHILD_ATTEND_CNT_LIST;//하위근태존재여부
	String[] ANL_END_YM_LIST;////연차종료년월ex201805
	String[] PK_ATTEND_NM_LIST;//근태명

	@Override
	protected void onJsonActionPost(String primitive, String result) {
		try {
			if(COMMON_SEARCHHOLGOINGEMPINFO.equals(primitive)){
				Log.d("onJsonActionPost","result = " + result);
				JSONObject obj = new JSONObject(result);
				JSONArray jsonArray = obj.getJSONArray("Data");
				for (int i=0; i<jsonArray.length();i++){
					JSONObject temp = jsonArray.getJSONObject(i);
					draftPrim.setEmpGradeNm(temp.getString("EMP_GRADE_NM").replace("급", ""));

//					{"Data":[{"CONTACT_TXT":"8003691","EMP_GRADE_NM":"4급","EMP_ID":"21501229",
//							"DPTNM":"정보처","EMP_NM":"안현","POST_NM":"대리"}]
//						,"Etc":[],"Msg":[{"result":"","retMsg":""}]}

//					ATTEND_CD_LIST = temp.getString("ATTEND_CD_LIST").split(",");
				}
			}else if(COMMON_SEARCHATTENDLIST.equals(primitive)){//근태 코드 정보
				Log.d("onJsonActionPost","result = " + result);
				JSONObject obj = new JSONObject(result);
				JSONArray jsonArray = obj.getJSONArray("Data");
				for (int i=0; i<jsonArray.length();i++){
					JSONObject temp = jsonArray.getJSONObject(i);

					ATTEND_CD_LIST = temp.getString("ATTEND_CD_LIST").split(",");
					ATTEND_NM_LIST = temp.getString("ATTEND_NM_LIST").split(",");
					LIMIT_D_CNT_LIST = temp.getString("LIMIT_D_CNT_LIST").split(",");
					CHILD_ATTEND_CNT_LIST = temp.getString("CHILD_ATTEND_CNT_LIST").split(",");
//					ANL_END_YM_LIST = temp.getString("ANL_END_YM_LIST").split(",");
					PK_ATTEND_NM_LIST = temp.getString("PK_ATTEND_NM_LIST").split(",");
				}

				S_ATTEND_CD = ATTEND_CD_LIST[0];
				draftPrim.setTag(DraftPrimitive.Tags.FORM_CODE_NAME, ATTEND_NM_LIST[0]);
				selectGuntaeCode.setText(ATTEND_NM_LIST[0]);

//			{"Data":[
// {"LIMIT_D_CNT_LIST":"0,0,0,0,0,0,0",
// "CHILD_ATTEND_CNT_LIST":"8,102,26,7,4,1,1",
// "ATTEND_NM_LIST":"연차휴가,경조사,특별휴가,공가,병가,결근,대체휴무",
// "ANL_END_YM_LIST":"",
// "PK_ATTEND_NM_LIST":"연월차휴가,경조사,특별휴가,공가,병가,결근,대체휴무",
// "ATTEND_CD_LIST":"11000,12000,13000,15000,16000,17000,18000"}]
// ,"Etc":[],"Msg":[{"result":"","retMsg":""}]}

			}else if(COMMON_SEARCHHOLGOINGCHECK.equals(primitive)){
				//테스트 test
//				final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ApplyVacationApprovalActivity.this);//테스트 test
//				alertDialog2.setTitle("결과");//테스트 test
//				alertDialog2.setMessage(""+result);//테스트 test
//				alertDialog2.setPositiveButton("확인", new DialogInterface.OnClickListener() {//테스트 test
//					@Override//테스트 test
//					public void onClick(DialogInterface dialogInterface, int i) {//테스트 test
//						dialogInterface.dismiss();//테스트 test
//					}
//				});//테스트 test
//				alertDialog2.setCancelable(true);//테스트 test
//				alertDialog2.show();//테스트 test

				// 2015-03-05 Join 추가 시작 - 상세한 휴가정보를 제공하기 위한 구문 추가
				draftPrim.setTotalVacation(tvExceptDate1.getText().toString());
				draftPrim.setExceptCount(tvExceptDate2.getText().toString());
				// 2015-03-05 Join 추가 끝
				JSONObject obj = new JSONObject(result);
				JSONArray jsonArray_msg = obj.getJSONArray("Msg");
				String msg_result = "";
				String msg_resultMsg = "";
				for (int i=0; i<jsonArray_msg.length();i++){
					JSONObject temp = jsonArray_msg.getJSONObject(i);
					msg_result = temp.getString("result");
					msg_resultMsg = temp.getString("retMsg");
				}

				if("1000".equals(msg_result)){
					ActivityLauncher.launchApprovalLine(ApplyVacationApprovalActivity.this, draftPrim, ApprovalConstant.EasyApprovalType.VACATION, activityTitle);
				}else{
					Toast.makeText(ApplyVacationApprovalActivity.this, msg_resultMsg, Toast.LENGTH_LONG).show();
					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
					alertDialog.setTitle("근태종류 선택");
					alertDialog.setMessage(""+msg_resultMsg);
					alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});
					alertDialog.setCancelable(true);
					alertDialog.show();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	protected void onReceive(Primitive primitive, SKTException e) {
		super.onReceive(primitive, e);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.apply_vacation_button_targetdate:
				super.selectDate(new PEDialogInterface.OnClickListener() {
	
					@Override
					public void onClick(Object date, String formattedDate) {
						applicationDate.setText(formattedDate);
						draftPrim.setTargetDate((Date)date);
					}
				}, draftPrim.getTargetDate());
				break;
			case R.id.apply_vacation_button_fromdate:
				super.selectDate( new PEDialogInterface.OnClickListener() {
	
					@Override
					public void onClick(Object date, String formattedDate) {
						if (draftPrim.getTargetDate().after((Date)date) == true) {
							alert("휴가 시작일을 신청일 이후로 선택하시기 바랍니다.");
						}
						// 2015-03-05 Join 수정 - 휴가 시작일 선택에 불편함이 있어 주석처리
						/*else if (DateUtil.diffDate((Date)date, draftPrim.getUntilDate()) >= term) {
							alert(termOverMessage());
						}*/
						else {
								exclusiveDateBackup = null;
								setFromDate(date, formattedDate);
								setUntilDate(date, formattedDate);	// 2015-03-05 Join 추가 - 휴가 시작일을 선택하면 휴가 종료일도 같이 세팅되도록 요청하여 추가
								setVacationCount();
								setExceptCount();
						}
					}
				}, draftPrim.getFromDate());
				break;
			case R.id.apply_vacation_button_untildate:
				super.selectDate( new PEDialogInterface.OnClickListener() {
	
					@Override
					public void onClick(Object date, String formattedDate) {
						if (draftPrim.getFromDate().after((Date)date) == true) {
							alert("휴가 종료일을 휴가 시작일 이후로 선택하시기 바랍니다.");
						} 
						// 2015-03-05 Join 수정 - 휴가 종료일 선택에 불편함이 있어 주석처리
						/*else if (DateUtil.diffDate(draftPrim.getFromDate(), (Date)date) >= term) {
							alert(termOverMessage());
						}*/ 
						else {
							exclusiveDateBackup = null;
							setUntilDate(date, formattedDate);
							setVacationCount();
							setExceptCount();
						}
					}
				}, draftPrim.getUntilDate());
				break;
			case R.id.apply_vacation_button_guntae://근태종류 버튼

				Intent wheenIntent = new Intent(ApplyVacationApprovalActivity.this, WheelGuntaeDialogActivity.class);
				startActivityForResult(wheenIntent, ApprovalConstant.RequestCode.REQUEST_WHEELDIALOGACTIITY_CODE);
				/*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setSingleChoiceItems((CharSequence[]) ATTEND_NM_LIST, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//Toast.makeText(ApplyVacationApprovalActivity.this, ""+ATTEND_CD_LIST[i]+":"+ATTEND_NM_LIST[i], Toast.LENGTH_LONG).show();
						if(ATTEND_CD_LIST != null){
							S_ATTEND_CD =  ATTEND_CD_LIST[i];
						}

						if(ANL_END_YM_LIST != null){
							S_ANL_END_YM = ANL_END_YM_LIST[i];
						}
						if("".equals(S_ANL_END_YM) || null == S_ANL_END_YM){
							SimpleDateFormat format = new SimpleDateFormat("yyyy");
							String strDate = format.format(new Date()).toString();
							S_ANL_END_YM = strDate+"12";
						}
						selectGuntaeCode.setText(ATTEND_NM_LIST[i]);
						draftPrim.setTag(DraftPrimitive.Tags.FORM_CODE_NAME, ATTEND_NM_LIST[i]);

						Log.d("ApplyApprovalLine","ApplyApprovalLine go Launch = "+ATTEND_NM_LIST[i]);
					}
				}).create();
				alertDialog.setTitle("근태종류 선택");
				alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
				alertDialog.setCancelable(true);
				alertDialog.show();*/


				/*if(vcPrim != null){
					super.selectGuntaeCode( new PEDialogInterface.OnClickListener() {

						@Override
						public void onClick(Object obj, String arg) {
							Log.d(TAG, "Guntae Selected: " + arg);
							VocCode vc = vcPrim.getVocCodeTree().getItem(arg);
							setFormCode(vc);
						}
					}, vcPrim.getVocCodeTree());
				}*/

				break;
			case R.id.apply_vacation_button_exceptdate:
				if (getVacationCount() < 2) {
					alert("근태기간이 2일이상일 경우에만 선택이 가능합니다.");
				} else {
					super.selectExceptDate(draftPrim.getFromDate(), draftPrim.getUntilDate(), exclusiveDateBackup, new PEDialogInterface.OnClickListener() {
						@Override
						public void onClick(Object obj, String arg) {
							S_ATTEND_EXCE_YMD_LIST = "";
							PrimitiveList exclusiveDate = new PrimitiveList(); 
							boolean[] userClicks = (boolean []) obj;
							exclusiveDateBackup = userClicks;
							Date fromDate = draftPrim.getFromDate();
							for (int i=0; i<userClicks.length; i++) {
								if (userClicks[i] == false) {
									exclusiveDate.add(DraftPrimitive.formatDate(DateUtil.addDate(fromDate, i)));
								}else if(userClicks[i] == true){
									S_ATTEND_EXCE_YMD_LIST += DraftPrimitive.formatDate(DateUtil.addDate(fromDate, i)).replace("-","")+",";
								}
							}
							S_ATTEND_EXCE_YMD_LIST = S_ATTEND_EXCE_YMD_LIST.substring(0, S_ATTEND_EXCE_YMD_LIST.length()-1);

							draftPrim.setExceptDay(S_ATTEND_EXCE_YMD_LIST);

							SKTUtil.log("TEST", exclusiveDate.toString());
							draftPrim.setExclusiveDate(exclusiveDate.toString());
							setExceptCount();
						}
					});
				}
				break;
			case R.id.apply_vacation_button_ok:
				String message = validationUI();
				if (message == null) {
					goSearchHolGoingCheck();
					/*// 2015-03-05 Join 추가 시작 - 상세한 휴가정보를 제공하기 위한 구문 추가
					draftPrim.setTotalVacation(tvExceptDate1.getText().toString());
					draftPrim.setExceptCount(tvExceptDate2.getText().toString());
					// 2015-03-05 Join 추가 끝
					ActivityLauncher.launchApprovalLine(ApplyVacationApprovalActivity.this, draftPrim, ApprovalConstant.EasyApprovalType.VACATION, activityTitle);*/
				}
				else
					alert(message);
				break;
			case R.id.apply_vacation_button_cancel:
				finish();
				break;
		} // switch
	}

	@Override
	public void initUI() {
		selectGuntaeCode = (Button) findViewById(R.id.apply_vacation_button_guntae);
		selectGuntaeCode.setOnClickListener(this);
		selectExceptDate = (Button) findViewById(R.id.apply_vacation_button_exceptdate);
		selectExceptDate.setOnClickListener(this);
		
		Button btnOk = (Button) findViewById(R.id.apply_vacation_button_ok);
		btnOk.setOnClickListener(this);
		Button btnCancel = (Button) findViewById(R.id.apply_vacation_button_cancel);
		btnCancel.setOnClickListener(this);

		apply_vacation_textview_term = (TextView) findViewById(R.id.apply_vacation_textview_term);

		// 근태코드 정보 기초 세팅
		/*VocCode initVc = vcPrim.getVocCodeTree().getItem("1_1_1");
		if (initVc == null) {
			alert("등록된 근태정보가 없습니다.", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		}
		setFormCode(initVc);*/
		
		// 각종 날짜 초기 세팅
		initDateUI();
	}

	@Override
	protected void onActivityResultX(int requestCode, int resultCode, Intent data) {
		super.onActivityResultX(requestCode, resultCode, data);
		Log.d("onActivityResultX","onActivityResultX " + requestCode+":"+resultCode);

		switch (requestCode){
			case ApprovalConstant.RequestCode.FINISH:
				if(resultCode == RESULT_OK){
					finish();
				}
				break;
			case ApprovalConstant.RequestCode.REQUEST_WHEELDIALOGACTIITY_CODE:

				if(RESULT_OK==resultCode){

					S_ATTEND_CD = data.getStringExtra("S_ATTEND_CD");
					S_ATTEND_NM = data.getStringExtra("S_ATTEND_NM");
					LIMIT_D_CNT = data.getStringExtra("LIMIT_D_CNT");
					Toast.makeText(ApplyVacationApprovalActivity.this, ""+S_ATTEND_CD+S_ATTEND_NM, Toast.LENGTH_LONG).show();
					draftPrim.setFormCode(S_ATTEND_CD);
					draftPrim.setTag(DraftPrimitive.Tags.FORM_CODE_NAME, S_ATTEND_NM);
					selectGuntaeCode.setText(S_ATTEND_NM);

					apply_vacation_textview_term.setText(LIMIT_D_CNT);

				}
					break;
		}
	}

	private void initDateUI() {
		Date currentDate = new Date(System.currentTimeMillis());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String strDate = format.format(currentDate).toString();
		
		Date initDate = null;
		try {
			initDate = format.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// 외출일 선택
		applicationDate = (Button) findViewById(R.id.apply_vacation_button_targetdate);
		applicationDate.setOnClickListener(this);
		
		applicationDate.setText(WheelDatePickerDialogHelper.generateDateFormat(initDate));
		draftPrim.setTargetDate(initDate);
		
		fromDate = (Button) findViewById(R.id.apply_vacation_button_fromdate);
		fromDate.setOnClickListener(this);
		
		fromDate.setText(WheelDatePickerDialogHelper.generateDateFormat(initDate));
		draftPrim.setFromDate(initDate);
		
		untilDate = (Button) findViewById(R.id.apply_vacation_button_untildate);
		untilDate.setOnClickListener(this);
		
		untilDate.setText(WheelDatePickerDialogHelper.generateDateFormat(initDate));
		draftPrim.setUntilDate(initDate);
		
		// 제외일 초기 세팅
		setVacationCount();
		setExceptCount();
        setAnlEmpYn();
	}

	private void setAnlEmpYn(){
        if("".equals(S_ANL_END_YM) || null == S_ANL_END_YM){
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String strDate = format.format(new Date()).toString();
            S_ANL_END_YM = strDate+"12";
        }
    }

	
	private void setFromDate(Object date, String formattedDate) {
		fromDate.setText(formattedDate);
		draftPrim.setFromDate((Date)date);
	}
	
	private void setUntilDate(Object date, String formattedDate) {
		untilDate.setText(formattedDate);
		draftPrim.setUntilDate((Date)date);
	}
	
	private void setFormCode(VocCode vc) {
		
		Log.d(TAG, "getCodeNm : "+vc.getCodeNm());
		Log.d(TAG, "getCode : "+vc.getCode());
		Log.d(TAG, "getTerm : "+vc.getTerm());
		
		selectGuntaeCode.setText(vc.getCodeNm());
		draftPrim.setFormCode(vc.getCode());
		draftPrim.setTag(DraftPrimitive.Tags.FORM_CODE_NAME, vc.getCodeNm());
		TextView term = (TextView) findViewById(R.id.apply_vacation_textview_term);
		term.setText(String.valueOf(vc.getTerm()));
		this.term = vc.getTerm();
	}
	
	private void setExceptCount() {
		int exceptCount = 0;
		
		if (exclusiveDateBackup != null) {
			for (int i=0; i<exclusiveDateBackup.length; i++) {
				// 2015-03-05 Join 수정 시작 - 제외일 산정 방법 변경에 따른 수정
				// if (exclusiveDateBackup[i] == false)
				if (exclusiveDateBackup[i] == true)				
					exceptCount++;
				// 2015-03-05 Join 수정 끝
			}
		}
		tvExceptDate2 = (TextView) findViewById(R.id.apply_vacation_textview_exceptdate2);
		tvExceptDate2.setText(String.valueOf(exceptCount));
	}
	
	private void setVacationCount() {
		int vacationCount = 0;
		
		vacationCount = DateUtil.diffDate(draftPrim.getFromDate(), draftPrim.getUntilDate()) + 1;
		
		tvExceptDate1 = (TextView) findViewById(R.id.apply_vacation_textview_exceptdate1);
		tvExceptDate1.setText(String.valueOf(vacationCount));
	}
	
	private int getVacationCount() {
		return DateUtil.diffDate(draftPrim.getFromDate(), draftPrim.getUntilDate()) + 1;
	}
	
	private String termOverMessage() {
		String message = (String) draftPrim.getTag(DraftPrimitive.Tags.FORM_CODE_NAME) + "는 " + this.term + "일까지 사용할 수 있습니다. 잔여일 이내로 신청하여 주시기 바랍니다.";
		return message;
	}

	@Override
	public void resetUI() {
	}

	@Override
	public String validationUI() {
		return setDraftPrimitive();
	}
	
	private String setDraftPrimitive() {
		try {

			if (draftPrim.getUntilDate().before(draftPrim.getFromDate()) == true) {
				return "휴가 시작일을 휴가 종료일 이전으로 선택하시기 바랍니다.";
			}
			// 2015-03-05 Join 수정 시작 - 휴가일수 산정 시 제외일을 염두하지 않아 기존 구문 주석처리 후 제외일까지 산정하여 휴가일수를 추출하는 구문 추가
		/*if (DateUtil.diffDate(draftPrim.getFromDate(), draftPrim.getUntilDate()) >= term) {
			return termOverMessage();
		}*/
			int realVactionTerm = 0;		// 실제 휴가일수 = 총 휴가일수 - 제외일수
			realVactionTerm = Integer.parseInt(tvExceptDate1.getText().toString()) - Integer.parseInt(tvExceptDate2.getText().toString());
//		Log.d(TAG, "realVactionTerm ======= " + realVactionTerm);
//		if( realVactionTerm > this.term) {//잔여일 확인 메시지
//			return termOverMessage();
//		}
			// 2015-03-05 Join 수정 끝
			if("".equals(S_ATTEND_CD)){
				return "근태종류 항목을 선택하시기 바랍니다.";
			}

			// 연락처
			EditText telNum = (EditText) findViewById(R.id.apply_vacation_edittext_telnum);
			if (telNum.getText().toString().trim().length() == 0)
				return "연락처 항목이 입력되지 않았습니다.";
			draftPrim.setTelNum(telNum.getText().toString());

			// 적요
			EditText descript = (EditText) findViewById(R.id.apply_vacation_edittext_descript);
			if (descript.getText().toString().trim().length() == 0)
				return "적요 항목이 입력되지 않았습니다.";
			draftPrim.setDescript(descript.getText().toString());

			// 나머지는 컨트럴에서 변경이 일어날 시 즉시 데이터가 들어간다.
			SKTUtil.log("TEST", "DraftPrimitive: " + draftPrim.toParameterString());
		}catch(Exception e){
			e.printStackTrace();
			//테스트 test
//			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);//테스트 test
//			alertDialog.setTitle("에러");//테스트 test
//			alertDialog.setMessage(""+e.toString());//테스트 test
//			alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {//테스트 test
//				@Override//테스트 test
//				public void onClick(DialogInterface dialogInterface, int i) {//테스트 test
//					dialogInterface.dismiss();//테스트 test
//				}
//			});//테스트 test
//			alertDialog.setCancelable(true);//테스트 test
//			alertDialog.show();//테스트 test
		}

		
		return null;
	}
}