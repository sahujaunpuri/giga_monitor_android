package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.core.Discovery;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class InitialActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int REQUEST_EXIT = 1;
    private ArrayList<Device> mDevices;
    private Discovery mDiscoveryThread;
    private ProgressBar pbInitial;
    private TextView tvDevicesFound, tvQrCode, tvNewDevice, tvAppVersion;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        mContext = this;

        initComponents();

        searchDevices();
    }

    private void initComponents() {
        tvDevicesFound = (TextView) findViewById(R.id.tv_devices_founds);
        tvQrCode       = (TextView) findViewById(R.id.tv_scan_qr_code);
        tvNewDevice    = (TextView) findViewById(R.id.tv_setup_new_device);
        tvAppVersion   = (TextView) findViewById(R.id.app_version);
        pbInitial      = (ProgressBar) findViewById(R.id.pb_initial);

        pbInitial.getIndeterminateDrawable().setColorFilter(Color.parseColor("#009900"), PorterDuff.Mode.SRC_IN);

        tvQrCode.setOnClickListener(this);
        tvNewDevice.setOnClickListener(this);
        tvDevicesFound.setOnClickListener(this);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String text = getResources().getString(R.string.label_version);

            tvAppVersion.setText(text + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setup_new_device:
                startNewDeviceActivity();
                break;
            case R.id.tv_scan_qr_code:
                startQrCode();
                break;
            case R.id.tv_devices_founds:
                startSearchActivity();
                break;
        }
    }

    private void startSearchActivity() {
        if(mDevices != null) {
            Intent i = new Intent(this, DeviceSearchListActivity.class);

            Bundle extras = new Bundle();
            extras.putSerializable("devicesSearch", mDevices);

            i.putExtras(extras);
//            startActivity(i);
            startActivityForResult(i, REQUEST_EXIT);
        }

    }

    private void startQrCode() {
        Intent i = new Intent(InitialActivity.this, com.google.zxing.client.android.CaptureActivity.class);
//        startActivity(i);
        startActivityForResult(i, REQUEST_EXIT);
    }

    private void startNewDeviceActivity() {
        Intent i = new Intent(this, DeviceFormActivity.class);
//        startActivity(i);
        startActivityForResult(i, REQUEST_EXIT);
    }

    public void searchDevices(){
        if (mDiscoveryThread == null) {
            mDiscoveryThread = prepareDiscoveryThread();
        } else {
            stopAndRefresh();
        }

        mDiscoveryThread.start();

        String text = getResources().getString(R.string.searching_dialog_messsage);

        tvDevicesFound.setText(text);

        pbInitial.setVisibility(View.VISIBLE);
    }

    private void stopAndRefresh() {
        mDiscoveryThread.interrupt();
        mDiscoveryThread = prepareDiscoveryThread();
        pbInitial.setVisibility(View.GONE);
    }

    public Discovery prepareDiscoveryThread() {
        return new Discovery(mContext, mDeviceReceiver);
    }

    private Discovery.DiscoveryReceiver mDeviceReceiver = new Discovery.DiscoveryReceiver() {
        @Override
        public void onReceiveDevices(ArrayList<Device> devices) {
            mDevices = new ArrayList<>(devices);

            InitialActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Locale.getDefault().toString().equals("pt_BR")) {
                        tvDevicesFound.setText(mDevices.size() + " dispositivos encontrados.");
                    } else {
                        tvDevicesFound.setText("Found " + mDevices.size() + " devices.");
                    }

                    pbInitial.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_initial, menu);
        if(DeviceManager.getInstance().getDevices().isEmpty())
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getMenuInflater().inflate(R.menu.menu_initial_return, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            searchDevices();
        }
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
}
