package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.content.Context;
import android.media.MediaRecorder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zappts on 04/08/17.
 */

public class MediaConverter {

    MediaRecorder myMediaRecorder;

    public MediaConverter(MediaRecorder mediaRecorder) {
        myMediaRecorder = mediaRecorder;
    }

    public MediaRecorder convertMedia() {
        Method[] methods = myMediaRecorder.getClass().getMethods();
        myMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        myMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myMediaRecorder.setVideoFrameRate(24);
        myMediaRecorder.setVideoSize(720, 480);

        for (Method method: methods){
            try{
                if (method.getName().equals("setAudioChannels")){
                    method.invoke(myMediaRecorder, String.format("audio-param-number-of-channels=%d", 1));
                }
                else if(method.getName().equals("setAudioEncodingBitRate")){
                    method.invoke(myMediaRecorder,12200);
                }
                else if(method.getName().equals("setVideoEncodingBitRate")){
                    method.invoke(myMediaRecorder, 3000000);
                }
                else if(method.getName().equals("setAudioSamplingRate")){
                    method.invoke(myMediaRecorder,8000);
                }
                else if(method.getName().equals("setVideoFrameRate")){
                    method.invoke(myMediaRecorder,24);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        myMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        return myMediaRecorder;
    }

}
