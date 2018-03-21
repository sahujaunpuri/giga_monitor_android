package br.inatel.icc.gigasecurity.gigamonitor.activities;

/**
 * Created by zappts on 21/03/18.
 */

public class Channels {
    public int mPosition;
    public String mChannelName;

    public Channels(int position, String channelName){
        mPosition = position;
        mChannelName = channelName;
    }

    public int getPosition(){
        return mPosition;
    }

    public String getChannelName(){
        return mChannelName;
    }

    public void setPosition(int position){
        mPosition = position;
    }
}
