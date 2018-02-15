package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class AboutActivity extends ActionBarActivity {

    /**
     * Labels
     */
    private TextView mSerialNumberLabel, mSoftwareLabel, mHardwareLabel, mVideoChannelLabel, mMacAddressLabel, mVideoModeLabel;

    /**
     * Texts
     */
    private TextView mSerialNumberTxt, mSoftwareTxt, mHardwareTxt, mVideoChannelTxt, mMacAddressTxt, mVideoModeTxt, mDSS;

    private Device mDevice;
    private DeviceManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        mSerialNumberLabel = (TextView) findViewById(R.id.label_serial_number);
        mSoftwareLabel     = (TextView) findViewById(R.id.label_software);
        mHardwareLabel     = (TextView) findViewById(R.id.label_hardware);
        mVideoChannelLabel = (TextView) findViewById(R.id.label_video_channel);
        mMacAddressLabel   = (TextView) findViewById(R.id.label_mac_address);
        mVideoModeLabel    = (TextView) findViewById(R.id.label_video_mode);
        mSerialNumberTxt   = (TextView) findViewById(R.id.serial_number_txt);
        mSoftwareTxt       = (TextView) findViewById(R.id.software_txt);
        mHardwareTxt       = (TextView) findViewById(R.id.hardware_txt);
        mVideoChannelTxt   = (TextView) findViewById(R.id.video_channel_txt);
        mMacAddressTxt     = (TextView) findViewById(R.id.mac_address_txt);
        mVideoModeTxt      = (TextView) findViewById(R.id.video_mode_txt);
        mDSS                = (TextView) findViewById(R.id.dss_txt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSerialNumberTxt.setHint(mDevice.getSerialNumber());
        mSoftwareTxt.setHint(mDevice.getSoftwareVersion());
        mHardwareTxt.setHint(mDevice.getHardwareVersion());
        mVideoChannelTxt.setHint(String.valueOf(mDevice.getChannelNumber()));
        mMacAddressTxt.setHint(mDevice.getMacAddress());
        mVideoModeTxt.setHint(mDevice.getConnectionMethodString());
        mDSS.setHint(mDevice.dss ? "Sim" : "Não");
    }
}
