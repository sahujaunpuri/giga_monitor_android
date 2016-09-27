package br.inatel.icc.gigasecurity.gigamonitor.config.wifi;

import android.content.Context;
import android.util.Log;

import com.basic.G;
import com.xm.MyConfig;
import com.xm.javaclass.SDK_NetWifiDevice;
import com.xm.javaclass.SDK_NetWifiDeviceAll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;


/**
 * File: WifiConfig.java
 * Creation date: 06/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class WifiConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class WifiAPListConfig extends Config2Abstract<SDK_NetWifiDeviceAll> implements Serializable{

    private List<WifiItem> wifiList = new ArrayList<>();
    private List<String> wifiSSIDList = new ArrayList<>();

    public WifiAPListConfig() {
    }



    @Override
    public SDK_NetWifiDeviceAll getAsNetSdkConfig() {
        final int size = wifiList.size();
        SDK_NetWifiDevice[] array = new SDK_NetWifiDevice[size];
        if(size > 0){
            WifiItem wifiItem;
            for(int i = 0 ; i < size; ++i) {
                wifiItem = wifiList.get(i);
                SDK_NetWifiDevice device = new SDK_NetWifiDevice();
                G.SetValue(device.st_0_sSSID, wifiItem.getSSID());
                device.st_1_nRSSI = wifiItem.getRSSI();
                device.st_2_nChannel = wifiItem.getChannel();
                G.SetValue(device.st_3_sNetType, wifiItem.getNetType());
                G.SetValue(device.st_4_sEncrypType, wifiItem.getEncrypType());
                G.SetValue(device.st_5_sAuth, wifiItem.getAuth());

                array[i] = device;

            }
        }

        mConfig.st_1_vNetWifiDeviceAll = array;

        return mConfig;
    }

    @Override
    public long getConfigKey() {
        return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_WIFI_AP_LIST;
    }

    @Override
    public Class<SDK_NetWifiDeviceAll> getConfigType() {
        return SDK_NetWifiDeviceAll.class;
    }

    @Override
    public void fillFromNetSdkConfig(SDK_NetWifiDeviceAll config) {
        wifiList.clear();
        wifiSSIDList.clear();
        if(config.st_0_nDevNumber > 0){
            for(int i = 0 ; i < config.st_0_nDevNumber; ++i) {
                SDK_NetWifiDevice device = config.st_1_vNetWifiDeviceAll[i];
                WifiItem wifiItem = new WifiItem(

                        G.ToString(device.st_0_sSSID),
                        device.st_1_nRSSI,
                        device.st_2_nChannel,
                        G.ToString(device.st_3_sNetType),
                        G.ToString(device.st_4_sEncrypType),
                        G.ToString(device.st_5_sAuth)

                );
                if(!wifiSSIDList.contains(wifiItem.getSSID())){
                    wifiSSIDList.add(wifiItem.getSSID());
                    wifiList.add(wifiItem);
                }

                Log.d("testGetWifiAPList", "ssid:" + G.ToString(device.st_0_sSSID));
            }
        }

    }

    @Override
    public boolean setConfig(Long loginID) throws Exception {
        return false ;
    }

    @Override
    public void setConfigTask(Context context, Long loginID, ConfigSetTaskListener configListener) {
    }

    public List<WifiItem> getWifiList() {
        return wifiList;
    }


    public class WifiItem implements Serializable{
        
        public String mSSSID;
        public int mRSSI;
        public int mChannel;
        public String mNetType;
        public String mEncrypType;
        public String mAuth;

        public WifiItem() {
        }

        public WifiItem(String mSSSID, int mRSSI, int mChannel, String mNetType, String mEncrypType, String mAuth) {
            this.mSSSID = mSSSID;
            this.mRSSI = mRSSI;
            this.mChannel = mChannel;
            this.mNetType = mNetType;
            this.mEncrypType = mEncrypType;
            this.mAuth = mAuth;
        }



        public String getSSID() {
            return mSSSID;
        }

        public void setSSSID(String mSSSID) {
            this.mSSSID = mSSSID;
        }

        public int getRSSI() {
            return mRSSI;
        }

        public void setRSSI(int mRSSI) {
            this.mRSSI = mRSSI;
        }

        public int getChannel() {
            return mChannel;
        }

        public void setChannel(int mChannel) {
            this.mChannel = mChannel;
        }

        public String getNetType() {
            return mNetType;
        }

        public void setNetType(String mNetType) {
            this.mNetType = mNetType;
        }

        public String getEncrypType() {
            return mEncrypType;
        }

        public void setEncrypType(String mEncrypType) {
            this.mEncrypType = mEncrypType;
        }

        public String getAuth() {
            return mAuth;
        }

        public void setAuth(String mAuth) {
            this.mAuth = mAuth;
        }
    }



}
