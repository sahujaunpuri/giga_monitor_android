package br.inatel.icc.gigasecurity.gigamonitor.activities;

/**
 * Created by zappts on 21/03/18.
 */

public class ChannelsOrder {
    public int mPosition;
    public String mChannelName;

    public ChannelsOrder(int position, String channelName){
        mPosition = position;
        mChannelName = channelName;
    }

    public int getPosition(){
        return mPosition;
    }

    public String getChannelName(){
        return mChannelName;
    }
}
