package br.inatel.icc.gigasecurity.gigamonitor;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.basic.G;
import com.xm.DevInfo;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.SDK_AllAlarmIn;
import com.xm.SDK_AllAlarmOut;
import com.xm.SDK_NetDHCPConfig;
import com.xm.SDK_NetWifiConfig;
import com.xm.Systime;
import com.xm.javaclass.SDK_CONFIG_NET_COMMON;
import com.xm.javaclass.SDK_CONFIG_NET_COMMON_V3;
import com.xm.javaclass.SDK_LogItem;
import com.xm.javaclass.SDK_LogList;
import com.xm.javaclass.SDK_LogSearchCondition;
import com.xm.javaclass.SDK_NatConfig;
import com.xm.javaclass.SDK_NetDDNSConfig;
import com.xm.javaclass.SDK_NetDDNSConfigALL;
import com.xm.javaclass.SDK_NetDNSConfig;
import com.xm.javaclass.SDK_NetUPNPConfig;
import com.xm.javaclass.SDK_NetWifiDevice;
import com.xm.javaclass.SDK_NetWifiDeviceAll;
import com.xm.javaclass.SDK_RemoteServerConfig;
import com.xm.javaclass.SDK_WifiStatusInfo;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ConfigNetSdkTest extends ApplicationTestCase<Application> {
    public ConfigNetSdkTest() {
        super(Application.class);
    }


    private long loginId;
    private Device device;

    private String ipTest;
    private int portTest;
    private String serialTest;

    private String macTest; //SetConfigOverNet

    private String usernameTest;
    private String passwordTest;

    private DeviceManager mDeviceManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mDeviceManager = DeviceManager.getInstance();

        ipTest = "192.168.1.14";
        portTest = 34567;
        serialTest = "2e98dcd9dfddf036";
        macTest =  "6c:fd:b9:74:c3:88"; //SetConfigOverNet


        usernameTest = "admin";
        passwordTest = "";

        device = loginLAN();
        loginId = device.getLoginID();
        if (loginId <= 0) {
            assertTrue("Unable to Lan Login (loginId=" + loginId + "). Are you in the right network? Device IP and Port are correct?", false);
            return;
        }

    }

    public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    return interfaceAddress.getBroadcast().toString().substring(1);
                }
            }
        }
        return null;
    }

    private Device loginLAN() throws Exception {
        Device device = new Device();

        device.setIpAddress(ipTest);
        device.setTCPPort(portTest);
        device.setUsername(usernameTest);
        device.setPassword(passwordTest);

        loginId = mDeviceManager.loginOnDevice(device, LoginMethod.LAN);
        assertTrue("Unable to Lan Login (loginId=" + loginId + "). Are you in the right network? Device IP and Port are correct?", loginId > 0);
        return device;
    }

    public void testLoginCloud() throws Exception {
        DevInfo devInfo = new DevInfo();

        devInfo.Ip = "192.168.1.5";
        devInfo.TCPPort = 34567;
        devInfo.UserName = "admin".getBytes();
        devInfo.PassWord = "";
        devInfo.SerialNumber = "2e98dcd9dfddf036";
        devInfo.HTTPPort = 80;
        devInfo.UDPPort = 34568;
        devInfo.HostIp = "2e98dcd9dfddf036";
        devInfo.ChanNum = 0;
        devInfo.Socketstyle = MyConfig.SocketStyle.SOCKETNR;


        final long loginId = NetSdk.getInstance().onLoginDev(0, devInfo, null, MyConfig.SocketStyle.SOCKETNR);

        Log.d("testLoginCloud", "loginID: " + loginId);

        assertTrue("FAILED! loginId: " + loginId + " should be greater than 0", loginId > 0);
    }

    public void configDM() throws Exception {
        int sdkConfigNetWifi = MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI;
        SDK_NetWifiConfig config = (SDK_NetWifiConfig) mDeviceManager.getConfig(loginId,
                sdkConfigNetWifi, new SDK_NetWifiConfig());

        assertTrue(config != null);
    }

    public void testGetSystemTime() throws Exception {
        Systime timeConfig = (Systime)mDeviceManager.getConfig(loginId,
                MyConfig.SdkConfigType.E_SDK_CONFIG_SYS_TIME, new Systime());

        assertTrue(timeConfig != null);
    }


    public void testGetDHCPConfig() throws Exception {
        SDK_NetDHCPConfig dhcpConfig = new SDK_NetDHCPConfig();
        boolean b = NetSdk.getInstance().H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DHCP, -1, dhcpConfig, 5000);
        assertTrue("testEtherNetConfig failed", b);
    }

    // WORKING! Do not test, otherwise the DVR will change its IP
    public void atestSetDHCPConfig() throws  Exception {

        NetSdk netSdk = NetSdk.getInstance();

        SDK_NetDHCPConfig originalDhcpConfig = new SDK_NetDHCPConfig();
        boolean got = netSdk.H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DHCP, 0, originalDhcpConfig, 5000);
        if (!got) {
            assertTrue("testSetDCHPConfig: failed to get original dhcp", false);
            return;
        }

        SDK_NetDHCPConfig dhcpConfigModifier = new SDK_NetDHCPConfig();
        dhcpConfigModifier.bEnable_1 = true;
        boolean didSet = netSdk.H264DVRSetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DHCP, 0, dhcpConfigModifier, 5000);
        if (!didSet) {
            assertTrue("testSetDCHPConfig: failed to set dhcp", false);
            return;
        }

        SDK_NetDHCPConfig modifiedDhcpConfig = new SDK_NetDHCPConfig();
        boolean gotModified = netSdk.H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DHCP, 0, modifiedDhcpConfig, 5000);
        if (!gotModified) {
            assertTrue("testSetDCHPConfig: failed to get modified dhcp", false);
            return;
        }

        boolean changed = originalDhcpConfig.bEnable_1 != modifiedDhcpConfig.bEnable_1;
        assertTrue("testSetDCHPConfig: SDK_NetDHCPConfig.bEnable_1 was not modified", changed);
    }

    public void testGetDNSConfig() throws  Exception {
        SDK_NetDNSConfig dnsConfig = new SDK_NetDNSConfig();

        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't get the config", get);

        Log.d("testGetDNSConfig", "Primary DNS Server IP: " + dnsConfig.st_0_PrimaryDNS.getIp()
                + "\nSecondary DNS Server IP: " + dnsConfig.st_1_SecondaryDNS.getIp());
    }

    public void testSetDNSConfig() throws  Exception {
        SDK_NetDNSConfig dnsConfig = new SDK_NetDNSConfig();

        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't get the config", get);
        Log.d("testSetDNSConfig", "Original:\nPrimary DNS Server IP: " + dnsConfig.st_0_PrimaryDNS.getIp()
                + "\nSecondary DNS Server IP: " + dnsConfig.st_1_SecondaryDNS.getIp());

        dnsConfig.st_0_PrimaryDNS.setIp(200, 0, 0, 1); dnsConfig.st_1_SecondaryDNS.setIp(10, 10, 10, 10);
        boolean set = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't set the config", set);

        dnsConfig = new SDK_NetDNSConfig();
        boolean get2 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't get the modified config", get2);
        Log.d("testSetDNSConfig", "Modified:\nPrimary DNS Server IP: " + dnsConfig.st_0_PrimaryDNS.getIp()
                + "\nSecondary DNS Server IP: " + dnsConfig.st_1_SecondaryDNS.getIp());

        dnsConfig.st_0_PrimaryDNS.setIp(192, 168, 1, 1); dnsConfig.st_1_SecondaryDNS.setIp(8, 8, 8, 8);
        boolean set2 = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't set the config back to original", set2);

        dnsConfig = new SDK_NetDNSConfig();
        boolean get3 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DNS, dnsConfig);
        assertTrue("Couldn't get the modified config", get3);
        Log.d("testSetDNSConfig", "Modified:\nPrimary DNS Server IP: " + dnsConfig.st_0_PrimaryDNS.getIp()
                + "\nSecondary DNS Server IP: " + dnsConfig.st_1_SecondaryDNS.getIp());
    }

    public void printEthernetConfig(String tag, String info, SDK_CONFIG_NET_COMMON netConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \nHostName: ").append(G.ToString(netConfig.st_00_HostName));
        sb.append("\nHostIP: ").append(netConfig.st_01_HostIP.getIp());
        sb.append("\nSubmask: ").append(netConfig.st_02_Submask.getIp());
        sb.append("\nGatway: ").append(netConfig.st_03_Gateway.getIp());
        sb.append("\nHttpPort: ").append(netConfig.st_04_HttpPort);
        sb.append("\nTCPPort: ").append(netConfig.st_05_TCPPort);
        sb.append("\nSSLPort: ").append(netConfig.st_06_SSLPort);
        sb.append("\nUDPPort: ").append(netConfig.st_07_UDPPort);
        sb.append("\nMaxConn: ").append(netConfig.st_08_MaxConn);
        sb.append("\nMonMode: ").append(netConfig.st_09_MonMode);
        sb.append("\nMaxBps: ").append(netConfig.st_10_MaxBps);
        sb.append("\nTransferPlan: ").append(netConfig.st_11_TransferPlan);
        sb.append("\nbUseHSDownLoad: ").append(netConfig.st_12_bUseHSDownLoad);
        sb.append("\nsMac: ").append(G.ToString(netConfig.st_13_sMac));
        sb.append("\nZarg0: ").append(G.ToString(netConfig.st_14_Zarg0));

        Log.d(tag, info + sb.toString());
    }

    public void testGetEthernetConfig() throws Exception {
        SDK_CONFIG_NET_COMMON netConfig = new SDK_CONFIG_NET_COMMON();
        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't get the config", get);
        printEthernetConfig("testGetEthernetConfig","" , netConfig);
    }

    public void testSetEthernetConfig() throws Exception {
        SDK_CONFIG_NET_COMMON netConfig = new SDK_CONFIG_NET_COMMON();
        boolean get1 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't get the original config", get1);
        printEthernetConfig("testSetEthernetConfig", "Original: ", netConfig);

        netConfig.st_03_Gateway.setIp(192, 168, 1, 50);
        boolean set1 = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't set the config", set1);

        netConfig = new SDK_CONFIG_NET_COMMON();
        boolean get2 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't get the modified config", get2);
        printEthernetConfig("testSetEthernetConfig", "Modified: ", netConfig);
        assertEquals("Gateways are different", "192.168.1.50", netConfig.st_03_Gateway.getIp());

        netConfig.st_03_Gateway.setIp(192, 168, 1, 114);
        boolean set2 = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't set back the original config", set2);

        netConfig = new SDK_CONFIG_NET_COMMON();
        boolean get3 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, netConfig);
        assertTrue("Couldn't get the original config again", get3);
        printEthernetConfig("testSetEthernetConfig", "Modified to original: ", netConfig);
    }

    public void testGetWifiConfig() throws Exception {

        SDK_NetWifiConfig netWifiConfig = new SDK_NetWifiConfig();
        boolean success = NetSdk.getInstance().H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI, -1, netWifiConfig, 2500);
        Log.d("testGetWifiConfig", String.valueOf(netWifiConfig));
        assertTrue(success);
    }

    public void testSetWifiConfig() throws Exception {

        SDK_NetWifiConfig netWifiConfig = new SDK_NetWifiConfig();
        NetSdk.getInstance().H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI, -1, netWifiConfig, 2500);

        netWifiConfig.bEnable = true;
        netWifiConfig.HostIP = "192.168.1.11";
        netWifiConfig.sSSID = "GGAT";
        NetSdk.getInstance().H264DVRSetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI, -1, netWifiConfig, 2500);

        netWifiConfig = new SDK_NetWifiConfig();
        boolean success = NetSdk.getInstance().H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI, -1, netWifiConfig, 2500);

        assertTrue(success);

    }

    public void testGetWifiAPList() throws Exception {
        SDK_NetWifiDeviceAll mNetWifiDeviceAll = new SDK_NetWifiDeviceAll();
        DevConfig mDevConfig = new DevConfig(loginId);
        byte[] buf = mDevConfig.getConfig(MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI_AP_LIST, -1, G.Sizeof(mNetWifiDeviceAll));
        if(buf != null) {
            G.BytesToObj(mNetWifiDeviceAll, buf);
            com.xm.javaclass.SDK_NetWifiConfig mSDK_NetWifiConfig = new com.xm.javaclass.SDK_NetWifiConfig();
            Log.d("testGetWifiAPList", "size:" + G.Sizeof(mSDK_NetWifiConfig));
            buf = mDevConfig.getConfig(MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI, -1, G.Sizeof(mSDK_NetWifiConfig));
            if(buf != null) {
                G.BytesToObj(mSDK_NetWifiConfig, buf);
                assertTrue(mNetWifiDeviceAll.st_0_nDevNumber > 0);
                for(int i = 0 ; i < mNetWifiDeviceAll.st_0_nDevNumber; ++i) {
                    SDK_NetWifiDevice device = mNetWifiDeviceAll.st_1_vNetWifiDeviceAll[i];
                    Log.d("testGetWifiAPList", "ssid:"+G.ToString(device.st_0_sSSID));
                }
            }
        }
    }

//    public void testGetWifiConfig() throws Exception {
//
//        mSDK_NetWifiConfig.st_00_bEnable = true;
//        mSDK_NetWifiConfig.st_03_nChannel = 0;
//        mSDK_NetWifiConfig.st_04_sNetType = device.st_3_sNetType;
//        mSDK_NetWifiConfig.st_05_sEncrypType = device.st_4_sEncrypType;
//        mSDK_NetWifiConfig.st_06_sAuth = device.st_5_sAuth;
//        G.SetValue(mSDK_NetWifiConfig.st_01_sSSID, device.st_0_sSSID);
//        com.basic.G.SetValue(mSDK_NetWifiConfig.st_08_sKeys, "");
//        NetSdk.getInstance().H264DVRSetDevConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI,-1, com.basic.G.ObjToBytes(mSDK_NetWifiConfig), 3000);
//
//    }

    public void testGetWifiStatusConfig() throws Exception {

        SDK_WifiStatusInfo wifiStatusInfo = new SDK_WifiStatusInfo();
        DevConfig devConfig = new DevConfig(loginId);
        byte[] buf = devConfig.getConfig(MyConfig.SdkConfigType.E_SDK_WIFI_STATUS, -1, G.Sizeof(wifiStatusInfo));

        if(buf != null) {
            G.BytesToObj(wifiStatusInfo, buf);
            Log.d("testGetWifiStatusConfig", "Status:"+wifiStatusInfo.st_0_connectStatus);
            assertTrue(true);
        }
        assertTrue("Unable to get Wifi Status", false);
    }

    private void printUPNPConfig(String tag, String info, SDK_NetUPNPConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nEnabled: ").append(config.st_0_bEnable);
        sb.append("\nState: ").append(config.st_1_bState);
        sb.append("\nArg0: ").append(G.ToString(config.st_2_arg0));
        sb.append("\nHTTP port: ").append(config.st_3_iHTTPPort);
        sb.append("\nMedia port: ").append(config.st_4_iMediaPort);
        sb.append("\nMobile port: ").append(config.st_5_iMobliePort);

        Log.d(tag, info + sb.toString());
    }

    public void testGetUPNPConfig() throws Exception {
        SDK_NetUPNPConfig config = new SDK_NetUPNPConfig();
        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP, config);
        assertTrue("Couldn't get SDK_NetUPNPConfig", get);

        printUPNPConfig("testGetUPNPConfig", "", config);
    }

    public void testSetUPNPConfig() throws Exception {
        final SDK_NetUPNPConfig originalConfig = new SDK_NetUPNPConfig();
        final boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP, originalConfig);
        assertTrue("Couldn't get SDK_NetUPNPConfig", get);

        printUPNPConfig("testSetUPNPConfig", "Original: \n", originalConfig);

        SDK_NetUPNPConfig modifiedConfig = new SDK_NetUPNPConfig();
        modifiedConfig.st_0_bEnable = !originalConfig.st_0_bEnable; // modified
        modifiedConfig.st_1_bState = originalConfig.st_1_bState;
        modifiedConfig.st_2_arg0 = originalConfig.st_2_arg0;
        modifiedConfig.st_3_iHTTPPort = originalConfig.st_3_iHTTPPort;
        modifiedConfig.st_4_iMediaPort = originalConfig.st_4_iMediaPort;
        modifiedConfig.st_5_iMobliePort = originalConfig.st_5_iMobliePort;

        final boolean set = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP, modifiedConfig);
        assertTrue("Not set", set);

        modifiedConfig = new SDK_NetUPNPConfig();
        final boolean get2 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP, originalConfig);
        assertTrue("Couldn't get SDK_NetUPNPConfig", get2);

        printUPNPConfig("testSetUPNPConfig", "Modified: \n", modifiedConfig);

        assertNotSame("Enable not set!", originalConfig.st_0_bEnable, modifiedConfig.st_0_bEnable);

        final boolean set2 = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_UPNP, originalConfig);
        assertTrue("Not set back to original", set2);

    }

    public void testGetNatConfig() throws Exception {
        SDK_NatConfig natConfig = new SDK_NatConfig();
        DevConfig devConfig = new DevConfig(loginId);
        final byte[] buf = devConfig.getConfig(MyConfig.SdkConfigType.E_SDK_CONFIG_NAT, 0, G.Sizeof(natConfig));
        if (buf != null) {
            G.BytesToObj(natConfig, buf);
            Log.d("TEST", "bEnable:" + natConfig.st_0_bEnable + "\n"
                    + "MTU:" + natConfig.st_1_nMTU + "\n"
                    + "serverAddr:" + new String(natConfig.st_2_serverAddr) + "\n"
                    + "serverPort:" + natConfig.st_3_serverPort);

        }
    }

    public void testSetNavConfig() throws Exception {
        DevConfig devConfig = new DevConfig(loginId);
        SDK_NatConfig natConfig = new SDK_NatConfig();
        natConfig.st_0_bEnable = 1;
        natConfig.st_1_nMTU = 1280; //1280
        G.SetValue(natConfig.st_2_serverAddr, "200.98.128.50".getBytes()); //"200.98.128.50"
        natConfig.st_3_serverPort = 8000; //8000
        boolean ret = devConfig.setConfig(MyConfig.SdkConfigType.E_SDK_CONFIG_NAT, -1, G.ObjToBytes(natConfig));

        testGetNatConfig();

        assertTrue(ret);

    }

    public void printDDNSConfig(String tag, String info, SDK_NetDDNSConfig ddnsConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nEnabled: ").append(ddnsConfig.st_0_Enable);
        sb.append("\nOnline: ").append(ddnsConfig.st_1_Online);
        sb.append("\narg0: ").append(G.ToString(ddnsConfig.st_4_arg0));
        sb.append("\nDDNSKey: ").append(G.ToString(ddnsConfig.st_2_DDNSKey));
        sb.append("\nHostName: ").append(G.ToString(ddnsConfig.st_3_HostName));

        SDK_RemoteServerConfig serverConfig = ddnsConfig.st_5_Server;

        sb.append("\nServerName: ").append(G.ToString(serverConfig.st_0_ServerName));
        sb.append("\nServerIP: ").append(G.ToString(serverConfig.st_1_ip.st_0_ip));
        sb.append("\nServerPort: ").append(serverConfig.st_2_Port);
        sb.append("\nServerUserName: ").append(G.ToString(serverConfig.st_3_UserName));
        sb.append("\nServerPassword: ").append(G.ToString(serverConfig.st_4_Password));
        sb.append("\nServerAnonymity: ").append(serverConfig.st_5_Anonymity);

        Log.d(tag, info + sb.toString());
    }

    public void testGetDDNSConfig() throws Exception {
        SDK_NetDDNSConfigALL config = new SDK_NetDDNSConfigALL();
        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS, config);
        assertTrue("Couldn't get SDK_NetDDNSConfigALL", get);

        for(int i = 0; i < config.st_0_ddnsConfig.length; i++) {
            SDK_NetDDNSConfig ddnsConfig = config.st_0_ddnsConfig[i];
            if (ddnsConfig == null) break;
            Log.d("testGetDDNSConfig", "ddnsConfig num " + i);
            printDDNSConfig("testGetDDNSConfig", "", ddnsConfig);
        }
    }

    public void testSetDDNSConfig() throws Exception {
        SDK_NetDDNSConfigALL config = new SDK_NetDDNSConfigALL();
        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS, config);
        assertTrue("Couldn't get SDK_NetDDNSConfigALL", get);
        printDDNSConfig("testSetDDNSConfig", "dsa",  config.st_0_ddnsConfig[0]);

        boolean enabled = config.st_0_ddnsConfig[0].st_0_Enable;

        SDK_RemoteServerConfig serverConfig = config.st_0_ddnsConfig[0].st_5_Server;

        G.SetValue( serverConfig.st_3_UserName, "Coffee");
        config.st_0_ddnsConfig[0].st_0_Enable = !enabled;
        config.st_0_ddnsConfig[0].st_5_Server = serverConfig;

        boolean set = mDeviceManager.setConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS, config);

        SDK_NetDDNSConfigALL configGet = new SDK_NetDDNSConfigALL();
        boolean get2 = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS, configGet);
        printDDNSConfig("testSetDDNSConfig", "dsa",  config.st_0_ddnsConfig[0]);

        assertTrue("Not set", set);
    }

    public void testPTV() throws Exception {
       //mDeviceManager.testPTV(device);
    }

    public void testGetDDNSInfo() throws Exception {
        fail("Method \"H264DVRGetDDNSInfo\" not found");
    }

    public void testGetCloudConfig() throws Exception {
        SDK_NatConfig config = new SDK_NatConfig();
        boolean get = mDeviceManager.getConfig2(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_NAT, config);
        StringBuilder sb = new StringBuilder();

        sb.append("\nEnabled: ").append(config.st_0_bEnable);
        sb.append("\nMTU: ").append(config.st_1_nMTU);
        sb.append("\nServerAddr: ").append(G.ToString(config.st_2_serverAddr));
        sb.append("\nServerPort: ").append(config.st_3_serverPort);

        Log.d("testGetCloudConfig", sb.toString());

        assertTrue("Couldn't get SDK_NatConfig", get);
    }

    public void testGetLogEventList() throws Exception {
        SDK_LogSearchCondition searchCondition = new SDK_LogSearchCondition();
        searchCondition.st_0_nType = 0;
        searchCondition.st_1_iLogPosition = 1;
        searchCondition.st_2_stBeginTime.st_0_year = 2014;
        searchCondition.st_2_stBeginTime.st_1_month = 12;
        searchCondition.st_2_stBeginTime.st_2_day = 15;
        searchCondition.st_2_stBeginTime.st_4_hour = 00;
        searchCondition.st_2_stBeginTime.st_5_minute = 00;
        searchCondition.st_2_stBeginTime.st_6_second = 00;
        searchCondition.st_3_stEndTime.st_0_year = 2014;
        searchCondition.st_3_stEndTime.st_1_month = 12;
        searchCondition.st_3_stEndTime.st_2_day = 15;
        searchCondition.st_3_stEndTime.st_4_hour = 23;
        searchCondition.st_3_stEndTime.st_5_minute = 59;
        searchCondition.st_3_stEndTime.st_6_second = 59;

        SDK_LogList logList = mDeviceManager.getEventList(device, searchCondition);

        assertTrue(logList.st_0_iNumLog > 0);

        Log.d("testGetLogEventList", "\nlogList.st_0_iNumLog: " + logList.st_0_iNumLog);
        for (int i = 0; i < logList.st_1_Logs.length; i++) {
            SDK_LogItem item = logList.st_1_Logs[i];
            StringBuilder sb = new StringBuilder();
            sb.append("\nLogItem.st_4_iLogPosition: ").append(item.st_4_iLogPosition);
            sb.append("\nLogItem.st_0_sType: ").append(G.ToString(item.st_0_sType));
            sb.append("\nLogItem.st_1_sUser: ").append(G.ToString(item.st_1_sUser));
            sb.append("\nLogItem.st_2_sData: ").append(G.ToString(item.st_2_sData));
            sb.append("\nLogItem.st_3_stLogTime: ").append(Utils.parseSDKTime(item.st_3_stLogTime));
            Log.d("testGetLogEventList", sb.toString());
        }
    }

    public void testSetConfigOverNet() throws Exception {

        SDK_CONFIG_NET_COMMON_V3 config = new SDK_CONFIG_NET_COMMON_V3();

        G.SetValue(config.st_14_sMac, macTest);
        G.SetValue(config.st_15_UserName, usernameTest);
        G.SetValue(config.st_16_Password, passwordTest);
        com.basic.G.SetValue(config.st_17_LocalMac, "00:00:00:00:00:00");

        config.st_18_nPasswordType = 1;
        config.st_01_HostIP.st_0_ip[0] = (byte)192;
        config.st_01_HostIP.st_0_ip[1] = (byte)168;
        config.st_01_HostIP.st_0_ip[2] = (byte)1;
        config.st_01_HostIP.st_0_ip[3] = (byte)20;

        config.st_03_Gateway.st_0_ip[0] = (byte)192;
        config.st_03_Gateway.st_0_ip[1] = (byte)168;
        config.st_03_Gateway.st_0_ip[2] = (byte)1;
        config.st_03_Gateway.st_0_ip[3] = (byte)144;

        config.st_02_Submask.st_0_ip[0] = (byte)255;
        config.st_02_Submask.st_0_ip[1] = (byte)255;
        config.st_02_Submask.st_0_ip[2] = (byte)254;
        config.st_02_Submask.st_0_ip[3] = (byte)0;


        Log.d("DevConfig", "error:" + NetSdk.getInstance().GetLastError());
        NetSdk.getInstance().SetConfigOverNet(MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, -1, G.ObjToBytes(config), 25000);
        assertTrue(NetSdk.getInstance().GetLastError() < 0);
    }

    public void testAlarmChan() throws Exception {
        NetSdk netSdk = NetSdk.getInstance();
        assertTrue("Couldn't open alarm channel", netSdk.SetupAlarmChan(loginId));
        assertTrue("Couldn't close alarm channel", netSdk.CloseAlarmChan(loginId));
    }

    public void testAlarmIn() throws Exception {
        SDK_AllAlarmIn mAllAlarmIn =  mDeviceManager.getAlarmInConfig(device);
        assertNotNull("Couldn't get alarm in config", mAllAlarmIn);

        final boolean originalValue = mAllAlarmIn.alarmInParam[0].bEnable;
        mAllAlarmIn.alarmInParam[0].bEnable = !originalValue;

        boolean set = mDeviceManager.setAllAlarmInConfig(device, mAllAlarmIn);
        assertTrue("Couldn't set alarm in config", set);

        mAllAlarmIn =  mDeviceManager.getAlarmInConfig(device);
        assertNotNull("Couldn't get alarm in config", mAllAlarmIn);

        boolean modifiedValue = mAllAlarmIn.alarmInParam[0].bEnable;

        assertNotSame("Not set!", originalValue, modifiedValue);
    }

    public void testAlarmOut() throws Exception {
        NetSdk mNetSdk = NetSdk.getInstance();
        SDK_AllAlarmOut mAllAlarmOut = new SDK_AllAlarmOut();
        boolean get = mNetSdk.H264DVRGetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_OUT, -1,mAllAlarmOut, 5000);
        System.out.println("Get: "+get+" OutType: "+mAllAlarmOut.alarmOutParam[0].AlarmOutType);
        mAllAlarmOut.alarmOutParam[0].AlarmOutType = 1;
        boolean set = mNetSdk.H264DVRSetDevConfig(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_OUT, -1,
                mAllAlarmOut, 5000);

        Log.d("testAlarmOut", " set: " + set);
    }

    @Override
    protected void tearDown(){

    }

    public class DevConfig {
        private NetSdk mNetSdk;
        private long loginId;
        public DevConfig(long loginid) {
            mNetSdk = NetSdk.getInstance();
            this.loginId = loginid;
        }
        public byte[] getConfig(int commd,int chnid,int bufsize) {
            boolean bret = false;
            if(bufsize <= 0)
                return null;
            byte[] buf = new byte[bufsize];
            bret = mNetSdk.H264DVRGetDevConfig2(loginId, commd, chnid,buf,5000);
            if(bret)
                return buf;
            else {
                Log.d("test",  "error:" + mNetSdk.GetLastError());
                return null;
            }
        }
        public boolean setConfig(int commd,int chnid,byte[] config) {
            boolean bret = false;
            bret = mNetSdk.H264DVRSetDevConfig2(loginId, commd, chnid, config, 5000);
            if(!bret) {
                Log.d("test", "error:" + mNetSdk.GetLastError());
            }
            return bret;
        }
    }
}