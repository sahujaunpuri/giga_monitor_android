package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.FavoritesDevicesAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class FavoritesDevicesListActivity extends ActionBarActivity {
    private ExpandableListView mListView;
    private FavoritesDevicesAdapter mCustomAdapter;
    private DeviceManager mDeviceManager;
    private ArrayList<Device> mDevices;
    private TextView mTextViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_devices_list);

        mTextViewBack = (TextView) findViewById(R.id.text_view_back);

        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDeviceManager = DeviceManager.getInstance();
        mDevices = mDeviceManager.getDevices();

        mListView = (ExpandableListView)findViewById(R.id.expandable_list_view_fav_devices);
        mCustomAdapter = new FavoritesDevicesAdapter(FavoritesDevicesListActivity.this, mDevices);
        mListView.setAdapter(mCustomAdapter);


    }

}
