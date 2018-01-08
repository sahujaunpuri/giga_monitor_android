package br.inatel.icc.gigasecurity.gigamonitor.config.ddns;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;

public class DDNSConfigActivity extends ActionBarActivity {

    // Debug tag
    // private final String TAG = DDNSConfigActivity.class.getSimpleName();

    private Device mDevice;

    private DeviceManager mManager;

    private Spinner mTypeSpinner;
    private EditText mDomainET, mUserNameET, mPasswordET;
    private TextView mTextViewBack, mTextViewSave;
    private Switch mSwitch;

    private int mType;
    private boolean mEnabled;
    private String mDomain, mUserName, mPassword;

    private ConfigListener listener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
            mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
            initData();
            mManager.currentContext = mContext;
            initViews();
        }

        @Override
        public void onSetConfig() {
            int messageId = R.string.saved;
            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.collapse = mManager.getDevices().indexOf(mDevice);
            mManager.saveData();

            finish();
        }

        @Override
        public void onError() {
            int messageId = R.string.invalid_device_save;
            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.collapse = mManager.getDevices().indexOf(mDevice);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddns_config);
        findViews();

        getSupportActionBar().hide();

        if (savedInstanceState == null) {

            mManager = DeviceManager.getInstance();
            mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
            mContext = this;

            mManager.getJsonConfig(mDevice, "NetWork.NetDDNS", listener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("device", mDevice);
        super.onSaveInstanceState(outState);
    }

    private void findViews() {
        mSwitch = (Switch) findViewById(R.id.switch_ddns);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_ddns_type);
        mDomainET = (EditText) findViewById(R.id.edit_text_ddns_domain);
        mUserNameET = (EditText) findViewById(R.id.edit_text_ddns_user_name);
        mPasswordET = (EditText) findViewById(R.id.edit_text_ddns_password);
        mTextViewBack = (TextView) findViewById(R.id.text_view_back);
        mTextViewSave = (TextView) findViewById(R.id.text_view_save);
    }

    private void initData() {
        mEnabled = mDevice.isDdnsEnable();
        mDomain = mDevice.getDdnsDomain();
        mUserName = mDevice.getDdnsUserName();
    }

    private void initViews() {
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTextViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(DDNSConfigActivity.this);
                if (validate()) {
                    save();
                }
            }
        });


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEnabled = isChecked;
                setDdnsUIEnabled(isChecked);
            }
        });

        mSwitch.setChecked(mEnabled);

        // TODO Load all items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ddns_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(mType);

        mDomainET.setText(mDomain);
        mUserNameET.setText(mUserName);
//        mPasswordET.setText(mPassword);
    }

    private void setDdnsUIEnabled(boolean enabled) {
        mDomainET.setEnabled(enabled);
        mUserNameET.setEnabled(enabled);
        mPasswordET.setEnabled(enabled);
        mTypeSpinner.setEnabled(enabled);
    }

    private boolean validate() {
        mEnabled = mSwitch.isChecked();
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

        mDevice.setDdnsDomain(mDomain);
        mDevice.setDdnsUserName(mUserName);
        mDevice.setDdnsEnable(mEnabled);

        mManager.setDDNSConfig(mDevice);

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
