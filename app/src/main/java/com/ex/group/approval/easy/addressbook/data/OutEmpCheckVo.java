package com.ex.group.approval.easy.addressbook.data;

import java.io.Serializable;

public class OutEmpCheckVo implements Serializable{

	public OutEmpCheckVo() {
	}

	private String empId; //사번 v
	private String empNm; //이름 
	private String codeAttend; //외출유형v
	private String outDate; //외출일v
	private String outStTime; //외출시작시간
	private String outEdTime; //외출종료시간
	private String outCompId; //동행자 사원번호v 
	private String outChildNo; //육아외출자녀번호v 
	private String outPlace; //행선지v
	private String outNote; //용무v
	private String regDate;//신청일자
	private String postNM;//직급
	private String Dptnm; //부서
	private String procClass; //신청서유형
	
	
	public String getProcClass() {
		return procClass;
	}
	public void setProcClass(String procClass) {
		this.procClass = procClass;
	}
	public String getPostNM() {
		return postNM;
	}
	public void setPostNM(String postNM) {
		this.postNM = postNM;
	}
	public String getDptnm() {
		return Dptnm;
	}
	public void setDptnm(String dptnm) {
		Dptnm = dptnm;
	}
	public String getEmpNm() {
		return empNm;
	}
	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getCodeAttend() {
		return codeAttend;
	}
	public void setCodeAttend(String codeAttend) {
		this.codeAttend = codeAttend;
	}
	public String getOutDate() {
		return outDate;
	}
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	public String getOutPlace() {
		return outPlace;
	}
	public void setOutPlace(String outPlace) {
		this.outPlace = outPlace;
	}
	public String getOutNote() {
		return outNote;
	}
	public void setOutNote(String outNote) {
		this.outNote = outNote;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getOutStTime() {
		return outStTime;
	}
	public void setOutStTime(String outStTime) {
		this.outStTime = outStTime;
	}
	public String getOutEdTime() {
		return outEdTime;
	}
	public void setOutEdTime(String outEdTime) {
		this.outEdTime = outEdTime;
	}
	public String getOutCompId() {
		return outCompId;
	}
	public void setOutCompId(String outCompId) {
		this.outCompId = outCompId;
	}
	public String getOutChildNo() {
		return outChildNo;
	}
	public void setOutChildNo(String outChildNo) {
		this.outChildNo = outChildNo;
	}
	
	
	

	

}
