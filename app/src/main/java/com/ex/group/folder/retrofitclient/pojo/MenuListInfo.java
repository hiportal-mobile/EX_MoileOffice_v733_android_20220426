package com.ex.group.folder.retrofitclient.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MenuListInfo {
    @SerializedName("userId")
    String userId;
    @SerializedName("deptCd")
    String deptCd;
    @SerializedName("mealStDt")
    String mealStDt;

    @SerializedName("mealEndDt")
    String mealEndDt;

    //response

    @SerializedName("result")
    public String result;

    @SerializedName("resultMsg")
    public String resultMsg;

    @SerializedName("menuList")
    public List<MenuList> menuList = new ArrayList<>();


    //implements Comparable<AppInfoList>
    public static class MenuList implements Comparable{
        @SerializedName("groupCd")
        String groupCd;
        @SerializedName("breakfast1")
        String breakfast1;
        @SerializedName("breakfast2")
        String breakfast2;
        @SerializedName("launch1")
        String launch1;
        @SerializedName("launch2")
        String launch2;
        @SerializedName("dinner1")
        String dinner1;
        @SerializedName("dinner2")
        String dinner2;
        @SerializedName("mealDt")
        String mealDt;

        @SerializedName("runYN")
        String runYN="Y";

        public String getRunYN() {
            return runYN;
        }

        public void setRunYN(String runYN) {
            this.runYN = runYN;
        }

        public String getMealDt() {
            return mealDt;
        }

        public void setMealDt(String mealDt) {
            this.mealDt = mealDt;
        }

        public String getGroupCd() {
            return groupCd;
        }

        public void setGroupCd(String groupCd) {
            this.groupCd = groupCd;
        }

        public String getBreakfast1() {
            return breakfast1;
        }

        public void setBreakfast1(String breakfast1) {
            this.breakfast1 = breakfast1;
        }

        public String getBreakfast2() {
            return breakfast2;
        }

        public void setBreakfast2(String breakfast2) {
            this.breakfast2 = breakfast2;
        }

        public String getLaunch1() {
            return launch1;
        }

        public void setLaunch1(String launch1) {
            this.launch1 = launch1;
        }

        public String getLaunch2() {
            return launch2;
        }

        public void setLaunch2(String launch2) {
            this.launch2 = launch2;
        }

        public String getDinner1() {
            return dinner1;
        }

        public void setDinner1(String dinner1) {
            this.dinner1 = dinner1;
        }

        public String getDinner2() {
            return dinner2;
        }

        public void setDinner2(String dinner2) {
            this.dinner2 = dinner2;
        }

        @Override
        public int compareTo(@NonNull Object o) {
        /*AppInfoList target = appInfoList;
        int targetDownCount =Integer.parseInt(target.getDownCount());
        if(Integer.parseInt(this.downCount)<targetDownCount){
            return -1;
        }else if(Integer.parseInt(this.downCount)>targetDownCount){
            return 1;
        }else{
            return 0;
        }*/
            MenuList target = (MenuList) o;
            int targetDate = Integer.parseInt(target.getMealDt());
            if(Integer.parseInt(this.mealDt)<targetDate){return -1;}
            else if(Integer.parseInt(this.mealDt)>targetDate){return 1;}
            else{return 0;}

        }
    }


    public MenuListInfo(String userId, String deptCd, String mealStDt, String mealEndDt) {
        this.userId = userId;
        this.deptCd = deptCd;
        this.mealStDt = mealStDt;
        this.mealEndDt = mealEndDt;

    }


}
