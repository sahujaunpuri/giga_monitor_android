package br.inatel.icc.gigasecurity.gigamonitor.config.general;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.DatePickerFragment;
import br.inatel.icc.gigasecurity.gigamonitor.ui.MyTimePickerDialog;
import br.inatel.icc.gigasecurity.gigamonitor.ui.TimePicker;
import br.inatel.icc.gigasecurity.gigamonitor.ui.TimePickerFragment;

public class GeneralConfigActivity extends ActionBarActivity {

    // Debug tag
    private static String TAG = GeneralConfigActivity.class.getSimpleName();

    private Device mDevice;

    private Button mTimeButton, mDateButton;

    private String mDateFormat;

    private Calendar mSystemTime;

    private char mDateSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_general_config);
        findViews();
        initData(savedInstanceState == null ? getIntent().getExtras() : savedInstanceState);
        initViews();
    }

    private void findViews() {
        mTimeButton = (Button) findViewById(R.id.button_general_config_time_picker);
        mDateButton = (Button) findViewById(R.id.button_general_config_date_picker);
    }

    private void initData(Bundle extras) {
        mDevice = (Device) extras.getSerializable("device");
        // TODO load from device using SDK_CONFIG_NORMAL (missing)
        mDateSeparator = '/';
        mDateFormat = "MM dd yyyy";
    }

    private void initViews() {
        mSystemTime = mDevice.getSystemTime();
        mTimeButton.setText(getTimeFormatter().format(mSystemTime.getTime().getTime()));
        mDateButton.setText(getDateFormatter().format(mSystemTime.getTime().getTime()));
    }

    private String getDatePattern() {
        return mDateFormat.replace(' ', mDateSeparator);
    }

    private SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat(getDatePattern());
    }

    private SimpleDateFormat getTimeFormatter() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    private void setDeviceTime() {
        // TODO set device time
    }

    // callback from mTimeButton and mDateButton declared in xml
    public void showTimePickerDialog(View v) {
        switch (v.getId()) {
            case R.id.button_general_config_time_picker:
                TimePickerFragment dialog = new TimePickerFragment();
                dialog.setOnTimeSetListener(new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                        mSystemTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mSystemTime.set(Calendar.MINUTE, minute);
                        mSystemTime.set(Calendar.SECOND, seconds);

                        mTimeButton.setText(getTimeFormatter().format(mSystemTime.getTime()));

                        setDeviceTime();
                    }
                });
                Bundle args = new Bundle();
                args.putSerializable(TimePickerFragment.KEY_SERIALIZABLE_CALENDAR, mSystemTime);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.button_general_config_date_picker:
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mSystemTime.set(Calendar.YEAR, year);
                        mSystemTime.set(Calendar.MONTH, monthOfYear);
                        mSystemTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mDateButton.setText(getDateFormatter().format(mSystemTime.getTime()));

                        setDeviceTime();
                    }
                });
                Bundle extras = new Bundle();
                extras.putSerializable(DatePickerFragment.KEY_SERIALIZABLE_CALENDAR, mSystemTime);
                fragment.setArguments(extras);
                fragment.show(getSupportFragmentManager(), "datePicker");
                break;
        }
    }
}
