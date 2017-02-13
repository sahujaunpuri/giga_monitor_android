//package br.inatel.icc.gigasecurity.gigamonitor.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CompoundButton;
//import android.widget.RadioButton;
//import android.widget.TextView;
//
//import com.xm.SDK_AllAlarmOut;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
///**
// * Created by filipecampos on 04/04/2016.
// */
//public class DeviceAlarmOutAdapter extends BaseAdapter {
//
//    private SDK_AllAlarmOut mAllAlarmOutConfig;
//    private Device mDevice;
//    private LayoutInflater mInflater;
//    private Context mContext;
//
//    public DeviceAlarmOutAdapter(Context mContext, SDK_AllAlarmOut mAllAlarmOutConfig, Device mDevice) {
//        this.mAllAlarmOutConfig = mAllAlarmOutConfig;
//        this.mDevice = mDevice;
//        this.mInflater = LayoutInflater.from(mContext);
//        this.mContext = mContext;
//    }
//
//    @Override
//    public int getCount() {
//        return mAllAlarmOutConfig.alarmOutParam.length;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mAllAlarmOutConfig.alarmOutParam[position];
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = mInflater.inflate(R.layout.list_view_alarm_out, null);
//
//        ItemHolder itemHolder = new ItemHolder();
//
//        itemHolder.tvStatus    = (TextView) convertView.findViewById(R.id.tv_status_result);
//        itemHolder.rbConfig    = (RadioButton) convertView.findViewById(R.id.radio_button_config);
//        itemHolder.rbManual    = (RadioButton) convertView.findViewById(R.id.radio_button_manual);
//        itemHolder.rbStop      = (RadioButton) convertView.findViewById(R.id.radio_button_stop);
//        itemHolder.tvChnNumber = (TextView) convertView.findViewById(R.id.tv_channel_number);
//
//        int chnNumber = position + 1;
//
//        itemHolder.tvChnNumber.setText("Canal " + chnNumber);
//
//        switch (mAllAlarmOutConfig.alarmOutParam[position].AlarmOutStatus) {
//            case 0:
//                itemHolder.tvStatus.setText("Open");
//                break;
//            case 1:
//                itemHolder.tvStatus.setText("Close");
//                break;
//        }
//
//        if(mAllAlarmOutConfig.alarmOutParam[position].AlarmOutType == 0) {
//            itemHolder.rbConfig.setChecked(true);
//        } else if (mAllAlarmOutConfig.alarmOutParam[position].AlarmOutType == 1) {
//            itemHolder.rbManual.setChecked(true);
//        } else if (mAllAlarmOutConfig.alarmOutParam[position].AlarmOutType == 2) {
//            itemHolder.rbStop.setChecked(true);
//        }
//
//        final int positionFinal = position;
//
//        itemHolder.rbConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    mAllAlarmOutConfig.alarmOutParam[positionFinal].AlarmOutType = 0;
//                }
//            }
//        });
//
//        itemHolder.rbManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    mAllAlarmOutConfig.alarmOutParam[positionFinal].AlarmOutType = 1;
//                }
//            }
//        });
//
//        itemHolder.rbStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    mAllAlarmOutConfig.alarmOutParam[positionFinal].AlarmOutType = 2;
//                }
//            }
//        });
//
//        return convertView;
//    }
//
//    public class ItemHolder {
//        public TextView tvStatus, tvChnNumber;
//        public RadioButton rbConfig, rbManual, rbStop;
//    }
//}
