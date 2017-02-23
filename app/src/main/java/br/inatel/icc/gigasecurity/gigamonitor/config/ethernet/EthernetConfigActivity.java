package br.inatel.icc.gigasecurity.gigamonitor.config.ethernet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class EthernetConfigActivity extends ActionBarActivity implements OnCheckedChangeListener {

    private final String TAG = EthernetConfigActivity.class.getSimpleName();

    private Context mContext;
    private Device mDevice;
//    private EthernetConfig mConfig;
    private EditText mHostNameEditText, mHostIPEditText, mSubmaskEditText, mGatewayEditText, mMacEditText;
            //mHttpPortEditText, mTcpPortEditText, mSslPortEditText, mUdpPortEditText,
            //mMaxConnEditText, mMonModeEditText, mMaxBpsEditText, mTransferPlanEditText, mMacEditText,
            //mZarg0EditText;

    //private CheckBox mHSDowloadCheckBox;

    private String mHostName, mHostIP, mSubmask, mGateway, mMac, mMonMode, mArg0;
    private int mHttpPort, mTcpPort, mSslPort, mUdpPort, mMaxConn, mMaxBps, mTransferPlan;

    //private boolean mHSDownloadEnabled;

    private DeviceManager mManager;

    private ConfigListener mListener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
            initData();
            initViews();

        }

        @Override
        public void onSetConfig() {
            int messageId = R.string.saved;

            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.saveDevices(mContext);

            finish();
        }

        @Override
        public void onError(){
            int messageId = R.string.invalid_device_save;

            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.saveDevices(mContext);

            finish();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethernet_config);
        findViews();
        mContext = this;
        mManager = DeviceManager.getInstance();

//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//        }
        mDevice = mManager.findDeviceBySN((String) getIntent().getExtras().getSerializable("device"));
        mManager.getJsonConfig(mDevice, "NetWork.NetCommon", mListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        final EthernetCommonConfig commonConfig = mEthernetConfig.getCommonConfig();
//        commonConfig
//                .setHostName(mHostName)
//                .setHostIP(Utils.parseIp(mHostIP))
//                .setSubmask(Utils.parseIp(mSubmask))
//                .setGateway(Utils.parseIp(mGateway))
//                .setHttpPort(mHttpPort)
//                .setTcpPort(mTcpPort)
//                .setSslPort(mSslPort)
//                .setUdpPort(mUdpPort)
//                .setMaxConn(mMaxConn)
//                .setMonMode(mMonMode)
//                .setMaxBps(mMaxBps)
//                .setTransferPlan(mTransferPlan)
//                .setUseHSDownLoad(mHSDownloadEnabled)
//                .setMac(mMac)
//                .setZarg0(mArg0);
//
//        final EthernetDHCPConfig dhcpConfig = mEthernetConfig.getDHCPConfig();
//        dhcpConfig.setDhcp1Enabled(mDhcpEnabled);
//
//        final EthernetDNSConfig dnsConfig = mEthernetConfig.getDNSConfig();
//        dnsConfig.setPrimaryDNS(mPrimaryDns).setSecondaryDNS(mSecondaryDns);
//
//        mDevice.setCurrentConfig(mEthernetConfig);
//
//        outState.putSerializable("device", mDevice);
        super.onSaveInstanceState(outState);
    }

    private void findViews() {
        mHostNameEditText = (EditText) findViewById(R.id.edit_text_ethernet_host_name);
        mHostIPEditText = (EditText) findViewById(R.id.edit_text_ethernet_host_ip);
        mSubmaskEditText = (EditText) findViewById(R.id.edit_text_ethernet_submask);
        mGatewayEditText = (EditText) findViewById(R.id.edit_text_ethernet_gateway);
        /*mHttpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_http_port);
        mTcpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_tcp_port);
        mSslPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_ssl_port);
        mUdpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_udp_port);
        mMaxConnEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_conn);
        mMonModeEditText = (EditText) findViewById(R.id.edit_text_ethernet_mon_mode);
        mMaxBpsEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_bps);
        mTransferPlanEditText = (EditText) findViewById(R.id.edit_text_ethernet_transfer_plan);*/
        mMacEditText = (EditText) findViewById(R.id.edit_text_ethernet_mac);
        //mZarg0EditText = (EditText) findViewById(R.id.edit_text_ethernet_arg0);
        //mHSDowloadCheckBox = (CheckBox) findViewById(R.id.edit_text_ethernet_use_hs_download);
        //mHSDowloadCheckBox.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mHostName = mDevice.getHostname();
        mHostIP = mDevice.getIpAddress();
        mSubmask = mDevice.getSubmask();
        mGateway = mDevice.getGateway();
        mHttpPort = mDevice.getHttpPort();
        mTcpPort = mDevice.getTCPPort();
        mSslPort = mDevice.getSslPort();
        mUdpPort = mDevice.getUdpPort();
        mMaxConn = mDevice.getTcpMaxConn();
        mMonMode = mDevice.getMonMode();
        mMaxBps = mDevice.getMaxBPS();
        mTransferPlan = mDevice.getTransferPlan();
        mMac = mDevice.getMacAddress();
//        mHSDownloadEnabled = commonConfig.useHSDownload();
//        mArg0 = commonConfig.getZarg0();

        /*
        final EthernetDNSConfig dnsConfig = mEthernetConfig.getDNSConfig();
        mPrimaryDns = dnsConfig.getPrimaryDNS();
        mSecondaryDns = dnsConfig.getSecondaryDNS();

        final EthernetDHCPConfig dhcpConfig = mEthernetConfig.getDHCPConfig();
        mDhcpEnabled = dhcpConfig.isDhcp1Enabled() || dhcpConfig.isDhcp2Enabled()
                || dhcpConfig.isDhcp3Enabled() || dhcpConfig.isDhcp4Enabled();*/
    }

    private void initViews() {
        ((Activity) this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHostNameEditText.setText(mHostName);
                mHostIPEditText.setText(mHostIP);
                mSubmaskEditText.setText(mSubmask);
                mGatewayEditText.setText(mGateway);
                /*mHttpPortEditText.setText(String.valueOf(mHttpPort));
                mTcpPortEditText.setText(String.valueOf(mTcpPort));
                mSslPortEditText.setText(String.valueOf(mSslPort));
                mUdpPortEditText.setText(String.valueOf(mUdpPort));
                mMaxConnEditText.setText(String.valueOf(mMaxConn));
                mMonModeEditText.setText(String.valueOf(mMonMode));
                mMaxBpsEditText.setText(String.valueOf(mMaxBps));
                mTransferPlanEditText.setText(String.valueOf(mTransferPlan));*/
                //mHSDowloadCheckBox.setChecked(mHSDownloadEnabled);
                mMacEditText.setText(mMac);
                //mZarg0EditText.setText(mArg0);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            /*case R.id.edit_text_ethernet_use_hs_download:
                enableHighSpeedDownload(isChecked);
                break;*/
            default:
                break;
        }
    }

    private void enableHighSpeedDownload(boolean enabled) {
        //mHSDownloadEnabled = enabled;
    }

    private boolean isFieldsValid() {
        boolean hasError = false;

        // TODO

        return !hasError;
    }

    private void save() {

        mDevice.setHostname(mHostNameEditText.getText().toString());
        mDevice.setIpAddress(mHostIPEditText.getText().toString());
        mDevice.setSubmask(mSubmaskEditText.getText().toString());
        mDevice.setGateway(mGatewayEditText.getText().toString());

        /*commonConfig.setHttpPort(Integer.valueOf(mHttpPortEditText.getText().toString()));
        commonConfig.setTcpPort(Integer.valueOf(mTcpPortEditText.getText().toString()));
        commonConfig.setSslPort(Integer.valueOf(mSslPortEditText.getText().toString()));
        commonConfig.setUdpPort(Integer.valueOf(mUdpPortEditText.getText().toString()));
        commonConfig.setMaxConn(Integer.valueOf(mMaxConnEditText.getText().toString()));
        commonConfig.setMonMode(Integer.valueOf(mMonModeEditText.getText().toString()));
        commonConfig.setMaxBps(Integer.valueOf(mMaxBpsEditText.getText().toString()));
        commonConfig.setTransferPlan(Integer.valueOf(mTransferPlanEditText.getText().toString()));*/
        //commonConfig.setUseHSDownLoad(mHSDowloadCheckBox.isChecked());
        //commonConfig.setMac(mMacEditText.getText().toString());
        //commonConfig.setZarg0(mZarg0EditText.getText().toString());

        mManager.setEthernetConfig(mDevice);
    }

    private void startDeviceListActivity() {
        Intent i = new Intent(this, DeviceListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.config_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_config_save:
                Utils.hideKeyboard(this);
                if (isFieldsValid()) {
                    save();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
