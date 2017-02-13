//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.ListView;
//import android.widget.Spinner;
//
//import com.xm.javaclass.SDK_LogList;
//import com.xm.javaclass.SDK_LogSearchCondition;
//
//import java.text.DateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceEventsAdapter;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.ui.DatePickerFragment;
//
//public class DeviceEventsActivity extends ActionBarActivity    {
//
//    private DeviceManager mManager;
//
//    private Calendar mSystemTime;
//    private Calendar mSystemTimeEnd;
//
//    private Device mDevice;
//
//    private Button mDateStartButton;
//    private Button mDateEndButton;
//
//    private Spinner mSpinnerEventType;
//    private ListView mListView;
//
//    private Activity mActivity;
//
//    private String[] eventTypeNames;
//
//    private int[] eventTypeValues;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device_events);
//
//        mDateStartButton = (Button) findViewById(R.id.button_date_picker_start);
//        mDateEndButton = (Button) findViewById(R.id.button_date_picker_end);
//        mSpinnerEventType = (Spinner) findViewById(R.id.spinner_event_type);
//
//
//        eventTypeNames = getResources().getStringArray(R.array.event_code_types_names);
//        eventTypeValues = getResources().getIntArray(R.array.event_code_types_value);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, eventTypeNames);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinnerEventType.setAdapter(adapter);
//
//        mListView = (ListView) findViewById(R.id.list_view_events);
//
//        mActivity = this;
//        mDevice = (Device) getIntent().getExtras().getSerializable("device");
//        mManager = DeviceManager.getInstance();
//        mSystemTime = Calendar.getInstance();
//        mSystemTimeEnd = Calendar.getInstance();
//
//        setDateButtonText(mDateStartButton, mSystemTime.getTime());
//        setDateButtonText(mDateEndButton, mSystemTimeEnd.getTime());
//    }
//
//    public void setDateButtonText(final Button button, Date date) {
//        final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
//        button.setText(dateFormat.format(date));
//    }
//
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button_date_picker_start:
//                showDatePicker(mSystemTime, mDateStartButton);
//                break;
//            case R.id.button_date_picker_end:
//                showDatePicker(mSystemTimeEnd, mDateEndButton);
//                break;
//            case R.id.button_event_search:
//                findEvents();
//                break;
//        }
//
//    }
//
//    private void showDatePicker(final Calendar calendar, final Button button ) {
//        DatePickerFragment fragment = new DatePickerFragment();
//        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, monthOfYear);
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                setDateButtonText(button, calendar.getTime());
//            }
//        });
//        Bundle extras = new Bundle();
//        extras.putSerializable(DatePickerFragment.KEY_SERIALIZABLE_CALENDAR, calendar);
//        fragment.setArguments(extras);
//        fragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    private void findEvents() {
//        final ProgressDialog progressDialog = ProgressDialog.show(mActivity, "Searching Files",
//                "Please wait...", true, false);
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                SDK_LogSearchCondition searchCondition = new SDK_LogSearchCondition();
//
//                searchCondition.st_0_nType = eventTypeValues[mSpinnerEventType.getSelectedItemPosition()];
//                searchCondition.st_1_iLogPosition = 1;
//
//                searchCondition.st_2_stBeginTime.st_0_year = mSystemTime.get(Calendar.YEAR);
//                searchCondition.st_2_stBeginTime.st_1_month = mSystemTime.get(Calendar.MONTH) + 1;
//                searchCondition.st_2_stBeginTime.st_2_day = mSystemTime.get(Calendar.DAY_OF_MONTH);
//                searchCondition.st_2_stBeginTime.st_4_hour   = 00;//mSystemTime.get(Calendar.HOUR);
//                searchCondition.st_2_stBeginTime.st_5_minute = 00;//mSystemTime.get(Calendar.MINUTE);
//                searchCondition.st_2_stBeginTime.st_6_second = 00;//mSystemTime.get(Calendar.SECOND) ;
//
//                searchCondition.st_3_stEndTime.st_0_year = mSystemTimeEnd.get(Calendar.YEAR);
//                searchCondition.st_3_stEndTime.st_1_month = mSystemTimeEnd.get(Calendar.MONTH) + 1;
//                searchCondition.st_3_stEndTime.st_2_day = mSystemTimeEnd.get(Calendar.DAY_OF_MONTH);
//                searchCondition.st_3_stEndTime.st_4_hour   = 23;//mSystemTimeEnd.get(Calendar.HOUR);
//                searchCondition.st_3_stEndTime.st_5_minute = 59;//mSystemTimeEnd.get(Calendar.MINUTE);
//                searchCondition.st_3_stEndTime.st_6_second = 59;// mSystemTimeEnd.get(Calendar.SECOND) ;
//
//
//
//                final SDK_LogList events = mManager.getEventList(mDevice, searchCondition);
//
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mListView.setAdapter(new DeviceEventsAdapter(mActivity, events));
//                        progressDialog.dismiss();
//                    }
//                });
//            }
//        }).start();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.simple_menu, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
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
//
//}
