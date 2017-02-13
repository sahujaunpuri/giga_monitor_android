package br.inatel.icc.gigasecurity.gigamonitor.listeners;

/**
 * Created by zappts on 2/7/17.
 */

public interface PlaybackListener {
    void onComplete();
    void onChangeProgress(int progress);
    void onCompleteSeek();
    void onPlayState(int state); //2: playing, 3:paused
}
