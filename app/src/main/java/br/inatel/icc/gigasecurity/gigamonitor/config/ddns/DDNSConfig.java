//package br.inatel.icc.gigasecurity.gigamonitor.config.ddns;
//
//
//import com.basic.G;
////import com.xm.MyConfig;
//
//import com.lib.sdk.struct.SDK_NetDDNSConfig;
//import com.lib.sdk.struct.SDK_NetDDNSConfigALL;
//import com.lib.sdk.struct.SDK_RemoteServerConfig;
//
//import java.io.Serializable;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//
///**
// * File: DDNSConfig.java
// * Creation date: 14/10/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class DDNSConfig.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class DDNSConfig extends Config2Abstract<SDK_NetDDNSConfigALL> implements Serializable {
//
//    private boolean mEnable;
//    private boolean mOnline;
//    private String mDDNSKey;
//    private String mDomain;
//    private String mUserName;
//    private String mArg0;
//    private com.lib.sdk.struct.SDK_RemoteServerConfig mServer;
//    SDK_NetDDNSConfigALL gigaDDNSConfigALL;
//
//    private Integer mGigaDdnsIndex = 0;
//    private boolean mDdnsInvalid;
//
//    public DDNSConfig() {
//    }
//
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_NetDDNSConfigALL config) {
//
//        setGigaDDNSConfigALL(config);
//
//        SDK_NetDDNSConfig gigaDDNSConfig1 = null;
//
//        for (mGigaDdnsIndex = 0; mGigaDdnsIndex < config.st_0_ddnsConfig.length; mGigaDdnsIndex++)
//
//        for (SDK_NetDDNSConfig sdk_netDDNSConfig : config.st_0_ddnsConfig) {
//            if(G.ToString(sdk_netDDNSConfig.st_5_Server.st_0_ServerName).toLowerCase().contains("gigaddns")){
//                gigaDDNSConfig1 = sdk_netDDNSConfig;
//                break;
//            }
//        }
//
//        if(gigaDDNSConfig1 == null){
//            mDdnsInvalid = true;
//            return;
//        }
//
//        boolean enabled = config.st_0_ddnsConfig[0].st_0_Enable;
//        boolean online = config.st_0_ddnsConfig[0].st_1_Online;
//
//        byte[] keyBuff = config.st_0_ddnsConfig[0].st_2_DDNSKey;
//        String ddnsKey = G.ToString(keyBuff);
//
//        byte[] hostNameBuff = config.st_0_ddnsConfig[0].st_3_HostName;
//        String hostName = G.ToString(hostNameBuff);
//
//        mServer = config.st_0_ddnsConfig[0].st_5_Server;
//
//        byte[] serverNameBuff = mServer.st_0_ServerName;
//        String serverName = G.ToString(serverNameBuff);
//
//        byte[] userNameBuff = mServer.st_3_UserName;
//        String userName = G.ToString(userNameBuff);
//
//        byte[] passwordBuff = mServer.st_4_Password;
//        String password = G.ToString(passwordBuff);
//
//        int serverPort = mServer.st_2_Port;
//
//        int anonymity = mServer.st_5_Anonymity;
//
//        InetAddress inetAddress = null;
//
//        try {
//            inetAddress = InetAddress.getByAddress(mServer.st_1_ip.st_0_ip);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//
////        //mType = mConfig.getDdnsType();
////        if (inetAddress != null) Log.d("DDNSConfigActivity", inetAddress.toString());
//        mEnable = enabled;
//        mDomain = hostName;
//        mOnline = online;
//        mUserName = userName;
//        mDDNSKey = ddnsKey;
//    }
//
//    @Override
//    public SDK_NetDDNSConfigALL getAsNetSdkConfig() {
//        mGigaDdnsIndex = mGigaDdnsIndex -1;
//        mConfig.st_0_ddnsConfig[mGigaDdnsIndex].st_0_Enable = isEnable();
//        mConfig.st_0_ddnsConfig[mGigaDdnsIndex].st_1_Online = isOnline();
//        G.SetValue( mConfig.st_0_ddnsConfig[mGigaDdnsIndex].st_2_DDNSKey, getDDNSKey());
//        G.SetValue( mConfig.st_0_ddnsConfig[mGigaDdnsIndex].st_3_HostName, getDomain());
//        mConfig.st_0_ddnsConfig[mGigaDdnsIndex].st_5_Server = getServer();
//
//        return mConfig;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return 0;
//        //return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS;
//    }
//
//    @Override
//    public Class<SDK_NetDDNSConfigALL> getConfigType() {
//        return SDK_NetDDNSConfigALL.class;
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
//    public boolean isOnline() {
//        return mOnline;
//    }
//
//    public void setOnline(boolean mOnline) {
//        this.mOnline = mOnline;
//    }
//
//    public String getDDNSKey() {
//        return mDDNSKey;
//    }
//
//    public void setDDNSKey(String mDDNSKey) {
//        this.mDDNSKey = mDDNSKey;
//    }
//
//    public String getDomain() {
//        return mDomain;
//    }
//
//    public void setHostName(String mHostName) {
//        this.mDomain = mHostName;
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
//    public SDK_RemoteServerConfig getServer() {
//        return mServer;
//    }
//
//    public void setServer(SDK_RemoteServerConfig mServer) {
//        this.mServer = mServer;
//    }
//
//    public boolean isDdnsInvalid() {
//        return mDdnsInvalid;
//    }
//
//    public void setDdnsInvalid(boolean mDdnsInvalid) {
//        this.mDdnsInvalid = mDdnsInvalid;
//    }
//
//    public String getUserName() {
//        return mUserName;
//    }
//
//    public void setUserName(String userName) {
//        this.mUserName = userName;
//    }
//
//    public SDK_NetDDNSConfigALL getGigaDDNSConfigALL() {
//        return gigaDDNSConfigALL;
//    }
//
//    public void setGigaDDNSConfigALL(SDK_NetDDNSConfigALL gigaDDNSConfig) {
//        this.gigaDDNSConfigALL = gigaDDNSConfig;
//    }
//}
