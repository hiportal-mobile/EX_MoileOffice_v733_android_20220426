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

	// 2015-03-05 Join ?????? ??????
	private TextView tvExceptDate1 = null;		// ??? ???????????? (?????????????????? ????????? ??? = ????????? ??????????????? - ??????????????? ??? ????????? ???)
	private TextView tvExceptDate2 = null;		// ????????????
	// 2015-03-05 Join ?????? ???

	private boolean[] exclusiveDateBackup = null;
	private int term = 0;

	private final static String activityTitle = "???????????????";

	ArrayList<GuntaeCdVo> guntaeList1 = new ArrayList<>();


	//?????? ?????? ?????? [??????]??????
	String S_EMP_ID = "";//????????????ex)21212121
	String S_ATTEND_APPL_YMD = "";//????????????ex)20190527
	String S_ATTEND_CD = "";//????????????ex)
	String S_ATTEND_NM = "";//???????????? ???
	String LIMIT_D_CNT = "";//??????????????????
	String S_ATTEND_END_YMD = "";//???????????????ex)20190527
	String S_CONTECT_TXT = "";//?????????ex)
	String S_NOTE = "";//??????ex)
	String S_ATTEND_EXCE_YMD_LIST = "";//????????? ex)20190102,20190102
	String S_ANL_END_YM = "";//?????????????????? ex)201905


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
		//getAttendList("1", "22000");//?????????????????? ??????
    }

	//?????? ?????? ?????? ??????
	public void getHolEmpInfo(){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			params.put("S_API_URL", "/searchHolGoingEmpInfo");
			params.put("S_APP_URL", "/searchHolGoingEmpInfo");
			params.put("S_EMP_ID", ""+SKTUtil.getGMPAuth(ApplyVacationApprovalActivity.this).get(AuthData.ID_ID));

		/*	//??????????????????
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


    //?????? ?????? ?????? ??????
    public void getAttendList(String S_ATTEND_CLASS, String S_PARREND_ATTEND_CD){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			params.put("S_API_URL", "/searchAttendList");
			params.put("S_APP_URL", "/searchAttendList");
			params.put("S_ATTEND_CLASS", "1");
			params.put("S_PARREND_ATTEND_CD", "22200");
			params.put("S_EMP_ID", ""+SKTUtil.getGMPAuth(ApplyVacationApprovalActivity.this).get(AuthData.ID_ID));

		/*	//??????????????????
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
	//?????? ?????? ??????
	public void goSearchHolGoingCheck(){
		Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
		try {
			//?????? ?????? ?????? [??????]??????
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
			//????????? test
//			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);//????????? test
//			alertDialog.setTitle("??????");//????????? test
//			alertDialog.setMessage(""+e.toString());//????????? test
//			alertDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {//????????? test
//				@Override//????????? test
//				public void onClick(DialogInterface dialogInterface, int i) {//????????? test
//					dialogInterface.dismiss();//????????? test
//				}
//			});//????????? test
//			alertDialog.setCancelable(true);//????????? test
//			alertDialog.show();//????????? test

			e.printStackTrace();
		}
	}


	String[] ATTEND_CD_LIST ;//????????????
	String[] ATTEND_NM_LIST;//?????????
	String[] LIMIT_D_CNT_LIST;//????????????
	String[] CHILD_ATTEND_CNT_LIST;//????????????????????????
	String[] ANL_END_YM_LIST;////??????????????????ex201805
	String[] PK_ATTEND_NM_LIST;//?????????

	@Override
	protected void onJsonActionPost(String primitive, String result) {
		try {
			if(COMMON_SEARCHHOLGOINGEMPINFO.equals(primitive)){
				Log.d("onJsonActionPost","result = " + result);
				JSONObject obj = new JSONObject(result);
				JSONArray jsonArray = obj.getJSONArray("Data");
				for (int i=0; i<jsonArray.length();i++){
					JSONObject temp = jsonArray.getJSONObject(i);
					draftPrim.setEmpGradeNm(temp.getString("EMP_GRADE_NM").replace("???", ""));

//					{"Data":[{"CONTACT_TXT":"8003691","EMP_GRADE_NM":"4???","EMP_ID":"21501229",
//							"DPTNM":"?????????","EMP_NM":"??????","POST_NM":"??????"}]
//						,"Etc":[],"Msg":[{"result":"","retMsg":""}]}

//					ATTEND_CD_LIST = temp.getString("ATTEND_CD_LIST").split(",");
				}
			}else if(COMMON_SEARCHATTENDLIST.equals(primitive)){//?????? ?????? ??????
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
// "ATTEND_NM_LIST":"????????????,?????????,????????????,??????,??????,??????,????????????",
// "ANL_END_YM_LIST":"",
// "PK_ATTEND_NM_LIST":"???????????????,?????????,????????????,??????,??????,??????,????????????",
// "ATTEND_CD_LIST":"11000,12000,13000,15000,16000,17000,18000"}]
// ,"Etc":[],"Msg":[{"result":"","retMsg":""}]}

			}else if(COMMON_SEARCHHOLGOINGCHECK.equals(primitive)){
				//????????? test
//				final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ApplyVacationApprovalActivity.this);//????????? test
//				alertDialog2.setTitle("??????");//????????? test
//				alertDialog2.setMessage(""+result);//????????? test
//				alertDialog2.setPositiveButton("??????", new DialogInterface.OnClickListener() {//????????? test
//					@Override//????????? test
//					public void onClick(DialogInterface dialogInterface, int i) {//????????? test
//						dialogInterface.dismiss();//????????? test
//					}
//				});//????????? test
//				alertDialog2.setCancelable(true);//????????? test
//				alertDialog2.show();//????????? test

				// 2015-03-05 Join ?????? ?????? - ????????? ??????????????? ???????????? ?????? ?????? ??????
				draftPrim.setTotalVacation(tvExceptDate1.getText().toString());
				draftPrim.setExceptCount(tvExceptDate2.getText().toString());
				// 2015-03-05 Join ?????? ???
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
					alertDialog.setTitle("???????????? ??????");
					alertDialog.setMessage(""+msg_resultMsg);
					alertDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
							alert("?????? ???????????? ????????? ????????? ??????????????? ????????????.");
						}
						// 2015-03-05 Join ?????? - ?????? ????????? ????????? ???????????? ?????? ????????????
						/*else if (DateUtil.diffDate((Date)date, draftPrim.getUntilDate()) >= term) {
							alert(termOverMessage());
						}*/
						else {
								exclusiveDateBackup = null;
								setFromDate(date, formattedDate);
								setUntilDate(date, formattedDate);	// 2015-03-05 Join ?????? - ?????? ???????????? ???????????? ?????? ???????????? ?????? ??????????????? ???????????? ??????
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
							alert("?????? ???????????? ?????? ????????? ????????? ??????????????? ????????????.");
						} 
						// 2015-03-05 Join ?????? - ?????? ????????? ????????? ???????????? ?????? ????????????
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
			case R.id.apply_vacation_button_guntae://???????????? ??????

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
				alertDialog.setTitle("???????????? ??????");
				alertDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
					alert("??????????????? 2???????????? ???????????? ????????? ???????????????.");
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
					/*// 2015-03-05 Join ?????? ?????? - ????????? ??????????????? ???????????? ?????? ?????? ??????
					draftPrim.setTotalVacation(tvExceptDate1.getText().toString());
					draftPrim.setExceptCount(tvExceptDate2.getText().toString());
					// 2015-03-05 Join ?????? ???
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

		// ???????????? ?????? ?????? ??????
		/*VocCode initVc = vcPrim.getVocCodeTree().getItem("1_1_1");
		if (initVc == null) {
			alert("????????? ??????????????? ????????????.", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		}
		setFormCode(initVc);*/
		
		// ?????? ?????? ?????? ??????
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
		
		// ????????? ??????
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
		
		// ????????? ?????? ??????
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
				// 2015-03-05 Join ?????? ?????? - ????????? ?????? ?????? ????????? ?????? ??????
				// if (exclusiveDateBackup[i] == false)
				if (exclusiveDateBackup[i] == true)				
					exceptCount++;
				// 2015-03-05 Join ?????? ???
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
		String message = (String) draftPrim.getTag(DraftPrimitive.Tags.FORM_CODE_NAME) + "??? " + this.term + "????????? ????????? ??? ????????????. ????????? ????????? ???????????? ????????? ????????????.";
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
				return "?????? ???????????? ?????? ????????? ???????????? ??????????????? ????????????.";
			}
			// 2015-03-05 Join ?????? ?????? - ???????????? ?????? ??? ???????????? ???????????? ?????? ?????? ?????? ???????????? ??? ??????????????? ???????????? ??????????????? ???????????? ?????? ??????
		/*if (DateUtil.diffDate(draftPrim.getFromDate(), draftPrim.getUntilDate()) >= term) {
			return termOverMessage();
		}*/
			int realVactionTerm = 0;		// ?????? ???????????? = ??? ???????????? - ????????????
			realVactionTerm = Integer.parseInt(tvExceptDate1.getText().toString()) - Integer.parseInt(tvExceptDate2.getText().toString());
//		Log.d(TAG, "realVactionTerm ======= " + realVactionTerm);
//		if( realVactionTerm > this.term) {//????????? ?????? ?????????
//			return termOverMessage();
//		}
			// 2015-03-05 Join ?????? ???
			if("".equals(S_ATTEND_CD)){
				return "???????????? ????????? ??????????????? ????????????.";
			}

			// ?????????
			EditText telNum = (EditText) findViewById(R.id.apply_vacation_edittext_telnum);
			if (telNum.getText().toString().trim().length() == 0)
				return "????????? ????????? ???????????? ???????????????.";
			draftPrim.setTelNum(telNum.getText().toString());

			// ??????
			EditText descript = (EditText) findViewById(R.id.apply_vacation_edittext_descript);
			if (descript.getText().toString().trim().length() == 0)
				return "?????? ????????? ???????????? ???????????????.";
			draftPrim.setDescript(descript.getText().toString());

			// ???????????? ??????????????? ????????? ????????? ??? ?????? ???????????? ????????????.
			SKTUtil.log("TEST", "DraftPrimitive: " + draftPrim.toParameterString());
		}catch(Exception e){
			e.printStackTrace();
			//????????? test
//			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);//????????? test
//			alertDialog.setTitle("??????");//????????? test
//			alertDialog.setMessage(""+e.toString());//????????? test
//			alertDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {//????????? test
//				@Override//????????? test
//				public void onClick(DialogInterface dialogInterface, int i) {//????????? test
//					dialogInterface.dismiss();//????????? test
//				}
//			});//????????? test
//			alertDialog.setCancelable(true);//????????? test
//			alertDialog.show();//????????? test
		}

		
		return null;
	}
}