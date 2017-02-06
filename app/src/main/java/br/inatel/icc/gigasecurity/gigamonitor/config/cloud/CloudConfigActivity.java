//package br.inatel.icc.gigasecurity.gigamonitor.config.cloud;
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
//public class CloudConfigActivity extends ActionBarActivity {
//
//    private Device mDevice;
//    private DeviceManager mManager;
//
//    private CloudConfig mConfig;
//
//    private CheckBox mEnableCheckBox;
//    private EditText mMtuEditText, mServerAddrEditText, mServerPortEditText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cloud_config);
//        findViews();
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            mManager = DeviceManager.getInstance();
//
//            mConfig = mDevice.getCloudConfig();
//            mConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
//                @Override
//                public void onPreFinish(Boolean success) {
//                    if (!success) {
//                        String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);
//                        Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
//                        finish();
//                        return;
//                    }
//                    initViews();
//                }
//
//                @Override
//                public void onFinish(Boolean success) {}
//            });
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mConfig = mDevice.getCloudConfig();
//            initViews();
//        }
//
//    }
//
//    private void findViews() {
//        mEnableCheckBox = (CheckBox) findViewById(R.id.check_box_cloud_enable);
//        mMtuEditText = (EditText) findViewById(R.id.edit_text_cloud_mtu);
//        mServerAddrEditText = (EditText) findViewById(R.id.edit_text_cloud_server_addr);
//        mServerPortEditText = (EditText) findViewById(R.id.edit_text_cloud_server_port);
//    }
//
//    private void initViews() {
//        mEnableCheckBox.setChecked(mConfig.isEnable());
//        mMtuEditText.setText(String.valueOf(mConfig.getMTU()));
//        mServerAddrEditText.setText(mConfig.getServerAddr());
//        mServerPortEditText.setText(String.valueOf(mConfig.getServerPort()));
//    }
//
//    public void save() {
//       mConfig.setEnable(mEnableCheckBox.isChecked());
//       mConfig.setMTU(Integer.valueOf(mMtuEditText.getText().toString()));
//       mConfig.setServerAddr(mServerAddrEditText.getText().toString());
//       mConfig.setServerPort(Integer.valueOf(mServerPortEditText.getText().toString()));
//
//        mConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener(){
//
//            @Override
//            public void onFinish(Boolean success) {
//                if (success) {
//                     final int resId = success ? R.string.saved : R.string.invalid_device_save;
//                    Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.config_form, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch(id) {
//            case android.R.id.home:
//                finish();
//                return true;
//            case R.id.action_config_save:
//                Utils.hideKeyboard(this);
//                save();
//                return true;
//            default: return false;
//        }
//    }
//}
