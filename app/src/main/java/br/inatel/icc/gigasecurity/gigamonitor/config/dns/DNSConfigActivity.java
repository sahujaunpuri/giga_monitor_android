package br.inatel.icc.gigasecurity.gigamonitor.config.dns;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class  DNSConfigActivity extends ActionBarActivity {

    // Debug tag
    private static String TAG = DNSConfigActivity.class.getSimpleName();

    private static final int SEARCH_ACTIVITY_REQUEST = 0;

    private Device mDevice;
    private DeviceManager mManager;
    private Context mContext;
    private ProgressDialog dialog;

    EditText editTextDNSPrimaryAddress;
    EditText editTextDNSSecundaryAddress;

    String mPrimaryAddress;
    String mSecondaryAddress;

    private ConfigListener listener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
            initData();
            initViews();
        }

        @Override
        public void onSetConfig() {
            int messageId = R.string.saved;
            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.collapse = mManager.getDevices().indexOf(mDevice);
            mManager.saveData();

            //TODO: get others fields
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Carregando");
            dialog.show();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
        setContentView(R.layout.activity_dns_config);
        findViews();
        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
        mContext = this;

        mManager.getJsonConfig(mDevice, "NetWork.NetDNS", listener);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void findViews() {
        editTextDNSPrimaryAddress = (EditText) findViewById(R.id.edit_text_dns_primary_address);
        editTextDNSSecundaryAddress = (EditText) findViewById(R.id.edit_text_dns_secondary_address);
    }



    private void initData() {
        mPrimaryAddress = mDevice.getPrimaryDNS();
        mSecondaryAddress = mDevice.getSecondaryDNS();


    }

    private void initViews() {
        editTextDNSPrimaryAddress.setText(String.valueOf(mPrimaryAddress));
        editTextDNSSecundaryAddress.setText(String.valueOf(mSecondaryAddress));

    }

    private boolean isFieldsValid() {
        boolean hasError = false;

        mPrimaryAddress = editTextDNSPrimaryAddress.getText().toString().trim();
        if (!Utils.isValidIP(mPrimaryAddress)) {
            editTextDNSPrimaryAddress.setError("Invalid Address");
            hasError = true;
        }

        mSecondaryAddress = editTextDNSSecundaryAddress.getText().toString().trim();
        if (!Utils.isValidIP(mSecondaryAddress)) {
            editTextDNSSecundaryAddress.setError("Invalid Address");
            hasError = true;
        }

        return !hasError;
    }

    private void save() {
        if(isFieldsValid()){
            mDevice.setPrimaryDNS(mPrimaryAddress);
            mDevice.setSecondaryDNS(mSecondaryAddress);
            mManager.setDNSConfig(mDevice);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                save();
                return true;
            default: return false;
        }
    }
}
