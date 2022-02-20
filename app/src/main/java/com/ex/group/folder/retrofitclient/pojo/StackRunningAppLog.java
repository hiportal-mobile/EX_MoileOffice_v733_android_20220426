package com.ex.group.folder.retrofitclient.pojo;

import com.google.gson.annotations.SerializedName;

public class StackRunningAppLog {

    //request params;


    @SerializedName("userId")
    String userId;
    @SerializedName("deviceNm")
    String deviceNm;
    @SerializedName("mdn")
    String mdn;
    @SerializedName("appId")
    String appId;
    @SerializedName("deviceGubun")
    String deviceGubun;
    @SerializedName("userType")
    String userType;

    public StackRunningAppLog(String userId, String deviceNm, String mdn, String appId, String deviceGubun, String userType){

        this.userId =userId;
        this.deviceNm =deviceNm;
        this.mdn = mdn;
        this.appId = appId;
        this.deviceGubun = deviceGubun; //제조사 이름
        this.userType = userType;

    }

    @SerializedName("result")
    public String result;
    @SerializedName("resultMsg")
    public String resultMsg;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
