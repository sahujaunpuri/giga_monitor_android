package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class DeviceConfigActivity extends ActionBarActivity {

    private final String KEY_ARGS = "device";

    private Button btConfig, btRestart;
    private Device mDevice;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);

        initComponents();

        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        mDevice = (Device) extras.getSerializable(KEY_ARGS);


        btConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConfigMenuActivity();
            }
        });

        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRebootDialog();
            }
        });
    }

    private void startConfigMenuActivity() {
        Bundle extras = new Bundle();
        extras.putSerializable("device", mDevice);

        Intent intent = new Intent(DeviceConfigActivity.this, ConfigMenuActivity.class);
        intent.putExtras(extras);

        startActivity(intent);
    }

    private void startDeviceListActivity() {
        Intent intent = new Intent(DeviceConfigActivity.this, DeviceListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void initComponents() {
        btConfig       = (Button) findViewById(R.id.button_config_config);
        btRestart      = (Button) findViewById(R.id.button_config_restart);
        mDeviceManager = DeviceManager.getInstance();
    }

    private void showRebootDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceConfigActivity.this);

        String text = getResources().getString(R.string.label_reboot_dialog);
        String labelYes = getResources().getString(R.string.label_yes);

        builder.setMessage(text)
                .setPositiveButton(labelYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //mDeviceManager.rebootDevice(mDevice);

                        startDeviceListActivity();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled dialog.
                    }
                });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_device_config, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
