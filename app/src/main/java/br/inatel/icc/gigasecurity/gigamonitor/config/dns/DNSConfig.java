//package br.inatel.icc.gigasecurity.gigamonitor.config.dns;
//
//import com.basic.G;
//import com.lib.sdk.struct.CONFIG_IPAddress;
//import com.lib.sdk.struct.SDK_NetDNSConfig;
//
//import java.io.Serializable;
//import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.model.NetDNSConfig;
//
///**
// * File: EthernetDNSConfig.java
// * Creation date: 09/10/2014
// * Author: Denis Vilela
// * <p/>
// * Purpose: Declaration of class EthernetDNSConfig.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class DNSConfig extends Config2Abstract<SDK_NetDNSConfig> implements Serializable {
//    private CONFIG_IPAddress primaryDNS;
//    private CONFIG_IPAddress secondaryDNS;
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_NetDNSConfig config) {
//
//        this.primaryDNS = config.st_0_PrimaryDNS;
//        this.secondaryDNS = config.st_1_SecondaryDNS;
//    }
//
//    @Override
//    public SDK_NetDNSConfig getAsNetSdkConfig() {
//
//        mConfig.st_0_PrimaryDNS = this.primaryDNS;
//        mConfig.st_1_SecondaryDNS = this.secondaryDNS;
//
//        return mConfig;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS;
//    }
//
//    @Override
//    public Class<SDK_NetDNSConfig> getConfigType() {
//        return SDK_NetDNSConfig.class;
//    }
//
//    public String getPrimaryDNS() {
//        return primaryDNS.getIp();
//    }
//
//        public DNSConfig setPrimaryDNS(int[] ip) {
//        this.primaryDNS.setIp(ip[0], ip[1], ip[2], ip[3]);
//        return this;
//    }
//
//    public String getSecondaryDNS() {
//        return secondaryDNS.getIp();
//    }
//
//    public DNSConfig setSecondaryDNS(int[] ip) {
//        this.secondaryDNS.setIp(ip[0], ip[1], ip[2], ip[3]);
//        return this;
//    }
//}
