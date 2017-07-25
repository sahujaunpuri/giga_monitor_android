package br.inatel.icc.gigasecurity.gigamonitor.listeners;

/**
 * Created by zappts on 25/07/17.
 */

public interface DownloadPlaybackListener {

    void onStartDownload(int fileSize);
    void onProgressDownload(int currentProgress, int totalProgress);
    void onFinishDownload();
    void onCancelDownload();
}
