package br.inatel.icc.gigasecurity.gigamonitor.config;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.AboutActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.password.PasswordConfigActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class ConfigMenuActivity extends ActionBarActivity implements OnClickListener {
    public static final String KEY_ARGS = "device";

    // debug tag
    private final String TAG = ConfigMenuActivity.class.getSimpleName();

    private TextView mPasswordTextView, mRebootDeviceTextView, mAboutTextView, mNetworkTextView, mBackTextView;
    private Device mDevice;
    private int deviceId;
    private DeviceManager mDeviceManager;
    private LinearLayout mNetworkLinearLayout, mPasswordLinearLayout, mRebootDeviceLinearLayout, mAboutLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTextViews();
        initLayouts();
        setListeners();


        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
//        mDevice = (Device) extras.getSerializable(KEY_ARGS);
        deviceId = (int) extras.getSerializable(KEY_ARGS);

        mDeviceManager = DeviceManager.getInstance();

        getSupportActionBar().hide();
    }

    private void initTextViews() {
        mPasswordTextView     = (TextView) findViewById(R.id.text_view_password_config);
        mRebootDeviceTextView = (TextView) findViewById(R.id.text_view_reboot_device);
        mAboutTextView        = (TextView) findViewById(R.id.text_view_about_device);
        mNetworkTextView      = (TextView) findViewById(R.id.text_view_network);
        mBackTextView         = (TextView) findViewById(R.id.text_view_cancel);
    }

    private void initLayouts() {
        mNetworkLinearLayout      = (LinearLayout) findViewById(R.id.linear_layout_network);
        mPasswordLinearLayout     = (LinearLayout) findViewById(R.id.linear_layout_password);
        mRebootDeviceLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_reboot);
        mAboutLinearLayout        = (LinearLayout) findViewById(R.id.linear_layout_about);
    }

    private void setListeners() {
        mBackTextView.setOnClickListener(this);
        mNetworkLinearLayout.setOnClickListener(this);
        mPasswordLinearLayout.setOnClickListener(this);
        mRebootDeviceLinearLayout.setOnClickListener(this);
        mAboutLinearLayout.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("device", deviceId);
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
            case R.id.linear_layout_password:
                intent = new Intent(this, PasswordConfigActivity.class);
                break;
            case R.id.linear_layout_reboot:
                showRebootDialog();
                break;
            case R.id.linear_layout_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.linear_layout_network:
                intent = new Intent(this, ConfigMenuNetworkActivity.class);
                break;
            case R.id.text_view_cancel:
                finish();
                break;
            default:
                break;
        }

        if (intent != null) {
            Bundle extras = new Bundle();
            extras.putSerializable("device", deviceId);
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
                        mDeviceManager.rebootDevice(mDeviceManager.findDeviceById(deviceId));

//                        startDeviceListActivity();
                        finish();
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
