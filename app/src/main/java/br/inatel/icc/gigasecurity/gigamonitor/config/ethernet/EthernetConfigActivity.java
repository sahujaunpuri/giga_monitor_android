package br.inatel.icc.gigasecurity.gigamonitor.config.ethernet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;
import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mDeviceManager;

public class EthernetConfigActivity extends ActionBarActivity implements OnCheckedChangeListener {

    private final String TAG = EthernetConfigActivity.class.getSimpleName();

    private Context mContext;
    private Device mDevice;
    private Device temp;
    private int position;
//    private EthernetConfig mConfig;
    private EditText mHostNameEditText, mHostIPEditText, mSubmaskEditText, mGatewayEditText, mMacEditText, mHttpPortEditText, mTcpPortEditText, mSslPortEditText, mUdpPortEditText;
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
            mDeviceManager.currentContext = mContext;
            initData();
            initViews();

        }

        @Override
        public void onSetConfig() {
            int messageId = R.string.saved;

            mDevice = temp;
            mManager.logoutDevice(mDevice);
            mManager.getDevices().remove(position);
            mDevice.isLogged = false;
//            mDevice.setChannelNumber(0);
//            temp.isLogged = false;
//            temp.setChannelNumber(0);
            mManager.addDevice(temp, position);
//            mManager.updateSurfaceViewManager(position);
            mManager.collapse = position;
//            mManager.getExpandableListAdapter().collapseGroup(position);
//            mManager.getExpandableListAdapter().childViewHolder.get(position).recyclerViewChannels = null;


            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            task.cancel(true);
            finish();

        }

        @Override
        public void onError(){
//            int messageId = R.string.invalid_device_save;

//            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
//            mManager.saveData();
//            mManager.collapse = mManager.getDevices().indexOf(mDevice);

//            finish();
        }
    };

    private ProgressDialog pd;
    private AsyncTask<Void, Void, Void> task;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethernet_config);
        findViews();
        mContext = this;
        pd = new ProgressDialog(mContext);
        mManager = DeviceManager.getInstance();

        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
        position = mManager.getDevicePosition(mDevice);
        temp = new Device(mDevice);
        mManager.getJsonConfig(mDevice, "NetWork.NetCommon", mListener);

        task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
//                pd = new ProgressDialog(mContext);
                pd.setTitle("Configurando");
                pd.setMessage("Aguarde");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    mManager.setEthernetConfig(temp, mListener);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if(pd.isShowing())
                    pd.dismiss();
                mListener.onSetConfig();
            }
        };

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
        mHttpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_http_port);
        mTcpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_tcp_port);
        mSslPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_ssl_port);
        mUdpPortEditText = (EditText) findViewById(R.id.edit_text_ethernet_udp_port);
        /*mMaxConnEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_conn);
        mMonModeEditText = (EditText) findViewById(R.id.edit_text_ethernet_mon_mode);
        mMaxBpsEditText = (EditText) findViewById(R.id.edit_text_ethernet_max_bps);
        mTransferPlanEditText = (EditText) findViewById(R.id.edit_text_ethernet_transfer_plan);*/
        mMacEditText = (EditText) findViewById(R.id.edit_text_ethernet_mac);
        //mZarg0EditText = (EditText) findViewById(R.id.edit_text_ethernet_arg0);
        //mHSDowloadCheckBox = (CheckBox) findViewById(R.id.edit_text_ethernet_use_hs_download);
        //mHSDowloadCheckBox.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mDeviceManager.currentContext = mContext;
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
        (this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHostNameEditText.setText(mHostName);
                mHostIPEditText.setText(mHostIP);
                mSubmaskEditText.setText(mSubmask);
                mGatewayEditText.setText(mGateway);
                mHttpPortEditText.setText(String.valueOf(mHttpPort));
                mTcpPortEditText.setText(String.valueOf(mTcpPort));
                mSslPortEditText.setText(String.valueOf(mSslPort));
                mUdpPortEditText.setText(String.valueOf(mUdpPort));
                /*mMaxConnEditText.setText(String.valueOf(mMaxConn));
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

//        mManager.logoutDevice(mDevice);
        temp.setHostname(mHostNameEditText.getText().toString());
        temp.setIpAddress(mHostIPEditText.getText().toString());
        temp.setSubmask(mSubmaskEditText.getText().toString());
        temp.setGateway(mGatewayEditText.getText().toString());
        if(mDevice.getTCPPort() == 0)
            temp.setTCPPort(Integer.parseInt(mTcpPortEditText.getText().toString()));
        else
            temp.setTCPPort(mDevice.getTCPPort());
        temp.setHttpPort(Integer.parseInt(mHttpPortEditText.getText().toString()));
        temp.setSslPort(Integer.parseInt(mSslPortEditText.getText().toString()));
        temp.setUdpPort(Integer.parseInt(mUdpPortEditText.getText().toString()));

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

        task.execute((Void[])null);
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
