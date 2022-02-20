package com.ex.group.folder.retrofitclient.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ElecMemoAlarmInfo {

    //request
    @SerializedName("userId")
    String userId;


    //response
    @SerializedName("result")
    public String result;

    @SerializedName("resultMessage")
    public String resultMessage;

    @SerializedName("cnt")
    public String cnt;

    @SerializedName("elecCnt")
    public String elecCnt;

    @SerializedName("memoCnt")
    public String memoCnt;

    public ElecMemoAlarmInfo(String userId){
        this.userId= userId;
    }





}
