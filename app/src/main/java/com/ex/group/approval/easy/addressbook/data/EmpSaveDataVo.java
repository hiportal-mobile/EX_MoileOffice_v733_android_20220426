package com.ex.group.approval.easy.addressbook.data;

import java.io.Serializable;

public class EmpSaveDataVo implements Serializable{

	public EmpSaveDataVo() {
	}

	private String SAWONBUNHO; //사용자 id
	private String APP_MSG_NEED; // 제목
	private String APP_FORM_NAME; //양식명
	private String APP_PROCESS_TYPE; //프로세스타입 (INSERT)
	private String APP_PRINT_TYPE; //프린트옵션
	private String APP_DOC_SUBJECT; //응용리턴메세지 필요여부
	private String APP_ATTACH_INFO; //첨부정보 (NULL)
	private String APP_RECEIVE_MESSAGE_URL; //처리결과 반환 호출 URL 
	private String APP_APPROVAL_URL; //본문URL
	private String APP_RETURN_RESPONSE_NEED; // 응용쪽에서 gw로 결과호출 URL의 응답값을 리턴여부
	private String APP_APROVAL_ID;// 응용문서ID
	private String APPL_ID;//응용식별코드
	public String getSAWONBUNHO() {
		return SAWONBUNHO;
	}
	public void setSAWONBUNHO(String sAWONBUNHO) {
		SAWONBUNHO = sAWONBUNHO;
	}
	public String getAPP_MSG_NEED() {
		return APP_MSG_NEED;
	}
	public void setAPP_MSG_NEED(String aPP_MSG_NEED) {
		APP_MSG_NEED = aPP_MSG_NEED;
	}
	public String getAPP_FORM_NAME() {
		return APP_FORM_NAME;
	}
	public void setAPP_FORM_NAME(String aPP_FORM_NAME) {
		APP_FORM_NAME = aPP_FORM_NAME;
	}
	public String getAPP_PROCESS_TYPE() {
		return APP_PROCESS_TYPE;
	}
	public void setAPP_PROCESS_TYPE(String aPP_PROCESS_TYPE) {
		APP_PROCESS_TYPE = aPP_PROCESS_TYPE;
	}
	public String getAPP_PRINT_TYPE() {
		return APP_PRINT_TYPE;
	}
	public void setAPP_PRINT_TYPE(String aPP_PRINT_TYPE) {
		APP_PRINT_TYPE = aPP_PRINT_TYPE;
	}
	public String getAPP_DOC_SUBJECT() {
		return APP_DOC_SUBJECT;
	}
	public void setAPP_DOC_SUBJECT(String aPP_DOC_SUBJECT) {
		APP_DOC_SUBJECT = aPP_DOC_SUBJECT;
	}
	public String getAPP_ATTACH_INFO() {
		return APP_ATTACH_INFO;
	}
	public void setAPP_ATTACH_INFO(String aPP_ATTACH_INFO) {
		APP_ATTACH_INFO = aPP_ATTACH_INFO;
	}
	public String getAPP_RECEIVE_MESSAGE_URL() {
		return APP_RECEIVE_MESSAGE_URL;
	}
	public void setAPP_RECEIVE_MESSAGE_URL(String aPP_RECEIVE_MESSAGE_URL) {
		APP_RECEIVE_MESSAGE_URL = aPP_RECEIVE_MESSAGE_URL;
	}
	public String getAPP_APPROVAL_URL() {
		return APP_APPROVAL_URL;
	}
	public void setAPP_APPROVAL_URL(String aPP_APPROVAL_URL) {
		APP_APPROVAL_URL = aPP_APPROVAL_URL;
	}
	public String getAPP_RETURN_RESPONSE_NEED() {
		return APP_RETURN_RESPONSE_NEED;
	}
	public void setAPP_RETURN_RESPONSE_NEED(String aPP_RETURN_RESPONSE_NEED) {
		APP_RETURN_RESPONSE_NEED = aPP_RETURN_RESPONSE_NEED;
	}
	public String getAPP_APROVAL_ID() {
		return APP_APROVAL_ID;
	}
	public void setAPP_APROVAL_ID(String aPP_APROVAL_ID) {
		APP_APROVAL_ID = aPP_APROVAL_ID;
	}
	public String getAPPL_ID() {
		return APPL_ID;
	}
	public void setAPPL_ID(String aPPL_ID) {
		APPL_ID = aPPL_ID;
	}
	
	
	


}
