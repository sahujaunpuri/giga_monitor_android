package br.inatel.icc.gigasecurity.gigamonitor.config.password;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;
import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mDeviceManager;

public class PasswordConfigActivity extends ActionBarActivity {

    private Device mDevice;
    private DeviceManager mManager;
    private Context mContext;

    private EditText mOldEditText, mNewEditText, mConfirmNewEditText;

    private ConfigListener mListener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
        }

        @Override
        public void onSetConfig() {
            mDeviceManager.currentContext = mContext;
            int messageId = R.string.saved;

            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.saveData();

            finish();
        }

        @Override
        public void onError(){
            int messageId = R.string.invalid_device_save;

            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();
            mManager.saveData();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_config);
        findViews();

        mContext = this;
        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceBySN((String) getIntent().getExtras().getSerializable("device"));
    }

    private void findViews() {
        mOldEditText = (EditText) findViewById(R.id.edit_text_pass_actual);
        mNewEditText = (EditText) findViewById(R.id.edit_text_pass_new);
        mConfirmNewEditText = (EditText) findViewById(R.id.edit_text_pass_new_confirm);
    }

    private void save() {
        final Activity activity = this;

        final String oldPass = mOldEditText.getText().toString();
        final String newPass = mNewEditText.getText().toString();
        final String confNewPass = mConfirmNewEditText.getText().toString();
        String msg;

        if(!mDevice.getPassword().equals(oldPass)){
            msg = "Antiga senha incorreta.";
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        } else if (!newPass.equals(confNewPass)){
            msg = "Confimação inválida";
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        } else {
            mManager.changePassword(mDevice, oldPass, newPass, mListener);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config_form, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_config_save:
                save();
                Utils.hideKeyboard(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
