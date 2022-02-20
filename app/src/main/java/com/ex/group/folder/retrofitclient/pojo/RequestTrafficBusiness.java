package com.ex.group.folder.retrofitclient.pojo;

import com.google.gson.annotations.SerializedName;

public class RequestTrafficBusiness {
    @SerializedName("userId")
    String userId;
    @SerializedName("salesDt")
    String salesDt;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSalesDt() {
        return salesDt;
    }

    public void setSalesDt(String salesDt) {
        this.salesDt = salesDt;
    }








    //response
    @SerializedName("reportDt")
    public String reportDt;
    @SerializedName("yesterdayCnt")
    public String yesterdayCnt;
    @SerializedName("lastYearCnt")
    public String lastYearCnt;
    @SerializedName("nowCnt")
    public String nowCnt;
    @SerializedName("increasePt")
    public String increasePt;
    @SerializedName("yesterdaySales")
    public String yesterdaySales;
    @SerializedName("yesterdayTraffic")
    public String yseterdayTraffic;
    @SerializedName("hipassUseRate")
    public  String hipassUseRate;
    @SerializedName("result")
    public String result;
    @SerializedName("resultMsg")
    public String resultMsg;




    public String getReportDt() {
        return reportDt;
    }

    public void setReportDt(String reportDt) {
        this.reportDt = reportDt;
    }

    public String getYesterdayCnt() {
        return yesterdayCnt;
    }

    public void setYesterdayCnt(String yesterdayCnt) {
        this.yesterdayCnt = yesterdayCnt;
    }

    public String getLastYearCnt() {
        return lastYearCnt;
    }

    public void setLastYearCnt(String lastYearCnt) {
        this.lastYearCnt = lastYearCnt;
    }

    public String getNowCnt() {
        return nowCnt;
    }

    public void setNowCnt(String nowCnt) {
        this.nowCnt = nowCnt;
    }

    public String getIncreasePt() {
        return increasePt;
    }

    public void setIncreasePt(String increasePt) {
        this.increasePt = increasePt;
    }

    public String getYesterdaySales() {
        return yesterdaySales;
    }

    public void setYesterdaySales(String yesterdaySales) {
        this.yesterdaySales = yesterdaySales;
    }

    public String getYseterdayTraffic() {
        return yseterdayTraffic;
    }

    public void setYseterdayTraffic(String yseterdayTraffic) {
        this.yseterdayTraffic = yseterdayTraffic;
    }

    public String getHipassUseRate() {
        return hipassUseRate;
    }

    public void setHipassUseRate(String hipassUseRate) {
        this.hipassUseRate = hipassUseRate;
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



    public RequestTrafficBusiness(String userId, String salesDt){
        this.userId= userId;
        this.salesDt =salesDt;
    }
}
