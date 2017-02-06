//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import com.xm.ChnInfo;
//import com.xm.MyConfig;
//import com.xm.NetSdk.OnAlarmListener;
//import com.xm.SDK_AllAlarmIn;
//import com.xm.SDK_AllAlarmOut;
//import com.xm.video.MySurfaceView;
//
//public class AlarmTestActivity extends ActionBarActivity {
//
//    // Debug tag
//    private final String TAG = AlarmTestActivity.class.getSimpleName();
//
//    private Device mDevice;
//
//    private DeviceManager mManager;
//
//    private MySurfaceView mSurfaceView;
//
//    private Button mAlarmInButton, mAlarmOutButton;
//
//    private SDK_AllAlarmIn mAllAlarmInConfig;
//    private SDK_AllAlarmOut mAllAlarmOutConfig;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm_test);
//        mSurfaceView = (MySurfaceView) findViewById(R.id.surface_view_1);
//        mAlarmInButton = (Button) findViewById(R.id.button_alarm_in_switch);
//        mAlarmOutButton = (Button) findViewById(R.id.button_alarm_out_switch);
//
//        mManager = DeviceManager.getInstance();
//        mManager.initAlarm(mAlarmListener);
//
//        mSurfaceView.init(this, 0);
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//        }
//
//        if (!mManager.setupDeviceAlarm(mDevice.getLoginID())) {
//            setText("Error setting alarm up");
//        } else {
//            onPlayLive(null);
//        }
//
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                mAllAlarmInConfig = mManager.getAlarmInConfig(mDevice);
//                mAllAlarmOutConfig = mManager.getAlarmOutConfig(mDevice);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                mAlarmInButton.setText("Alarm in on? " + mAllAlarmInConfig.alarmInParam[0].bEnable);
//                mAlarmOutButton.setText("Alarm out type: " + mAllAlarmOutConfig.alarmOutParam[0].AlarmOutType
//                        + " status: " + mAllAlarmOutConfig.alarmOutParam[0].AlarmOutStatus);
//            }
//        }.execute();
//    }
//
//
//    public void onPlayLive(View sender) {
//        ChnInfo chnInfo = new ChnInfo();
//        chnInfo.nStream = MyConfig.StreamType.Main;
//        chnInfo.ChannelNo = 0;
//
//        mManager.startDeviceVideo(mDevice.getLoginID(), mSurfaceView, 0, chnInfo);
//        setStartButtonEnabled(false);
//        setStopButtonEnabled(true);
//    }
//
//    public void stopVideo(View sender) {
//        mManager.stopDeviceVideo(mDevice.getLoginID(), mSurfaceView);
//        setStartButtonEnabled(true);
//        setStopButtonEnabled(false);
//    }
//
//    public void setStartButtonEnabled(boolean enabled) {
//        findViewById(R.id.button_start_video).setEnabled(enabled);
//    }
//
//    public void setStopButtonEnabled(boolean enabled) {
//        findViewById(R.id.button_stop_video).setEnabled(enabled);
//    }
//
//    private void setText(String msg) {
//        ((TextView)findViewById(R.id.text1)).setText(msg);
//    }
//
//    private final OnAlarmListener mAlarmListener = new OnAlarmListener() {
//        @Override
//        public void onAlarm(long loginID, int channel, int event, int status, int[] dateTimeArray) {
//            final StringBuilder sb = new StringBuilder();
//
//            sb.append(String.format("loginID: %d Channel: %d EventType: %d Status: %d", loginID,
//                    channel, event, status));
//
//            Object[] arrayOfObject = new Object[6];
//            arrayOfObject[0] = dateTimeArray[0];
//            arrayOfObject[1] = dateTimeArray[1];
//            arrayOfObject[2] = dateTimeArray[2];
//            arrayOfObject[3] = dateTimeArray[4];
//            arrayOfObject[4] = dateTimeArray[5];
//            arrayOfObject[5] = dateTimeArray[6];
//            sb.append(String.format(" %04d-%02d-%02d %02d:%02d:%02d", arrayOfObject));
//
//            Log.d(TAG, "onAlarm " + sb.toString());
//
//            AlarmTestActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    setText(sb.toString());
//                }
//            });
//        }
//    };
//
//    public void alarmInClick(View v) {
//        mAllAlarmInConfig.alarmInParam[0].bEnable = !mAllAlarmInConfig.alarmInParam[0].bEnable;
//
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                return mManager.setAllAlarmInConfig(mDevice, mAllAlarmInConfig);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean bool) {
//                super.onPostExecute(bool);
//                if(bool) mAlarmInButton.setText("Alarm in on? " + mAllAlarmInConfig.alarmInParam[0].bEnable);
//            }
//        }.execute();
//    }
//
//    public void alarmOutClick(View v) {
//        int type = mAllAlarmOutConfig.alarmOutParam[0].AlarmOutType;
//        mAllAlarmOutConfig.alarmOutParam[0].AlarmOutType = type == 1 ? 0 : 1;
//
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                return mManager.setAllAlarmOutConfig(mDevice, mAllAlarmOutConfig);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean bool) {
//                super.onPostExecute(bool);
//                if(bool) {
//                    mAlarmOutButton.setText("Alarm out type: " + mAllAlarmOutConfig.alarmOutParam[0].AlarmOutType
//                            + " status: " + mAllAlarmOutConfig.alarmOutParam[0].AlarmOutStatus);
//                }
//            }
//        }.execute();
//    }
//
//    @Override
//    protected void onPause() {
//        mManager.closeDeviceAlarm(mDevice.getLoginID());
//        stopVideo(null);
//        super.onPause();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable("device", mDevice);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.simple_menu, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch (id) {
//            case android.R.id.home:
//                finish();
//                return true;
//            default:
//                return false;
//        }
//    }
//}
