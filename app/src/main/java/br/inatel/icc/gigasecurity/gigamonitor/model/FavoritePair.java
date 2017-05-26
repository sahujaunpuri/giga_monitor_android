package br.inatel.icc.gigasecurity.gigamonitor.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by zappts on 5/26/17.
 */

public class FavoritePair implements Serializable {
    @Expose public int deviceId;
    @Expose public int channelNumber;

    public FavoritePair(int deviceId, int channelNumber) {
        this.deviceId = deviceId;
        this.channelNumber = channelNumber;
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
}
