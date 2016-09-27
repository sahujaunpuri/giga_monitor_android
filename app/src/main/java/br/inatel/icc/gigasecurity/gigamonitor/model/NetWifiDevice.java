package br.inatel.icc.gigasecurity.gigamonitor.model;

/**
 * File: NetWifiDevice.java
 * Creation date: 09/09/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class NetWifiDevice.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class NetWifiDevice {

    String ssid;
    int rssi; //SEE SDK_RSSI_SINGNAL
    int channel;
    String netType; //Infra, Adhoc
    String encrypType; //NONE, WEP, TKIP, AES
    String auth; //OPEN, SHARED, WEPAUTO, WPAPSK, WPA2PSK, WPANONE, WPA, WPA2

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getEncrypType() {
        return encrypType;
    }

    public void setEncrypType(String encrypType) {
        this.encrypType = encrypType;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
