package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by zappts on 5/26/17.
 */

public class FavoritePair implements Serializable{
    @Expose public int deviceId;
    @Expose public int channelNumber;
    @Expose public int id;

    public FavoritePair(int deviceId, int channelNumber) {
        this.deviceId = deviceId;
        this.channelNumber = channelNumber;
        this.id = deviceId + channelNumber;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    @Override
    public String toString() {
        return "<Id:" + deviceId + " Channel:" + channelNumber + ">";
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if(o instanceof FavoritePair){
            FavoritePair current = (FavoritePair) o;
            if(current.deviceId == this.deviceId && current.channelNumber == this.channelNumber)
                ret = true;
        }
        return ret;
    }
}
