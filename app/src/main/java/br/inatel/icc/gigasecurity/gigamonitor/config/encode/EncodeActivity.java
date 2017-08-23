package br.inatel.icc.gigasecurity.gigamonitor.config.encode;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 23/08/17.
 */

public class EncodeActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private Device mDevice;
    private DeviceManager mManager;
    private TextView tvChannel, channelNumber;
    private Spinner channelSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));

        setContentView(R.layout.activity_encode);

        tvChannel       = (TextView) findViewById(R.id.tv_channel);
        channelNumber   = (TextView) findViewById(R.id.channel_number);
        channelSpinner  = (Spinner) findViewById(R.id.channel_spinner);

        channelNumber.setText("1");
        channelSpinner.setOnItemSelectedListener(this);
        populateChannelSpinner();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        channelNumber.setText(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void populateChannelSpinner() {
        ArrayList<String> channelList = new ArrayList<>();
        for (int i=0; i<mDevice.getChannelNumber(); i++) {
            channelList.add(String.valueOf(i + 1));
        }
        ArrayAdapter<String> channelAdapter = new ArrayAdapter<>(this, android.support.v7.appcompat.R.layout.abc_action_menu_item_layout, channelList);
        channelAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        channelSpinner.setAdapter(channelAdapter);
    }

}
