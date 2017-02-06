//package br.inatel.icc.gigasecurity.gigamonitor.config.upnp;
//
//import com.basic.G;
//import com.xm.MyConfig;
//import com.xm.javaclass.SDK_NetUPNPConfig;
//
//import java.io.Serializable;
//
//import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;
//
///**
// * File: WifiConfig.java
// * Creation date: 06/10/2014
// * Author: rinaldo.bueno
// * <p/>
// * Purpose: Declaration of class WifiConfig.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class UpnpConfig extends Config2Abstract<SDK_NetUPNPConfig> implements Serializable{
//
//    public boolean mEnable;
//    public boolean mState;
//    public String  mArg0;
//    public int mHTTPPort;
//    public int mMediaPort;
//    public int mMobliePort;
//
//    public UpnpConfig() {
//    }
//
//    public UpnpConfig(boolean mEnable, boolean mState, String mArg0, int mHTTPPort, int mMediaPort, int mMobliePort) {
//        this.mEnable = mEnable;
//        this.mState = mState;
//        this.mArg0 = mArg0;
//        this.mHTTPPort = mHTTPPort;
//        this.mMediaPort = mMediaPort;
//        this.mMobliePort = mMobliePort;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP;
//    }
//
//    @Override
//    public Class<SDK_NetUPNPConfig> getConfigType() {
//        return SDK_NetUPNPConfig.class;
//    }
//
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_NetUPNPConfig sdkNetWifiConfig){
//        setEnable(sdkNetWifiConfig.st_0_bEnable);
//        setState(sdkNetWifiConfig.st_1_bState);
//
//        setArg0(G.ToString(sdkNetWifiConfig.st_2_arg0));
//
//        setHTTPPort(sdkNetWifiConfig.st_3_iHTTPPort);
//        setMediaPort(sdkNetWifiConfig.st_4_iMediaPort);
//        setMobliePort(sdkNetWifiConfig.st_5_iMobliePort);
//    }
//
//    @Override
//    public SDK_NetUPNPConfig getAsNetSdkConfig(){
//        SDK_NetUPNPConfig sdkNetConfig = new SDK_NetUPNPConfig();
//
//        sdkNetConfig.st_0_bEnable = isEnable();
//        sdkNetConfig.st_1_bState = isState();
//
//        G.SetValue(sdkNetConfig.st_2_arg0, getArg0());
//
//        sdkNetConfig.st_3_iHTTPPort = getHTTPPort();
//        sdkNetConfig.st_4_iMediaPort = getMediaPort();
//        sdkNetConfig.st_5_iMobliePort = getMobliePort();
//
//        return sdkNetConfig;
//    }
//
//    public boolean isEnable() {
//        return mEnable;
//    }
//
//    public void setEnable(boolean mEnable) {
//        this.mEnable = mEnable;
//    }
//
//    public boolean isState() {
//        return mState;
//    }
//
//    public void setState(boolean mState) {
//        this.mState = mState;
//    }
//
//    public String getArg0() {
//        return mArg0;
//    }
//
//    public void setArg0(String mArg0) {
//        this.mArg0 = mArg0;
//    }
//
//    public int getHTTPPort() {
//        return mHTTPPort;
//    }
//
//    public void setHTTPPort(int mHTTPPort) {
//        this.mHTTPPort = mHTTPPort;
//    }
//
//    public int getMediaPort() {
//        return mMediaPort;
//    }
//
//    public void setMediaPort(int mMediaPort) {
//        this.mMediaPort = mMediaPort;
//    }
//
//    public int getMobliePort() {
//        return mMobliePort;
//    }
//
//    public void setMobliePort(int mMobliePort) {
//        this.mMobliePort = mMobliePort;
//    }
//
//
//}
