//package br.inatel.icc.gigasecurity.gigamonitor.config.ethernet;
//
//import com.basic.G;
//import com.xm.MyConfig;
//import com.xm.javaclass.SDK_CONFIG_NET_COMMON;
//
//import java.io.Serializable;
//
//import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;
//
///**
// * File: EthernetCommonConfig.java
// * Creation date: 08/10/2014
// * Author: Denis Vilela
// * <p/>
// * Purpose: Declaration of class EthernetCommonConfig.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class EthernetConfig extends Config2Abstract<SDK_CONFIG_NET_COMMON> implements Serializable {
//
//    public static enum PortType { TCP_PORT, UDP_PORT, SSL_PORT }
//
//    private byte[] hostName;
//    private com.xm.javaclass.CONFIG_IPAddress hostIP;
//    private com.xm.javaclass.CONFIG_IPAddress submask;
//    private com.xm.javaclass.CONFIG_IPAddress gateway;
//    private int httpPort;
//    private int tcpPort;
//    private int sslPort;
//    private int udpPort;
//    private int maxConn;
//    private int monMode;
//    private int maxBps;
//    private int transferPlan;
//    private boolean useHSDownLoad;
//    private byte[] mac;
//    private byte[] zarg0;
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET;
//    }
//
//    @Override
//    public Class<SDK_CONFIG_NET_COMMON> getConfigType() {
//        return SDK_CONFIG_NET_COMMON.class;
//    }
//
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_CONFIG_NET_COMMON config) {
//        this.hostName = config.st_00_HostName;
//        this.hostIP = config.st_01_HostIP;
//        this.submask = config.st_02_Submask;
//        this.gateway = config.st_03_Gateway;
//        this.httpPort = config.st_04_HttpPort;
//        this.tcpPort = config.st_05_TCPPort;
//        this.sslPort = config.st_06_SSLPort;
//        this.udpPort = config.st_07_UDPPort;
//        this.maxConn = config.st_08_MaxConn;
//        this.monMode = config.st_09_MonMode;
//        this.maxBps = config.st_10_MaxBps;
//        this.transferPlan = config.st_11_TransferPlan;
//        this.useHSDownLoad = config.st_12_bUseHSDownLoad;
//        this.mac = config.st_13_sMac;
//        this.zarg0 = config.st_14_Zarg0;
//    }
//
//    @Override
//    public SDK_CONFIG_NET_COMMON getAsNetSdkConfig() {
//        mConfig.st_00_HostName = this.hostName;
//        mConfig.st_01_HostIP = this.hostIP;
//        mConfig.st_02_Submask = this.submask;
//        mConfig.st_03_Gateway = this.gateway;
//        mConfig.st_04_HttpPort = this.httpPort;
//        mConfig.st_05_TCPPort = this.tcpPort;
//        mConfig.st_06_SSLPort = this.sslPort;
//        mConfig.st_07_UDPPort = this.udpPort;
//        mConfig.st_08_MaxConn = this.maxConn;
//        mConfig.st_09_MonMode = this.monMode;
//        mConfig.st_10_MaxBps = this.maxBps;
//        mConfig.st_11_TransferPlan = this.transferPlan;
//        mConfig.st_12_bUseHSDownLoad = this.useHSDownLoad;
//        mConfig.st_13_sMac = this.mac;
//        mConfig.st_14_Zarg0 = this.zarg0;
//        return mConfig;
//    }
//
//    public String getHostName() {
//        return G.ToString(hostName);
//    }
//
//    public EthernetConfig setHostName(String hostName) {
//        this.hostName = hostName.getBytes();
//        return this;
//    }
//
//    public String getHostIP() {
//        return hostIP.getIp();
//    }
//
//    public EthernetConfig setHostIP(int[] ip) {
//        this.hostIP.setIp(ip[0], ip[1], ip[2], ip[3]);
//        return this;
//    }
//
//    public String getSubmask() {
//        return submask.getIp();
//    }
//
//    public EthernetConfig setSubmask(int[] ip) {
//        this.submask.setIp(ip[0], ip[1], ip[2], ip[3]);
//        return this;
//    }
//
//    public String getGateway() {
//        return gateway.getIp();
//    }
//
//    public EthernetConfig setGateway(int[] ip) {
//        this.gateway.setIp(ip[0], ip[1], ip[2], ip[3]);
//        return this;
//    }
//
//    public int getHttpPort() {
//        return httpPort;
//    }
//
//    public EthernetConfig setHttpPort(int httpPort) {
//        this.httpPort = httpPort;
//        return this;
//    }
//
//    public int getTcpPort() {
//        return tcpPort;
//    }
//
//    public EthernetConfig setTcpPort(int tcpPort) {
//        this.tcpPort = tcpPort;
//        return this;
//    }
//
//    public int getSslPort() {
//        return sslPort;
//    }
//
//    public EthernetConfig setSslPort(int sslPort) {
//        this.sslPort = sslPort;
//        return this;
//    }
//
//    public int getUdpPort() {
//        return udpPort;
//    }
//
//    public EthernetConfig setUdpPort(int udpPort) {
//        this.udpPort = udpPort;
//        return this;
//    }
//
//    public int getMaxConn() {
//        return maxConn;
//    }
//
//    public EthernetConfig setMaxConn(int maxConn) {
//        this.maxConn = maxConn;
//        return this;
//    }
//
//    public int getMonMode() {
//        return monMode;
//    }
//
//    public EthernetConfig setMonMode(int monMode) {
//        this.monMode = monMode;
//        return this;
//    }
//
//    public int getMaxBps() {
//        return maxBps;
//    }
//
//    public EthernetConfig setMaxBps(int maxBps) {
//        this.maxBps = maxBps;
//        return this;
//    }
//
//    public int getTransferPlan() {
//        return transferPlan;
//    }
//
//    public EthernetConfig setTransferPlan(int transferPlan) {
//        this.transferPlan = transferPlan;
//        return this;
//    }
//
//    public boolean useHSDownload() {
//        return useHSDownLoad;
//    }
//
//    public EthernetConfig setUseHSDownLoad(boolean useHSDownLoad) {
//        this.useHSDownLoad = useHSDownLoad;
//        return this;
//    }
//
//    public String getMac() {
//        return G.ToString(mac);
//    }
//
//    public EthernetConfig setMac(String mac) {
//        this.mac = mac.getBytes();
//        return this;
//    }
//
//    public String getZarg0() {
//        return G.ToString(zarg0);
//    }
//
//    public EthernetConfig setZarg0(String zarg0) {
//        this.zarg0 = zarg0.getBytes();
//        return this;
//    }
//}
