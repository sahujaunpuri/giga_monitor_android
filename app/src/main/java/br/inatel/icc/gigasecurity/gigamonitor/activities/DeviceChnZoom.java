package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.xm.ChnInfo;
import com.xm.video.MySurfaceView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class DeviceChnZoom extends ActionBarActivity {

    private MySurfaceView mySurfaceView;
    private Device mDevice;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_chn_zoom);

        initComponents();


    }

    private void initComponents() {
        mySurfaceView = (MySurfaceView) findViewById(R.id.surface_view_zoom);
        mDeviceManager = DeviceManager.getInstance();

        if(getIntent().getExtras() != null){
            mDevice = (Device) getIntent().getExtras().getSerializable("device");

            int mySurfaceViewID = DeviceListActivity.mySurfaceViewID++;
            int chnPosition = getIntent().getExtras().getInt("chnPosition");

            mySurfaceView.init(DeviceChnZoom.this, mySurfaceViewID);

            final long loginID = mDevice.getLoginID();
            final ChnInfo chnInfo = new ChnInfo();
            chnInfo.ChannelNo = chnPosition;
            final MySurfaceView finalMySurfaceView = mySurfaceView;


            mDeviceManager.startDeviceVideo2(loginID, mySurfaceViewID, chnInfo, new DeviceManager.StartDeviceVideoListener() {
                @Override
                public void onSuccessStartDevice(long handleID) {
                    finalMySurfaceView.initData();
                }

                @Override
                public void onErrorStartDevice() {
                    Log.w("Error", "Start Device Video - Login ID: " + mDevice.getLoginID());
                }
            });

            //mySurfaceView.ma

        } else {
            Toast.makeText(this, "Erro ao abrir dispositivo.", Toast.LENGTH_LONG).show();
        }
    }

}
