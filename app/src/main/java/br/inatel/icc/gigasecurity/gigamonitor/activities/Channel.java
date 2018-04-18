package br.inatel.icc.gigasecurity.gigamonitor.activities;

/**
 * Created by zappts on 21/03/18.
 */

public class Channel {
    public String mChannelId;
    public int mChannelNewGrid;
    public int mChannelOldGrid;

    public Channel(String channelId, int channelOldGrid, int channelNewGrid){
        mChannelId = channelId;
        mChannelOldGrid = channelOldGrid;
        mChannelNewGrid = channelNewGrid;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public int getChannelNewGrid() {
        return mChannelNewGrid;
    }

    public int getChannelOldGrid() {
        return mChannelOldGrid;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    public void setChannelNewGrid(int channelNewGrid) {
        mChannelNewGrid = channelNewGrid;
    }

    public void setChannelOldGrid(int channelOldGrid) {
        mChannelOldGrid = channelOldGrid;
    }
}
