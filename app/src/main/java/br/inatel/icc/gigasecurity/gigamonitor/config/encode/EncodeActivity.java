package br.inatel.icc.gigasecurity.gigamonitor.config.encode;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

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
    private Switch audioSwitch, audioSwitch_secundary;
    private Spinner channelSpinner, resolutionSpinner, frameRateSpinner, qualitySpinner;
    private Spinner channelSpinner_secundary, resolutionSpinner_secundary, frameRateSpinner_secundary, qualitySpinner_secundary;
    private static int CHANNELSPINNERID = 1;
    private static int RESOLUTIONSPINNERID = 2;
    private static int FRAMETRATESPINNERID = 3;
    private static int QUALITYSPINNERID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));

        setContentView(R.layout.activity_encode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        channelSpinner  = (Spinner) findViewById(R.id.channel_spinner);
        resolutionSpinner   = (Spinner) findViewById(R.id.resolution_spinner);
        frameRateSpinner    = (Spinner) findViewById(R.id.frame_rate_spinner);
        qualitySpinner      = (Spinner) findViewById(R.id.quality_spinner);
        audioSwitch = (Switch) findViewById(R.id.audio_toggle_button);

        channelSpinner.setOnItemSelectedListener(this);
        populateSpinner(CHANNELSPINNERID);


        resolutionSpinner.setOnItemSelectedListener(this);
        populateSpinner(RESOLUTIONSPINNERID);
        frameRateSpinner.setOnItemSelectedListener(this);
        populateSpinner(FRAMETRATESPINNERID);
        qualitySpinner.setOnItemSelectedListener(this);
        populateSpinner(QUALITYSPINNERID);

        audioSwitch.setChecked(false);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.channel_spinner:
//                channelNumber.setText(parent.getItemAtPosition(position).toString());
//                break;
//            case R.id.resolution_spinner:
//                resolutionValue.setText(parent.getItemAtPosition(position).toString());
//                break;
//            case R.id.frame_rate_spinner:
//                frameRateValue.setText(parent.getItemAtPosition(position).toString());
//                break;
//            case R.id.quality_spinner:
//                qualityValue.setText(parent.getItemAtPosition(position).toString());
//                break;
//            default:
//                break;
//        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void finish() {
        super.finish();
        if (audioSwitch.isChecked()) {
            Log.e("Audio", "true");
        } else {
            Log.e("Audio", "false");
        }
    }

    private void populateSpinner(final int spinnerId) {
        if (spinnerId == CHANNELSPINNERID) {
            ArrayList<String> channelList = new ArrayList<>();
            for (int i=0; i<mDevice.getChannelNumber(); i++) {
                channelList.add(String.valueOf(i + 1));
            }
            ArrayAdapter<String> channelAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, channelList);
            channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            channelSpinner.setAdapter(channelAdapter);
        } else if (spinnerId == RESOLUTIONSPINNERID) {
            ArrayList<String> resolutionList = populateResolutionSpinner();
            ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, resolutionList);
            resolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            resolutionSpinner.setAdapter(resolutionAdapter);
        } else if (spinnerId == FRAMETRATESPINNERID) {
            ArrayList<String> frameRateList = populateFrameRateSpinner();
            ArrayAdapter<String> frameRateAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, frameRateList);
            frameRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            frameRateSpinner.setAdapter(frameRateAdapter);
        } else if (spinnerId == QUALITYSPINNERID) {
            ArrayList<String> qualityList = populateQualitySpinner();
            ArrayAdapter<String> qualityAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, qualityList);
            qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            qualitySpinner.setAdapter(qualityAdapter);
        }
    }

    private ArrayList<String> populateResolutionSpinner() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.d1_resolution));
        list.add(getResources().getString(R.string.resolution_960h));
        list.add(getResources().getString(R.string.resolution_720p));
        list.add(getResources().getString(R.string.resolution_1080p));
        return list;
    }

    private ArrayList<String> populateFrameRateSpinner() {
        ArrayList<String> list = new ArrayList<>();
        int size;
//        if (tbStream.isChecked()) {
//            size = 12;
//        } else {
            size = 27;
//        }

        for (int i=0; i<size; i++) {
            list.add(String.valueOf(i + 1));
        }
        return list;
    }

    private ArrayList<String> populateQualitySpinner() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.excellent_quality));
        list.add(getResources().getString(R.string.very_good_quality));
        list.add(getResources().getString(R.string.good_quality));
        list.add(getResources().getString(R.string.regular_quality));
        list.add(getResources().getString(R.string.bad_quality));
        list.add(getResources().getString(R.string.very_bad_quality));
        return list;
    }

}
