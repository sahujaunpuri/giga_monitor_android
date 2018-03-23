package br.inatel.icc.gigasecurity.gigamonitor.activities;

/**
 * Created by zappts on 21/03/18.
 */

public class Channels {
    public int mChannel;
    public String mChannelName;

    public Channels(int channel, String channelName){
        mChannel = channel;
        mChannelName = channelName;
    }

    public int getChannel(){
        return mChannel;
    }

    public String getChannelName(){
        return mChannelName;
    }

    public void setChannel(int channel){
        mChannel = channel;
    }

    public void setChannelName(String channelName){
        mChannelName = channelName;
    }
}
