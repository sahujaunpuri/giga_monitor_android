//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
//public class IntercomTestActivity extends ActionBarActivity {
//    // Debug tag
//    private String TAG = IntercomTestActivity.class.getSimpleName();
//
//    private DeviceManager mManager;
//
//    private Device mDevice;
//    private boolean mOnStreaming;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bidirectional_talk_test);
//
//        Button startButton = (Button) findViewById(R.id.button_start_intercom);
//        Button stopButton = (Button) findViewById(R.id.button_stop_intercom);
//
//        // Extract device
//        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
//        mDevice = (Device) extras.getSerializable("device");
//
//        mManager = DeviceManager.getInstance();
//
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startButtonClick();
//            }
//        });
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopButtonClick();
//            }
//        });
//    }
//
//    private void startButtonClick() {
//        if (!mOnStreaming) {
//            startTalk(mDevice);
//        } else {
//            Toast.makeText(this, "Audio Started", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void startTalk(Device d) {
//        String msg;
//
//        if (mOnStreaming = mManager.startDeviceIntercom(this, d)) {
//            msg = "Intercom Started";
//        } else {
//            msg = "An error occurred while starting intercom.";
//        }
//
//        showToastMessage(msg);
//    }
//
//    private void stopButtonClick() {
//        if (mOnStreaming) {
//            stopTalk(mDevice);
//        } else {
//            Toast.makeText(this, "Audio Stopped", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void stopTalk(Device d) {
//        String msg;
//
//        if (mOnStreaming = mManager.stopDeviceIntercom(d)) {
//            msg = "Intercom Stopped";
//        } else {
//            msg = "An error occurred while stopping intercom.";
//        }
//
//        showToastMessage(msg);
//    }
//
//    @Override
//    protected void onPause() {
//        stopTalk(mDevice);
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
//
//    private void showToastMessage(String msg) {
//        Toast.makeText(this, msg,
//                Toast.LENGTH_SHORT).show();
//    }
//}
