package br.inatel.icc.gigasecurity.gigamonitor.task;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 5/3/17.
 */

public class AudioRecordThread extends Thread {
    final private String TAG = "AudioRecord";
    private boolean exitFlag = false;
    private boolean pauseFlag = false;
    private AudioRecord mAudioRecord = null;
    private Device mDevice;
    private DeviceManager mDeviceManager;

    public AudioRecordThread(Device device){
        this.mDevice = device;
        this.mDeviceManager = DeviceManager.getInstance();
    }

    public boolean Start() {
        exitFlag = false;
        int minBufSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize);
        if (mAudioRecord == null || mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
            return false;
        else {
            super.start();
            return true;
        }
    }

    public void Stop() {
        exitFlag = true;
    }

    public void Pause(boolean pause) {
        pauseFlag = pause;
        String command;
        if (pauseFlag) {
            command = "ResumeUpload";
        } else {
            command = "PauseUpload";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("Name", "OPTalk");
            json.put("SessionID", "0x00000002");
            JSONObject c_jsonObj = new JSONObject();
            c_jsonObj.put("Action", command);
            json.put("OPTalk", c_jsonObj);

            mDeviceManager.generalCommand(json, mDevice, 1430);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (mAudioRecord == null) {
            return;
        }
        mAudioRecord.startRecording();
        int bufferSizeInBytes = 640;
        byte[] audioData = new byte[bufferSizeInBytes];

        while (!exitFlag) {
            int readSize = mAudioRecord.read(audioData, 0, bufferSizeInBytes);

            if (AudioRecord.ERROR_INVALID_OPERATION != readSize && readSize > 0 && !pauseFlag) {
                mDeviceManager.sendAudio(mDevice, audioData, readSize);
            } else {
                SystemClock.sleep(5);
            }
        }

        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING){
                mAudioRecord.stop();
            }
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }
}
