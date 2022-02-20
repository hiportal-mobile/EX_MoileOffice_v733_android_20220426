package com.ex.group.approval.easy.util;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eunjy on 2018-10-26.
 */

public class RetrieveAppInfo {
    @SerializedName("resultMsg")
    public String resultMsg;
    public String result;
    public String storeVer;
    public List<Info> appInfoList = new ArrayList<>();

    public String getResultMsg() {
        return resultMsg;
    }

    public String getResult() {
        return result;
    }

    public List<Info> getAppInfoList() {
        return appInfoList;
    }


    public class Info {

        public String fileSize;
        public String hybridYn;
        public String installUrl;
        public String appNm;
        public String appId;
        public String appVer;
        public String downCount;
        public String packageNm;
        public String introTitle;
        public String iconUrl;
        public String hybridUrl;
        public String appType;
        public String vpnYn;
        public String mdn;

        public String getFileSize() {
            return fileSize;
        }

        public String getHybridYn() {
            return hybridYn;
        }

        public String getInstallUrl() {
            return installUrl;
        }

        public String getAppNm() {
            return appNm;
        }

        public String getAppId() {
            return appId;
        }

        public String getAppVer() {
            return appVer;
        }

        public String getDownCount() {
            return downCount;
        }

        public String getPackageNm() {
            return packageNm;
        }

        public String getIntroTitle() {
            return introTitle;
        }

        public void setIntroTitle(String introTitle) {
            this.introTitle = introTitle;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public String getHybridUrl() {
            return hybridUrl;
        }

        public String getAppType() {
            return appType;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }

        public String getVpnYn() {
            return vpnYn;
        }

        public void setVpnYn(String vpnYn) {
            this.vpnYn = vpnYn;
        }

        public String getMdn() {
            return mdn;
        }

        public void setMdn(String mdn) {
            this.mdn = mdn;
        }
    }



}
