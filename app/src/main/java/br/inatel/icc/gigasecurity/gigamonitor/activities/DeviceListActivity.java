package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.ListComponent;

public class DeviceListActivity extends ActionBarActivity {

    public static Context mContext;
    public static RelativeLayout layoutMain;
    public static ExpandableListView mExpandableListView;
    public static DeviceExpandableListAdapter mAdapter;
    public static DeviceManager mDeviceManager;
    public static ArrayList<Device> mDevices;
    public static int previousGroup = -1;
    public static int mySurfaceViewID = 0;
    public static ArrayList<ListComponent> listComponents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            //mAdapter.setClosedOnFirstVisibleItem(previousGroup);

            if(previousGroup != -1) {
                mExpandableListView.scrollTo(previousGroup, 0);
            }
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        initComponents();

        //if don't have any device registered, start InitialActivity.
        if(mDevices.size() == 0) {
            startInitialActivity();
            finish();
        }

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (previousGroup == -1) {
                    previousGroup = groupPosition;

                    return false;
                } else {
                    mAdapter.stopChannels(previousGroup);

                    mAdapter = new DeviceExpandableListAdapter(mContext, mDevices);
                    mExpandableListView.setAdapter(mAdapter);

                    previousGroup = -1;

                    return true;
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (previousGroup != -1) mAdapter.refreshVideo(previousGroup);
    }

    private void initComponents() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view2);
        mDeviceManager      = DeviceManager.getInstance();
        mContext            = DeviceListActivity.this;
        layoutMain          = (RelativeLayout) findViewById(R.id.layout_main);

        loadDevices();

        mAdapter = new DeviceExpandableListAdapter(mContext, mDevices);
        mExpandableListView.setAdapter(mAdapter);

    }

    public static void loadDevices() {
        if(mDevices == null) {
            mDevices = mDeviceManager.loadDevices(mContext);

            listComponents.clear();

            for(int i=0; i < mDevices.size(); i++) {
                ListComponent listComponent = new ListComponent(mDevices.get(i));

                listComponents.add(listComponent);
            }
        }
    }

    public static boolean hasGroupExpanded() {
        boolean check = false;

        for(int i = 0; i < mExpandableListView.getCount(); i++) {
            if(mExpandableListView.isGroupExpanded(i)){
                check = true;
            }
        }

        return check;
    }

    public static void openItemList(int position){
        mExpandableListView.expandGroup(position);
    }

    public static void closeList() {
        /*mAdapter = new DeviceExpandableListAdapter(mContext, mDevices);
        mExpandableListView.setAdapter(mAdapter);*/

        for(int i = 0; i < mDevices.size(); i++) {
            if(mExpandableListView.isGroupExpanded(i)){
                mExpandableListView.collapseGroup(i);
            }
        }

    }

    private void startInitialActivity() {
        Intent i = new Intent(this, InitialActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case (R.id.action_add):
                startInitialActivity();

                return true;
            case (R.id.action_edit):
                startEditActivity();

                return true;
            case (R.id.action_media):
                startMediaActivity();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startMediaActivity() {
        Intent intent = new Intent(mContext, MediaActivity.class);

        startActivity(intent);
    }

    private void startEditActivity() {
        Bundle extras = new Bundle();
        extras.putSerializable("devices", mDevices);

        Intent intent = new Intent(mContext, DeviceEditListActivity.class);
        intent.putExtras(extras);

        startActivity(intent);
    }

}
