package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.BuildConfig;
import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.StatePreferences;
import br.inatel.icc.gigasecurity.gigamonitor.ui.CustomTypeDialog;
import io.fabric.sdk.android.Fabric;

public class DeviceListActivity extends ActionBarActivity implements View.OnClickListener {
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
    private TextView mTextViewEdit;
    private ImageView mImageViewAdd, mImageViewGallery;
    private ImageButton mImageViewCloud3Btn;
    private LinearLayout mLinearLayoutHeader;

    // teste
    public DeviceManager mManager;
    public Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        mDeviceManager = DeviceManager.getInstance();
        if(mDeviceManager.mFunUserHandler == -1)
            mDeviceManager.init(this);

        initComponents();
        setListeners();

        //if don't have any device registered, start InitialActivity.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        if(mDevices.size() == 1) {
            startInitialActivity();
            editor.putBoolean("newUser", true);
            editor.putBoolean("cloud2", false);
            editor.commit();
        } else {
            if (prefs.getBoolean("cloud2", true)) {
                editor.putBoolean("newUser", false);
                editor.commit();
                for (Device device : mDevices) {
                    if (!device.alreadyOptimized)
                        device.optimize = true;
                }
            }
        }

        try {
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
//                    parent.collapseGroup(previousGroup);
//                    previousGroup = -1;
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
        } catch (Exception error) {
            error.printStackTrace();
        }

        mDeviceManager.setSharedPreferences(mContext.getSharedPreferences("state", MODE_PRIVATE));
        getSupportActionBar().hide();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mImageViewCloud3Btn.setVisibility(View.GONE);
            getSupportActionBar().hide();
            mLinearLayoutHeader.setVisibility(View.GONE);
            if(previousGroup != -1) {
                mExpandableListView.scrollTo(previousGroup, 0);
            }
        } else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            mLinearLayoutHeader.setVisibility(View.VISIBLE);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            boolean cloud2 = prefs.getBoolean("cloud2", true);
            boolean newUser = prefs.getBoolean("newUser", true);
//            if (cloud2 && !newUser) {
                mImageViewCloud3Btn.setVisibility(View.VISIBLE);
//            }
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

//        try {
//
//            mManager = DeviceManager.getInstance();
//            mDevice = mDeviceManager.getDevices().get(1);
//
//            int[] channelOrder = mDevice.getChannelOrder();
//            Log.e("Device List Order: ", ""+channelOrder[0]+", "+channelOrder[1]+", "+channelOrder[2]+", "+channelOrder[3]);
//
//        } catch (NullPointerException e) {
//
//            Log.e("onResume Bug: ", e.toString());
//
//        }

        if(mDeviceManager.collapse >=0 && previousGroup == mDeviceManager.collapse) {
            if(mExpandableListView.isGroupExpanded(mDeviceManager.collapse))
                mExpandableListView.collapseGroup(mDeviceManager.collapse);
            mDeviceManager.collapse = -1;
            previousGroup = -1;
            mAdapter.notifyDataSetChanged();
        }

        //load last state
        if(!mDeviceManager.loadedState) {
            statePreferences = mDeviceManager.loadState();
            previousGroup = statePreferences.previousGroup;
            if (previousGroup > -1 && mDeviceManager.networkType > -1) {
                mExpandableListView.expandGroup(previousGroup);
            } else if (mDeviceManager.networkType == 1 && mDeviceManager.someDeviceIsRecording()) {
                Toast.makeText(mContext, "Finalize a gravação", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "onResume: group: " + previousGroup + ", channel: " + statePreferences.previousChannel + ", grid: " + statePreferences.previousGrid + ", HD: " + statePreferences.previousHD);
            mDeviceManager.loadedState = true;
        }

        getSupportActionBar().hide();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

//        if (prefs.getBoolean("cloud2", true) && !prefs.getBoolean("newUser", true)) {
            mImageViewCloud3Btn.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        statePreferences = new StatePreferences();
        statePreferences.previousGroup = previousGroup;

        try {
            if(previousGroup > -1) {
                ChannelsManager channelsManager = mDeviceManager.getDeviceChannelsManagers().get(previousGroup);
                statePreferences.previousChannel = channelsManager.lastFirstVisibleItem;
                statePreferences.previousGrid = channelsManager.numQuad;
                statePreferences.previousLastGrid = channelsManager.lastNumQuad;
                statePreferences.previousLastVisibleChannel = channelsManager.lastFirstItemBeforeSelectChannel;
            }
            mDeviceManager.saveState(statePreferences);
        } catch (Exception error) {
            error.printStackTrace();
        }
        mDeviceManager.saveState(statePreferences);
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
        mImageViewGallery = (ImageView) findViewById(R.id.image_view_gallery);
        mTextViewEdit     = (TextView) findViewById(R.id.text_view_edit);
        mImageViewAdd     = (ImageView) findViewById(R.id.image_view_add);
        mImageViewCloud3Btn = (ImageButton) findViewById(R.id.image_button_cloud3);
        mLinearLayoutHeader = (LinearLayout) findViewById(R.id.layout_header);
    }

    private void setListeners() {
        mImageViewGallery.setOnClickListener(this);
        mTextViewEdit.setOnClickListener(this);
        mImageViewAdd.setOnClickListener(this);
        mImageViewCloud3Btn.setOnClickListener(this);
    }

    private void verifyIfSomeChannelIsSoundingOrRecording() {
        mDeviceManager.getExpandableListAdapter().stopActions();
        mDeviceManager.setDevicesLogout(false);
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
        Intent intent = new Intent(mContext, DeviceEditListActivity.class);
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

    private void showCustomDialog () {
        new CustomTypeDialog(mContext, new CustomTypeDialog.OnDialogClickListener() {
            @Override
            public void onDialogImageRunClick() {
//                mImageViewCloud3Btn.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_edit:
                startEditActivity();
                break;
            case R.id.image_view_add:
                startInitialActivity();
                break;
            case R.id.image_view_gallery:
                startMediaActivity();
                break;
            case (R.id.image_button_cloud3):
                showCustomDialog();
                break;
        }
    }
}