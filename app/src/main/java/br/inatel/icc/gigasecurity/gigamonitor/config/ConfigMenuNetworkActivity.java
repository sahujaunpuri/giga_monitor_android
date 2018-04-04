package br.inatel.icc.gigasecurity.gigamonitor.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.ddns.DDNSConfigActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.dns.DNSConfigActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.ethernet.EthernetConfigActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class ConfigMenuNetworkActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String KEY_ARGS = "device";

    // debug tag
    private final String TAG = ConfigMenuActivity.class.getSimpleName();

    private LinearLayout mEthernetLinearLayout, mDNSLinearLayout, mDDNSLinearLayout;
    private TextView mCancelTextView;
    private Device mDevice;
    private int deviceId;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_network_config_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        setListeners();


        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        deviceId = (int) extras.getSerializable(KEY_ARGS);
        mDeviceManager = DeviceManager.getInstance();

        getSupportActionBar().hide();
    }

    private void findViews() {
        mEthernetLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_ethernet);
        mDDNSLinearLayout     = (LinearLayout) findViewById(R.id.linear_layout_ddns);
        mDNSLinearLayout      = (LinearLayout) findViewById(R.id.linear_layout_dns);
        mCancelTextView       = (TextView) findViewById(R.id.text_view_cancel);
    }

    private void setListeners() {
        mEthernetLinearLayout.setOnClickListener(this);
        mDNSLinearLayout.setOnClickListener(this);
        mDDNSLinearLayout.setOnClickListener(this);
        mCancelTextView.setOnClickListener(this);
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
            case R.id.linear_layout_ethernet:
                intent = new Intent(this, EthernetConfigActivity.class);
                break;
            case R.id.linear_layout_dns:
                intent = new Intent(this, DNSConfigActivity.class);
                break;
            case R.id.linear_layout_ddns:
                intent = new Intent(this, DDNSConfigActivity.class);
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

}
