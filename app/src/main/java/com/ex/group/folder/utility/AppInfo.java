package com.ex.group.folder.utility;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by eunjy on 2018-10-18.
 */

public class AppInfo {

    Context context;
    AppInfo appInfo;
    public String essentialGubun;
    public String appType;
    public String vpnYn;
    public String appId;
    public String packageName;
    public String appName;
    public String apkName;
    public String versionName;
    public String appCondition;
    public int appConditionCode;
    public int appConditionImg;
    public String appDownloadUrl;
    public String appIconUrl;
    public Drawable appIcon;
    public String introTitle;
    public String fileSize;
    public String hybridYn;
    public String hybridUrl;
    public String downCount;
    public String deleteApp;
    public String flag;
    public String flagDetail;


    public AppInfo(Context context){
        context = this.context;
    }

    public AppInfo(String appId, String appName, String packageName, String versionName){
        this.appId = appId;
        this.appName = appName;
        this.packageName = packageName;
        this.versionName = versionName;
    }

    public AppInfo(String appId, String appName, String packageName, String versionName, String appCondition, int appConditionCode){
        this.appId = appId;
        this.appName = appName;
        this.packageName = packageName;
        this.versionName = versionName;
        this.appCondition = appCondition;
        this.appConditionCode = appConditionCode;

    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public String getEssentialGubun() {
        return essentialGubun;
    }

    public void setEssentialGubun(String essentialGubun) {
        this.essentialGubun = essentialGubun;
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


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }


    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }


    public String getAppCondition() {
        return appCondition;
    }

    public void setAppCondition(String appCondition) {
        this.appCondition = appCondition;
    }


    public int getAppConditionCode() {
        return appConditionCode;
    }

    public void setAppConditionCode(int appConditionCode) {
        this.appConditionCode = appConditionCode;
    }


    public int getAppConditionImg() {
        return appConditionImg;
    }

    public void setAppConditionImg(int appConditionImg) {
        this.appConditionImg = appConditionImg;
    }


    public String getAppDownloadUrl() {
        return appDownloadUrl;
    }

    public void setAppDownloadUrl(String appDownloadUrl) {
        this.appDownloadUrl = appDownloadUrl;
    }


    public String getAppIconUrl() {
        return appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }


    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }


    public String getIntroTitle() {
        return introTitle;
    }

    public void setIntroTitle(String introTitle) {
        this.introTitle = introTitle;
    }


    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public String getHybridYn() {
        return hybridYn;
    }

    public void setHybridYn(String hybridYn) {
        this.hybridYn = hybridYn;
    }


    public String getHybridUrl() {
        return hybridUrl;
    }

    public void setHybridUrl(String hybridUrl) {
        this.hybridUrl = hybridUrl;
    }


    public String getDownCount() {
        return downCount;
    }

    public void setDownCount(String downCount) {
        this.downCount = downCount;
    }


    public String getDeleteApp() {
        return deleteApp;
    }

    public void setDeleteApp(String deleteApp) {
        this.deleteApp = deleteApp;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }


    public String getFlagDetail() {
        return flagDetail;
    }

    public void setFlagDetail(String flagDetail) {
        this.flagDetail = flagDetail;
    }
}
