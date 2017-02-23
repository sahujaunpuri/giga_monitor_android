package br.inatel.icc.gigasecurity.gigamonitor.config.upnp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class UpnpConfigActivity extends ActionBarActivity {

    // Debug tag
    private final String TAG = UpnpConfigActivity.class.getSimpleName();

    private Device mDevice;
    private DeviceManager mManager;
    private Context mContext;

    private CheckBox mEnableCheckBox, mStateCheckBox;
    private EditText mHttpPortEditText, mMediaPortEditText, mMobilePortEditText, mArg0EditText;

    private ConfigListener listener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
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
        public void onError() {
            int messageId = R.string.invalid_device_save;
            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upnp_config);
        findViews();

        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceBySN((String) getIntent().getExtras().getSerializable("device"));
        mContext = this;

        mManager.getJsonConfig(mDevice, "NetWork.Upnp", listener);
    }

    public void findViews() {
        mEnableCheckBox = (CheckBox) findViewById(R.id.check_box_cloud_enable);
//        mStateCheckBox = (CheckBox) findViewById(R.id.check_box_upnp_state);
        mHttpPortEditText = (EditText) findViewById(R.id.edit_text_upnp_http_port);
        mMediaPortEditText = (EditText) findViewById(R.id.edit_text_upnp_media_port);
        mMobilePortEditText = (EditText) findViewById(R.id.edit_text_upnp_mobile_port);
        mArg0EditText = (EditText) findViewById(R.id.edit_text_upnp_arg0);
    }

    public void initViews() {
        mEnableCheckBox.setChecked(mDevice.isUpnpEnable());
//        mStateCheckBox.setChecked(mConfig.isState());
//        mArg0EditText.setText(mConfig.getArg0());
        mHttpPortEditText.setText(String.valueOf(mDevice.getHTTPPort()));
        mMediaPortEditText.setText(String.valueOf(mDevice.getMediaPort()));
        mMobilePortEditText.setText(String.valueOf(mDevice.getMobilePort()));
    }

    private void save() {
        Utils.hideKeyboard(this);


        mDevice.setUpnpEnable(mEnableCheckBox.isChecked());
//        mConfig.setState(mStateCheckBox.isChecked());
//        mConfig.setArg0(mArg0EditText.getText().toString());
        mDevice.setHTTPPort(Integer.parseInt(mHttpPortEditText.getText().toString()));
        mDevice.setMediaPort(Integer.parseInt(mMediaPortEditText.getText().toString()));
        mDevice.setMobilePort(Integer.parseInt(mMobilePortEditText.getText().toString()));

        mManager.setUpnpConfig(mDevice);
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
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
