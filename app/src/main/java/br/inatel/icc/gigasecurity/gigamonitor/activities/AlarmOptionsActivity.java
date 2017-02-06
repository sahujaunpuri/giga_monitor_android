//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
///**
// ** Created by palomacosta on 24/02/2015.
// */
//public class AlarmOptionsActivity extends ActionBarActivity {
//
//    private Device mDevice;
//
//    private Button mAlarmIn, mAlarmOut;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm_options);
//
//        mAlarmIn = (Button) findViewById(R.id.button_alarm_in);
//        mAlarmOut = (Button) findViewById(R.id.button_alarm_out);
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//        }
//    }
//
//    public void alarmIn(View v) {
//
//        if(v == mAlarmIn) {
//
//            Intent intent = new Intent(this, AlarmInActivity.class);
//            Bundle extras = new Bundle();
//            extras.putSerializable("device", mDevice);
//            intent.putExtras(extras);
//            startActivity(intent);
//        }
//    }
//
//    public void alarmOut(View v) {
//
//        if (v == mAlarmOut) {
//
//            Intent intent = new Intent(this, AlarmOutActivity.class);
//            Bundle extras = new Bundle();
//            extras.putSerializable("device", mDevice);
//            intent.putExtras(extras);
//            startActivity(intent);
//        }
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
