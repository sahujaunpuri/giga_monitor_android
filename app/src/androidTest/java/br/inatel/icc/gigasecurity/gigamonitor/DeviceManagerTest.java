package br.inatel.icc.gigasecurity.gigamonitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;

import com.xm.ChnInfo;
import com.xm.NetSdk;
import com.xm.video.MySurfaceView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.activities.SurfaceViewTestActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
import br.inatel.icc.gigasecurity.gigamonitor.util.BitmapUtil;

/**
 * Created by Denis Vilela on 02/09/2014.
 * With collaboration of Rinaldo Bueno
 */
public class DeviceManagerTest extends ActivityInstrumentationTestCase2<SurfaceViewTestActivity> {

    private String ipTest;
    private int portTest;
    private String serialTest;

    private String usernameTest;
    private String passwordTest;


    private DeviceManager mDeviceManager;

    public DeviceManagerTest() {
        super(SurfaceViewTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mDeviceManager = DeviceManager.getInstance();

        ipTest = "192.168.1.32";
        portTest = 34567;
        serialTest = "7966afb78aed9678";

        usernameTest = "admin";
        passwordTest = "";

    }

    public void testSearchDevice() throws Exception {

        final ArrayList<Device> result = mDeviceManager.searchDevices();
        assertTrue("Devices not Found", result.size() > 0);
    }

    public void testLoginLAN() throws Exception {
        final Device device = loginLAN();
        assertTrue(device.getLoginID() >= 0);
    }

    private Device loginLAN() throws Exception {
        Device device = new Device();

        device.setIpAddress(ipTest);
        device.setTCPPort(portTest);
        device.setUsername(usernameTest);
        device.setPassword(passwordTest);

        final long loginId = mDeviceManager.loginOnDevice(device, LoginMethod.LAN);
        assertTrue("Unable to Lan Login (loginId=" + loginId + "). Are you in the right network? Device IP and Port are correct?", loginId > 0);
        return device;
    }

    public void testLoginCloud() throws Exception {
        Device device = new Device();

        device.setIpAddress(serialTest);
        device.setUsername(usernameTest);
        device.setPassword(passwordTest);

        final long loginId = mDeviceManager.loginOnDevice(device, LoginMethod.CLOUD);
        assertTrue("Unable to Cloud Login (loginId=" + loginId + ") Device Serial number is correct?", loginId > 0);
    }

    public void testSnapshot() throws Exception {
        final SurfaceViewTestActivity deviceListActivity = getActivity();
        final Context context = deviceListActivity.getApplicationContext();
        final MySurfaceView sv = deviceListActivity.getSurfaceViews()[0];
        sv.init(context, 0);

        Device device = loginLAN();

        Bitmap picture = mDeviceManager.takeSnapshot(device, 0);

        assertTrue("Unable to take snapshot (picture is null)", picture != null);
    }

    /*
    public void testSnapshotFromVideoData() throws Exception {
        final Device device = loginLAN();
        final long loginId = device.getLoginID();

        if (loginId == 0) {
            assertTrue("Unable to take snapshot (loginId is 0)", false);
            return;
        }

        NetSdk mNetSdk = NetSdk.getInstance(); //FIXME: change to deviceManager
        mNetSdk.SetupAlarmChan(loginId);
        mNetSdk.SetAlarmMessageCallBack();
        final ChnInfo chnInfo = new ChnInfo();
        chnInfo.ChannelNo = 0;
        chnInfo.nStream = 1;
        final long playHandle = mNetSdk.onRealPlay(0, loginId, chnInfo);
        if (playHandle > 0) {
            final MyVideoData videoData = new MyVideoData(getActivity(), 0);
            mNetSdk.setDataCallback(playHandle);
            videoData.initData();

            int count = 0;
            Bitmap bm = null;
            while (bm == null) {
                if (count >= 10) {
                    break;
                }

                try {
                    bm = videoData.getFrameBitmap();

                    if (bm != null) {
                        break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    Thread.sleep(500);
                    count++;
                }
            }
            BitmapUtil.saveIntoExternalStorage(getActivity(), bm);
            assertTrue("Unable to take snapshot (bm is null)", bm != null);
        }
    }
    */

    /*
    public void testVideoPause() throws Exception {
        final Device device = loginLAN();
        final long loginId = device.getLoginID();

        if (loginId == 0) {
            assertTrue("Unable to take snapshot (loginId is 0)", false);
            return;
        }

        NetSdk mNetSdk = NetSdk.getInstance(); //FIXME: change to deviceManager
        mNetSdk.SetupAlarmChan(loginId);
        mNetSdk.SetAlarmMessageCallBack();
        final ChnInfo chnInfo = new ChnInfo();
        chnInfo.ChannelNo = 0;
        chnInfo.nStream = 1;
        final long playHandle = mNetSdk.onRealPlay(0, loginId, chnInfo);
        if (playHandle > 0) {
            final MyVideoData videoData = new MyVideoData(getActivity(), 0);
            mNetSdk.setDataCallback(playHandle);
            videoData.initData();
        }
    }
    */

    public void testSnapshotFromSurface() throws Exception {
        final SurfaceViewTestActivity deviceListActivity = getActivity();
        final Context context = deviceListActivity.getApplicationContext();
        final MySurfaceView sv = deviceListActivity.getSurfaceViews()[0];
        sv.init(context, 0);
        sv.initRecord(BitmapUtil.getAlbumStorageDir(context).getPath());

        final Device device = loginLAN();
        final long loginId = device.getLoginID();

        if (loginId <= 0) {
            assertTrue("Unable to take snapshot (loginId is 0)", false);
            return;
        }

        NetSdk mNetSdk = NetSdk.getInstance();
        mNetSdk.SetupAlarmChan(loginId);
        mNetSdk.SetAlarmMessageCallBack();
        final ChnInfo chnInfo = new ChnInfo();
        chnInfo.ChannelNo = 0;
        chnInfo.nStream = 1;
        final long playHandle = mNetSdk.onRealPlay(0, loginId, chnInfo);
        mNetSdk.setDataCallback(playHandle);
        sv.initData();

        int count = 0;

        Bitmap picture = mDeviceManager.takeSnapshot(context, sv);

        while (picture == null) {
            if (count >= 10) {
                break;
            }

            picture = mDeviceManager.takeSnapshot(context, sv);

            if (picture != null) {
                break;
            }

            Thread.sleep(500);
            count++;
        }

        if (picture != null) {
            BitmapUtil.saveIntoExternalStorage(context, picture);
        }

        assertTrue("Unable to take snapshot (picture is null)", picture != null);
    }

//    public void testChangePassword() throws Exception {
//        final long result = mDeviceManager.changePassword("admin", "", "newPass");
//        assertEquals("Unable to Change Pass (result=" + result + ").", MyConfig.ModifyPwd.SUCCESS, result);
//    }

    public void testLogout() throws Exception {
        final Device device = loginLAN();
        final long success = mDeviceManager.logout(device);
        assertTrue(success != 0);
    }


}
