package br.inatel.icc.gigasecurity.gigamonitor.config.time;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.MyTimePickerDialog;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by zappts on 09/03/18.
 */

public class TimeConfigActivity extends ActionBarActivity implements View.OnClickListener{
    private DeviceManager mDeviceManager;
    private Device mDevice;
    private TextView mTextViewBack, mTextViewSave, mTextViewDate, mTextViewTime;
    private Calendar calendar;

    private ConfigListener configListener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                calendar.setTime(dateFormat.parse(mDevice.getTimeString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setDateText();
            setTimeText();

            View.OnClickListener datePickListener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    showDatePicker();
                }
            };

            View.OnClickListener timePickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker();
                }
            };

            mTextViewDate.setOnClickListener(datePickListener);
            mTextViewTime.setOnClickListener(timePickListener);
        }

        @Override
        public void onSetConfig() {
            int messageId = R.string.saved;
            Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();

            finish();
        }

        @Override
        public void onError() {
            String error = "Erro, não foi possível salvar a configuração";
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_config);
        getSupportActionBar().hide();
        findViews();

        calendar = Calendar.getInstance();
        mDeviceManager = DeviceManager.getInstance();
        mDevice = mDeviceManager.findDeviceById((int) getIntent().getExtras().getSerializable("device"));
        mDeviceManager.getTimeConfig(mDevice, configListener);

    }

    private void findViews(){
        mTextViewBack = (TextView) findViewById(R.id.text_view_back);
        mTextViewSave = (TextView) findViewById(R.id.text_view_save);
        mTextViewDate = (TextView) findViewById(R.id.text_view_date);
        mTextViewTime = (TextView) findViewById(R.id.text_view_time);
        mTextViewSave.setOnClickListener(this);
        mTextViewBack.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_form, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_config_save:
                save();
                Utils.hideKeyboard(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_back:
                finish();
                break;
            case R.id.text_view_save:
                save();
                Utils.hideKeyboard(this);
                break;
        }
    }

    private void showDatePicker(){
        DatePickerDialog datePicker = new DatePickerDialog(TimeConfigActivity.this, R.style.Base_Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                setDateText();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showTimePicker(){
        MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(br.inatel.icc.gigasecurity.gigamonitor.ui.TimePicker view, int hourOfDay, int minute, int seconds) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, seconds);
                setTimeText();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), true);
        timePickerDialog.show();
    }

    private void setDateText(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        mTextViewDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setTimeText(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mTextViewTime.setText(timeFormat.format(calendar.getTime()));
    }

    private void save(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        mDevice.setTimeString(dateFormat.format(calendar.getTime()));
        mDeviceManager.setTimeConfig(mDevice);
    }

}
