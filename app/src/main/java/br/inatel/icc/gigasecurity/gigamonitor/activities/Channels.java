package br.inatel.icc.gigasecurity.gigamonitor.activities;

/**
 * Created by zappts on 21/03/18.
 */

public class Channels {
    private int mOriginalPosition;
    private int mNewPosition;
    private String mChannelName;

    public Channels(int originalPosition, int newPosition, String channelName){
        mOriginalPosition = originalPosition;
        mNewPosition = newPosition;
        mChannelName = channelName;
    }

    public int getOriginalPosition(){
        return mOriginalPosition;
    }

    public int getNewPosition(){
        return mNewPosition;
    }

    public String getChannelName(){
        return mChannelName;
    }

    public void setOriginalPosition(int originalPosition){
        mOriginalPosition = originalPosition;
    }

    public void setNewPosition(int newPosition){
        mNewPosition = newPosition;
    }

    public void setChannelName(String channelName){
        mChannelName = channelName;
    }
}
