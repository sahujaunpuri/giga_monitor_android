//package br.inatel.icc.gigasecurity.gigamonitor.config.ethernet;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
//public class EthernetConfigActivity extends ActionBarActivity implements OnCheckedChangeListener {
//
//    // Debug tag
//    // private final String TAG = EthernetConfigActivity.class.getSimpleName();
//
//    private Context mContext;
//
//    private Device mDevice;
//
//    private EthernetConfig mConfig;
//
//    private EditText mHostNameEditText, mHostIPEditText, mSubmaskEditText, mGatewayEditText, mMacEditText;
//            //mHttpPortEditText, mTcpPortEditText, mSslPortEditText, mUdpPortEditText,
//            //mMaxConnEditText, mMonModeEditText, mMaxBpsEditText, mTransferPlanEditText, mMacEditText,
//            //mZarg0EditText;
//
//    //private CheckBox mHSDowloadCheckBox;
//
//    private String mHostName, mHostIP, mSubmask, mGateway, mMac, mArg0;
//
//    private int mHttpPort, mTcpPort, mSslPort, mUdpPort, mMaxConn, mMonMode, mMaxBps, mTransferPlan;
//
//    //private boolean mHSDownloadEnabled;
//
//    private DeviceManager mManager;
//
//    @Override
//    protected void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ethernet_config);
//        findViews();
//        mContext = this;
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            mManager = DeviceManager.getInstance();
//
//            mConfig = mDevice.getEthernetConfig();
//            mConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
//                @Override
//                public void onPreFinish(Boolean success) {
//                    if (!success) {
//                        String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);
//
//                        Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
//                        finish();
//                        return;
//                    }
//                    initData();
//                    initViews();
//                }
//
//                @Override
//                public void onFinish(Boolean success) {
//                }
//            });
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mConfig = mDevice.getEthernetConfig();
//            initData();
//            initViews();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
////        final EthernetCommonConfig commonConfig = mEthernetConfig.getCommonConfig();
////        commonConfig
////                .setHostName(mHostName)
////                .setHostIP(Utils.parseIp(mHostIP))
////                .setSubmask(Utils.parseIp(mSubmask))
////                .setGateway(Utils.parseIp(mGateway))
////                .setHttpPort(mHttpPort)
////                .setTcpPort(mTcpPort)
////                .setSslPort(mSslPort)
////                .setUdpPort(mUdpPort)
////                .setMaxConn(mMaxConn)
////                .setMonMode(mMonMode)
////                .setMaxBps(mMaxBps)
////                .setTransferPlan(mTransferPlan)
////                .setUseHSDownLoad(mHSDownloadEnabled)
////                .setMac(mMac)
////                .setZarg0(mArg0);
////
////        final EthernetDHCPConfig dhcpConfig = mEthernetConfig.getDHCPConfig();
////        dhcpConfig.setDhcp1Enabled(mDhcpEnabled);
////
////        final EthernetDNSConfig dnsConfig = mEthernetConfig.getDNSConfig();
////        dnsConfig.setPrimaryDNS(mPrimaryDns).setSecondaryDNS(mSecondaryDns);
////
////        mDevice.setEthernetConfig(mEthernetConfig);
////
////        outState.putSerializable("device", mDevice);
//        super.onSaveInstanceState(outState);
//    }
//
//    private void findViews() {
//        mHostNameEditText = (EditText) findViewById(R.id.edit_text_ethernet_host_name);
//        mHostIPEditText = (EditText) findViewById(R.id.edit_text_ethernet_host_ip);
//        mSubmaskEditText = (EditText) findViewById(R.id.edit_text_ethernet_submask);
//        mGatewayEditText = (EditText) findViewById(R.id.edit_text_ethernet_gateway);
//        /*mHttpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_http_port);
//        mTcpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_tcp_port);
//        mSslPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_ssl_port);
//        mUdpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_udp_port);
//        mMaxConnEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_conn);
//        mMonModeEditText = (EditText) findViewById(R.id.edit_text_ethernet_mon_mode);
//        mMaxBpsEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_bps);
//        mTransferPlanEditText = (EditText) findViewById(R.id.edit_text_ethernet_transfer_plan);*/
//        mMacEditText = (EditText) findViewById(R.id.edit_text_ethernet_mac);
//        //mZarg0EditText = (EditText) findViewById(R.id.edit_text_ethernet_arg0);
//        //mHSDowloadCheckBox = (CheckBox) findViewById(R.id.edit_text_ethernet_use_hs_download);
//        //mHSDowloadCheckBox.setOnCheckedChangeListener(this);
//    }
//
//    private void initData() {
//        final EthernetConfig commonConfig = mConfig;
//
//        mHostName = commonConfig.getHostName();
//        mHostIP = commonConfig.getHostIP();
//        mSubmask = commonConfig.getSubmask();
//        mGateway = commonConfig.getGateway();
//        mHttpPort = commonConfig.getHttpPort();
//        mTcpPort = commonConfig.getTcpPort();
//        mSslPort = commonConfig.getSslPort();
//        mUdpPort = commonConfig.getUdpPort();
//        mMaxConn = commonConfig.getMaxConn();
//        mMonMode = commonConfig.getMonMode();
//        mMaxBps = commonConfig.getMaxBps();
//        mTransferPlan = commonConfig.getTransferPlan();
//        //mHSDownloadEnabled = commonConfig.useHSDownload();
//        mMac = commonConfig.getMac();
//        mArg0 = commonConfig.getZarg0();
//
//        /*
//        final EthernetDNSConfig dnsConfig = mEthernetConfig.getDNSConfig();
//        mPrimaryDns = dnsConfig.getPrimaryDNS();
//        mSecondaryDns = dnsConfig.getSecondaryDNS();
//
//        final EthernetDHCPConfig dhcpConfig = mEthernetConfig.getDHCPConfig();
//        mDhcpEnabled = dhcpConfig.isDhcp1Enabled() || dhcpConfig.isDhcp2Enabled()
//                || dhcpConfig.isDhcp3Enabled() || dhcpConfig.isDhcp4Enabled();*/
//    }
//
//    private void initViews() {
//        mHostNameEditText.setText(mHostName);
//        mHostIPEditText.setText(mHostIP);
//        mSubmaskEditText.setText(mSubmask);
//        mGatewayEditText.setText(mGateway);
//        /*mHttpPortEditText.setText(String.valueOf(mHttpPort));
//        mTcpPortEditText.setText(String.valueOf(mTcpPort));
//        mSslPortEditText.setText(String.valueOf(mSslPort));
//        mUdpPortEditText.setText(String.valueOf(mUdpPort));
//        mMaxConnEditText.setText(String.valueOf(mMaxConn));
//        mMonModeEditText.setText(String.valueOf(mMonMode));
//        mMaxBpsEditText.setText(String.valueOf(mMaxBps));
//        mTransferPlanEditText.setText(String.valueOf(mTransferPlan));*/
//        //mHSDowloadCheckBox.setChecked(mHSDownloadEnabled);
//        mMacEditText.setText(mMac);
//        //mZarg0EditText.setText(mArg0);
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        switch (buttonView.getId()) {
//            /*case R.id.edit_text_ethernet_use_hs_download:
//                enableHighSpeedDownload(isChecked);
//                break;*/
//            default:
//                break;
//        }
//    }
//
//    private void enableHighSpeedDownload(boolean enabled) {
//        //mHSDownloadEnabled = enabled;
//    }
//
//    private boolean isFieldsValid() {
//        boolean hasError = false;
//
//        // TODO
//
//        return !hasError;
//    }
//
//    private void save() {
//        final EthernetConfig commonConfig = mConfig;
//
//        //commonConfig.setHostName(mHostNameEditText.getText().toString());
//        commonConfig.setHostIP(Utils.parseIp(mHostIPEditText.getText().toString()));
//        commonConfig.setSubmask(Utils.parseIp(mSubmaskEditText.getText().toString()));
//        commonConfig.setGateway(Utils.parseIp(mGatewayEditText.getText().toString()));
//        /*commonConfig.setHttpPort(Integer.valueOf(mHttpPortEditText.getText().toString()));
//        commonConfig.setTcpPort(Integer.valueOf(mTcpPortEditText.getText().toString()));
//        commonConfig.setSslPort(Integer.valueOf(mSslPortEditText.getText().toString()));
//        commonConfig.setUdpPort(Integer.valueOf(mUdpPortEditText.getText().toString()));
//        commonConfig.setMaxConn(Integer.valueOf(mMaxConnEditText.getText().toString()));
//        commonConfig.setMonMode(Integer.valueOf(mMonModeEditText.getText().toString()));
//        commonConfig.setMaxBps(Integer.valueOf(mMaxBpsEditText.getText().toString()));
//        commonConfig.setTransferPlan(Integer.valueOf(mTransferPlanEditText.getText().toString()));*/
//        //commonConfig.setUseHSDownLoad(mHSDowloadCheckBox.isChecked());
//        //commonConfig.setMac(mMacEditText.getText().toString());
//        //commonConfig.setZarg0(mZarg0EditText.getText().toString());
//
//        commonConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener() {
//            @Override
//            public void onFinish(Boolean success) {
//                int messageId = success ? R.string.saved : R.string.invalid_device_save;
//
//                messageId = R.string.saved;
//
//                Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
//
//                if (DeviceListActivity.previousGroup != -1) {
//
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setIpAddress(mHostIPEditText.getText().toString());
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setSubmask(mSubmaskEditText.getText().toString());
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setGateway(mGatewayEditText.getText().toString());
//                    /*DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setHttpPort(Integer.parseInt(mHttpPortEditText.getText().toString()));
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setTCPPort(Integer.parseInt(mTcpPortEditText.getText().toString()));
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setSslPort(Integer.parseInt(mSslPortEditText.getText().toString()));
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setUdpPort(Integer.parseInt(mUdpPortEditText.getText().toString()));
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setTcpMaxConn(Integer.parseInt(mMaxConnEditText.getText().toString()));
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setMonMode(mMonModeEditText.getText().toString());
//                    DeviceListActivity.mDevices.get(DeviceListActivity.previousGroup).setTransferPlan(Integer.parseInt(mTransferPlanEditText.getText().toString()));*/
//
//                    mManager.saveDevices(DeviceListActivity.mDevices);
//
//                    startDeviceListActivity();
//                }
//            }
//        });
//
//
//    }
//
//    private void startDeviceListActivity() {
//        Intent i = new Intent(this, DeviceListActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(i);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getMenuInflater().inflate(R.menu.config_form, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                return true;
//            case R.id.action_config_save:
//                Utils.hideKeyboard(this);
//                if (isFieldsValid()) {
//                    save();
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
