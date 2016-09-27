package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by Denis Vilela on 02/09/2014.
 * With collaboration of Rinaldo Bueno
 */
public class DeviceListPreferencesTest extends ActivityInstrumentationTestCase2<SurfaceViewTestActivity> {

    private static String TAG = DeviceListPreferencesTest.class.getSimpleName();

    private String ipTest;
    private int portTest;
    private String serialTest;

    private String usernameTest;
    private String passwordTest;


    private DeviceManager mDeviceManager;

    public DeviceListPreferencesTest() {
        super(SurfaceViewTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mDeviceManager = DeviceManager.getInstance();

        ipTest = "192.168.1.19";
        portTest = 34567;
        serialTest = "f556303ed557cdf4";

        usernameTest = "admin";
        passwordTest = "1";
    }


    public void testAddDeviceListPrefereces() throws Exception {
        ArrayList<Device> devices = new ArrayList<Device>();
        Device device = new Device();

        device.setIpAddress(ipTest);
        device.setTCPPort(portTest);
        device.setUsername(usernameTest);
        device.setPassword(passwordTest);
        device.setHostname("Test1");

        Device device2 = new Device();

        device2.setIpAddress("192.168.1.20");
        device2.setTCPPort(portTest);
        device2.setSerialNumber(serialTest);
        device2.setUsername(usernameTest);
        device2.setPassword(passwordTest);
        device.setHostname("Test2");

        devices.add(device);
        devices.add(device2);

        boolean success = mDeviceManager.saveDevices(getActivity().getBaseContext(), devices);
        assertTrue("Unable to save Devices.", success);
    }


    public void testLoadDeviceListPrefereces() throws Exception {
        ArrayList<Device> list = mDeviceManager.loadDevices(getActivity().getBaseContext());
        Log.d(TAG, "list size:" + list.size() );
        Log.d(TAG, "list:" + list );
        boolean success = list.size() > 0;

        assertTrue("Unable to load Devices. You sure that is any device to load?", success);
    }

//    @Suppress
    public void testRemoveDeviceListPrefereces() throws Exception {
        boolean success = mDeviceManager.cleanDevices(getActivity().getBaseContext());
        assertTrue("Unable to clean Devices.", success);

        ArrayList<Device> list = mDeviceManager.loadDevices(getActivity().getBaseContext());
        success = list.size() == 0;

        assertTrue("Unable to clean Devices.", success);
    }

}
