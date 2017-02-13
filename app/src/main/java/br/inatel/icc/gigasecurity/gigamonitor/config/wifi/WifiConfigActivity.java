//package br.inatel.icc.gigasecurity.gigamonitor.config.wifi;
//
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
//public class WifiConfigActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
//
//    // Debug tag
//    private static String TAG = WifiConfigActivity.class.getSimpleName();
//
//    private static final int SEARCH_ACTIVITY_REQUEST = 0;
//
//    private Device mDevice;
//
//    private WifiConfig mWifiConfig;
//
//    CheckBox checkboxWifiEnabled;
//    CheckBox checkboxWifiDhcpEnable;
//
//    EditText editTextWifiSsid;
//    EditText editTextWifiPassword;
//    EditText editTextWifiIpAddress;
//    EditText editTextWifiMask;
//    EditText editTextWifiGateway;
//
//    Spinner spinnerEncryptionType;
//
//    Boolean mWifiEnabled;
//    String mSsid;
//    String mIpAddress;
//    String mSubnetMask;
//    String mGateway;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wifi_config);
//        findViews();
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            mWifiConfig = mDevice.getWifiConfig();
//            mWifiConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
//               @Override
//                public void onPreFinish(Boolean success) {
//                    if(!success){
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
//                public void onFinish(Boolean success) {}
//            });
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mWifiConfig = mDevice.getWifiConfig();
//            initData();
//            initViews();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        mDevice.setWifiConfig(mWifiConfig);
//
//        outState.putSerializable("device", mDevice);
//        super.onSaveInstanceState(outState);
//    }
//
//    private void findViews() {
//        editTextWifiSsid = (EditText) findViewById(R.id.edit_text_wifi_password);
//        checkboxWifiEnabled = (CheckBox) findViewById(R.id.checkbox_wifi_enabled);
//        checkboxWifiDhcpEnable = (CheckBox) findViewById(R.id.checkbox_wifi_dhcp_enabled);
//        editTextWifiPassword = (EditText) findViewById(R.id.edit_text_wifi_password);
//        editTextWifiIpAddress = (EditText) findViewById(R.id.edit_text_wifi_ip_address);
//        editTextWifiMask = (EditText) findViewById(R.id.edit_text_wifi_mask);
//        editTextWifiGateway = (EditText) findViewById(R.id.edit_text_wifi_gateway);
//    }
//
//
//
//    private void initData() {
//        mWifiEnabled = mWifiConfig.isEnable();
//
////        mSsid = mWifiConfig.getSSID();
////        mPassword = wifiConfig.getPassword();  //TODO: Password is not in the Wifi model
//        mIpAddress = mWifiConfig.getHostIP();
//        mSubnetMask = mWifiConfig.getSubmask();
//        mGateway = mWifiConfig.getGateway();
//
//    }
//
//    private void initViews() {
////        editTextWifiSsid.setText(mSsid);
//        editTextWifiIpAddress.setText(mIpAddress);
//        editTextWifiMask.setText(mSubnetMask);
//        editTextWifiGateway.setText(mGateway);
//
////        checkboxWifiEnabled.setOnCheckedChangeListener(this);
////        checkboxWifiEnabled.setEnabled(false);
//
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        switch (buttonView.getId()) {
//            case R.id.checkbox_wifi_enabled:
//                setDhcpUIEnabled(isChecked);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void setDhcpUIEnabled(boolean enabled) {
//        checkboxWifiDhcpEnable.setEnabled(!enabled);
//        editTextWifiSsid.setEnabled(!enabled);
//        editTextWifiPassword.setEnabled(!enabled);
//        editTextWifiIpAddress.setEnabled(!enabled);
//        editTextWifiMask.setEnabled(!enabled);
//        editTextWifiGateway.setEnabled(!enabled);
//        spinnerEncryptionType.setEnabled(!enabled);
//    }
//
//    private boolean isFieldsValid() {
//        boolean hasError = false;
//
//        //TODO: Others validations and pass hardcode lines to strings.xml
//        mIpAddress = editTextWifiIpAddress.getText().toString().trim();
//        if (!Utils.isValidIP(mIpAddress)) {
//            editTextWifiIpAddress.setError("Invalid IP Address");
//            hasError = true;
//        }
//
//        mSubnetMask = editTextWifiMask.getText().toString().trim();
//        if (!Utils.isValidIP(mSubnetMask)) {
//            editTextWifiMask.setError("Invalid Subnet Mask");
//            hasError = true;
//        }
//
//        mGateway = editTextWifiGateway.getText().toString().trim();
//        if (!Utils.isValidIP(mGateway)) {
//            editTextWifiGateway.setError("Invalid Gateway");
//            hasError = true;
//        }
//
//        return !hasError;
//    }
//
//    private void save() {
//        if(isFieldsValid()){
//            //TODO: get others fields
//            mWifiConfig
//                    .setEnable(mWifiEnabled)
//                    .setSSID(mSsid)
//                    .setHostIP(mIpAddress)
//                    .setSubmask(mSubnetMask)
//                    .setGateway(mGateway);
//
//            mWifiConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener() {
//                @Override
//                public void onFinish(Boolean success) {
//                    if (success) {
//                        finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), R.string.invalid_device_save,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getMenuInflater().inflate(R.menu.form, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//                finish();
//                return true;
//            case R.id.action_save:
//                save();
//                return true;
//            default: return false;
//        }
//    }
//}
