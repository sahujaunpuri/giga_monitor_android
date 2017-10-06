package br.inatel.icc.gigasecurity.gigamonitor.config.encode;

import android.app.ProgressDialog;
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
import java.util.Arrays;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 23/08/17.
 */

public class EncodeActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private final String TAG = "EncodeActivity";
    private Device mDevice;
    private DeviceManager mManager;
    private Switch audioSwitch, audioSwitch_secondary;
    private Spinner channelSpinner, resolutionSpinner, frameRateSpinner, qualitySpinner;
    private Spinner resolutionSpinner_secondary, frameRateSpinner_secondary, qualitySpinner_secondary;
    private String[] primaryResolution;
    private int[] primaryFrameRate;
    private int[] primaryQualities;
    private Boolean[] primaryAudio;
    private String[] secondaryResolution;
    private int[] secondaryFrameRate;
    private int[] secondaryQualities;
    private Boolean[] secondaryAudio;
    private ArrayList currentPrimaryResolutions, currentPrimaryFPS, currentPrimaryQualities, currentPrimaryAudios;
    private ArrayList currentSecondaryResolutions, currentSecondaryFPS, currentSecondaryQualities, currentSecondaryAudios;
    private ProgressDialog progressDialog;
    private String[] qualities;
    private final String[] resolutions = {"D1", "HD1", "BCIF", "CIF", "QCIF", "VGA", "QVGA", "SVCD", "QQVGA", "ND1", "960H", "720P", "960", "UXGA", "1080P", "WUXGA", "2_5M", "3M", "5M", "1080N"};
    private final int[] resolutionsValues = {405504, 202752, 202752, 101376, 101376, 25344, 307200, 76800, 230400, 20480, 46080, 534528, 921600, 1228800, 1920000, 2073600, 2304000, 2635776, 3145728, 3145728, 5271552, 1036800};
    private final int maxFPS = 25;
    private int[] fps;
    private static int CHANNELSPINNERID = 1;
    private static int RESOLUTIONSPINNERID = 2;
    private static int FRAMETRATESPINNERID = 3;
    private static int QUALITYSPINNERID = 4;
    private static int PRIMARY = 1;
    private static int SECONDARY = 2;
    private ConfigListener configListener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
            primaryResolution = Arrays.copyOf(mDevice.getPrimaryResolution() , mDevice.getChannelNumber());
            primaryFrameRate = Arrays.copyOf(mDevice.getPrimaryFrameRate() , mDevice.getChannelNumber());
            primaryQualities = Arrays.copyOf(mDevice.getPrimaryQualities() , mDevice.getChannelNumber());
            primaryAudio = Arrays.copyOf(mDevice.getPrimaryAudio() , mDevice.getChannelNumber());
            secondaryResolution = Arrays.copyOf(mDevice.getSecondaryResolution() , mDevice.getChannelNumber());
            secondaryFrameRate = Arrays.copyOf(mDevice.getSecondaryFrameRate() , mDevice.getChannelNumber());
            secondaryQualities = Arrays.copyOf(mDevice.getSecondaryQualities() , mDevice.getChannelNumber());
            secondaryAudio = Arrays.copyOf(mDevice.getSecondaryAudio() , mDevice.getChannelNumber());

            currentPrimaryResolutions = new ArrayList();
            currentSecondaryResolutions = new ArrayList();
            for(int i=0; i<mDevice.getChannelNumber(); i++){
                currentPrimaryResolutions.add(resolutions[i]);
                currentSecondaryResolutions.add(resolutions[i]);
            }

            initSpinners();

            progressDialog.dismiss();
        }

        @Override
        public void onSetConfig() {

        }

        @Override
        public void onError() {
            Log.e(TAG, "onError: upload error");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_encode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initData();

        channelSpinner                = (Spinner) findViewById(R.id.channel_spinner);
        resolutionSpinner             = (Spinner) findViewById(R.id.resolution_spinner);
        frameRateSpinner              = (Spinner) findViewById(R.id.frame_rate_spinner);
        qualitySpinner                = (Spinner) findViewById(R.id.quality_spinner);
        audioSwitch                   = (Switch) findViewById(R.id.audio_toggle_button);
        resolutionSpinner_secondary   = (Spinner) findViewById(R.id.resolution_spinner_secondary);
        frameRateSpinner_secondary    = (Spinner) findViewById(R.id.frame_rate_spinner_secondary);
        qualitySpinner_secondary      = (Spinner) findViewById(R.id.quality_spinner_secondary);
        audioSwitch_secondary         = (Switch) findViewById(R.id.audio_toggle_button_secondary);

    }

    private void initData(){
        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
        fps = new int[maxFPS];
        for(int i=0; i<maxFPS; i++)
            fps[i] = i+1;

        qualities = new String[]{getResources().getString(R.string.very_bad_quality), getResources().getString(R.string.bad_quality), getResources().getString(R.string.regular_quality), getResources().getString(R.string.good_quality),
                getResources().getString(R.string.very_good_quality), getResources().getString(R.string.excellent_quality)};

        this.primaryResolution = new String[mDevice.getChannelNumber()];
        this.primaryFrameRate = new int[mDevice.getChannelNumber()];
        this.primaryQualities = new int[mDevice.getChannelNumber()];
        this.primaryAudio = new Boolean[mDevice.getChannelNumber()];
        this.secondaryResolution = new String[mDevice.getChannelNumber()];
        this.secondaryFrameRate = new int[mDevice.getChannelNumber()];
        this.secondaryQualities = new int[mDevice.getChannelNumber()];
        this.secondaryAudio = new Boolean[mDevice.getChannelNumber()];

        progressDialog = ProgressDialog.show(this, "Buscando Configurações", "Aguarde");
        mManager.getJsonConfig(mDevice, "Simplify.Encode", configListener);
    }

    private void initSpinners(){
        channelSpinner.setOnItemSelectedListener(this);
        populateSpinner(CHANNELSPINNERID, channelSpinner, PRIMARY);
        resolutionSpinner.setOnItemSelectedListener(this);
        populateSpinner(RESOLUTIONSPINNERID, resolutionSpinner, PRIMARY);
        frameRateSpinner.setOnItemSelectedListener(this);
        populateSpinner(FRAMETRATESPINNERID, frameRateSpinner, PRIMARY);
        qualitySpinner.setOnItemSelectedListener(this);
        populateSpinner(QUALITYSPINNERID, qualitySpinner, PRIMARY);

        resolutionSpinner_secondary.setOnItemSelectedListener(this);
        populateSpinner(RESOLUTIONSPINNERID, resolutionSpinner_secondary, SECONDARY);
        frameRateSpinner_secondary.setOnItemSelectedListener(this);
        populateSpinner(FRAMETRATESPINNERID, frameRateSpinner_secondary, SECONDARY);
        qualitySpinner_secondary.setOnItemSelectedListener(this);
        populateSpinner(QUALITYSPINNERID, qualitySpinner_secondary, SECONDARY);

        resolutionSpinner.setSelection(currentPrimaryResolutions.indexOf(primaryResolution[0]));
        frameRateSpinner.setSelection(/*currentPrimaryFPS.indexOf*/(primaryFrameRate[0])-1);
        qualitySpinner.setSelection(primaryQualities[0]-1);
        audioSwitch.setChecked(primaryAudio[0]);

        resolutionSpinner_secondary.setSelection(currentSecondaryResolutions.indexOf(secondaryResolution[0]));
        frameRateSpinner_secondary.setSelection(/*currentSecondaryFPS.indexOf*/(secondaryFrameRate[0])-1);
        qualitySpinner_secondary.setSelection(secondaryQualities[0]-1);
        audioSwitch_secondary.setChecked(secondaryAudio[0]);
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

    private void populateSpinner(final int spinnerId, Spinner spinner, final int stream) {
        if (spinnerId == CHANNELSPINNERID) {
            ArrayList<String> channelList = new ArrayList<>();
            for (int i=0; i<mDevice.getChannelNumber(); i++) {
                channelList.add(String.valueOf(i + 1));
            }
            ArrayAdapter<String> channelAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, channelList);
            channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            channelSpinner.setAdapter(channelAdapter);
        } else if (spinnerId == RESOLUTIONSPINNERID) {
//            ArrayList<String> resolutionList = populateResolutionSpinner();
//            ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, resolutionList);
            ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, currentPrimaryResolutions);
            resolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(resolutionAdapter);
        } else if (spinnerId == FRAMETRATESPINNERID) {
            ArrayList<String> frameRateList = populateFrameRateSpinner();
            ArrayAdapter<String> frameRateAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, frameRateList);
            frameRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(frameRateAdapter);
        } else if (spinnerId == QUALITYSPINNERID) {
            ArrayAdapter<String> qualityAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, qualities);
            qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(qualityAdapter);
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
        list.add(getResources().getString(R.string.very_bad_quality));
        list.add(getResources().getString(R.string.bad_quality));
        list.add(getResources().getString(R.string.regular_quality));
        list.add(getResources().getString(R.string.good_quality));
        list.add(getResources().getString(R.string.very_good_quality));
        list.add(getResources().getString(R.string.excellent_quality));

        return list;
    }

}
