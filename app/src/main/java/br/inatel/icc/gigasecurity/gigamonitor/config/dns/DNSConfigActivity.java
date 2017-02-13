//package br.inatel.icc.gigasecurity.gigamonitor.config.dns;
//
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.xm.javaclass.CONFIG_IPAddress;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
//public class DNSConfigActivity extends ActionBarActivity {
//
//    // Debug tag
//    private static String TAG = DNSConfigActivity.class.getSimpleName();
//
//    private static final int SEARCH_ACTIVITY_REQUEST = 0;
//
//    private Device mDevice;
//    private DeviceManager mManager;
//
//    private DNSConfig mConfig;
//
//    EditText editTextDNSPrimaryAddress;
//    EditText editTextDNSSecundaryAddress;
//
//    String mPrimaryAddress;
//    String mSecondaryAddress;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dns_config);
//        findViews();
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            mManager = DeviceManager.getInstance();
//
//            mConfig = mDevice.getDNSConfig();
//            mConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
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
//               }
//
//                @Override
//                public void onFinish(Boolean success) {}
//            });
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mConfig = mDevice.getDNSConfig();
//            initData();
//            initViews();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        mDevice.setDNSConfig(mConfig);
//
//        outState.putSerializable("device", mDevice);
//        super.onSaveInstanceState(outState);
//    }
//
//    private void findViews() {
//        editTextDNSPrimaryAddress = (EditText) findViewById(R.id.edit_text_dns_primary_address);
//        editTextDNSSecundaryAddress = (EditText) findViewById(R.id.edit_text_dns_secondary_address);
//    }
//
//
//
//    private void initData() {
//
//        mPrimaryAddress = mConfig.getPrimaryDNS();
//        mSecondaryAddress = mConfig.getSecondaryDNS();
//
//
//    }
//
//    private void initViews() {
//        editTextDNSPrimaryAddress.setText(String.valueOf(mPrimaryAddress));
//        editTextDNSSecundaryAddress.setText(String.valueOf(mSecondaryAddress));
//
//    }
//
//    private boolean isFieldsValid() {
//        boolean hasError = false;
//
//        mPrimaryAddress = editTextDNSPrimaryAddress.getText().toString().trim();
//        if (!Utils.isValidIP(mPrimaryAddress)) {
//            editTextDNSPrimaryAddress.setError("Invalid Address");
//            hasError = true;
//        }
//
//        mSecondaryAddress = editTextDNSSecundaryAddress.getText().toString().trim();
//        if (!Utils.isValidIP(mSecondaryAddress)) {
//            editTextDNSSecundaryAddress.setError("Invalid Address");
//            hasError = true;
//        }
//
//        return !hasError;
//    }
//
//    private void save() {
//        if(isFieldsValid()){
//            //TODO: get others fields
//            mConfig.setPrimaryDNS(Utils.parseIp(mPrimaryAddress));
//            mConfig.setSecondaryDNS(Utils.parseIp(mSecondaryAddress));
//
//            mConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener() {
//                @Override
//                public void onFinish(Boolean success) {
//                    if (success) {
//                        //finish();
//
//                        final int resId = success ? R.string.saved : R.string.invalid_device_save;
//                        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
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
