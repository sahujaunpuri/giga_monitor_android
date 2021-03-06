package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.util.Log;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;


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
    @Expose private int externalPort = 34567;
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

    //CONFIG ENCODE
    private int[] primaryBitRate;
    private String[] primaryBitRateControl;
    private String[] primaryCompression;
    private int[] primaryGOP;
    private String[] primaryResolution;
    private String[] primaryFrameRate;
    private int[] primaryQualities;
    private Boolean[] primaryAudio;
    private int[] secondaryBitRate;
    private String[] secondaryBitRateControl;
    private String[] secondaryCompression;
    private int[] secondaryGOP;
    private String[] secondaryResolution;
    private int[] secondaryFrameRate;
    private int[] secondaryQualities;
    private Boolean[] secondaryAudio;
    private String[] exImageSizePerChannel;
    private String[] imageSizePerChannel;
    private String[] maxEncodePowerPerChannel;
    private String primaryResolutionMask;
    private String primaryCompressionMask;
    private String secondaryResolutionMask;
    private String secondaryCompressionMask;

    //DeviceInfo
    public boolean dss = false;
    private String gigaCode;
    private String softwareVersion;
    private String hardwareVersion;
    private String buildTime;
    private String natCode;
    private String natStatus;
    private String timeString; //format: yyyy-MM-dd HH-mm-ss
    @Expose private int channelNumber = 0;
    @Expose private int numberOfAlarmsIn;
    @Expose private int numberOfAlarmsOut;
    @Expose public int audioInChannel;
    @Expose public int talkInChannel;
    @Expose public int talkOutChannel;
    @Expose public String connectionString;
    @Expose private String connectionNetworkName = null;
    @Expose private JSONObject simplifyEncodeJson;
    @Expose public boolean isFavorite = false;
    @Expose private boolean isEnable = true;
    @Expose private int[] channelOrder = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};

    //State
    public boolean isLogged = false;
    public boolean isOnline = false;
    private int connectionMethod = -1; //0 - IP:port, 1 - DDNS:port, 2 - SerialNumber
    public int loginAttempt = 1;
    public ChannelsManager channelsManager;
    public String message = "";
    @Expose private boolean ipPriorityConnection;
    @Expose private boolean domainPriorityConnection;
    @Expose private boolean cloudPriorityConnection;
    @Expose private String connectionMethodString;
    @Expose private boolean loginByIp = true;
    @Expose private boolean loginByDomain = true;
    @Expose private boolean loginByCloud = true;
    @Expose private int nextConnectionType = 0;
    public boolean allAttempstFail = false;

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
        this.externalPort = device.externalPort;
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
        this.connectionNetworkName = device.connectionNetworkName;
        this.ipPriorityConnection = true;
        this.domainPriorityConnection = true;
        this.cloudPriorityConnection = true;
        this.channelOrder[0] = -1;
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
                    message = "Conectando via IP" + "\nTentativa: " + loginAttempt;
                } else {
                    return -1;
                }
            }
            break;
            case 1: {     //domain:port
                if(domain != null && !domain.isEmpty()) {
                    if (externalPort != 0) {
                        connectionString = domain + ":" + externalPort;
                    } else {
                        connectionString = domain + ":" + tcpPort;
                    }
                    message = "Conectando via domínio/IP externo" + "\nTentativa: " + loginAttempt;
                } else {
                    return -1;
                }
            }
            break;
            case 2: {     //cloud
                if(!serialNumber.isEmpty()) {
                    connectionString = serialNumber;
                    message = "Conectando via cloud" + "\nTentativa: " + loginAttempt;
                } else {
                    return -1;
                }
            }
            break;
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
    }

    public int getId() {
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

    public int getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(final int externalPort) {
        this.externalPort = externalPort;
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

    public ChannelsManager getChannelsManager() {
        return channelsManager;
    }

    public void setChannelsManager(ChannelsManager channelsManager) {
        this.channelsManager = channelsManager;
    }

    public String getMessage() {
        return message;
    }

    public void setChannelNumberForQuad(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int[] getChannelOrder() {
       return channelOrder;
    }

    public void setChannelOrder(int[] channelOrder){
       this.channelOrder = channelOrder;
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

    public int getConnectionMethod() {
        return connectionMethod;
    }

    public void setConnectionMethod(int connectionMethod) {
        this.connectionMethod = connectionMethod;
    }

    public String getConnectionMethodString() {
        return connectionMethodString;
    }

    public void setConnectionMethodString(String connectionMethodString) {
        this.connectionMethodString = connectionMethodString;
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

    public String getConnectionNetworkName() {
        return connectionNetworkName;
    }

    public void setConnectionNetworkName(final String connectionNetworkName) {
        this.connectionNetworkName = connectionNetworkName;
    }

    public boolean isIpPriorityConnection() {
        return ipPriorityConnection;
    }

    public void setIpPriorityConnection(final boolean ipPriorityConnection) {
        this.ipPriorityConnection = ipPriorityConnection;
    }

    public boolean isDomainPriorityConnection() {
        return domainPriorityConnection;
    }

    public void setDomainPriorityConnection(final boolean domainPriorityConnection) {
        this.domainPriorityConnection = domainPriorityConnection;
    }

    public boolean isCloudPriorityConnection() {
        return cloudPriorityConnection;
    }

    public void setCloudPriorityConnection(final boolean cloudPriorityConnection) {
        this.cloudPriorityConnection = cloudPriorityConnection;
    }

    public String[] getPrimaryResolution() {
        return primaryResolution;
    }

    public String[] getPrimaryFrameRate() {
        return primaryFrameRate;
    }

    public int[] getPrimaryQualities() {
        return primaryQualities;
    }

    public Boolean[] getPrimaryAudio() {
        return primaryAudio;
    }

    public String[] getSecondaryResolution() {
        return secondaryResolution;
    }

    public int[] getSecondaryFrameRate() {
        return secondaryFrameRate;
    }

    public int[] getSecondaryQualities() {
        return secondaryQualities;
    }

    public Boolean[] getSecondaryAudio() {
        return secondaryAudio;
    }

    public String[] getExImageSizePerChannel() {
        return exImageSizePerChannel;
    }

    public int getExImageSizePerChannel(int channel) {
        return parseHexStringToInt(exImageSizePerChannel[channel]);
    }

    public String[] getImageSizePerChannel() {
        return imageSizePerChannel;
    }

    public int getImageSizePerChannel(int channel) {
        return parseHexStringToInt(imageSizePerChannel[channel]);
    }

    public String[] getMaxEncodePowerPerChannel() {
        return maxEncodePowerPerChannel;
    }

    public int getMaxEncodePowerPerChannel(int channel){
        return parseHexStringToInt(maxEncodePowerPerChannel[channel]);
    }

    public int getPrimaryResolutionMask() {
        return parseHexStringToInt(primaryResolutionMask);
    }

    public int getPrimaryCompressionMask() {
        return parseHexStringToInt(primaryCompressionMask);
    }

    public int getSecondaryResolutionMask() {
        return parseHexStringToInt(secondaryResolutionMask);
    }

    public int getSecondaryCompressionMask() {
        return parseHexStringToInt(secondaryCompressionMask);
    }

    private int parseHexStringToInt(String hexString){
        hexString = hexString.substring(2);
        return (int) Long.parseLong(hexString, 16);
    }

    public void setImageSizePerChannel(JSONArray jsonArray){
        for(int i=0; i<channelNumber; i++){
            try {
                imageSizePerChannel[i] = jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setExImageSizePerChannel(JSONArray jsonArray){
        for(int i=0; i<channelNumber; i++){
            try {
                exImageSizePerChannel[i] = jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMaxEncodePowerPerChannel(JSONArray jsonArray){
        for(int i=0; i<channelNumber; i++){
            try {
                maxEncodePowerPerChannel[i] = jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setEncodeMasks(JSONArray jsonArray){
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            primaryResolutionMask = jsonObject.getString("ResolutionMask");
            primaryCompressionMask = jsonObject.getString("CompressionMask");
            jsonObject = jsonArray.getJSONObject(1);
            secondaryResolutionMask = jsonObject.getString("ResolutionMask");
            secondaryCompressionMask = jsonObject.getString("CompressionMask");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isLoginByIp() {
        return loginByIp;
    }

    public void setLoginByIp(boolean loginByIp) {
        this.loginByIp = loginByIp;
    }

    public boolean isLoginByDomain() {
        return loginByDomain;
    }

    public void setLoginByDomain(boolean loginByDomain) {
        this.loginByDomain = loginByDomain;
    }

    public boolean isLoginByCloud() {
        return loginByCloud;
    }

    public void setLoginByCloud(boolean loginByCloud) {
        this.loginByCloud = loginByCloud;
    }

    public int getNextConnectionType() {
        return nextConnectionType;
    }

    public void setNextConnectionType(int nextConnectionType) {
        this.nextConnectionType = nextConnectionType;
    }

    public String getNatCode() {
        return natCode;
    }

    public void setNatCode(String natCode) {
        this.natCode = natCode;
    }

    public String getNatStatus() {
        return natStatus;
    }

    public void setNatStatus(String natStatus) {
        this.natStatus = natStatus;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public void initEncodeArrays(){
        this.primaryResolution = new String[channelNumber];
        this.primaryFrameRate = new String[channelNumber];
        this.primaryQualities = new int[channelNumber];
        this.primaryAudio = new Boolean[channelNumber];
        this.secondaryResolution = new String[channelNumber];
        this.secondaryFrameRate = new int[channelNumber];
        this.secondaryQualities = new int[channelNumber];
        this.secondaryAudio = new Boolean[channelNumber];
        this.primaryBitRate  = new int[channelNumber];
        this.primaryBitRateControl = new String[channelNumber];
        this.primaryCompression = new String[channelNumber];
        this.primaryGOP = new int[channelNumber];
        this.secondaryBitRate = new int[channelNumber];
        this.secondaryBitRateControl = new String[channelNumber];
        this.secondaryCompression = new String[channelNumber];
        this.secondaryGOP = new int[channelNumber];

        this.exImageSizePerChannel = new String[channelNumber];
        this.imageSizePerChannel = new String[channelNumber];
        this.maxEncodePowerPerChannel = new String[channelNumber];
    }

    public void parsePrimaryConfigs(int channel, JSONObject streamConfig){
        try {
            JSONObject videoConfig = streamConfig.getJSONObject("Video");
            primaryBitRate[channel] = videoConfig.getInt("BitRate");
            primaryBitRateControl[channel] = videoConfig.getString("BitRateControl");
            primaryCompression[channel] = videoConfig.getString("Compression");
            primaryGOP[channel] = videoConfig.getInt("GOP");
            primaryResolution[channel] = videoConfig.getString("Resolution");
            primaryFrameRate[channel] = videoConfig.getString("FPS");
            primaryQualities[channel] = videoConfig.getInt("Quality");
            primaryAudio[channel] = streamConfig.getBoolean("AudioEnable");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void parseSecondaryConfigs(int channel, JSONObject streamConfig){
        try {
            JSONObject videoConfig = streamConfig.getJSONObject("Video");
            secondaryBitRate[channel] = videoConfig.getInt("BitRate");
            secondaryBitRateControl[channel] = videoConfig.getString("BitRateControl");
            secondaryCompression[channel] = videoConfig.getString("Compression");
            secondaryGOP[channel] = videoConfig.getInt("GOP");
            secondaryResolution[channel] = videoConfig.getString("Resolution");
            secondaryFrameRate[channel] = videoConfig.getInt("FPS");
            secondaryQualities[channel] = videoConfig.getInt("Quality");
            secondaryAudio[channel] = streamConfig.getBoolean("AudioEnable");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public JSONObject setSecondarySecondaryJsonConfig(int channel, JSONObject streamConfig){
        JSONObject configJson = null;
        try {
            configJson = streamConfig;
            JSONObject videoConfig = configJson.getJSONObject("Video");
            videoConfig.put("Resolution", secondaryResolution[channel]);
            videoConfig.put("FPS", secondaryFrameRate[channel]);
            videoConfig.put("Quality",secondaryQualities[channel]);
        } catch (JSONException e){
            e.printStackTrace();
        }

        return configJson;
    }

    public void resetAttempts() {
        this.allAttempstFail = false;
    }

    public void setSecondaryResolution(String[] secondaryResolution) {
        this.secondaryResolution = secondaryResolution;
    }

    public void setSecondaryFrameRate(int[] secondaryFrameRate) {
        this.secondaryFrameRate = secondaryFrameRate;
    }

    public void setSecondaryQualities(int[] secondaryQualities) {
        this.secondaryQualities = secondaryQualities;
    }

    public JSONObject getSimplifyEncodeJson() {
        return simplifyEncodeJson;
    }

    public void setSimplifyEncodeJson(JSONObject simplifyEncodeJson) {
        this.simplifyEncodeJson = simplifyEncodeJson;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
