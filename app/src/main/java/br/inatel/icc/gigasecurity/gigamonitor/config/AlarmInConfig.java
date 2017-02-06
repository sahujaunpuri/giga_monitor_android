//package br.inatel.icc.gigasecurity.gigamonitor.config;
//
//import com.xm.MyConfig;
//import com.xm.javaclass.SDK_ALARM_INPUTCONFIG;
//
//import java.io.Serializable;
//
///**
// * Created by palomacosta on 26/02/2015.
// */
//public class AlarmInConfig extends Config2Abstract<SDK_ALARM_INPUTCONFIG> implements Serializable {
//
//    public boolean mEnable;
//    public int mSensorType;
//
//    public AlarmInConfig() {}
//
//    public AlarmInConfig (boolean enable, int sensorType) {
//
//        this.mEnable = enable;
//        this.mSensorType = sensorType;
//
//    }
//
//    @Override
//    public void fillFromNetSdkConfig(SDK_ALARM_INPUTCONFIG config) {
//
//        setEnable(config.st_0_bEnable);
//        setSensorType(Integer.valueOf(config.st_2_iSensorType));
//
//    }
//
//    @Override
//    public SDK_ALARM_INPUTCONFIG getAsNetSdkConfig() {
//
//        mConfig.st_0_bEnable = isEnable();
//        mConfig.st_2_iSensorType = getSensorType();
//
//        return mConfig;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_IN;
//    }
//
//    @Override
//    public Class<SDK_ALARM_INPUTCONFIG> getConfigType() {
//        return SDK_ALARM_INPUTCONFIG.class;
//    }
//
//    public boolean isEnable() {
//        return mEnable;
//    }
//
//    public void setEnable(boolean mEnable) {
//        this.mEnable = mEnable;
//    }
//
//    public int getSensorType() {
//        return mSensorType;
//    }
//
//    public void setSensorType(int mSensorType) {
//        this.mSensorType = mSensorType;
//    }
//}
