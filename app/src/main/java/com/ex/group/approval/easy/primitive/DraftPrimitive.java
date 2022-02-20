package com.ex.group.approval.easy.primitive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ex.group.approval.easy.constant.ApprovalConstant;
import com.ex.group.approval.easy.constant.UserInfo;
import com.skt.pe.common.activity.SKTActivity.Action;
import com.skt.pe.common.primitive.Primitive;

@SuppressWarnings("serial")
public class DraftPrimitive extends Primitive {
	// 간이기안 - 외출/휴가 신청하기
	public final static String name = "COMMON_APPROVALSTAGING_DRAFT";
	private String userId, childNoList, appUrl,procClass , empNm, empPosition, regDate, deptNm, empGradeNm ;
	private Date fromDate, untilDate, endYmd, staYmd, targetDate, fromTime, untilTime;
	private String totalVacation; // 총 휴가일 수
	private String cooperator,formCode, descript,note, outingPlace , contectNo, exceptDateList,anlEndYm;
	private String exceptCount,contectTxt; // 제외일 수
	private String PROC_CLASS;//외출 사용
	private String exceptDay = "";//제외일 ex)20150501,20150502

	//2021.07 사무외출 대체휴무 잔여시간
	private String GEN_TIME_YN = "";

	//2021/.07 외출 자가결재 급수명
	private String POST_NM = "";



	private String url = "";


	private final static String[] paramNames = { "systemType", "draftKind", "S_ATTEND_STA_YMD", "S_ATTEND_END_YMD", "S_OUTNG_YMD",
			"S_OUTNG_STA_HM", "S_OUTNG_END_HM", "S_ATTEND_EXCE_YMD_LIST", "S_ATTEND_CD", "telNum", "S_OUTNG_PLACE", "descript",
			"isOwnCar", "S_COMP_EMP_ID", "approvalStep", "approvalKind", "S_EMP_ID", "S_APP_URL",
			"S_OUTNG_HOUR_CHILD_PER_NO","S_PROC_CLASS","S_EMP_NAME", "S_EMP_POSITION" , "S_ATTEND_APPL_YMD", "S_EMP_DEPT" ,"S_CONTECT_TXT","S_ANL_END_YM" , "S_CONTECT_TXT",
			"S_NOTE" , "formDate", "untilDate"};

	boolean isOwnCar = false;
	public DraftPrimitive() {
		super(DraftPrimitive.name, paramNames, Action.SERVICE_RETRIEVING);
		this.init();
	}

	public String getEmpGradeNm() {
		return empGradeNm;
	}

	public void setEmpGradeNm(String empGradeNm) {
		this.empGradeNm = empGradeNm;
	}

	public void setExceptDay(String exceptDay){
		this.exceptDay = exceptDay;
	}
	public String getExceptDay(){
		return this.exceptDay;
	}

	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}

	public void setPROC_CLASS(String PROC_CLASS){
		this.PROC_CLASS = PROC_CLASS;
	}

	public String getPROC_CLASS(){
		return this.PROC_CLASS;
	}

	private void init() {
		this.setSystemType(ApprovalConstant.SystemType.EASY);
		UserInfo userInfo = UserInfo.getInstance();
		this.setUserID(userInfo.getUserID());
	}

	public void setSystemType(String systemType) {
		super.addParameter(paramNames[0], systemType);
	}
	public String getSystemType() {
		return super.getParameter(paramNames[0]);
	}

	public void setDraftKind(String draftKind) {
		super.addParameter(paramNames[1], draftKind);
	}

	public String getDraftKind() {
		return super.getParameter(paramNames[1]);
	}

	public void setStaYmd(Date staYmd) {
		this.staYmd = staYmd;
		super.addParameter(paramNames[2], formatDate(staYmd));
	}

	public Date getStaYmd() {
		return this.staYmd;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setEndYmd(Date endYmd) {
		this.endYmd = endYmd;
		super.addParameter(paramNames[3], formatDate(endYmd));
	}

	public Date getEndYmd() {
		return this.endYmd;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
		super.addParameter(paramNames[4], formatDate(targetDate));
	}

	public Date getTargetDate() {
		return this.targetDate;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
		super.addParameter(paramNames[5], formatTime(fromTime));
	}

	public Date getFromTime() {
		return this.fromTime;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setUntilTime(Date untilTime) {
		this.untilTime = untilTime;
		super.addParameter(paramNames[6], formatTime(untilTime));
	}

	public Date getUntilTime() {
		return this.untilTime;
	}
	public Date getUntilDate() {
		return this.untilDate;
	}
	

	public void setExclusiveDate(String exclusiveDate) {
		super.addParameter(paramNames[7], exclusiveDate);
		this.exceptDateList = exclusiveDate;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
		super.addParameter(paramNames[8], formCode);
	}

	// 2021.09 사무 / 사무(대체휴무) 일시 용무를 입력하지 않아도됨(분기처리를 위한 외출유형코드값)
	public String getFormCode(){ return this.formCode;};

	public void setTelNum(String telNum) {
		super.addParameter(paramNames[9], telNum);
		this.contectNo = telNum;
	}

	public String getTelNum() {
		return this.contectNo;
	}

	public void setLocation(String location) {
		this.outingPlace = location;
		super.addParameter(paramNames[10], location);
	}

	public void setDescript(String descript) {
		super.addParameter(paramNames[11], descript);
		this.descript = descript;
	}

	public void setIsOwnCar(boolean isOwnCar) {
		String strIsOwnCar = isOwnCar == true ? ApprovalConstant.Boolean.TRUE : ApprovalConstant.Boolean.FALSE;
		this.isOwnCar = isOwnCar;
		super.addParameter(paramNames[12], strIsOwnCar);
	}
	public boolean getIsOwnCar(){
		return this.isOwnCar;
	}

	public void setCooperator(String cooperator) {
		this.cooperator = cooperator;
		super.addParameter(paramNames[13], cooperator);
	}

	public void setApprovalStep(String approvalStep) {
		super.addParameter(paramNames[14], approvalStep);
	}

	public String getApprovalStep() {
		return super.getParameter(paramNames[14]);
	}

	public String getApprovalKind() {
		return super.getParameter(paramNames[15]);
	}

	public void setApprovalKind(String approvalKind) {
		super.addParameter(paramNames[15], approvalKind);
	}

	public void setUserID(String userID) {
		this.userId = userID;
		super.addParameter(paramNames[16], userID);
	}

	public void setAppUrl(String appUrl) {
		super.addParameter(paramNames[17], appUrl);
		this.appUrl = appUrl;
	}

	public void setChildNoList(String childNoList) {
		super.addParameter(paramNames[18], childNoList);
		this.childNoList = childNoList;
	}
	
	public void setProcClass(String procClass) {
		super.addParameter(paramNames[19], procClass);
		this.procClass = procClass;
	}
	public void setEmpNm(String empNm) {
		super.addParameter(paramNames[20], empNm);
		this.empNm = empNm;
	}
	public void setEmpPosition(String position) {
		super.addParameter(paramNames[21], position);
		this.empPosition = position;
	}
	public void setRegDate(String regDate) {
		super.addParameter(paramNames[22], regDate);
		this.regDate = regDate;
	}
	public void setDeptNm(String deptNm) {
		super.addParameter(paramNames[23], deptNm);
		this.deptNm = deptNm;
	}
	
	public void setAnlEndYm(String anlEndYm) {
		super.addParameter(paramNames[24], deptNm);
		this.anlEndYm = anlEndYm;
	}
	
	public void setConTectTxt(String contectTxt) {
		super.addParameter(paramNames[25], contectTxt);
		this.contectTxt = contectTxt;
	}
	public void setNote(String note) {
		super.addParameter(paramNames[26], note);
		this.note = note;
	}
	public void setFromDate(Date fromDate) {
		super.addParameter(paramNames[26], formatDate(fromDate));
		this.fromDate = fromDate;
	}
	public void setUntilDate(Date untilDate) {
		super.addParameter(paramNames[26], formatDate(untilDate));
		this.untilDate = untilDate;
	}

	//2021.07 사무외출 대체휴무 잔여시간
	public String getGEN_TIME_YN() {
		return GEN_TIME_YN;
	}
	public void setGEN_TIME_YN(String GEN_TIME_YN) {
		this.GEN_TIME_YN = GEN_TIME_YN;
	}

	//2021.07 1급 자가결재(외출)
	public String getPOST_NM() {
		return POST_NM;
	}
	public void setPOST_NM(String POST_NM) {
		this.POST_NM = POST_NM;
	}

	public static String formatDate(Date inputDate) {
		SimpleDateFormat format = new SimpleDateFormat(DateFormat.dateFormat);
		return format.format(inputDate).toString();
		
	}

	public static Date parseDate(String inputDate) {
		SimpleDateFormat format = new SimpleDateFormat(DateFormat.dateFormat);
		try {
			return format.parse(inputDate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String formatTime(Date inputTime) {
		SimpleDateFormat format = new SimpleDateFormat(DateFormat.timeFormat);
		return format.format(inputTime).toString();
	}
	

	private static class DateFormat {
		public final static String dateFormat = "yyyy-MM-dd";
		public final static String timeFormat = "HH:mm";
	}

	public static class DraftKind {
		public final static String OUTSIDE = "0";
		public final static String VACATIION = "1";
	}

	public static class ApprovalKind {
		public final static String DRAFTER = "125";
		public final static String NORMAL = "110";
		public final static String AUTHORIZED = "113";
		public final static String SUBSTITUTED = "114";
		public final static String NONE = "none";
		// public final static String NORMAL = "normal";
		// public final static String AUTHORIZED = "authorized";
		// public final static String SUBSTITUTED = "substituted";
		// public final static String NONE = "none";
	}

	public static class Tags {
		public final static String FORM_CODE_NAME = "FORM_CODE_NAME";
	}

	public String getTotalVacation() {
		return totalVacation;
	}

	public void setTotalVacation(String totalVacation) {
		this.totalVacation = totalVacation;
	}

	public String getExceptCount() {
		return exceptCount;
	}

	public void setExceptCount(String exceptCount) {
		this.exceptCount = exceptCount;
	}
	
	public String getAppUrl() {
		return this.appUrl;
	}
	public String formCode(){
		return this.formCode;
	}
	
	public String getChildNoList() {
		return this.childNoList;
	}
	public String getProcClass(){
		return this.procClass;
	}
	public String getEmpNm() {
		return this.empNm;
	}
	
	public String getEmpPosition(){
		return this.empPosition;
	}
	public String getRegDate(){
		return this.regDate;
	}
	public String getDeptNm(){
		return this.deptNm;
	}
	public String getCooperator(){
		return this.cooperator;
	}
	public String getDescript(){
		return this.descript;
	}
	
	public String getLocation(){
		return this.outingPlace;
	}
	
	public String getContectNo(){
		return this.contectNo;	
	}
	
	public String getExceptDateList(){
		return this.exceptDateList;
	}
	public String getAnlEndYm(){
		return this.anlEndYm;
	}
	public String getContectTxt(){
		return this.contectTxt;
	}
	public String getNote(){
		return this.note;
	}
}
