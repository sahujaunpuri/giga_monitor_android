package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.xm.NetSdk;
import com.xm.SDK_AllAlarmIn;
import com.xm.javaclass.SDK_ALARM_INPUTCONFIG;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.AlarmInConfig;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 ** Created by palomacosta on 25/02/2015.
 */
public class AlarmInActivity extends ActionBarActivity {

    private final String TAG = AlarmInActivity.class.getSimpleName();

    private RadioButton mNormallyClosed, mNormallyOpen;

    private CheckBox mEnable;

    private boolean mCheckEnable;

    private NetSdk mNetSdk;

    private Device mDevice;

    private AlarmInConfig alarmInConfig;

    private DeviceManager mManager;

    private SDK_AllAlarmIn mAllAlarmInConfig;
    private SDK_ALARM_INPUTCONFIG alarmInputConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_in);

        mEnable = (CheckBox) findViewById(R.id.check_box_alarm_in_enable);
        mNormallyClosed = (RadioButton) findViewById(R.id.check_box_normally_closed);
        mNormallyOpen = (RadioButton) findViewById(R.id.check_box_normally_open);

        mManager = DeviceManager.getInstance();
        mManager.initAlarm(mAlarmListener);

        if (savedInstanceState == null) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");
        } else {
            mDevice = (Device) savedInstanceState.getSerializable("device");
        }

        mAllAlarmInConfig = mManager.getAlarmInConfig(mDevice);

        mEnable.isChecked();

    }

    public void isEnableChecked() {

        if (mEnable.isChecked()) {

            mCheckEnable = alarmInConfig.mEnable;
            mCheckEnable = true;

        } else {

            mCheckEnable = alarmInConfig.mEnable;
            mCheckEnable = false;
        }
    }


    public void saveAlarmIn() {
        // TODO
    }

    private final NetSdk.OnAlarmListener mAlarmListener = new NetSdk.OnAlarmListener() {
        @Override
        public void onAlarm(long loginID, int channel, int event, int status, int[] dateTimeArray) {
            final StringBuilder sb = new StringBuilder();

            sb.append(String.format("loginID: %d Channel: %d EventType: %d Status: %d", loginID,
                    channel, event, status));

            Object[] arrayOfObject = new Object[6];
            arrayOfObject[0] = dateTimeArray[0];
            arrayOfObject[1] = dateTimeArray[1];
            arrayOfObject[2] = dateTimeArray[2];
            arrayOfObject[3] = dateTimeArray[4];
            arrayOfObject[4] = dateTimeArray[5];
            arrayOfObject[5] = dateTimeArray[6];
            sb.append(String.format(" %04d-%02d-%02d %02d:%02d:%02d", arrayOfObject));

            Log.d(TAG, "onAlarm " + sb.toString());

            AlarmInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setText(sb.toString());
                }
            });
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("device", mDevice);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_alarm_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveAlarmIn();
            default:
                return false;
        }
    }
}
