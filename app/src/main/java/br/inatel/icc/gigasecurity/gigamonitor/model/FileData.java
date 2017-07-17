package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.text.TextUtils;

import com.basic.G;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.inatel.icc.gigasecurity.gigamonitor.util.OPCompressPic;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by zappts on 2/6/17.
 */

public class FileData implements Serializable{
    private String mFileName;
    public int mFileType;
    private int mStreamType;
    public String mFileBeginDate;
    public String mFileBeginTime;
    public String mFileEndDate;
    public String mFileEndTime;

    private H264_DVR_FILE_DATA mFileData = null;
    private OPCompressPic mFileOpComPic = null;

    public FileData(H264_DVR_FILE_DATA fileData, OPCompressPic op) {
        this.parseFromData(fileData);
        mFileOpComPic = op;
    }

    public void parseFromData(H264_DVR_FILE_DATA fileData) {
        this.mFileData = fileData;
        this.mFileName = G.ToString(fileData.st_2_fileName);
        this.mFileType = Utils.getIntFileType(mFileName);
        this.mStreamType = fileData.st_6_StreamType;
        this.mFileBeginDate = String.format("%04d-%02d-%02d",
                fileData.st_3_beginTime.st_0_year,
                fileData.st_3_beginTime.st_1_month,
                fileData.st_3_beginTime.st_2_day);
        this.mFileBeginTime = String.format("%02d:%02d:%02d",
                fileData.st_3_beginTime.st_4_hour,
                fileData.st_3_beginTime.st_5_minute,
                fileData.st_3_beginTime.st_6_second);
        this.mFileEndDate = String.format("%04d-%02d-%02d",
                fileData.st_4_endTime.st_0_year,
                fileData.st_4_endTime.st_1_month,
                fileData.st_4_endTime.st_2_day);
        this.mFileEndTime = String.format("%02d:%02d:%02d",
                fileData.st_4_endTime.st_4_hour,
                fileData.st_4_endTime.st_5_minute,
                fileData.st_4_endTime.st_6_second);
    }

    public void setFileName(String name) {
        if(!TextUtils.isEmpty(name)){
            G.SetValue(mFileData.st_2_fileName, name);
            mFileName = name;
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public int getStreamType() {
        return mStreamType;
    }

    public int getFileType() {
        return mFileType;
    }

    public String getBeginDateStr() {
        if ( null == mFileBeginDate) {
            return "";
        }
        return mFileBeginDate;
    }

    public String getBeginTimeStr() {
        if ( null == mFileBeginTime) {
            return "";
        }
        return mFileBeginTime;
    }

    public String getBeginTimeStr(int hour, int minute, int second) {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", mFileData.st_3_beginTime.st_0_year,
                mFileData.st_3_beginTime.st_1_month, mFileData.st_3_beginTime.st_2_day, hour, minute, second);
    }

    public String getStartTimeOfYear() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:dd").format(new Date(
                mFileData.st_3_beginTime.st_0_year - 1900, mFileData.st_3_beginTime.st_1_month - 1,
                mFileData.st_3_beginTime.st_2_day, mFileData.st_3_beginTime.st_4_hour,
                mFileData.st_3_beginTime.st_5_minute, mFileData.st_3_beginTime.st_6_second));
    }

    public String getEndTimeStr() {
        if ( null == mFileEndTime) {
            return "";
        }
        return mFileEndTime;
    }

    public String getEndTimeStr(int hour, int minute, int second) {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", mFileData.st_4_endTime.st_0_year,
                mFileData.st_4_endTime.st_1_month, mFileData.st_4_endTime.st_2_day, hour, minute, second);
    }

    public String getEndTimeOfYear() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:dd").format(new Date(
                mFileData.st_4_endTime.st_0_year - 1900, mFileData.st_4_endTime.st_1_month - 1,
                mFileData.st_4_endTime.st_2_day, mFileData.st_4_endTime.st_4_hour,
                mFileData.st_4_endTime.st_5_minute, mFileData.st_4_endTime.st_6_second));
    }



    public H264_DVR_FILE_DATA getFileData() {
        return mFileData;
    }

    public void setBeginDate(Date date){
        mFileData.st_3_beginTime.st_0_year = date.getYear() + 1900;
        mFileData.st_3_beginTime.st_1_month = date.getMonth() + 1;
        mFileData.st_3_beginTime.st_2_day = date.getDate();
        mFileData.st_3_beginTime.st_4_hour = date.getHours();
        mFileData.st_3_beginTime.st_5_minute = date.getMinutes();
        mFileData.st_3_beginTime.st_6_second = date.getSeconds();
        parseFromData(mFileData);
    }

    public Date getBeginDate() {
        SDK_SYSTEM_TIME beginTm = mFileData.st_3_beginTime;
        return new Date(beginTm.st_0_year - 1900, beginTm.st_1_month - 1,
                beginTm.st_2_day, beginTm.st_4_hour,
                beginTm.st_5_minute, beginTm.st_6_second);
    }

    public void setEndDate(Date date){
        mFileData.st_4_endTime.st_0_year = date.getYear() + 1900;
        mFileData.st_4_endTime.st_1_month = date.getMonth() + 1;
        mFileData.st_4_endTime.st_2_day = date.getDate();
        mFileData.st_4_endTime.st_4_hour = date.getHours();
        mFileData.st_4_endTime.st_5_minute = date.getMinutes();
        mFileData.st_4_endTime.st_6_second = date.getSeconds();
        parseFromData(mFileData);
    }

    public Date getEndDate() {
        SDK_SYSTEM_TIME endTm = mFileData.st_4_endTime;
        return new Date(endTm.st_0_year - 1900, endTm.st_1_month - 1,
                endTm.st_2_day, endTm.st_4_hour,
                endTm.st_5_minute, endTm.st_6_second);
    }

    public boolean hasSeachedFile() {
        if ( null == mFileName || mFileName.length() == 0 ) {
            return false;
        }
        return true;
    }

    public long getTotalTime(){
        if(mFileData != null)
            return mFileData.getLongEndTime() - mFileData.getLongStartTime();
        else
            return -1;
    }

    public long getStartTime(){
        return mFileData.getLongStartTime();
    }

    public OPCompressPic getmFileOpComPic() {
        return mFileOpComPic;
    }

    public void setmFileOpComPic(OPCompressPic mFileOpComPic) {
        this.mFileOpComPic = mFileOpComPic;
    }
}
