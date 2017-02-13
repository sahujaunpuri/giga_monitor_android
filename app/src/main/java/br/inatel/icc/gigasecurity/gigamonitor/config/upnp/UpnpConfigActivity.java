//package br.inatel.icc.gigasecurity.gigamonitor.config.upnp;
//
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
//public class UpnpConfigActivity extends ActionBarActivity {
//
//    // Debug tag
//    private final String TAG = UpnpConfigActivity.class.getSimpleName();
//
//    private Device mDevice;
//
//    private DeviceManager mManager;
//
//    private UpnpConfig mConfig;
//
//    private CheckBox mEnableCheckBox, mStateCheckBox;
//    private EditText mHttpPortEditText, mMediaPortEditText, mMobilePortEditText, mArg0EditText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upnp_config);
//        findViews();
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            mManager = DeviceManager.getInstance();
//
//            mConfig = mDevice.getUpnpConfig();
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
//                    initViews();
//                }
//
//                @Override
//                public void onFinish(Boolean success) {
//                }
//            });
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mConfig = mDevice.getUpnpConfig();
//            initViews();
//        }
//    }
//
//    public void findViews() {
//        mEnableCheckBox = (CheckBox) findViewById(R.id.check_box_cloud_enable);
//        mStateCheckBox = (CheckBox) findViewById(R.id.check_box_upnp_state);
//        mHttpPortEditText = (EditText) findViewById(R.id.edit_text_upnp_http_port);
//        mMediaPortEditText = (EditText) findViewById(R.id.edit_text_upnp_media_port);
//        mMobilePortEditText = (EditText) findViewById(R.id.edit_text_upnp_mobile_port);
//        mArg0EditText = (EditText) findViewById(R.id.edit_text_upnp_arg0);
//    }
//
//    public void initViews() {
//        mEnableCheckBox.setChecked(mConfig.isEnable());
//        mStateCheckBox.setChecked(mConfig.isState());
//        mArg0EditText.setText(mConfig.getArg0());
//        mHttpPortEditText.setText(String.valueOf(mConfig.getHTTPPort()));
//        mMediaPortEditText.setText(String.valueOf(mConfig.getMediaPort()));
//        mMobilePortEditText.setText(String.valueOf(mConfig.getMobliePort()));
//    }
//
//    private void save() {
//        Utils.hideKeyboard(this);
//        if (mConfig == null) return ;
//
//        mConfig.setEnable( mEnableCheckBox.isChecked());
//        mConfig.setState(mStateCheckBox.isChecked());
//        mConfig.setArg0(mArg0EditText.getText().toString());
//        mConfig.setHTTPPort(Integer.parseInt(mHttpPortEditText.getText().toString()));
//        mConfig.setMediaPort(Integer.parseInt(mMediaPortEditText.getText().toString()));
//        mConfig.setMobliePort(Integer.parseInt(mMobilePortEditText.getText().toString()));
//
//        mConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener(){
//
//            @Override
//            public void onFinish(Boolean success) {
//                if (success) {
//                    final int resId = success ? R.string.saved : R.string.invalid_device_save;
//                    Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
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
//                save();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
