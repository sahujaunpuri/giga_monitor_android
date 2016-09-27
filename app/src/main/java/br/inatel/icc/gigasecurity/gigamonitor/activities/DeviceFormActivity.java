package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;


public class DeviceFormActivity extends ActionBarActivity{

    private static final int SEARCH_ACTIVITY_REQUEST = 0;

    EditText etName;
    EditText etSerial;
    EditText etIpAddress;
    EditText etDomain;
    EditText etPort;
    EditText etUsername;
    EditText etPassword;

    int editPosition = -1;
    DeviceManager deviceManager = DeviceManager.getInstance();
    Device mDevice;
    public ArrayList<Device> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        initForm();

        if(getIntent().getExtras() != null) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");
            editPosition = getIntent().getExtras().getInt("index", -1);

            getSupportActionBar().setTitle("Editar Dispositivo");
            setForm(mDevice);
        } else {
            mDevice = new Device();
        }

        arrayList = deviceManager.loadDevices(this);

        checkEdit();
    }

    public void initForm() {
        etName      = (EditText)findViewById(R.id.edit_text_device_form_name);
        etSerial    = (EditText)findViewById(R.id.edit_text_device_form_serial);
        etIpAddress = (EditText)findViewById(R.id.edit_text_device_form_ip_address);
        etDomain    = (EditText) findViewById(R.id.edit_text_device_form_domain);
        etPort      = (EditText)findViewById(R.id.edit_text_device_form_Port);
        etUsername  = (EditText)findViewById(R.id.edit_text_device_form_username);
        etPassword  = (EditText)findViewById(R.id.edit_text_device_form_password);
    }

    private void checkEdit() {
        if(editPosition != -1){
            arrayList.remove(editPosition);
        }
    }

    private void setForm(Device device) {
        etName.setText(device.getHostname());
        etSerial.setText(device.getSerialNumber());
        etIpAddress.setText(device.getIpAddress());
        etDomain.setText(device.getDomain());
        etPort.setText(String.valueOf(device.getTCPPort()));
        etUsername.setText(device.getUsername());
        etPassword.setText(device.getPassword());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_ACTIVITY_REQUEST && resultCode == RESULT_OK){
            Device device = (Device) data.getSerializableExtra("device");
            setForm(device);
        }
    }


    public boolean save() {
        boolean isHostnameFilled = !TextUtils.isEmpty(etName.getText().toString());
        boolean isPortFilled = !TextUtils.isEmpty(etPort.getText().toString());
        boolean isSerialNumberFilled = !TextUtils.isEmpty(etSerial.getText().toString());

        if (isHostnameFilled && (isPortFilled || isSerialNumberFilled)) {
            mDevice.setHostname(etName.getText().toString());
            mDevice.setDomain(etDomain.getText().toString());
            mDevice.setSerialNumber(etSerial.getText().toString());
            mDevice.setIpAddress(etIpAddress.getText().toString());
            if (!TextUtils.isEmpty(etPort.getText().toString())) {
                mDevice.setTCPPort(Integer.parseInt(etPort.getText().toString()));
            }
            mDevice.setUsername(etUsername.getText().toString());
            mDevice.setPassword(etPassword.getText().toString());

            return true;
        }

        return false;
    }

    private void startDeviceListActivity() {
        Intent i = new Intent(this, DeviceListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
                if(save()) {
                    arrayList.add(mDevice);
                    DeviceManager.getInstance().saveDevices(this, arrayList);
                    DeviceListActivity.mDevices = null;
                    //DeviceListActivity.listComponentOlds = new ArrayList<>();
                    //NavUtils.navigateUpFromSameTask(this);
                    startDeviceListActivity();

                    finish();

                    return true;
                } else {
                    Toast.makeText(this, R.string.invalid_device_save, Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
