//package br.inatel.icc.gigasecurity.gigamonitor.config.cloud;
//
//import com.basic.G;
//import com.xm.MyConfig;
//import com.xm.javaclass.SDK_NatConfig;
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
//public class CloudConfig extends Config2Abstract<SDK_NatConfig> implements Serializable{
//
//    public boolean mEnable;
//    public int mMTU;
//    public String mServerAddr;
//    public int mServerPort;
//
//    public CloudConfig() {
//    }
//
//    public CloudConfig(boolean enable, int mtu, String serverAddr, int serverPort) {
//        this.mEnable = enable;
//        this.mMTU = mtu;
//        this.mServerAddr = serverAddr;
//        this.mServerPort = serverPort;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_NAT_STATUS_INFO;
//    }
//
//    @Override
//    public Class<SDK_NatConfig> getConfigType() {
//        return SDK_NatConfig.class;
//    }
//
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_NatConfig netsdkConfig){
//        setEnable(netsdkConfig.st_0_bEnable == true);
//        setMTU(Integer.valueOf(netsdkConfig.st_3_nMTU));
//        setServerAddr(G.ToString(netsdkConfig.st_4_serverAddr));
//        setServerPort(Integer.valueOf(netsdkConfig.st_5_serverPort));
//    }
//
//
//    @Override
//    public SDK_NatConfig getAsNetSdkConfig(){
//        mConfig.st_0_bEnable = isEnable() ? true : false;
//        mConfig.st_3_nMTU = getMTU();
//        G.SetValue(mConfig.st_4_serverAddr, getServerAddr());
//        mConfig.st_5_serverPort = getServerPort();
//
//        return mConfig;
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
//    public int getMTU() {
//        return mMTU;
//    }
//
//    public void setMTU(int mMTU) {
//        this.mMTU = mMTU;
//    }
//
//    public String getServerAddr() {
//        return mServerAddr;
//    }
//
//    public void setServerAddr(String mServerAddr) {
//        this.mServerAddr = mServerAddr;
//    }
//
//    public int getServerPort() {
//        return mServerPort;
//    }
//
//    public void setServerPort(int mServerPort) {
//        this.mServerPort = mServerPort;
//    }
//}
