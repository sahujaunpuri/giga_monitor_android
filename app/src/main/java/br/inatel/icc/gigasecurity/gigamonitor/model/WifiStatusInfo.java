package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.util.Log;
import com.basic.G;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.javaclass.SDK_WifiStatusInfo;
import static junit.framework.Assert.*;

/**
 * File: WifiStatusInfo.java
 * Creation date: 10/09/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class WifiStatusInfo.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class WifiStatusInfo extends SDK_WifiStatusInfo{

    private static String TAG = WifiStatusInfo.class.getSimpleName();
    int connectStatus;
    int strength;
    String rev;

    public WifiStatusInfo() {}

    public WifiStatusInfo(int connectStatus, int strength, String rev) {

        this.connectStatus = connectStatus;
        this.strength = strength;
        this.rev = rev;
    }

    public int getConnectStatus() {

        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
