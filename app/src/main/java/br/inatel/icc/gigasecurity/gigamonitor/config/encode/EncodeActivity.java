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
    private ArrayList<String> currentPrimaryResolutions, currentPrimaryFPS, currentPrimaryQualities, currentPrimaryAudios;
    private ArrayList<String> currentSecondaryResolutions, currentSecondaryFPS, currentSecondaryQualities, currentSecondaryAudios;
    private ProgressDialog progressDialog;
    private String[] qualities;
    private final String[] resolutions = {"D1", "HD1", "BCIF", "CIF", "QCIF", "VGA", "QVGA", "SVCD", "QQVGA", "ND1", "960H", "720P", "960", "UXGA", "1080P", "WUXGA", "2_5M", "3M", "5M", "1080N"};
    private final int[] resolutionsValues = {405504, 202752, 202752, 101376, 101376, 25344, 307200, 76800, 230400, 20480, 46080, 534528, 921600, 1228800, 1920000, 2073600, 2304000, 2635776, 3145728, 3145728, 5271552, 1036800};
    private final int maxFPS = 25;
    private String[] fps;
    private int currentChannel;
    private int minSubRes;
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
        currentChannel = 0;
        initMinSubRes();
        fps = new String[maxFPS];
        for(int i=0; i<maxFPS; i++)
            fps[i] = Integer.toString(i+1);

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

        updateSpinners(0);
    }

    private void updateSpinners(int channel){
        resolutionSpinner.setSelection(currentPrimaryResolutions.indexOf(primaryResolution[channel]));
        frameRateSpinner.setSelection(/*currentPrimaryFPS.indexOf*/(primaryFrameRate[channel])-1);
        qualitySpinner.setSelection(primaryQualities[channel]-1);
        audioSwitch.setChecked(primaryAudio[channel]);

        resolutionSpinner_secondary.setSelection(currentSecondaryResolutions.indexOf(secondaryResolution[channel]));
        frameRateSpinner_secondary.setSelection(/*currentSecondaryFPS.indexOf*/(secondaryFrameRate[channel])-1);
        qualitySpinner_secondary.setSelection(secondaryQualities[channel]-1);
        audioSwitch_secondary.setChecked(secondaryAudio[channel]);
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
        switch (parent.getId()) {
            case R.id.channel_spinner:
                
                break;
            case R.id.resolution_spinner:

                break;
            case R.id.frame_rate_spinner:

                break;
            default:
                break;
        }

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
            ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, currentPrimaryResolutions);
            resolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(resolutionAdapter);
        } else if (spinnerId == FRAMETRATESPINNERID) {
            ArrayAdapter<String> frameRateAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, fps);
            frameRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(frameRateAdapter);
        } else if (spinnerId == QUALITYSPINNERID) {
            ArrayAdapter<String> qualityAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, qualities);
            qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(qualityAdapter);
        }
    }

    private void updateResolutionSpinner(int nMark){
        int nCount = 0;
        for (int i = 0; i <= 20; ++i) {
            if (0 != (nMark & (1 << i))) {
                nCount++;
            }
        }
        String sv[] = new String[nCount];
        int j = 0;
        for (int i = 0; i <= 20; ++i) {
            if (0 != (nMark & (1 << i))) {
                sv[j] = resolutions[i];
                j++;
            }
        }

        primaryResolution = sv;
    }


    private int getResolutionSize(int resIndex){
        if (resIndex >= 0 && resIndex <= resolutions.length) {
            return resolutionsValues[resIndex];
        }
        return resolutionsValues[0];
    }

    private int getMainResMark() {
        int nRetMark = mDevice.getImageSizePerChannel(currentChannel);
        if (nRetMark == 0) {
            nRetMark = mDevice.getPrimaryResolutionMask();
        }
        return nRetMark;
    }

    private int getSubResMark(int mainRes) {
        int nRetMark = mDevice.getExImageSizePerChannel(mainRes);
        if (nRetMark == 0) {
            nRetMark = mDevice.getSecondaryResolutionMask();
        }
        return nRetMark;
    }

    private int initMinSubRes() {
        int nMinResSize = 0;
        int nMainMark = getMainResMark();
        for (int j = 0; j < resolutions.length; ++j) {
            if (0 != (nMainMark & (0x1 << j))) {
                int nSubResMark = getSubResMark(j);
                for (int i = 0; i < resolutions.length; ++i) {
                    if (0 != (nSubResMark & (0x1 << i))) {
                        if (nMinResSize == 0 || nMinResSize > getResolutionSize(i)) {
                            nMinResSize = getResolutionSize(i);
                            minSubRes = i;
                        }
                    }
                }
            }
        }
        return 0;
    }

    private int getLastAbility(int nMaxEncodePower, int resolutionIndex, int rate){
        return nMaxEncodePower - getResolutionSize(resolutionIndex) * rate;
    }

    private int getResMark(int nLastAbility, int nRate, int nSupportResMark){
        int nRetMark = 0;
        int nGetRes = 0;
        for (int i = 0; i <= 20; ++i) {
            if (0 != (nSupportResMark & (1 << i))) {
                nGetRes = getResolutionSize(i);
                if (nGetRes * nRate <= nLastAbility) {
                    nRetMark |= (0x1 << i);
                }
            }
        }
        return nRetMark;
    }

    int getMaxRate(int nLastAbility, int nResIndex) {
        int nResSize = getResolutionSize(nResIndex);
        int i = maxFPS;
        for (; i > -1; --i) {
            if (nResSize * i <= nLastAbility) {
                break;
            }
        }
        return i;
    }

    private void updateResolutionData(int stream){
        int nRetMark;
        int chnMaxEncodePower = mDevice.getMaxEncodePowerPerChannel(currentChannel);
        int nLastAbility = getLastAbility(chnMaxEncodePower, minSubRes, 1);
        if(stream == PRIMARY){
            nRetMark = getResMark(nLastAbility, Integer.parseInt(currentPrimaryFPS.get(currentChannel)), getMainResMark());
        }else{
            int resIndex = -1;
            String currentRes = currentPrimaryResolutions.get(currentChannel);
            for(int i=0; i<resolutions.length; i++)
                if(resolutions[i].equals(currentRes)) {
                    resIndex = i;
                    break;
                }
            nRetMark = getResMark(nLastAbility, 1, getSubResMark(resIndex));
        }

        updateResolutionSpinner(nRetMark);
    }

    private void updateFPSSpinner(int nMaxRate){
        int sv[] = new int[nMaxRate];
        for (int i = 0; i < nMaxRate; ++i)
            sv[i] = i + 1;

        primaryFrameRate = sv;
    }

    private void updateFPSData(int stream){
        int nMaxRate, nLastAbility;
        int chnMaxEncodePower = mDevice.getMaxEncodePowerPerChannel(currentChannel);
        if(stream == PRIMARY){
            nLastAbility = getLastAbility(chnMaxEncodePower, minSubRes, 1);
            nMaxRate = getMaxRate(nLastAbility, Integer.parseInt(currentPrimaryFPS.get(currentChannel)));
        }else{
            int resIndex = -1;
            String currentRes = currentPrimaryResolutions.get(currentChannel);
            for(int i=0; i<resolutions.length; i++)
                if(resolutions[i].equals(currentRes)) {
                    resIndex = i;
                    break;
                }
            nLastAbility = getLastAbility(chnMaxEncodePower, resIndex, 1);
            nMaxRate = getMaxRate(nLastAbility, getSubResMark(resIndex));
        }

        updateFPSSpinner(nMaxRate);
    }

}
