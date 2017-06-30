package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.util.Log;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;


/**
 * Created by rinaldo.bueno on 29/08/2014.
 */
public class Device implements Serializable {

    //Expose as need by GSON to exclude fields which should not be saved in Device List and void circular reference
    @Expose public String deviceName;
    @Expose public String hostname;

    //CONFIG_IPAddress
    @Expose private String ipAddress;
    @Expose private String domain;
    @Expose private String submask;
    @Expose private String macAddress;
    @Expose private String gateway;
    @Expose private String serialNumber;
    @Expose private int httpPort = 80;
    @Expose private int sslPort = 8443;
    @Expose private int tcpPort = 34567;
    @Expose private int udpPort = 34568;
    @Expose private int maxBPS;
    @Expose private int transferPlan;
    @Expose private String monMode = "TCP";
    @Expose private int tcpMaxConn = 10;

    //CONFIG DNS
    private String primaryDNS;
    private String secondaryDNS;

    //CONFIG UPnP
    private boolean upnpEnable;
    private int HTTPPort;
    private int MediaPort;
    private int MobilePort;

    //CONFIG DDNS
    private boolean ddnsEnable;
    private String ddnsDomain;
    private String ddnsUserName;

    @Expose private String username;
    @Expose private String password;

    //DeviceInfo
    private String gigaCode;
    private String softwareVersion;
    private String hardwareVersion;
    @Expose private int channelNumber = 0;
    @Expose private int numberOfAlarmsIn;
    @Expose private int numberOfAlarmsOut;
    @Expose public int audioInChannel;
    @Expose public int talkInChannel;
    @Expose public int talkOutChannel;
    @Expose public String connectionString;

    //State
    public boolean isLogged = false;
    public boolean isOnline = false;
    public int connectionMethod = -1; //0 - IP:port, 1 - DDNS:port, 2 - SerialNumber
    public int loginAttempt = 0;
    public ChannelsManager channelsManager;
    public String message = "Conectando via IP";

    private Calendar systemTime;

    public Device() {
    }

    public Device(Device device){
        this.deviceName = device.deviceName;
        this.hostname = device.hostname;
        this.ipAddress = device.ipAddress;
        this.domain = device.domain;
        this.submask = device.submask;
        this.macAddress = device.macAddress;
        this.gateway = device.gateway;
        this.serialNumber = device.serialNumber;
        this.httpPort = device.httpPort;
        this.sslPort = device.sslPort;
        this.tcpPort = device.tcpPort;
        this.udpPort = device.udpPort;
        this.maxBPS = device.maxBPS;
        this.transferPlan = device.transferPlan;
        this.monMode = device.monMode;
        this.tcpMaxConn = device.tcpMaxConn;
        this.primaryDNS = device.primaryDNS;
        this.secondaryDNS = device.secondaryDNS;
        this.upnpEnable = device.upnpEnable;
        this.HTTPPort = device.HTTPPort;
        this.MediaPort = device.MediaPort;
        this.MobilePort = device.MobilePort;
        this.ddnsEnable = device.ddnsEnable;
        this.ddnsDomain = device.ddnsDomain;
        this.ddnsUserName = device.ddnsUserName;
        this.username = device.username;
        this.password = device.password;
        this.channelNumber = device.channelNumber;
        this.numberOfAlarmsIn = device.numberOfAlarmsIn;
        this.numberOfAlarmsOut = device.numberOfAlarmsOut;
        this.audioInChannel = device.audioInChannel;
        this.talkInChannel = device.talkInChannel;
        this.talkOutChannel = device.talkOutChannel;
        this.connectionString = device.connectionString;
        this.isLogged  = device.isLogged ;
        this.isOnline = device.isOnline;
    }

    public Device(String deviceName) {
        this.deviceName = deviceName;
    }


    public Device(String deviceName, String ipAddress, String submask, String macAddress, String gateway, String serialNumber, int tcpPort, String gigaCode) {
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.submask = submask;
        this.macAddress = macAddress;
        this.gateway = gateway;
        this.serialNumber = serialNumber;
        this.tcpPort = tcpPort;
        this.gigaCode = gigaCode;
        this.username = "admin";
        this.password = "";
//        checkConnectionMethod();
    }

    public Device(SDK_CONFIG_NET_COMMON_V2 comm) {
        this.serialNumber = G.ToString(comm.st_14_sSn);
        this.macAddress = G.ToString(comm.st_13_sMac);
        this.hostname = G.ToString(comm.st_00_HostName);
        this.ipAddress = comm.st_01_HostIP.getIp();
        this.username = "admin";
        this.password = "";
        this.tcpPort = comm.st_05_TCPPort;
//        checkConnectionMethod();
    }

/*    public void checkConnectionMethod(){
        if(ipAddress != null && !ipAddress.isEmpty())
            connectionString = ipAddress+":"+tcpPort;
        else if(domain != null && !domain.isEmpty())
            connectionString = domain+":"+tcpPort;
        else if(!serialNumber.isEmpty())
            connectionString = serialNumber;
    }*/

    public int setConnectionString(int connectionMethod){
        this.connectionMethod = connectionMethod;
        switch (connectionMethod){
            case 0: {     //IP:port
                if(ipAddress != null && !ipAddress.isEmpty()) {
                    loginAttempt++;
                    connectionString = ipAddress + ":" + tcpPort;
                    message = "Conectando via IP";
                } else
                    return -1;
            } break;
            case 1: {     //domain:port
                if(domain != null && !domain.isEmpty()) {
                    connectionString = domain + ":" + tcpPort;
                    message = "Conectando via domínio";
                }else
                    return -1;
            }break;
            case 2: {     //cloud
                if(!serialNumber.isEmpty()) {
                    connectionString = serialNumber;
                    message = "Conectando via cloud";
                }else
                    return -1;
            }break;
        }
        return 1;
    }

    /*
   @property (strong, nonatomic, readonly) GSWiFiConfiguration *wiFiConfiguration;

   - (id)initWithDevice:(SDK_CONFIG_NET_COMMON_V2) device;
   - (id)initWithDictionary:(NSDictionary *) deviceDicionary;
   - (void)addDeviceInfo:(H264_DVR_DEVICEINFO) deviceInfo;

   -(id) copyWithZone:(NSZone *) zone;
   */

    @Override
    public int hashCode() {
        if ( null != this.serialNumber && !this.serialNumber.equals("")) {
            return (this.serialNumber).hashCode();
        } else
            return (this.deviceName).hashCode();

//        return super.hashCode();
    }

    public int getId() {
//        return (this.serialNumber).hashCode();
        if ( null != this.serialNumber && !this.serialNumber.equals("")) {
            return (this.serialNumber).hashCode();
        } else
            return (this.deviceName).hashCode();
    }

    public boolean hasLogin(){
        return isLogged;
    }

    public void setLogin(boolean login){
        isLogged = login;
    }

   public void printDeviceInfo(){
       Log.d("deviceInfo","================================================");
       Log.d("deviceInfo","DEVICE INFO:");
       Log.d("deviceInfo","------------------------------------------------");
       Log.d("deviceInfo","Hardware: "+getHardwareVersion());
       Log.d("deviceInfo","Serial Number:  "+ getSerialNumber());
       Log.d("deviceInfo","Number of channels:  "+ getChannelNumber());
       Log.d("deviceInfo","Alarm In:  "+ getNumberOfAlarmsIn());
       Log.d("deviceInfo","Alarm Out:  "+ getNumberOfAlarmsOut());
       //Log.d("deviceInfo","Device Type: "+ g.deviceTye == SDK_DEVICE_TYPE_IPC ? @"IPC" : @"DVR");
       Log.d("deviceInfo","================================================");
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSubmask() {
        return submask;
    }

    public void setSubmask(String submask) {
        this.submask = submask;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getTCPPort() {
        return tcpPort;
    }

    public void setTCPPort(int port) {
        this.tcpPort = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
        if (channelsManager != null && channelNumber > 1) {
            this.channelsManager.numQuad = 2;
            this.channelsManager.lastNumQuad = 2;
        } else if(channelsManager != null){
            this.channelsManager.numQuad = 1;
            this.channelsManager.lastNumQuad = 1;
        }
    }

    public int getNumberOfAlarmsIn() {
        return numberOfAlarmsIn;
    }

    public void setNumberOfAlarmsIn(int numberOfAlarmsIn) {
        this.numberOfAlarmsIn = numberOfAlarmsIn;
    }

    public int getNumberOfAlarmsOut() {
        return numberOfAlarmsOut;
    }

    public void setNumberOfAlarmsOut(int numberOfAlarmsOut) {
        this.numberOfAlarmsOut = numberOfAlarmsOut;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getSslPort() {
        return sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public String getMonMode() {
        return monMode;
    }

    public void setMonMode(String monMode) {
        this.monMode = monMode;
    }

    public int getTcpMaxConn() {
        return tcpMaxConn;
    }

    public void setTcpMaxConn(int tcpMaxConn) {
        this.tcpMaxConn = tcpMaxConn;
    }

    public Calendar getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Calendar systemTime) {
        this.systemTime = systemTime;
    }

    public int getTransferPlan() {
        return transferPlan;
    }

    public void setTransferPlan(int transferPlan) {
        this.transferPlan = transferPlan;
    }

    public int getMaxBPS(){
        return this.maxBPS;
    }

    public void setMaxBPS(int maxBPS){
        this.maxBPS = maxBPS;
    }

    public void setPrimaryDNS(String primaryDNS){
        this.primaryDNS = primaryDNS;
    }

    public String getPrimaryDNS(){
        return this.primaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS){
        this.secondaryDNS = secondaryDNS;
    }

    public String getSecondaryDNS(){
        return this.secondaryDNS;
    }

    public int getMediaPort() {
        return MediaPort;
    }

    public void setMediaPort(int mediaPort) {
        MediaPort = mediaPort;
    }

    public int getMobilePort() {
        return MobilePort;
    }

    public void setMobilePort(int mobilePort) {
        MobilePort = mobilePort;
    }

    public boolean isUpnpEnable() {
        return upnpEnable;
    }

    public void setUpnpEnable(boolean upnpEnable) {
        this.upnpEnable = upnpEnable;
    }

    public boolean isDdnsEnable() {
        return ddnsEnable;
    }

    public void setDdnsEnable(boolean ddnsEnable) {
        this.ddnsEnable = ddnsEnable;
    }

    public String getDdnsDomain() {
        return ddnsDomain;
    }

    public void setDdnsDomain(String ddnsDomain) {
        this.ddnsDomain = ddnsDomain;
    }

    public String getDdnsUserName() {
        return ddnsUserName;
    }

    public void setDdnsUserName(String ddnsUserName) {
        this.ddnsUserName = ddnsUserName;
    }

    public String getHostID() {
        String hostID;
        if (getIpAddress() != null && !getIpAddress().isEmpty()) {
            hostID = getIpAddress();
        } else if (getSerialNumber() != null && !getSerialNumber().isEmpty()) {
            hostID = getSerialNumber();
        } else {
            hostID = getMacAddress();
        }
        return hostID;
    }


    @Override
    public String toString() {
        return deviceName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
