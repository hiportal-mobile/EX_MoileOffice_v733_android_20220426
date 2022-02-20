package com.ex.group.elecappmemo;

import android.os.Bundle;

import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.common.vpn.SGVPNConnection;

public class SKTAction {

/*
    public SKTAction(String primitive, boolean pendding) {
        if (SKTActivity.vpnConn == null)
            SKTActivity.vpnConn = SGVPNConnection.getInstance();
        int status = SKTActivity.vpnConn.getStatus();
        Log.i("SKTActivity", "new Action vpn status=====>>>" + SKTActivity.vpnConn.getStatus());
        if (SKTActivity.this.dialog == null)
            if (SKTActivity.this.getParent() != null) {
                Log.i("SKTActivity", "dialog getParent");
                SKTActivity.this.dialog = new ExProgress((Context)SKTActivity.this.getParent());
                SKTActivity.this.dialog.setOwnerActivity(SKTActivity.this.getParent());
            } else {
                Log.i("SKTActivity", "dialog SKTActivity");
                SKTActivity.this.dialog = new ExProgress((Context)SKTActivity.this);
                SKTActivity.this.dialog.setOwnerActivity(SKTActivity.this);
            }
        Resources res = SKTActivity.this.getResources();
        String b_primitive = primitive;
        if ("_HISTORY_".equals(primitive))
            b_primitive = SKTActivity.this.b_parameters.getPrimitive();
        this.pendding = pendding;
        int resId = SKTActivity.this.onActionPre(b_primitive);
        if (pendding)
            switch (resId) {
                case 0:
                    this.serviceMessage = Resource.getString((Context)SKTActivity.this, "RES_RETRIEVING");
                    break;
                case 1:
                    this.serviceMessage = Resource.getString((Context)SKTActivity.this, "RES_SENDING");
                    break;
                default:
                    this.serviceMessage = res.getString(resId);
                    break;
            }
        this.primitive = primitive;
    }*/
}
