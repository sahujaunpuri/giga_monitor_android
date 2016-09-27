package br.inatel.icc.gigasecurity.gigamonitor.config.dhcp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DHCPListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class DHCPConfigActivity extends ActionBarActivity {

    // Debug tag
    private final String TAG = DHCPConfigActivity.class.getSimpleName();

    private Device mDevice;
    private DeviceManager mManager;

    private DHCPConfig mConfig;
    private int mIndex;

    private ListView mListView;

    private DHCPListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhcp);

        mDevice = (Device) getIntent().getExtras().getSerializable("device");

        mManager = DeviceManager.getInstance();

        mConfig = mDevice.getDhcpConfig();


        if (savedInstanceState == null) {
            mDevice = (Device) getIntent().getExtras().getSerializable("device");

            mConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
                @Override
                public void onPreFinish(Boolean success) {
                    if(!success){
                        String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);

                        Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
                        finish();

                        return;
                    }
                  findViews();

                }

                @Override
                public void onFinish(Boolean success) {}
            });
        } else {
            mDevice = (Device) savedInstanceState.getSerializable("device");
            mConfig = mDevice.getDhcpConfig();
           findViews();
        }
    }

    private void findViews() {
        mListView = (ListView) findViewById(R.id.list_view_dhcp);

        mAdapter = new DHCPListAdapter(this, mConfig);

        mListView.setAdapter(mAdapter);

        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
//        mListView.setOnItemClickListener(this, );



    }

//    private void setListeners() {
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mIndex = position;
//                initWidgets();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) { }
//        });
//
//        mEnabledCheckBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) { }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                G.SetValue(mConfigAll.st_0_vNetDHCPConfig[mIndex].st_1_ifName, s.toString());
//            }
//        });
//
//        mEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mConfigAll.st_0_vNetDHCPConfig[mIndex].st_0_bEnable = isChecked;
//            }
//        });
//    }



     private void save() {
         Utils.hideKeyboard(this);
         mConfig.setConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener(){

             @Override
             public void onFinish(Boolean success) {
                 if (success) {
                     final int resId = success ? R.string.saved : R.string.invalid_device_save;
                     Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
                 }
             }
         });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_form, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_config_save:
                save();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }
}
