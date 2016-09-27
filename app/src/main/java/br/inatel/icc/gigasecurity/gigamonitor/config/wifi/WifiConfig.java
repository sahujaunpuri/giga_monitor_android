package br.inatel.icc.gigasecurity.gigamonitor.config.wifi;

import com.xm.MyConfig;
import com.xm.SDK_NetWifiConfig;

import java.io.Serializable;

import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;

/**
 * File: WifiConfig.java
 * Creation date: 06/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class WifiConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class WifiConfig extends ConfigAbstract<SDK_NetWifiConfig> implements Serializable{

    public boolean mEnable;
    public String mSSID;
    public String mNetType;
    public String mEncrypType;
    public String mAuth;
    public int mKeyType;
    public String mKeys;
    public String mHostIP;
    public String mSubmask;
    public String mGateway;
    public int mChannel;

    WifiAPListConfig wifiAPListConfig;

    public WifiConfig() {
    }

    public WifiConfig(boolean mEnable, String mSSID, int mChannel, String mNetType, String mEncrypType, String mAuth, int mKeyType, String mKeys, String mHostIP, String mSubmask, String mGateway) {
        this.mEnable = mEnable;
        this.mSSID = mSSID;
        this.mChannel = mChannel;
        this.mNetType = mNetType;
        this.mEncrypType = mEncrypType;
        this.mAuth = mAuth;
        this.mKeyType = mKeyType;
        this.mKeys = mKeys;
        this.mHostIP = mHostIP;
        this.mSubmask = mSubmask;
        this.mGateway = mGateway;
    }

    @Override
    public long getConfigKey() {
        return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI;
    }

    @Override
    public Class<SDK_NetWifiConfig> getConfigType() {
        return SDK_NetWifiConfig.class;
    }


    @Override
    public SDK_NetWifiConfig getAsNetSdkConfig() {
        mConfig.bEnable = isEnable();
        mConfig.Gateway = getGateway();
        mConfig.HostIP = getHostIP();
        mConfig.nKeyType = getKeyType();
        mConfig.nChannel = getChannel();
        mConfig.sAuth = getAuth();
        mConfig.sEncrypType = getEncrypType();
        mConfig.sKeys = getKeys();
        mConfig.sSSID = getSSID();
        mConfig.sNetType = getNetType();
        mConfig.Submask = getSubmask();

        return mConfig;
    }

    public void fillFromWifiItem(WifiAPListConfig.WifiItem config) {
        mEnable = true;
        mChannel = config.getChannel();
        mAuth = config.getAuth();
        mEncrypType = config.mEncrypType;
        mSSID = config.mSSSID;
        mNetType = config.getNetType();

    }

    @Override
    public void fillFromNetSdkConfig(SDK_NetWifiConfig config) {

        mEnable = config.bEnable;
        mHostIP = config.HostIP ;
        mGateway = config.Gateway;
        mChannel = config.nChannel;
        mKeyType = config.nKeyType;
        mAuth = config.sAuth;
        mEncrypType = config.sEncrypType;
        mKeys = config.sKeys;
        mSSID = config.sSSID;
        mSubmask = config.Submask;
        mNetType = config.sNetType;

    }

    public WifiAPListConfig getWifiAPListConfig() {
        if(wifiAPListConfig == null){
            wifiAPListConfig = new WifiAPListConfig();
        }
        return wifiAPListConfig;
    }

    public boolean isEnable() {
        return mEnable;
    }

    public WifiConfig setEnable(boolean enable) {
        this.mEnable = enable;
        return this;
    }

    public String getSSID() {
        return mSSID;
    }

    public WifiConfig setSSID(String ssid) {
        this.mSSID = ssid;
        return this;
    }

    public int getChannel() {
        return mChannel;
    }

    public WifiConfig setChannel(int channel) {
        this.mChannel = channel;
        return this;
    }

    public String getNetType() {
        return mNetType;
    }

    public WifiConfig setNetType(String netType) {
        this.mNetType = netType;
        return this;
    }

    public String getEncrypType() {
        return mEncrypType;
    }

    public WifiConfig setEncrypType(String encrypType) {
        this.mEncrypType = encrypType;
        return this;
    }

    public String getAuth() {
        return mAuth;
    }

    public WifiConfig setAuth(String auth) {
        this.mAuth = auth;
        return this;
    }

    public int getKeyType() {
        return mKeyType;
    }

    public WifiConfig setKeyType(int keyType) {
        this.mKeyType = keyType; return this;
    }

    public String getKeys() {
        return mKeys;
    }

    public WifiConfig setKeys(String keys) {
        this.mKeys = keys; return this;
    }

    public String getHostIP() {
        return mHostIP;
    }

    public WifiConfig setHostIP(String hostIP) {
        this.mHostIP = hostIP; return this;
    }

    public String getSubmask() {
        return mSubmask;
    }

    public WifiConfig setSubmask(String submask) {
        this.mSubmask = submask; return this;
    }

    public String getGateway() {
        return mGateway;
    }

    public WifiConfig setGateway(String gateway) {
        this.mGateway = gateway; return this;
    }


}
