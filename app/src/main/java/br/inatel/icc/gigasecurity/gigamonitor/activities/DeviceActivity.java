//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
//public class DeviceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
//
//    private Device mDevice;
//
//    private enum Item {
//        MONITOR,
//        INTERCOM,
//        PLAYBACK,
//        PTZ,
//        EVENTS,
//        ALARM,
//        SETTINGS
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device);
//
//        Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
//        mDevice = (Device) extras.getSerializable("device");
//
//        if (mDevice == null) {
//            Toast.makeText(this, "Device not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//
//        ListView listView = (ListView) findViewById(R.id.list_view_device_functions);
//
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.device_functions,
//                android.R.layout.simple_list_item_1);
//
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//        startActivityForItem(Item.values()[position]);
//    }
//
//    private void startActivityForItem(Item item) {
//        Intent intent = null;
//
//        switch (item) {
//
//            case MONITOR:
//                intent = new Intent(this, DeviceMonitorActivity.class);
//                break;
//            case INTERCOM:
//                intent = new Intent(this, IntercomTestActivity.class);
//                break;
//            case PLAYBACK:
//                intent = new Intent(this, DevicePlaybackActivity.class);
//                break;
//            case PTZ:
//                intent = new Intent(this, PTZActivity.class);
//                break;
//            case EVENTS:
//                intent = new Intent(this, DeviceEventsActivity.class);
//                break;
//            case ALARM:
//                intent = new Intent(this, AlarmTestActivity.class);
//                break;
//            case SETTINGS:
//                intent = new Intent(this, ConfigMenuActivity.class);
//                break;
//            default:
//                break;
//        }
//
//            if(intent != null) {
//                Bundle extras = new Bundle();
//                extras.putSerializable("device", mDevice);
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//    }
//
//    /*@Override
//    protected void onDestroy() {
//        try {
//            DeviceManager.getInstance().logout(mDevice);
//        } catch (Exception e) {
//            Log.w("DeviceActivity", "Couldn't logout from device", e);
//        }
//        super.onDestroy();
//    }*/
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