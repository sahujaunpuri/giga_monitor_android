package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewManager;

public class DeviceListActivity extends ActionBarActivity {

    public static Context mContext;
    public static ExpandableListView mExpandableListView;
    public static DeviceExpandableListAdapter mAdapter;
    public static DeviceManager mDeviceManager;
    public static ArrayList<Device> mDevices;
    public static int previousGroup = -1;
    public static int expandedGroups = 0;
    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        /*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            if(previousGroup != -1) {
                mAdapter.onChangeOrientation(previousGroup);
                mExpandableListView.scrollTo(previousGroup, 0);
            }
        } else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }*/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        initComponents();

//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, DeviceListActivity.class));



//        if(previousGroup != -1) {
//            mAdapter.onChangeOrientation(previousGroup);
//        }

        //if don't have any device registered, start InitialActivity.
        if(mDevices.size() == 0) {
            startInitialActivity();
//            finish();
        }

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (previousGroup == -1) {
                    previousGroup = groupPosition;
                    return false;
                }else if(previousGroup == groupPosition){
                    previousGroup = -1;
                    return false;
                }else {
                    parent.collapseGroup(previousGroup);
                    parent.expandGroup(groupPosition);
                    previousGroup = groupPosition;
                    return true;
                }
            }
        });
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
    protected void onStop(){
        super.onStop();
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
            mExpandableListView.collapseGroup(mDeviceManager.collapse);
            mAdapter.onGroupCollapsed(mDeviceManager.collapse);
            mDeviceManager.collapse = -1;
        }
        mAdapter.notifyDataSetChanged();
//        mExpandableListView.invalidate();
//        if (previousGroup != -1) mAdapter.playChannels(previousGroup);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        collapseAll();
//        if (previousGroup != -1) mAdapter.stopChannels(previousGroup);
    }

    private void initComponents() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view2);
        mDeviceManager      = DeviceManager.getInstance();
        mContext            = DeviceListActivity.this;
        mAdapter            = mDeviceManager.getExpandableListAdapter(mContext);
        mDevices            = mDeviceManager.getDevices();
        mDeviceManager.currentContext = this;
        mAdapter.mContext = this;
        mAdapter.setDevices(mDevices);
        mAdapter.notifyDataSetChanged();


//        loadDevices();

//        mAdapter = new DeviceExpandableListAdapter(mContext, mDevices);

        mExpandableListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
//        if(mDevices == null) {
//            mDevices = mDeviceManager.getDevices();
//
//        }

    }

    /*public static void loadDevices() {
        if(mDevices == null) {
            mDevices = mDeviceManager.getDevices();

            surfaceViewManagers.clear();

            for(int i=0; i < mDevices.size(); i++) {
                SurfaceViewManager surfaceViewManager = new SurfaceViewManager(mDevices.get(i));

                surfaceViewManagers.add(surfaceViewManager);
            }
        }
    }*/

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
        Bundle extras = new Bundle();
        extras.putSerializable("devices", mDevices);

        Intent intent = new Intent(mContext, DeviceEditListActivity.class);
        intent.putExtras(extras);

        startActivity(intent);
    }

    public static boolean hasExpandedGroup(){
        return expandedGroups == 0;
    }

    public static void collapseGroup(int groupPosition){
        mExpandableListView.collapseGroup(groupPosition);
        previousGroup = -1;
    }


}
