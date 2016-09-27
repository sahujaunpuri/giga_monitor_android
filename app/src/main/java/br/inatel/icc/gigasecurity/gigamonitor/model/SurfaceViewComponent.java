package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.widget.ProgressBar;

import com.xm.ChnInfo;
import com.xm.video.MySurfaceView;

import java.io.Serializable;

/**
 * Created by filipecampos on 03/12/2015.
 */
public class SurfaceViewComponent implements Serializable {

    public MySurfaceView mySurfaceView;
    public ProgressBar progressBar;
    public int mySurfaceViewID;
    public long realPlayHandleID;
    public ChnInfo chnInfo;
    public boolean isPlaying = false;
    public boolean isHD = false;

    public MySurfaceView getMySurfaceView() {
        return mySurfaceView;
    }

    public void setMySurfaceView(MySurfaceView mySurfaceView) {
        this.mySurfaceView = mySurfaceView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public int getMySurfaceViewID() {
        return mySurfaceViewID;
    }

    public void setMySurfaceViewID(int mySurfaceViewID) {
        this.mySurfaceViewID = mySurfaceViewID;
    }

    public long getRealPlayHandleID() {
        return realPlayHandleID;
    }

    public void setRealPlayHandleID(long realPlayHandleID) {
        this.realPlayHandleID = realPlayHandleID;
    }

    public ChnInfo getChnInfo() {
        return chnInfo;
    }

    public void setChnInfo(ChnInfo chnInfo) {
        this.chnInfo = chnInfo;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isHD() {
        return isHD;
    }

    public void setIsHD(boolean isHD) {
        this.isHD = isHD;
    }

}
