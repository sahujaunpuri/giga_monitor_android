package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private MenuItem atualizeButton;
    private ImageView ivSearch;
    private TextView tvDevicesFound, tvCancel, tvSearchingDevices;
    private ImageButton imgBtnNewDevice, imgBtnQrCode, imgBtnRefresh;
    private Context mContext;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial);

        mDeviceManager = DeviceManager.getInstance();

        mContext = this;

        initComponents();

        searchDevices();

        getSupportActionBar().hide();

        setCancelAction();
    }

    private void initComponents() {
        tvDevicesFound     = (TextView) findViewById(R.id.tv_devices_founds);
        imgBtnQrCode       = (ImageButton) findViewById(R.id.tv_scan_qr_code);
        imgBtnNewDevice    = (ImageButton) findViewById(R.id.tv_setup_new_device);
        tvCancel           = (TextView) findViewById(R.id.text_view_cancel);
        ivSearch           = (ImageView) findViewById(R.id.ic_search);
        tvSearchingDevices = (TextView) findViewById(R.id.text_view_searching);
        imgBtnRefresh      = (ImageButton) findViewById(R.id.image_btn_refresh);

        imgBtnRefresh.setOnClickListener(this);
        imgBtnQrCode.setOnClickListener(this);
        imgBtnNewDevice.setOnClickListener(this);
        tvDevicesFound.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
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
            case R.id.text_view_cancel:
                finish();
                break;
            case R.id.image_btn_refresh:
                atualizeButton.setVisible(false);
                imgBtnRefresh.setVisibility(View.GONE);
                searchDevices();
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
        startActivityForResult(i, REQUEST_EXIT);
    }

    private void startNewDeviceActivity() {
        Intent i = new Intent(this, DeviceFormActivity.class);
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

        tvSearchingDevices.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.VISIBLE);
        tvDevicesFound.setVisibility(View.GONE);
        imgBtnRefresh.setVisibility(View.GONE);
    }

    private void stopAndRefresh() {
        try {
            mDiscoveryThread.interrupt();
            mDiscoveryThread = prepareDiscoveryThread();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            tvSearchingDevices.setVisibility(View.GONE);
            ivSearch.setVisibility(View.GONE);
            imgBtnRefresh.setVisibility(View.VISIBLE);
        }
    }

    public Discovery prepareDiscoveryThread() {
        return new Discovery(mContext, mDeviceReceiver);
    }

    private Discovery.DiscoveryReceiver mDeviceReceiver = new Discovery.DiscoveryReceiver() {
        @Override
        public void onReceiveDevices(ArrayList<Device> devices) {
            mDevices = devicesThatAreNotAddedYet(devices);

            InitialActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(Locale.getDefault().toString().equals("pt_BR")) {
                            tvDevicesFound.setText(mDevices.size() + " dispositivos encontrados.");
                        } else {
                            tvDevicesFound.setText("Found " + mDevices.size() + " devices.");
                        }
                        atualizeButton.setVisible(true);
                        tvSearchingDevices.setVisibility(View.GONE);
                        ivSearch.setVisibility(View.GONE);
                        tvDevicesFound.setVisibility(View.VISIBLE);
                        imgBtnRefresh.setVisibility(View.VISIBLE);
                    } catch (Exception error){
                        error.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onFailedSearch() {
            InitialActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAndRefresh();
                }
            });
        }
    };

    public void setCancelAction() {
        if(DeviceManager.getInstance().getDevices().size()<2) {
            tvCancel.setVisibility(View.INVISIBLE);
        } else {
            tvCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(DeviceManager.getInstance().getDevices().size()<2) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getMenuInflater().inflate(R.menu.menu_initial_return, menu);
        atualizeButton = menu.findItem(R.id.action_refresh);
        atualizeButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            atualizeButton.setVisible(false);
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

    private ArrayList<Device> devicesThatAreNotAddedYet(ArrayList<Device> devices) {
        ArrayList<Device> newDevices = new ArrayList<>();
        try {
            for (Device dev: devices) {
                boolean alreadyAdded = false;
                for (Device device: mDeviceManager.getDevices()) {
                    if (dev.getSerialNumber().equals(device.getSerialNumber())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    newDevices.add(dev);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return newDevices;
    }
}
