package br.inatel.icc.gigasecurity.gigamonitor.config.about;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class AboutActivity extends ActionBarActivity {

    /**
     * Labels
     */
    private TextView mSerialNumberLabel, mSoftwareLabel, mVideoChannelLabel, mMacAddressLabel, mVideoModeLabel, mTextViewBack;

    /**
     * Texts
     */
    private TextView mSerialNumberTxt, mSoftwareTxt, mVideoChannelTxt, mMacAddressTxt, mVideoModeTxt, mTextBuildTime, mDSS, mNatCodeTxt, mNatStatusTxt;

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

        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().hide();
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
        mVideoChannelLabel = (TextView) findViewById(R.id.label_video_channel);
        mMacAddressLabel   = (TextView) findViewById(R.id.label_mac_address);
        mVideoModeLabel    = (TextView) findViewById(R.id.label_video_mode);
        mSerialNumberTxt   = (TextView) findViewById(R.id.serial_number_txt);
        mSoftwareTxt       = (TextView) findViewById(R.id.software_txt);
        mVideoChannelTxt   = (TextView) findViewById(R.id.video_channel_txt);
        mMacAddressTxt     = (TextView) findViewById(R.id.mac_address_txt);
        mVideoModeTxt      = (TextView) findViewById(R.id.video_mode_txt);
        mTextViewBack      = (TextView) findViewById(R.id.text_view_back);
        mTextBuildTime     = (TextView) findViewById(R.id.txt_build_time);
        mDSS               = (TextView) findViewById(R.id.dss_txt);
        mNatCodeTxt        = (TextView) findViewById(R.id.natcode_txt);
        mNatStatusTxt      = (TextView) findViewById(R.id.natstatus_txt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSerialNumberTxt.setHint(mDevice.getSerialNumber());
        mSoftwareTxt.setHint(mDevice.getSoftwareVersion());
        mVideoChannelTxt.setHint(String.valueOf(mDevice.getChannelNumber()));
        mMacAddressTxt.setHint(mDevice.getMacAddress());
        mVideoModeTxt.setHint(mDevice.getConnectionMethodString());
        mTextBuildTime.setHint(mDevice.getBuildTime());
        mDSS.setHint(mDevice.dss ? "Sim" : "Não");
        mNatCodeTxt.setHint(mDevice.getNatCode());
        mNatStatusTxt.setHint(mDevice.getNatStatus().equals("Conneted") ? "Conectado" : "Conexão");
    }
}
