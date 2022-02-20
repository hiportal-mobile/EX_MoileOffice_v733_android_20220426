package com.ex.group.approval.easy.addressbook.data;


public class VacEmpInfoVo {

	public VacEmpInfoVo() {
	}

	private String empId;//사원번호
	private String empGradeNm;//직급명
	private String contactTxt; //연락처
	private String dptNm;//부서
	private String empNm;//이름
	private String postNm; //직위명
	private String regDate;//신청일자
	private String attendCd;//근태유형 (ex) 11200
	private String attendStDate;//근태시작일
	private String attendEdDate;//근태종료일
	private String attendNote;//용무
	private String attendExcepList; //제외일리스트
	private String attendEndYM; //연차종료년월 202002
	//private Date fromDate, untilDate, targetDate, fromTime, untilTime;
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpGradeNm() {
		return empGradeNm;
	}
	public void setEmpGradeNm(String empGradeNm) {
		this.empGradeNm = empGradeNm;
	}
	public String getContactTxt() {
		return contactTxt;
	}
	public void setContactTxt(String contactTxt) {
		this.contactTxt = contactTxt;
	}
	public String getDptNm() {
		return dptNm;
	}
	public void setDptNm(String dptNm) {
		this.dptNm = dptNm;
	}
	public String getEmpNm() {
		return empNm;
	}
	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}
	public String getPostNm() {
		return postNm;
	}
	public void setPostNm(String postNm) {
		this.postNm = postNm;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getAttendCd() {
		return attendCd;
	}
	public void setAttendCd(String attendCd) {
		this.attendCd = attendCd;
	}
	public String getAttendStDate() {
		return attendStDate;
	}
	public void setAttendStDate(String attendStDate) {
		this.attendStDate = attendStDate;
	}
	public String getAttendEdDate() {
		return attendEdDate;
	}
	public void setAttendEdDate(String attendEdDate) {
		this.attendEdDate = attendEdDate;
	}
	public String getAttendNote() {
		return attendNote;
	}
	public void setAttendNote(String attendNote) {
		this.attendNote = attendNote;
	}
	public String getAttendExcepList() {
		return attendExcepList;
	}
	public void setAttendExcepList(String attendExcepList) {
		this.attendExcepList = attendExcepList;
	}
	public String getAttendEndYM() {
		return attendEndYM;
	}
	public void setAttendEndYM(String attendEndYM) {
		this.attendEndYM = attendEndYM;
	}
	
	
}
