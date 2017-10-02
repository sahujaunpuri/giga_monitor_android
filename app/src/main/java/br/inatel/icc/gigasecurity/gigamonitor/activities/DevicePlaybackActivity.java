package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lib.SDKCONST;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DevicePlaybacksAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.ui.DatePickerFragment;

import static br.inatel.icc.gigasecurity.gigamonitor.R.string.label_begin_hour_playback;
import static br.inatel.icc.gigasecurity.gigamonitor.R.string.label_end_hour_playback;

public class DevicePlaybackActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener{

    private static final String ALARMTYPE = "Alarme";
    private static final String MOVIMENTTYPE = "Movimento";
    private static final String MANUALTYPE = "Manual";
    private static final String REGULARTYPE = "Contínuo";
    private static final String TAG = "Playback";

    private DeviceManager mManager;
    private ArrayList<FileData> allPlaybacks = new ArrayList<>();
    private ArrayList<FileData> playbacksShowed = new ArrayList<>();
    private DevicePlaybacksAdapter arrayAdapter = null;
    private Calendar mInitialTime;
    private Calendar endDate;
    private Device mDevice;
    private ListView mListView;
    private TextView tvDate;
    private TextView tvBeginHour;
    private TextView tvEndHour;
    private ImageView datePicker;
    private ImageView initialTimePicker;
    private ImageView endTimePicker;
    private Activity mActivity;
    private NumberPicker nbChannel;
    private LinearLayout layoutFindPlayback, layoutListPlayback, layout_spinner;
    private Spinner spinner;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_playback);

        mListView          = (ListView) findViewById(R.id.list_view_playbacks);
        tvDate             = (TextView) findViewById(R.id.text_view_playback_date);
        tvBeginHour        = (TextView) findViewById(R.id.textview_begin_hour_playback);
        tvEndHour          = (TextView) findViewById(R.id.textview_end_hour_playback);
        initialTimePicker  = (ImageView) findViewById(R.id.initial_time_button);
        endTimePicker      = (ImageView) findViewById(R.id.end_time_button);
        datePicker         = (ImageView) findViewById(R.id.calendar);
        nbChannel          = (NumberPicker) findViewById(R.id.nb_playback);
        layoutFindPlayback = (LinearLayout) findViewById(R.id.linear_layout_find_playback);
        layoutListPlayback = (LinearLayout) findViewById(R.id.linear_layout_list_playback);
        layout_spinner     = (LinearLayout) findViewById(R.id.linear_layout_spinner);
        spinner            = (Spinner) findViewById(R.id.playback_filter);

        mListView.setOnItemClickListener(this);

        mActivity = this;
        mManager = DeviceManager.getInstance();
        mDevice = mManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));

        mInitialTime = new GregorianCalendar();
        mInitialTime.set(Calendar.HOUR_OF_DAY, 0);
        mInitialTime.set(Calendar.MINUTE, 0);
        mInitialTime.set(Calendar.SECOND, 0);
        mInitialTime.set(Calendar.MILLISECOND, 0);
        String appendMinute = mInitialTime.get(Calendar.MINUTE) < 10 ? ":0" : ":";
        String appendHour = mInitialTime.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "";
        tvBeginHour.setText(appendHour + mInitialTime.get(Calendar.HOUR_OF_DAY) + appendMinute + mInitialTime.get(Calendar.MINUTE));

        endDate = new GregorianCalendar();
        appendMinute = endDate.get(Calendar.MINUTE) < 10 ? ":0" : ":";
        appendHour = endDate.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "";
        tvEndHour.setText(appendHour + endDate.get(Calendar.HOUR_OF_DAY) + appendMinute + endDate.get(Calendar.MINUTE));

        initialTimePicker.setBackgroundColor(0);
        endTimePicker.setBackgroundColor(0);

        View.OnClickListener datePickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        };
        View.OnClickListener initialClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(true);
            }
        };
        View.OnClickListener endClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(false);
            }
        };
        tvDate.setOnClickListener(datePickListener);
        datePicker.setOnClickListener(datePickListener);
        tvBeginHour.setOnClickListener(initialClickListener);
        initialTimePicker.setOnClickListener(initialClickListener);
        tvEndHour.setOnClickListener(endClickListener);
        endTimePicker.setOnClickListener(endClickListener);

        nbChannel.setMinValue(1);

        int maxValueNumberPick = mDevice.getChannelNumber();

        if(maxValueNumberPick > 32) {
            maxValueNumberPick = 32;
        }

        nbChannel.setMaxValue(maxValueNumberPick);

        setDateButtonText(mInitialTime.getTime());


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.playback_types, R.layout.custom_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedType = spinner.getSelectedItem().toString();
                int type = SDKCONST.FileType.SDK_RECORD_ALL;
                if (selectedType.equals(ALARMTYPE)) {
                    type = SDKCONST.FileType.SDK_RECORD_ALARM;
                } else if (selectedType.equals(MOVIMENTTYPE)) {
                    type = SDKCONST.FileType.SDK_RECORD_DETECT;
                } else if (selectedType.equals(MANUALTYPE)) {
                    type = SDKCONST.FileType.SDK_RECORD_MANUAL;
                } else if (selectedType.equals(REGULARTYPE)) {
                    type = SDKCONST.FileType.SDK_RECORD_REGULAR;
                }
                String stringType = String.valueOf(type);
                arrayAdapter.getFilter().filter(stringType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("NothingSelected", "Nada Selecionado");
            }
        });

    }

    public void setDateButtonText(Date date) {
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
    }

    private void showDatePicker() {
        DatePickerFragment fragment = new DatePickerFragment();

        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mInitialTime.set(Calendar.YEAR, year);
                mInitialTime.set(Calendar.MONTH, monthOfYear);
                mInitialTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, monthOfYear);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                setDateButtonText(mInitialTime.getTime());
            }
        });

        Bundle extras = new Bundle();
        extras.putSerializable(DatePickerFragment.KEY_SERIALIZABLE_CALENDAR, mInitialTime);

        fragment.setArguments(extras);
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePicker(final Boolean initial) {
        int hour, minute;
        final String title;
        if(initial){
            hour = mInitialTime.get(Calendar.HOUR_OF_DAY);
            minute = mInitialTime.get(Calendar.MINUTE);
            title = getString(label_begin_hour_playback);
        }else{
            hour = endDate.get(Calendar.HOUR_OF_DAY);
            minute = endDate.get(Calendar.MINUTE);
            title = getString(label_end_hour_playback);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String appendMinute, appendHour;
                appendMinute = minute < 10 ? ":0" : ":";
                appendHour = hour < 10 ? "0" : "";
                if (initial) {
                    tvBeginHour.setText(appendHour + hour + appendMinute + minute);
                    mInitialTime.set(Calendar.HOUR_OF_DAY, hour);
                    mInitialTime.set(Calendar.MINUTE, minute);
                } else {
                    tvEndHour.setText(appendHour + hour + appendMinute + minute);
                    endDate.set(Calendar.HOUR_OF_DAY, hour);
                    endDate.set(Calendar.MINUTE, minute);
                }
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    private ArrayList<FileData> infoToArray(H264_DVR_FILE_DATA files[]){
        ArrayList<FileData> playbacks = new ArrayList<FileData>();
        for (int i=files.length - 1; i>=0; i--) {
            H264_DVR_FILE_DATA file = files[i];
            FileData funFileData = new FileData(file, null);
            playbacks.add(funFileData);
        }
        return playbacks;
    }

//    private void filterPlaybacks(final String playType) {
//        if (arrayAdapter != null) {
//            arrayAdapter.notifyDataSetChanged();
//            arrayAdapter.filterPlaybacks(playType);
//        }
//    }

    private void findPlaybacks(final Integer type) {
        String title = getResources().getString(R.string.label_searching_files);
        String msg = getResources().getString(R.string.label_please_wait);

        final ProgressDialog progressDialog = ProgressDialog.show(mActivity, title,
                msg);

        new Thread(new Runnable() {

            @Override
            public void run() {
                H264_DVR_FINDINFO info = new H264_DVR_FINDINFO();
                info.st_0_nChannelN0 = nbChannel.getValue() - 1;
                info.st_1_nFileType = type;
                info.st_2_startTime.st_0_dwYear = mInitialTime.get(Calendar.YEAR);
                info.st_2_startTime.st_1_dwMonth = mInitialTime.get(Calendar.MONTH) + 1;
                info.st_2_startTime.st_2_dwDay = mInitialTime.get(Calendar.DAY_OF_MONTH);
                info.st_2_startTime.st_3_dwHour = mInitialTime.get(Calendar.HOUR_OF_DAY);
                info.st_2_startTime.st_4_dwMinute = mInitialTime.get(Calendar.MINUTE);
                info.st_2_startTime.st_5_dwSecond = 0;
                info.st_3_endTime.st_0_dwYear = endDate.get(Calendar.YEAR);
                info.st_3_endTime.st_1_dwMonth = endDate.get(Calendar.MONTH) + 1;
                info.st_3_endTime.st_2_dwDay = endDate.get(Calendar.DAY_OF_MONTH);
                info.st_3_endTime.st_3_dwHour = endDate.get(Calendar.HOUR_OF_DAY);
                info.st_3_endTime.st_4_dwMinute = endDate.get(Calendar.MINUTE);
                info.st_3_endTime.st_5_dwSecond = 59;
                info.st_6_StreamType = 2;

                mManager.findPlaybackList(mDevice, info, new PlaybackSearchListener() {
                    @Override
                    public void onFindList(H264_DVR_FILE_DATA files[]) {
                        allPlaybacks = infoToArray(files);
                        playbacksShowed = allPlaybacks;
                        layoutFindPlayback.setVisibility(View.GONE);
                        menuItem.setVisible(false);
                        layout_spinner.setVisibility(View.VISIBLE);
                        layoutListPlayback.setVisibility(View.VISIBLE);

                        arrayAdapter = new DevicePlaybacksAdapter(mActivity, playbacksShowed);
                        mListView.setAdapter(arrayAdapter);

                        progressDialog.dismiss();
                    }
                    @Override
                    public void onEmptyListFound() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = getResources().getString(R.string.label_no_records);
                                layoutListPlayback.setVisibility(View.GONE);
                                Toast.makeText(DevicePlaybackActivity.this, msg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle extras = new Bundle();
        extras.putSerializable("device", mDevice.getId());

        extras.putSerializable("fileData", (FileData) parent.getItemAtPosition(position));

        Intent intent = new Intent(this, DevicePlaybackVideoActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuItem = menu.findItem(R.id.menu_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(layoutListPlayback.getVisibility() == View.VISIBLE) {
                    layoutFindPlayback.setVisibility(View.VISIBLE);
                    menuItem.setVisible(true);
                    playbacksShowed.clear();
                    allPlaybacks.clear();
                    layout_spinner.setVisibility(View.GONE);
                    layoutListPlayback.setVisibility(View.GONE);
                } else {
                    finish();
                }

                return true;

            case R.id.menu_search:
                findPlaybacks(SDKCONST.FileType.SDK_RECORD_ALL);
                return true;

            default:
                return false;
        }
    }
}
