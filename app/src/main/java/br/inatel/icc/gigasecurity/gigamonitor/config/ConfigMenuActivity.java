package br.inatel.icc.gigasecurity.gigamonitor.config;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

//import br.inatel.icc.gigasecurity.gigamonitor.config.ddns.DDNSConfigActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.config.dns.DNSConfigActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ethernet.EthernetConfigActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.config.general.GeneralConfigActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.config.upnp.UpnpConfigActivity;

public class ConfigMenuActivity extends ActionBarActivity implements OnClickListener {
    public static final String KEY_ARGS = "device";

    // debug tag
    private final String TAG = ConfigMenuActivity.class.getSimpleName();

    private Button mWifiButton, mGeneralButton, mEthernetButton, mCloudButton, mDNSButton, mDHCPButton, mDDNSButton, mUPnPButton, mPasswordButton, mRebootDeviceButton;
    private Device mDevice;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        setListeners();

        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        mDevice = (Device) extras.getSerializable(KEY_ARGS);

        mDeviceManager = DeviceManager.getInstance();
    }

    private void findViews() {
        //mWifiButton         = (Button) findViewById(R.id.button_wifi_config);
        mGeneralButton      = (Button) findViewById(R.id.button_general_config);
        mEthernetButton     = (Button) findViewById(R.id.button_ethernet_config);
        //mCloudButton        = (Button) findViewById(R.id.button_cloud_config);
        mDNSButton          = (Button) findViewById(R.id.button_dns_config);
        //mDHCPButton         = (Button) findViewById(R.id.button_dhcp_config);
        mDDNSButton         = (Button) findViewById(R.id.button_ddns_config);
        mUPnPButton         = (Button) findViewById(R.id.button_upnp_config);
        //mPasswordButton     = (Button) findViewById(R.id.button_password_config);
        mRebootDeviceButton = (Button) findViewById(R.id.button_reboot_device);
    }

    private void setListeners() {
        //mWifiButton.setOnClickListener(this);
        mGeneralButton.setOnClickListener(this);
        mEthernetButton.setOnClickListener(this);
        //mCloudButton.setOnClickListener(this);
        mDNSButton.setOnClickListener(this);
        //mDHCPButton.setOnClickListener(this);
        mDDNSButton.setOnClickListener(this);
        mUPnPButton.setOnClickListener(this);
        //mPasswordButton.setOnClickListener(this);
        mRebootDeviceButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("device", mDevice);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            /*case R.id.button_wifi_config:
                intent = new Intent(this, WifiSearchActivity.class);
                break;*/
            case R.id.button_general_config:
                //intent = new Intent(this, GeneralConfigActivity.class);
                break;
            case R.id.button_ethernet_config:
                //intent = new Intent(this, EthernetConfigActivity.class);
                break;
           /* case R.id.button_cloud_config:
                intent = new Intent(this, CloudConfigActivity.class);
                break;*/
            case R.id.button_dns_config:
                //intent = new Intent(this, DNSConfigActivity.class);
                break;
            /*case R.id.button_dhcp_config:
                intent = new Intent(this, DHCPConfigActivity.class);
                break;*/
            case R.id.button_ddns_config:
                //intent = new Intent(this, DDNSConfigActivity.class);
                break;
            case R.id.button_upnp_config:
                //intent = new Intent(this, UpnpConfigActivity.class);
                break;
            /*case R.id.button_password_config:
                intent = new Intent(this, PasswordConfigActivity.class);
                break;*/
            case R.id.button_reboot_device:
                //showRebootDialog();
                break;
            default:
                break;
        }

        if (intent != null) {
            Bundle extras = new Bundle();
            extras.putSerializable("device", mDevice);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }



    private void showRebootDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigMenuActivity.this);

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

    private void startDeviceListActivity() {
        Intent intent = new Intent(ConfigMenuActivity.this, DeviceListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
