package br.inatel.icc.gigasecurity.gigamonitor.model;

/**
 * File: NetWifiApList.java
 * Creation date: 10/09/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class NetWifiDeviceAll.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class NetWifiDeviceAll {

    public static final int NET_MAX_AP_NUMBER = 200;

    NetWifiDevice netWifiDeviceAll[] = new NetWifiDevice[NET_MAX_AP_NUMBER];

    public NetWifiDeviceAll() {

    }

    public NetWifiDevice[] getNetWifiDeviceAll() {
        return netWifiDeviceAll;
    }

    public void setNetWifiDeviceAll(NetWifiDevice[] netWifiDeviceAll) {
        this.netWifiDeviceAll = netWifiDeviceAll;
    }
}
