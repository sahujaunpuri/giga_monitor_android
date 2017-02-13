package br.inatel.icc.gigasecurity.gigamonitor.listeners;

import com.lib.sdk.struct.H264_DVR_FILE_DATA;

/**
 * Created by zappts on 2/6/17.
 */

public interface PlaybackSearchListener {
    void onFindList(H264_DVR_FILE_DATA files[]);
    void onEmptyListFound();
}
