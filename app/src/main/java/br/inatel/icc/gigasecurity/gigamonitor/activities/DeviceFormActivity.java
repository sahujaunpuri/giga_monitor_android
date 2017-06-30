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
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;


public class DeviceFormActivity extends ActionBarActivity{

    private static final int SEARCH_ACTIVITY_REQUEST = 0;

    EditText etName;
    EditText etSerial;
    EditText etIpAddress;
    EditText etDomain;
    EditText etPort;
    EditText etUsername;
    EditText etPassword;
    String TAG = "DeviceForm";

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
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
            editPosition = getIntent().getExtras().getInt("index", -1);
            if(editPosition > -1) {
                mDevice = deviceManager.getDevices().get(editPosition);
            } else if (editPosition == -2) {
                mDevice = (Device) getIntent().getExtras().getSerializable("device");
            }


            getSupportActionBar().setTitle("Editar Dispositivo");
            setForm(mDevice);
        } else {
            mDevice = new Device();
        }

        arrayList = deviceManager.getDevices();


    }

    private void checkEdit() {
        if(editPosition != -1){
            arrayList.remove(editPosition);
        }
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

    private void setForm(Device device) {
        etName.setText(device.getDeviceName());
        etSerial.setText(device.getSerialNumber());
        etIpAddress.setText(device.getIpAddress());
        etDomain.setText(device.getDomain());
        etPort.setText(String.valueOf(device.getTCPPort()));
        if(etPort.getText().toString().isEmpty())
            etPort.setText("34567");
        etUsername.setText(device.getUsername());
        if(etUsername.getText().toString().isEmpty())
            etUsername.setText("admin");
        etPassword.setText(device.getPassword());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            Device device = (Device) data.getSerializableExtra("device");
            setForm(device);
        }
    }


    public boolean save() {
        boolean isHostnameFilled = !TextUtils.isEmpty(etName.getText().toString());
        boolean isIPFilled = !TextUtils.isEmpty(etIpAddress.getText().toString());
        boolean isUsernameFilled = !TextUtils.isEmpty(etUsername.getText().toString());

        if(isHostnameFilled && areConfigurationFieldsOk(isIPFilled)) {
            if(isIPFilled && !Utils.isValidIP(etIpAddress.getText().toString())){
                Toast.makeText(this, "Endereço de IP inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            mDevice.deviceName = etName.getText().toString();
            mDevice.setDomain(etDomain.getText().toString());
            mDevice.setIpAddress(etIpAddress.getText().toString());
            mDevice.setSerialNumber(etSerial.getText().toString());
            if (etPort.getText().toString().isEmpty()) {
                mDevice.setTCPPort(0);
            } else {
                mDevice.setTCPPort(Integer.parseInt(etPort.getText().toString()));
            }

            if(isUsernameFilled)
                mDevice.setUsername(etUsername.getText().toString());
            else
                mDevice.setUsername("admin");

            mDevice.setPassword(etPassword.getText().toString());
//            mDevice.checkConnectionMethod();
            return true;
        }

        return false;
    }

    private boolean areConfigurationFieldsOk(boolean isIPFilled) {
        boolean isPortFilled = !TextUtils.isEmpty(etPort.getText().toString());
        boolean isSerialNumberFilled = !TextUtils.isEmpty(etSerial.getText().toString());
        boolean isDNSFilled = !TextUtils.isEmpty(etDomain.getText().toString());

        if ((isIPFilled || isDNSFilled) && isPortFilled) {
            Integer port = Integer.valueOf(etPort.getText().toString());
            if (port > 0) {
                return true;
            }
        } else if (isSerialNumberFilled) {
            return true;
        }
        return false;
    }

    private void startDeviceListActivity() {
        if(!DeviceListActivity.running) {
            Intent i = new Intent(this, DeviceListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        setResult(RESULT_OK, null);
        finish();
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
                    if(editPosition > -1){
                        deviceManager.logoutDevice(mDevice);
                        checkEdit();
                        mDevice.isLogged = false;
                        mDevice.setChannelNumber(0);
//                        mDevice.checkConnectionMethod();
                        deviceManager.logoutDevice(mDevice);
                        deviceManager.addDevice(mDevice, editPosition);
                        deviceManager.updateSurfaceViewManager(editPosition);
                        deviceManager.collapse = editPosition;
                    } else if(deviceManager.findDeviceById(mDevice.getId()) != null) {
                        Toast.makeText(this, "Dispositivo já adicionado.", Toast.LENGTH_SHORT).show();
//                        deviceManager.logoutDevice(deviceManager.findDeviceById(mDevice.getId()));
//                        startDeviceListActivity();
                    } else {
                        deviceManager.addDevice(mDevice);
                        deviceManager.addSurfaceViewManager(mDevice);
                    }
                    startDeviceListActivity();

                    return true;
                } else {
                    Toast.makeText(this, R.string.invalid_device_save, Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
