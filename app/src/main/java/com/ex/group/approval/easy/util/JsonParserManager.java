package com.ex.group.approval.easy.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ex.group.approval.easy.addressbook.data.EmpSaveDataVo;
import com.ex.group.approval.easy.addressbook.data.GuntaeCdVo;
import com.ex.group.approval.easy.addressbook.data.OutEmpInfoVo;
import com.ex.group.approval.easy.addressbook.data.VacEmpInfoVo;

import android.util.Log;

public class JsonParserManager {

	// 외출 신청 대상자 정보 조회
	public static OutEmpInfoVo searchOutEmpInfo(JSONObject result) {
		OutEmpInfoVo vo = new OutEmpInfoVo();
		Log.d("", "JSONObject success" +  "    parser come in");
		JSONArray ja;
		try {
			ja = new JSONArray(result.getString("Data"));
			
			if (ja != null && ja.length() > 0) {
				JSONObject jsonObject;
				try {
					jsonObject = ja.getJSONObject(0);
					String str = "";
					str = jsonObject.optString("EMP_ID");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setEmpId(str);
					str = jsonObject.optString("EMP_NM");
					Log.d("", "JSONObject success" + str);
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setEmpNm(str);
					str = jsonObject.optString("EMPGRADE_NM");
					if (StringUtil.isEmptyString(str))
						str = " ";
					else
						vo.setEmpGradeNm(str);
					str = jsonObject.optString("POST_NM");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setPostNm(str);
					str = jsonObject.optString("ACC_TIME");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setOffiOutAccTime(str);
					str = jsonObject.optString("CHILD_NM_LIST");
					if(StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setChildNmList(str);
					str = jsonObject.optString("CHILD_PER_NO_LIST");
					if(StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setChildNoList(str);
					str = jsonObject.optString("OUTNG_HOUR_CHILD_PER_NO_LIST");
					if(StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setChildBirthList(str);
					str = jsonObject.optString("DPTNM");
					if(StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setDptNm(str);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return vo;
	}
	// 휴가 신청 대상자 정보 조회
	public static EmpSaveDataVo saveApplData(JSONObject result) {
		EmpSaveDataVo vo = new EmpSaveDataVo();
		Log.d("", "JSONObject success" +  "    parser come in");
		JSONArray ja;
		try {
			ja = new JSONArray(result.getString("Data"));
			
			if (ja != null && ja.length() > 0) {
				JSONObject jsonObject;
				try {
					jsonObject = ja.getJSONObject(0);
					String str = "";
					str = jsonObject.optString("SAWONBUNHO");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setSAWONBUNHO(str);
					str = jsonObject.optString("APP_MSG_NEED");
					Log.d("", "JSONObject success" + str);
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setAPP_MSG_NEED(str);
					str = jsonObject.optString("APP_FORM_NAME");
					if (StringUtil.isEmptyString(str))
						str = " ";
					else
						vo.setAPP_FORM_NAME(str);
					str = jsonObject.optString("APP_PROCESS_TYPE");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_PROCESS_TYPE(str);
					str = jsonObject.optString("APP_PRINT_TYPE");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_PRINT_TYPE(str);
					str = jsonObject.optString("APP_DOC_SUBJECT");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_DOC_SUBJECT(str);
					str = jsonObject.optString("APP_ATTACH_INFO");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_ATTACH_INFO(str);
					str = jsonObject.optString("APP_RECEIVE_MESSAGE_URL");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_RECEIVE_MESSAGE_URL(str);
					str = jsonObject.optString("APP_APPROVAL_URL");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_APPROVAL_URL(str);
					str = jsonObject.optString("APP_RETURN_RESPONSE_NEED");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_RETURN_RESPONSE_NEED(str);
					str = jsonObject.optString("APP_APROVAL_ID");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPP_APROVAL_ID(str);
					str = jsonObject.optString("APPL_ID");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setAPPL_ID(str);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return vo;
	}
	// 휴가 신청 대상자 정보 조회
	public static VacEmpInfoVo searchHolGoingEmpInfo(JSONObject result) {
		VacEmpInfoVo vo = new VacEmpInfoVo();
		Log.d("", "JSONObject success" +  "    parser come in");
		JSONArray ja;
		try {
			ja = new JSONArray(result.getString("Data"));
			
			if (ja != null && ja.length() > 0) {
				JSONObject jsonObject;
				try {
					jsonObject = ja.getJSONObject(0);
					String str = "";
					str = jsonObject.optString("EMP_ID");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setEmpId(str);
					str = jsonObject.optString("EMP_NM");
					Log.d("", "JSONObject success" + str);
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						Log.d("", "JSONObject success" + str);
						vo.setEmpNm(str);
					str = jsonObject.optString("EMP_GRADE_NM");
					if (StringUtil.isEmptyString(str))
						str = " ";
					else
						vo.setEmpGradeNm(str);
					str = jsonObject.optString("POST_NM");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setPostNm(str);
					str = jsonObject.optString("DPTNM");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setDptNm(str);
					str = jsonObject.optString("CONTACT_TXT");
					if (StringUtil.isEmptyString(str))
						str = "";
					else
						vo.setContactTxt(str);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return vo;
	}
	
	//근태코드정보
	public static ArrayList<GuntaeCdVo> searchAttendList(JSONObject result, int guntaeCategory) {
			ArrayList<GuntaeCdVo> vo = new ArrayList<GuntaeCdVo>();
			GuntaeCdVo subVo = new GuntaeCdVo();
			Log.d("", "JSONObject success" +  "    parser come in");
			JSONArray ja;
			try {
				ja = new JSONArray(result.getString("Data"));
				
				if (ja != null && ja.length() > 0) {
					JSONObject jsonObject;
					try {
						jsonObject = ja.getJSONObject(0);
						String str = "";
						str = jsonObject.optString("ATTEND_CD_LIST");
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							Log.d("", "JSONObject success" + str);
							subVo.setAttendCd(str);
						str = jsonObject.optString("ATTEND_NM_LIST");
						Log.d("", "JSONObject success" + str);
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							Log.d("", "JSONObject success" + str);
							subVo.setAttendNm(str);
						str = jsonObject.optString("PK_ATTEND_NM_LIST");
						if (StringUtil.isEmptyString(str))
							str = " ";
						else
							subVo.setPkAttendNm(str);
						str = jsonObject.optString("LIMMIT_D_CNT_LIST");
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							subVo.setLimmitDCnt(str);
						str = jsonObject.optString("CHILD_ATTEND_YN_LIST");
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							subVo.setChildAttendYn(str);
						str = jsonObject.optString("ANL_END_YM_LIST");
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							subVo.setAnlEndYm(str);
						String[] arr1 = null;
							if (StringUtil.isEmptyString(subVo.getAttendCd())){
								
							}else{
								arr1 = subVo.getAttendCd().split(",");
							};
						if(arr1.length <1) {
							
							
						}else{
							
							Log.d("################", arr1.length + ":: " );
							String[] arr2 = new String [arr1.length]; 
							String[] arr3 = new String [arr1.length];
							String[] arr4 = new String [arr1.length];
							String[] arr5 = new String [arr1.length];
							String[] arr6 = new String [arr1.length];
							
							if (StringUtil.isEmptyString(subVo.getAttendNm())){
								for (int i = 0; i < arr1.length; i++) {
									arr2[i] = "";
								}
							}else{
								arr2 =  subVo.getAttendNm().split(",");
							};
							if (StringUtil.isEmptyString(subVo.getAnlEndYm())){
								for (int i = 0; i < arr1.length; i++) {
									arr3[i] = "";
								}
							}else{
								arr3 = subVo.getAnlEndYm().split(",");
							};
							if (StringUtil.isEmptyString(subVo.getChildAttendYn())){
								for (int i = 0; i < arr1.length; i++) {
									arr4[i] = "";
								}
							}else{
								arr4 = subVo.getChildAttendYn().split(",");
							};
							if (StringUtil.isEmptyString(subVo.getLimmitDCnt())){
								for (int i = 0; i < arr1.length; i++) {
									arr5[i] = "";
								}
							}else{
								arr5 = subVo.getLimmitDCnt().split(",");
							};
							if (StringUtil.isEmptyString(subVo.getPkAttendNm())){
								for (int i = 0; i < arr1.length; i++) {
									arr6[i] = "";
								}
							}else{
								arr6 = subVo.getPkAttendNm().split(",");
							};
							
						if( arr1 != null || arr1.length > 0){	
							for (int j = 0; j < arr1.length; j++) {
								Log.d("JSONPARSERMANAGER :  : ",  " j : " + j + " attendCd : " + arr1[j]);
								GuntaeCdVo v1 = new GuntaeCdVo();
								if (arr1[j] == null || arr1[j] == "")
									v1.setAttendCd("");
								else
									v1.setAttendCd(arr1[j]);
									
								if (arr2[j] == null || arr2[j] == "")
									v1.setAttendNm("");
								else
									v1.setAttendNm(arr2[j]);
							
								if (arr3[j] == null || arr3[j] == "" )
									v1.setAnlEndYm("");
								else
									v1.setAnlEndYm(arr3[j]);
								if (arr4[j] == null || arr4[j] == "")
									v1.setChildAttendYn("");
								else
									v1.setChildAttendYn(arr4[j]);
								if (arr5[j] == null || arr5[j] == "")
									v1.setLimmitDCnt("");
								else
									v1.setLimmitDCnt(arr5[j]);
								if (arr6[j] == null || arr6[j] == "")
									v1.setPkAttendNm("");
								else
									v1.setPkAttendNm(arr6[j]);
								
								
								 v1.setGuntaeCategory(guntaeCategory);
								vo.add(j, v1); 
							}
						}
							
						}
				
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			} catch (JSONException e1) {

				e1.printStackTrace();
			}

			
			return vo;
			
			
		}	
	
	
	// 신청서 유형 가져오기
		public static String selectProcessClass(JSONObject result) {
			Log.d("", "JSONObject success" +  "    parser come in");
			JSONArray ja;
			String procClass= "";
			try {
				ja = new JSONArray(result.getString("Etc"));
				
				if (ja != null && ja.length() > 0) {
					JSONObject jsonObject;
					try {
						jsonObject = ja.getJSONObject(0);
						String str = "";
						str = jsonObject.optString("PROC_CLASS");
						if (StringUtil.isEmptyString(str))
							str = "";
						else
							Log.d("", "JSONObject success" + str);
						procClass = str;
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return procClass;
		}

}
