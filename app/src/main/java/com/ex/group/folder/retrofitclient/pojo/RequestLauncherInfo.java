package com.ex.group.folder.retrofitclient.pojo;

import com.google.gson.annotations.SerializedName;

public class RequestLauncherInfo {


    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

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

    @SerializedName("appVer")
String appVer;
@SerializedName("result")
String result;
@SerializedName("resulMsg")
String resultMsg;

    public RequestLauncherInfo(){

    }
}
