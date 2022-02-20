package com.ex.group.folder.retrofitclient.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MemoNoticePopInfo {

    //request
    @SerializedName("userId")
    String userId;

    //response
    @SerializedName("result")
    public String result;

    @SerializedName("resultMessage")
    public String resultMessage;


    @SerializedName("data")
    public List<NoticeData> data = new ArrayList<>();

    @SerializedName("attfl")
    public List<AttflData> attfl = new ArrayList<>();


    public String getResult() {
        return result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public List<NoticeData> getData() {
        return data;
    }

    public static class NoticeData {

        @SerializedName("ATTFL_SEQ")
        String ATTFL_SEQ;  //첨부파일 일련번호

        @SerializedName("NOTC_CLSS_CD")
        String NOTC_CLSS_CD;  //공지구분코드 ( 01:일반 , 02:긴급 )

        @SerializedName("BLTN_END_DATES")
        String BLTN_END_DATES;  //게시종료일

        @SerializedName("NOTC_MATR_TITL_NM")
        String NOTC_MATR_TITL_NM;  //제목

        @SerializedName("MEMO_NOTC_MATR_SEQ")
        String MEMO_NOTC_MATR_SEQ;  //공지사항 일련번호

        @SerializedName("NOTC_MATR_CTNT")
        String NOTC_MATR_CTNT;  //내용

        @SerializedName("BLTN_END_HMS")
        String BLTN_END_HMS;  //게시종료시간(HH:mm:ss)

        @SerializedName("BLTN_STRT_DATES")
        String BLTN_STRT_DATES;  //게시시작일

        @SerializedName("BLTN_STRT_HMS")
        String BLTN_STRT_HMS;  //게시시작시간(HH:mm:ss)

        @SerializedName("NOTC_MATR_DEL_YN")
        String NOTC_MATR_DEL_YN;  //삭제여부

        public String getATTFL_SEQ() {
            return ATTFL_SEQ;
        }
        public void setATTFL_SEQ(String ATTFL_SEQ) {
            this.ATTFL_SEQ = ATTFL_SEQ;
        }

        public String getNOTC_CLSS_CD() {
            return NOTC_CLSS_CD;
        }
        public void setNOTC_CLSS_CD(String NOTC_CLSS_CD) {
            this.NOTC_CLSS_CD = NOTC_CLSS_CD;
        }

        public String getBLTN_END_DATES() {
            return BLTN_END_DATES;
        }
        public void setBLTN_END_DATES(String BLTN_END_DATES) {
            this.BLTN_END_DATES = BLTN_END_DATES;
        }

        public String getNOTC_MATR_TITL_NM() {
            return NOTC_MATR_TITL_NM;
        }
        public void setNOTC_MATR_TITL_NM(String NOTC_MATR_TITL_NM) {
            this.NOTC_MATR_TITL_NM = NOTC_MATR_TITL_NM;
        }

        public String getMEMO_NOTC_MATR_SEQ() {
            return MEMO_NOTC_MATR_SEQ;
        }
        public void setMEMO_NOTC_MATR_SEQ(String MEMO_NOTC_MATR_SEQ) {
            this.MEMO_NOTC_MATR_SEQ = MEMO_NOTC_MATR_SEQ;
        }

        public String getNOTC_MATR_CTNT() {
            return NOTC_MATR_CTNT;
        }
        public void setNOTC_MATR_CTNT(String NOTC_MATR_CTNT) {
            this.NOTC_MATR_CTNT = NOTC_MATR_CTNT;
        }

        public String getBLTN_END_HMS() {
            return BLTN_END_HMS;
        }
        public void setBLTN_END_HMS(String BLTN_END_HMS) {
            this.BLTN_END_HMS = BLTN_END_HMS;
        }

        public String getBLTN_STRT_DATES() {
            return BLTN_STRT_DATES;
        }
        public void setBLTN_STRT_DATES(String BLTN_STRT_DATES) {
            this.BLTN_STRT_DATES = BLTN_STRT_DATES;
        }

        public String getBLTN_STRT_HMS() {
            return BLTN_STRT_HMS;
        }
        public void setBLTN_STRT_HMS(String BLTN_STRT_HMS) {
            this.BLTN_STRT_HMS = BLTN_STRT_HMS;
        }

        public String getNOTC_MATR_DEL_YN() {
            return NOTC_MATR_DEL_YN;
        }
        public void setNOTC_MATR_DEL_YN(String NOTC_MATR_DEL_YN) {
            this.NOTC_MATR_DEL_YN = NOTC_MATR_DEL_YN;
        }

    }

    public static class AttflData {
        @SerializedName("ATTFL_SEQ")
        String ATTFL_SEQ;  //첨부파일 일련번호

        @SerializedName("ATTFL_NM")
        String ATTFL_NM;  //첨부파일명

        @SerializedName("DOWN_PATH_IOS")
        String DOWN_PATH_IOS;  //아이폰 문서변환 url

        @SerializedName("DOWN_PATH_AND")
        String DOWN_PATH_AND;  //안드로이드 문서변환 url

        @SerializedName("ATTFL_PATH")
        String ATTFL_PATH;  //첨부파일 경로

        @SerializedName("ORTX_FLNM")
        String ORTX_FLNM;  //첨부파일 원본명

        @SerializedName("ATTFL_SQNO")
        String ATTFL_SQNO;  //순번

        @SerializedName("ATCM_CLSS_CD")
        String ATCM_CLSS_CD;  //첨부파일구분( N:공지사항 , M:메모보고 )

        public AttflData(String ortxFlnm) {
            this.ORTX_FLNM = ortxFlnm;
        }

        public String getATTFL_SEQ() {
            return ATTFL_SEQ;
        }

        public void setATTFL_SEQ(String ATTFL_SEQ) {
            this.ATTFL_SEQ = ATTFL_SEQ;
        }

        public String getATTFL_NM() {
            return ATTFL_NM;
        }

        public void setATTFL_NM(String ATTFL_NM) {
            this.ATTFL_NM = ATTFL_NM;
        }

        public String getDOWN_PATH_IOS() {
            return DOWN_PATH_IOS;
        }

        public void setDOWN_PATH_IOS(String DOWN_PATH_IOS) {
            this.DOWN_PATH_IOS = DOWN_PATH_IOS;
        }

        public String getDOWN_PATH_AND() {
            return DOWN_PATH_AND;
        }

        public void setDOWN_PATH_AND(String DOWN_PATH_AND) {
            this.DOWN_PATH_AND = DOWN_PATH_AND;
        }

        public String getATTFL_PATH() {
            return ATTFL_PATH;
        }

        public void setATTFL_PATH(String ATTFL_PATH) {
            this.ATTFL_PATH = ATTFL_PATH;
        }

        public String getORTX_FLNM() {
            return ORTX_FLNM;
        }

        public void setORTX_FLNM(String ORTX_FLNM) {
            this.ORTX_FLNM = ORTX_FLNM;
        }

        public String getATTFL_SQNO() {
            return ATTFL_SQNO;
        }

        public void setATTFL_SQNO(String ATTFL_SQNO) {
            this.ATTFL_SQNO = ATTFL_SQNO;
        }

        public String getATCM_CLSS_CD() {
            return ATCM_CLSS_CD;
        }

        public void setATCM_CLSS_CD(String ATCM_CLSS_CD) {
            this.ATCM_CLSS_CD = ATCM_CLSS_CD;
        }




    }

    public MemoNoticePopInfo(String userId){
        this.userId= userId;
    }





}
