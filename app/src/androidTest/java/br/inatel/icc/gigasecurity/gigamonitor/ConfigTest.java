package br.inatel.icc.gigasecurity.gigamonitor;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.xm.SDK_NetWifiConfig;
import com.xm.javaclass.SDK_CONFIG_NET_COMMON;
import com.xm.javaclass.SDK_NetWifiDeviceAll;

import br.inatel.icc.gigasecurity.gigamonitor.config.ethernet.EthernetConfig;
import br.inatel.icc.gigasecurity.gigamonitor.config.wifi.WifiConfig;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ConfigTest extends ApplicationTestCase<Application> {
    public ConfigTest() {
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

        ipTest = "192.168.1.169";
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


    public void configAbstract() throws Exception {
        WifiConfig wifiConfig = new WifiConfig();
        SDK_NetWifiConfig config = wifiConfig.getConfig(loginId);
        config.HostIP = "192.168.1.14";
        wifiConfig.setConfig(loginId);

        config = wifiConfig.getConfig(loginId);

        assertTrue(config != null);
    }


    public void testWifiAPList() throws Exception {
        WifiConfig wifiConfig = new WifiConfig();
        final SDK_NetWifiDeviceAll config = wifiConfig.getWifiAPListConfig().getConfig(loginId);
        wifiConfig.getWifiAPListConfig().fillFromNetSdkConfig(config);
//        config = wifiConfig.getConfig(loginId);

//        assertTrue(config != null);
    }


    public void testEthernetConfig() throws Exception {
        EthernetConfig ethernetConfig = new EthernetConfig();
        final SDK_CONFIG_NET_COMMON config = ethernetConfig.getConfig(loginId);
        ethernetConfig.fillFromNetSdkConfig(config);

//        config = wifiConfig.getConfig(loginId);

        assertTrue(ethernetConfig.getConfigEntity() != null);
    }

    @Override
    protected void tearDown(){

    }


}