package br.inatel.icc.gigasecurity.gigamonitor.config.ddns;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basic.G;
import com.xm.MyConfig;
import com.xm.NetSdk;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.NetDDNSConfig;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class DDNSConfigActivity extends ActionBarActivity {

    // Debug tag
    // private final String TAG = DDNSConfigActivity.class.getSimpleName();

    private Device mDevice;

    private DeviceManager mManager;

    private Spinner mTypeSpinner;
    private EditText mDomainET, mUserNameET, mPasswordET;
    private CheckBox mEnabledCheckBox;

    private int mType;
    private boolean mEnabled;
    private String mDomain, mUserName, mPassword;

    private DDNSConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddns_config);
        findViews();


        if (savedInstanceState == null) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");

            mManager = DeviceManager.getInstance();

            mConfig = mDevice.getDdnsConfig();

            mConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
                @Override
                public void onPreFinish(Boolean success) {
                    String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);

                    if (!success) {
                        Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
                        finish();

                        return;
                    }else{
                        if(mConfig.isDdnsInvalid()){
                            Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
                            finish();

                            return;
                        }
                    }

                    initData();
                    initViews();
                }

                @Override
                public void onFinish(Boolean success) {}
            });
        } else {
            mDevice = (Device) savedInstanceState.getSerializable("device");
            mConfig = mDevice.getDdnsConfig();
            initData();
            initViews();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("device", mDevice);
        super.onSaveInstanceState(outState);
    }

    private void findViews() {
        mEnabledCheckBox = (CheckBox) findViewById(R.id.check_box_ddns_enable);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_ddns_type);
        mDomainET = (EditText) findViewById(R.id.edit_text_ddns_domain);
        mUserNameET = (EditText) findViewById(R.id.edit_text_ddns_user_name);
        mPasswordET = (EditText) findViewById(R.id.edit_text_ddns_password);
    }

    private void initData() {

        mEnabled = mConfig.isEnable();
        mDomain = mConfig.getDomain();
        mUserName = mConfig.getUserName();


    }

    private void initViews() {
        mEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEnabled = isChecked;
                setDdnsUIEnabled(isChecked);
            }
        });

        mEnabledCheckBox.setChecked(mEnabled);

        // TODO Load all items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ddns_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(mType);

        mDomainET.setText(mDomain);
        mUserNameET.setText(mUserName);
        mPasswordET.setText(mPassword);
    }

    private void setDdnsUIEnabled(boolean enabled) {
        mDomainET.setEnabled(enabled);
        mUserNameET.setEnabled(enabled);
        mPasswordET.setEnabled(enabled);
        mTypeSpinner.setEnabled(enabled);
    }

    private boolean validate() {
        mEnabled = mEnabledCheckBox.isChecked();
        if (!mEnabled) {
            return true;
        }

        boolean isValid = true;


        mDomain = mDomainET.getText().toString();
        /* Domain
        if (!Utils.isValidDomain(mDomain) && !Utils.isValidIP(mDomain)) {
            mDomainET.setError("Invalid Domain");
            isValid = false;
        }*/

        // Username
        mUserName = mUserNameET.getText().toString().trim();
        if (mUserName.isEmpty()) {
            mUserNameET.setError("Invalid Username");
            isValid = false;
        }


        mPassword = mPasswordET.getText().toString().trim();
        /* Password
        if (mPassword.isEmpty()) {
            mPasswordET.setError("Invalid Password");
            isValid = false;
        }*/

        return isValid;
    }

    private void save() {

        G.SetValue(mConfig.getGigaDDNSConfigALL().st_0_ddnsConfig[0].st_3_HostName, mDomain);
        G.SetValue(mConfig.getGigaDDNSConfigALL().st_0_ddnsConfig[0].st_5_Server.st_3_UserName, mUserName);

        mConfig.getGigaDDNSConfigALL().st_0_ddnsConfig[0].st_0_Enable = mEnabled;

        if (mManager.setConfig2(mDevice.getLoginID(), MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DDNS, mConfig.getGigaDDNSConfigALL())) {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.invalid_device_save, Toast.LENGTH_LONG).show();
        }

        /*final NetDDNSConfig config = new NetDDNSConfig();
        config.enabled = mEnabled;
        if (mEnabled) {
            config.ddnsType = mType;
            config.domainName = mDomain;
            config.userName = mUserName;
            config.password = mPassword;
        }

        mConfig.setConfigTask(this, mDevice.getLoginID(),
                new ConfigAbstract.ConfigSetTaskListener() {
            @Override
            public void onFinish(Boolean success) {
                if (success) {
                    final int resId = success ? R.string.saved : R.string.invalid_device_save;
                    Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
                }
            }
        });*/
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
                if (validate()) {
                    save();
                }
                return true;
            default:
                return false;
        }
    }
}
