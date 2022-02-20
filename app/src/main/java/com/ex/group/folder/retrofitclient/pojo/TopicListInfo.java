package com.ex.group.folder.retrofitclient.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TopicListInfo {
    @SerializedName("userId")
    String userId;


    //response

    @SerializedName("result")
    public String result;

    @SerializedName("resultMsg")
    public String resultMsg;

    @SerializedName("newsList")
    public List<NewsList> newsList = new ArrayList<>();

    //
    public class NewsList{

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getServiceDt() {
            return serviceDt;
        }

        public void setServiceDt(String serviceDt) {
            this.serviceDt = serviceDt;
        }

        public String getContentsUrl() {
            return contentsUrl;
        }

        public void setContentsUrl(String contentsUrl) {
            this.contentsUrl = contentsUrl;
        }

        @SerializedName("title")
        String title;
        @SerializedName("serviceDt")
        String serviceDt;
        @SerializedName("contentsUrl")
        String contentsUrl;
    }

    public TopicListInfo(String urserId){
        this.userId = userId;
    }



}
