package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.SDK_AlarmOutConfig;
import com.xm.SDK_AllAlarmOut;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceAlarmOutAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 ** Created by palomacosta on 25/02/2015.
 */
public class AlarmOutActivity extends ActionBarActivity {

    private final String TAG = AlarmOutActivity.class.getSimpleName();
    private Device mDevice;
    private DeviceManager mManager;
    private TextView mAlarmStatusResult;
    private ListView mListView;
    private SDK_AllAlarmOut mAllAlarmOutConfig;
    private DeviceAlarmOutAdapter mDeviceAlarmOutAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_out);

        initComponents();

        if (savedInstanceState == null) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");
        } else {
            mDevice = (Device) savedInstanceState.getSerializable("device");
        }

        mAllAlarmOutConfig     = mManager.getAlarmOutConfig(mDevice);
        mDeviceAlarmOutAdapter = new DeviceAlarmOutAdapter(AlarmOutActivity.this, mAllAlarmOutConfig, mDevice);
        mListView.setAdapter(mDeviceAlarmOutAdapter);

    }

    private void initComponents() {

        mListView = (ListView) findViewById(R.id.list_view_alarm_out);

        mManager = DeviceManager.getInstance();
    }

    public void saveAlarmOut() {

        for(int i = 0; i < mDeviceAlarmOutAdapter.getCount(); i++) {

            mAllAlarmOutConfig.alarmOutParam[i] = (SDK_AlarmOutConfig) mDeviceAlarmOutAdapter.getItem(i);

        }

        if(mManager.setAllAlarmOutConfig(mDevice, mAllAlarmOutConfig)) {
            Toast.makeText(this, "Salvo com sucesso.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao salvar.", Toast.LENGTH_LONG).show();
        }

    }

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
                saveAlarmOut();
            default:
                return false;
        }
    }
}
