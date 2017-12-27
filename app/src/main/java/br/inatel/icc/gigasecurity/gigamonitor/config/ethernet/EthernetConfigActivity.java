package br.inatel.icc.gigasecurity.gigamonitor.config.ethernet;

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

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mDeviceManager;

public class EthernetConfigActivity extends ActionBarActivity implements OnCheckedChangeListener {

    private final String TAG = EthernetConfigActivity.class.getSimpleName();

    private Context mContext;
    private Device mDevice;
    private Device temp;
    private int position;
    private EditText mHostIPEditText, mSubmaskEditText, mGatewayEditText;
    private int intentIndex;

    private String mHostIP, mSubmask, mGateway;

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
            if (intentIndex != -3) {
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
            }

            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            task.cancel(true);
            pd.dismiss();
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

        intentIndex = getIntent().getExtras().getInt("index");
        if (intentIndex == -3) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");
            position = -1;

            temp = new Device(mDevice);
//            mHostIP = (String) getIntent().getExtras().getSerializable("ipAddress");
//            mSubmask = (String) getIntent().getExtras().getSerializable("submask");
//            mGateway = (String) getIntent().getExtras().getSerializable("gateway");
            initData();

            initViews();
        } else {
            mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
            position = mManager.getDevicePosition(mDevice);
            Log.d("EthernetConfigActivity", "Device ID: " + String.valueOf(mDevice.getDeviceId()));
            temp = mDevice;
            mManager.getJsonConfig(mDevice, "NetWork.NetCommon", mListener);
        }

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
                    if (intentIndex == -3) {
                        mManager.setEthernetConfigOffline(temp);
                    } else {
                        mManager.setEthernetConfig(temp, mListener);
                    }
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
        mHostIPEditText = (EditText) findViewById(R.id.edit_text_ethernet_host_ip);
        mSubmaskEditText = (EditText) findViewById(R.id.edit_text_ethernet_submask);
        mGatewayEditText = (EditText) findViewById(R.id.edit_text_ethernet_gateway);
    }

    private void initData() {
        mDeviceManager.currentContext = mContext;
        mHostIP = mDevice.getIpAddress();
        mSubmask = mDevice.getSubmask();
        mGateway = mDevice.getGateway();
    }

    private void initViews() {
        (this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHostIPEditText.setText(mHostIP);
                mSubmaskEditText.setText(mSubmask);
                mGatewayEditText.setText(mGateway);
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
        if (intentIndex == -3) {
//            temp.setHostname((String) getIntent().getExtras().getSerializable("hostname"));
//            temp.setHttpPort((int) getIntent().getExtras().getSerializable("httpPort"));
//            temp.setMacAddress((String) getIntent().getExtras().getSerializable("macAddress"));
//            temp.setMaxBPS((int) getIntent().getExtras().getSerializable("maxBps"));
//            temp.setMonMode((String) getIntent().getExtras().getSerializable("monMode"));
//            temp.setSslPort((int) getIntent().getExtras().getSerializable("sslPort"));
//            temp.setTcpMaxConn((int) getIntent().getExtras().getSerializable("tcpMaxConn"));
//            temp.setTCPPort((int) getIntent().getExtras().getSerializable("tcpPort"));
//            temp.setTransferPlan((int) getIntent().getExtras().getSerializable("transferPlan"));
//            temp.setUdpPort((int) getIntent().getExtras().getSerializable("udpPort"));
            temp.setConnectionMethod(0);
            temp.setConnectionString(0);
        }
        temp.setIpAddress(mHostIPEditText.getText().toString());
        temp.setSubmask(mSubmaskEditText.getText().toString());
        temp.setGateway(mGatewayEditText.getText().toString());

        task.execute((Void[]) null);
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
