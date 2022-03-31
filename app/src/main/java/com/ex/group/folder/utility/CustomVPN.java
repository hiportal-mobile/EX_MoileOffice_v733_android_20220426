
package com.ex.group.folder.utility;


import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.skt.pe.common.vpn.SGVPNConnection;

public class CustomVPN{
   private IBinder binder;
   private SGVPNConnection vpnConn;
   private Intent intent;

    public void setBinder(IBinder binder) {
        this.binder = binder;
    }

    public IBinder getBinder() {
        return binder;
    }

    public void setVpnConn(SGVPNConnection vpnConn) {
        this.vpnConn = vpnConn;
    }

    public SGVPNConnection getVpnConn(){
        return vpnConn;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    private static CustomVPN instance = null;
    public static  synchronized CustomVPN getInstance(){
        if(null == instance){
            instance = new CustomVPN();
        }
        return instance;
    }

}
