package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import com.crashlytics.android.Crashlytics;

import br.inatel.icc.gigasecurity.gigamonitor.model.StatePreferences;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
import io.fabric.sdk.android.Fabric;

public class DeviceListActivity extends ActionBarActivity {
    private final String TAG = "MainActivity";
    public static Context mContext;
    public static ExpandableListView mExpandableListView;
    public static DeviceExpandableListAdapter mAdapter;
    public static DeviceManager mDeviceManager;
    public static ArrayList<Device> mDevices;
    public static int previousGroup = -1;
    public static int expandedGroups = 0;
    public static boolean running = false;
    private StatePreferences statePreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        Fabric.with(this, new Crashlytics());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        mDeviceManager = DeviceManager.getInstance();
        if(mDeviceManager.mFunUserHandler == -1)
            mDeviceManager.init(this);

        initComponents();

//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, DeviceListActivity.class));

        //if don't have any device registered, start InitialActivity.
        if(mDevices.size() == 1) {
            startInitialActivity();
//            finish();
        }

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(mDeviceManager.networkType == -1)
                    return true;
                if (previousGroup == -1) {
                    previousGroup = groupPosition;
                    parent.expandGroup(groupPosition, true);
                    return true;
                } else if (mDeviceManager.getDeviceChannelsManagers().get(previousGroup).recCounter > 0) {
                    Toast.makeText(mContext, "Finalize a gravação", Toast.LENGTH_SHORT).show();
                    return true;
                } else if(previousGroup == groupPosition){
                    /*parent.collapseGroup(previousGroup);
                    previousGroup = -1;*/
                    Log.d(TAG, "onGroupClick: ");
                    return true;
                } else {
                    parent.collapseGroup(previousGroup);
                    parent.expandGroup(groupPosition, true);
                    previousGroup = groupPosition;
                    return true;
                }
            }
        });

        mDeviceManager.setSharedPreferences(mContext.getSharedPreferences("state", MODE_PRIVATE));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            if(previousGroup != -1) {
                mExpandableListView.scrollTo(previousGroup, 0);
            }
        } else{
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        if(previousGroup != -1)
            mAdapter.onChangeOrientation(previousGroup);
    }

    @Override
    protected void onStart(){
        super.onStart();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("DeviceList", "Stop");
        verifyIfSomeChannelIsSoundingOrRecording();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDeviceManager.collapse >=0 && previousGroup == mDeviceManager.collapse) {
            if(mExpandableListView.isGroupExpanded(mDeviceManager.collapse))
                mExpandableListView.collapseGroup(mDeviceManager.collapse);
//            mAdapter.childViewHolder.get(mDeviceManager.collapse).recyclerViewChannels = null;
            mDeviceManager.collapse = -1;
            previousGroup = -1;
            mAdapter.notifyDataSetChanged();
        }

        //load last state
        if(!mDeviceManager.loadedState) {
            statePreferences = mDeviceManager.loadState();
            previousGroup = statePreferences.previousGroup;
            if (previousGroup > -1 && mDeviceManager.networkType > -1)
                mExpandableListView.expandGroup(previousGroup);
            else if(mDeviceManager.networkType == 1 && mDevices.size() > 1)
                Toast.makeText(mContext, "Finalize a gravação", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResume: group: " + previousGroup + ", channel: " + statePreferences.previousChannel + ", grid: " + statePreferences.previousGrid + ", HD: " + statePreferences.previousHD);
            mDeviceManager.loadedState = true;
        }

        mDeviceManager.loginAllDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();

        statePreferences = new StatePreferences();
        statePreferences.previousGroup = previousGroup;
        if(previousGroup > -1) {
            ChannelsManager channelsManager = mDeviceManager.getDeviceChannelsManagers().get(previousGroup);
            statePreferences.previousChannel = channelsManager.lastFirstVisibleItem;
            statePreferences.previousGrid = channelsManager.numQuad;
            statePreferences.previousLastGrid = channelsManager.lastNumQuad;
            statePreferences.previousLastVisibleChannel = channelsManager.lastFirstItemBeforeSelectChannel;
        }
        mDeviceManager.saveState(statePreferences);

//        SharedPreferences.Editor editor = mPreferences.edit();
//        editor.putInt("previousGroup", previousGroup);
//        if(previousGroup != -1) {
//            ChannelsManager channelsManager = mDeviceManager.getDeviceChannelsManagers().get(previousGroup);
//            previousChannel = channelsManager.lastFirstVisibleItem;
//            previousGrid = channelsManager.numQuad;
//            previousLastGrid = channelsManager.lastNumQuad;
//            editor.putInt("previousChannel", previousChannel);
//            editor.putInt("previousGrid", previousGrid);
//            editor.putInt("previousLastGrid", previousLastGrid);
//            editor.putInt("previousHD", channelsManager.hdChannel);
//            editor.putInt("previousLastVisibleChannel", channelsManager.lastFirstItemBeforeSelectChannel);
//        }
//        editor.apply();
    }

    private void initComponents() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view2);
        mContext            = DeviceListActivity.this;
        mAdapter            = mDeviceManager.getExpandableListAdapter(mContext, mExpandableListView);
        mDevices            = mDeviceManager.getDevices();
        previousGroup = -1;
        mDeviceManager.currentContext = this;
        mAdapter.mContext = this;
        mAdapter.setDevices(mDevices);

        mExpandableListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void verifyIfSomeChannelIsSoundingOrRecording() {
        mDeviceManager.getExpandableListAdapter().stopActions();
//        mDeviceManager.getExpandableListAdapter().verifyOverlayMenuVisibility();
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
//                collapseAll();
                startInitialActivity();

                return true;
            case (R.id.action_edit):
//                collapseAll();
                startEditActivity();

                return true;
            case (R.id.action_media):
//                collapseAll();
                startMediaActivity();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void collapseAll(){
        for(int i = 0; i<mDevices.size(); i++) {
            if(mExpandableListView.isGroupExpanded(i)){
                mExpandableListView.collapseGroup(i);
                mAdapter.onGroupCollapsed(i);
            }
        }
        previousGroup = -1;
    }

    private void startInitialActivity() {
        Intent i = new Intent(this, InitialActivity.class);

        startActivity(i);
    }

    private void startMediaActivity() {
        Intent intent = new Intent(mContext, MediaActivity.class);
        startActivity(intent);
    }

    private void startEditActivity() {
        /*Bundle extras = new Bundle();
        extras.putSerializable("devices", mDevices);*/
        Intent intent = new Intent(mContext, DeviceEditListActivity.class);
//        intent.putExtras(extras);
        startActivity(intent);
    }

    public static boolean hasExpandedGroup(){
        return expandedGroups == 0;
    }

    public static void collapseGroup(int groupPosition){
        mExpandableListView.collapseGroup(groupPosition);
        previousGroup = -1;
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }


}
