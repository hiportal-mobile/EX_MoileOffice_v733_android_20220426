package com.ex.group.approval.easy.addressbook.data;

import java.util.Date;

public class OutEmpInfoVo {

	public OutEmpInfoVo() {
	}

	public String empId;
	public String empNm;
	public String dptNm;
	public String empGradeNm;
	public String formCode;
	public String postNm;
	public String offiOutAccTime;
	private Date fromDate, untilDate, targetDate, fromTime, untilTime;
	private String childNmList; // 자녀 이름 목록
	private String childNoList; // 자녀 주민번호 목록
	private String childBirthList; // 자녀 생일 목록

	public String getChildNoList() {
		return childNoList;
	}

	public void setChildNoList(String childNoList) {
		this.childNoList = childNoList;
	}

	public String getChildBirthList() {
		return childBirthList;
	}

	public void setChildBirthList(String childBirthList) {
		this.childBirthList = childBirthList;
	}

	public String getChildNmList() {
		return childNmList;
	}

	public void setChildNmList(String childNmList) {
		this.childNmList = childNmList;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}

	public Date getUntilDate() {
		return this.untilDate;
	}

	public void setUntilTime(Date untilTime) {
		this.untilTime = untilTime;
	}

	public Date getUntilTime() {
		return this.untilTime;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public Date getTargetDate() {
		return this.targetDate;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getFromTime() {
		return this.fromTime;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpNm() {
		return empNm;
	}

	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}

	public String getDptNm() {
		return dptNm;
	}

	public void setDptNm(String dptNm) {
		this.dptNm = dptNm;
	}

	public String getEmpGradeNm() {
		return empGradeNm;
	}

	public void setEmpGradeNm(String empGradeNm) {
		this.empGradeNm = empGradeNm;
	}

	public String getPostNm() {
		return postNm;
	}

	public void setPostNm(String postNm) {
		this.postNm = postNm;
	}

	public String getOffiOutAccTime() {
		return offiOutAccTime;
	}

	public void setOffiOutAccTime(String offiOutAccTime) {
		this.offiOutAccTime = offiOutAccTime;
	}

}
