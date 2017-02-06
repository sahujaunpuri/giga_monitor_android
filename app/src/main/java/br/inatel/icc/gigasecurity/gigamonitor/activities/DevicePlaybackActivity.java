//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.NumberPicker;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.DevicePlaybacksAdapter;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;
//import br.inatel.icc.gigasecurity.gigamonitor.model.SDKFileType;
//import br.inatel.icc.gigasecurity.gigamonitor.ui.DatePickerFragment;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//
//public class DevicePlaybackActivity extends ActionBarActivity
//        implements AdapterView.OnItemClickListener{
//
//    private DeviceManager mManager;
//    private Calendar mSystemTime;
//    private Device mDevice;
//    private ListView mListView;
//    private TextView tvDate;
//    private Activity mActivity;
//    private NumberPicker nbChannel;
//    private LinearLayout layoutFindPlayback, layoutListPlayback;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device_playback);
//
//        mListView          = (ListView) findViewById(R.id.list_view_playbacks);
//        tvDate             = (TextView) findViewById(R.id.text_view_playback_date);
//        nbChannel          = (NumberPicker) findViewById(R.id.nb_playback);
//        layoutFindPlayback = (LinearLayout) findViewById(R.id.linear_layout_find_playback);
//        layoutListPlayback = (LinearLayout) findViewById(R.id.linear_layout_list_playback);
//        mListView.setOnItemClickListener(this);
//
//        mActivity = this;
//        mDevice = (Device) getIntent().getExtras().getSerializable("device");
//        mManager = DeviceManager.getInstance();
//        mSystemTime = Calendar.getInstance();
//
//        tvDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePicker();
//            }
//        });
//
//        nbChannel.setMinValue(1);
//
//        int maxValueNumberPick = mDevice.getChannelNumber();
//
//        if(maxValueNumberPick > 32) {
//            maxValueNumberPick = 32;
//        }
//
//        nbChannel.setMaxValue(maxValueNumberPick);
//
//        setDateButtonText(mSystemTime.getTime());
//    }
//
//    public void setDateButtonText(Date date) {
//        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
//    }
//
//
//
//    private void showDatePicker() {
//        DatePickerFragment fragment = new DatePickerFragment();
//
//        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                mSystemTime.set(Calendar.YEAR, year);
//                mSystemTime.set(Calendar.MONTH, monthOfYear);
//                mSystemTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                setDateButtonText(mSystemTime.getTime());
//            }
//        });
//
//        Bundle extras = new Bundle();
//        extras.putSerializable(DatePickerFragment.KEY_SERIALIZABLE_CALENDAR, mSystemTime);
//
//        fragment.setArguments(extras);
//        fragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    private void findPlaybacks() {
//        String title = getResources().getString(R.string.label_searching_files, true);
//        String msg = getResources().getString(R.string.label_please_wait, true);
//
//        final ProgressDialog progressDialog = ProgressDialog.show(mActivity, title,
//                msg);
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                FindInfo findInfo = new FindInfo();
//                findInfo.nChannelN0 = nbChannel.getValue() - 1;
//                findInfo.nFileType = SDKFileType.SDK_RECORD_ALL.getValue();
//
//                findInfo.startTime.year = mSystemTime.get(Calendar.YEAR);
//                findInfo.startTime.month = mSystemTime.get(Calendar.MONTH) + 1;
//                findInfo.startTime.day = mSystemTime.get(Calendar.DAY_OF_MONTH);
//                findInfo.startTime.hour = 0;
//                findInfo.endTime.year = mSystemTime.get(Calendar.YEAR);
//                findInfo.endTime.month = mSystemTime.get(Calendar.MONTH) + 1;
//                findInfo.endTime.day = mSystemTime.get(Calendar.DAY_OF_MONTH);
//                findInfo.endTime.hour = 24;
//
//                final ArrayList<FileDataGiga> playbacks = mManager.findPlaybacks(mDevice.getLoginID(), findInfo);
//
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(playbacks.size() == 0) {
//                            String msg = getResources().getString(R.string.label_no_records, true);
//                            Toast.makeText(DevicePlaybackActivity.this, msg, Toast.LENGTH_LONG).show();
//                        } else {
//                            layoutFindPlayback.setVisibility(View.GONE);
//                            layoutListPlayback.setVisibility(View.VISIBLE);
//
//                            mListView.setAdapter(new DevicePlaybacksAdapter(mActivity, playbacks));
//                        }
//
//                        progressDialog.dismiss();
//                    }
//                });
//            }
//        }).start();
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Bundle extras = new Bundle();
//        extras.putSerializable("device", mDevice);
//
//        extras.putSerializable("fileData", (FileDataGiga) parent.getItemAtPosition(position));
//
//        Intent intent = new Intent(this, DevicePlaybackVideoActivity.class);
//        intent.putExtras(extras);
//        startActivity(intent);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_playback, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case android.R.id.home:
//                if(layoutListPlayback.getVisibility() == View.VISIBLE) {
//                    layoutFindPlayback.setVisibility(View.VISIBLE);
//                    layoutListPlayback.setVisibility(View.GONE);
//                } else {
//                    finish();
//                }
//
//                return true;
//
//            case R.id.menu_search:
//                findPlaybacks();
//                return true;
//
//            default:
//                return false;
//        }
//    }
//}
