package com.ex.group.approval.easy.addressbook.data;

import java.util.ArrayList;

//대분류
public class GuntaeCdVo {

	public GuntaeCdVo() {
	}
	private String attendCd; // 근태코드
	private String attendNm; // 근태명 (한도일수)
	private String pkAttendNm; // 근태명
	private String limmitDCnt; // 한도일수
	private String childAttendYn; // 하위 근태 존재여부
	private String anlEndYm; // 연차 종료년월
	private int guntaeCategory;//분류구분

	public int getGuntaeCategory() {
		return guntaeCategory;
	}
	public void setGuntaeCategory(int guntaeCategory) {
		this.guntaeCategory = guntaeCategory;
	}
	public String getAttendCd() {
		return attendCd;
	}
	public void setAttendCd(String attendCd) {
		this.attendCd = attendCd;
	}
	public String getAttendNm() {
		return attendNm;
	}
	public void setAttendNm(String attendNm) {
		this.attendNm = attendNm;
	}
	public String getPkAttendNm() {
		return pkAttendNm;
	}
	public void setPkAttendNm(String pkAttendNm) {
		this.pkAttendNm = pkAttendNm;
	}
	public String getLimmitDCnt() {
		return limmitDCnt;
	}
	public void setLimmitDCnt(String limmitDCnt) {
		this.limmitDCnt = limmitDCnt;
	}
	public String getChildAttendYn() {
		return childAttendYn;
	}
	public void setChildAttendYn(String childAttendYn) {
		this.childAttendYn = childAttendYn;
	}
	public String getAnlEndYm() {
		return anlEndYm;
	}
	public void setAnlEndYm(String anlEndYm) {
		this.anlEndYm = anlEndYm;
	}
}
