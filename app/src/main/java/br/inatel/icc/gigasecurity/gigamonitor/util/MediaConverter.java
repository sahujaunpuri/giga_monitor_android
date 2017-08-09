package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zappts on 04/08/17.
 */

public class MediaConverter extends Thread {

//    private static final int TIMEOUT_USEC = 10000;
//
//    private static final String OUTPUT_VIDEO_MIME_TYPE = "video/avc";
//    private static final int OUTPUT_VIDEO_BIT_RATE = 2048 * 1024;
//    private static final int OUTPUT_VIDEO_FRAME_RATE = 30;
//    private static final int OUTPUT_VIDEO_IFRAME_INTERVAL = 10;
//    private static final int OUTPUT_VIDEO_COLOR_FORMAT =
//            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
//
//    private static final String OUTPUT_AUDIO_MIME_TYPE = "audio/mp4a-latm";
//    private static final int OUTPUT_AUDIO_CHANNEL_COUNT = 2;
//    private static final int OUTPUT_AUDIO_BIT_RATE = 128 * 1024;
//    private static final int OUTPUT_AUDIO_AAC_PROFILE =
//            MediaCodecInfo.CodecProfileLevel.AACObjectHE;
//    private static final int OUTPUT_AUDIO_SAMPLE_RATE_HZ = 44100;
//
//    private int mWidth = 720;
//    private int mHeight = 480;
//    private String mOutputFile, mInputFile;
//
//    public String changeResolution(File f)
//            throws Throwable {
//        mInputFile=f.getAbsolutePath();
//
//        String filePath = mInputFile.substring(0, mInputFile.lastIndexOf(File.separator));
//        String[] splitByDot = mInputFile.split("\\.");
//        String ext="";
//        if(splitByDot!=null && splitByDot.length>1)
//            ext = splitByDot[splitByDot.length-1];
//        String fileName = mInputFile.substring(mInputFile.lastIndexOf(File.separator)+1,
//                mInputFile.length());
//        if(ext.length()>0)
//            fileName=fileName.replace("."+ext, "_out.mp4");
//        else
//            fileName=fileName.concat("_out.mp4");
//
//        final File outFile = new File(Environment.getExternalStorageDirectory(), fileName);
//        if(!outFile.exists())
//            outFile.createNewFile();
//
//        mOutputFile=outFile.getAbsolutePath();
//
//        ChangerWrapper.changeResolutionInSeparatedThread(this);
//
//        return mOutputFile;
//    }
//
//    private static class ChangerWrapper implements Runnable {
//
//        private Throwable mThrowable;
//        private MediaConverter mChanger;
//
//        private ChangerWrapper(MediaConverter changer) {
//            mChanger = changer;
//        }
//
//        @Override
//        public void run() {
//            try {
//                mChanger.prepareAndChangeResolution();
//            } catch (Throwable th) {
//                mThrowable = th;
//            }
//        }
//
//        public static void changeResolutionInSeparatedThread(MediaConverter changer)
//                throws Throwable {
//            ChangerWrapper wrapper = new ChangerWrapper(changer);
//            Thread th = new Thread(wrapper, ChangerWrapper.class.getSimpleName());
//            th.start();
//            th.join();
//            if (wrapper.mThrowable != null)
//                throw wrapper.mThrowable;
//        }
//    }
//
//    private void prepareAndChangeResolution() throws Exception {
//        Exception exception = null;
//
//        MediaCodecInfo videoCodecInfo = selectCodec(OUTPUT_VIDEO_MIME_TYPE);
//        if (videoCodecInfo == null)
//            return;
//        MediaCodecInfo audioCodecInfo = selectCodec(OUTPUT_AUDIO_MIME_TYPE);
//        if (audioCodecInfo == null)
//            return;
//
//        MediaExtractor videoExtractor = null;
//        MediaExtractor audioExtractor = null;
////        OutputSurface outputSurface = null;
//        MediaCodec videoDecoder = null;
//        MediaCodec audioDecoder = null;
//        MediaCodec videoEncoder = null;
//        MediaCodec audioEncoder = null;
//        MediaMuxer muxer = null;
//        InputSurface inputSurface = null;
//        try {
//            videoExtractor = createExtractor();
//            int videoInputTrack = getAndSelectVideoTrackIndex(videoExtractor);
//            MediaFormat inputFormat = videoExtractor.getTrackFormat(videoInputTrack);
//
//            MediaMetadataRetriever m = new MediaMetadataRetriever();
//            m.setDataSource(mInputFile);
//            Bitmap thumbnail = m.getFrameAtTime();
//            int inputWidth = thumbnail.getWidth(),
//                    inputHeight = thumbnail.getHeight();
//
//            if(inputWidth>inputHeight){
//                if(mWidth<mHeight){
//                    int w = mWidth;
//                    mWidth=mHeight;
//                    mHeight=w;
//                }
//            }
//            else{
//                if(mWidth>mHeight){
//                    int w = mWidth;
//                    mWidth=mHeight;
//                    mHeight=w;
//                }
//            }
//
//            MediaFormat outputVideoFormat =
//                    MediaFormat.createVideoFormat(OUTPUT_VIDEO_MIME_TYPE, mWidth, mHeight);
//            outputVideoFormat.setInteger(
//                    MediaFormat.KEY_COLOR_FORMAT, OUTPUT_VIDEO_COLOR_FORMAT);
//            outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, OUTPUT_VIDEO_BIT_RATE);
//            outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, OUTPUT_VIDEO_FRAME_RATE);
//            outputVideoFormat.setInteger(
//                    MediaFormat.KEY_I_FRAME_INTERVAL, OUTPUT_VIDEO_IFRAME_INTERVAL);
//
//            AtomicReference<Surface> inputSurfaceReference = new AtomicReference<Surface>();
//            videoEncoder = createVideoEncoder(
//                    videoCodecInfo, outputVideoFormat, inputSurfaceReference);
//            inputSurface = new InputSurface(inputSurfaceReference.get());
//            inputSurface.makeCurrent();
//
//            outputSurface = new OutputSurface();
//            videoDecoder = createVideoDecoder(inputFormat, outputSurface.getSurface());
//
//            audioExtractor = createExtractor();
//            int audioInputTrack = getAndSelectAudioTrackIndex(audioExtractor);
//            MediaFormat inputAudioFormat = audioExtractor.getTrackFormat(audioInputTrack);
//            MediaFormat outputAudioFormat =
//                    MediaFormat.createAudioFormat(inputAudioFormat.getString(MediaFormat.KEY_MIME),
//                            inputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE),
//                            inputAudioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
//            outputAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, OUTPUT_AUDIO_BIT_RATE);
//            outputAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, OUTPUT_AUDIO_AAC_PROFILE);
//
//            audioEncoder = createAudioEncoder(audioCodecInfo, outputAudioFormat);
//            audioDecoder = createAudioDecoder(inputAudioFormat);
//
//            muxer = new MediaMuxer(mOutputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//
//            changeResolution(videoExtractor, audioExtractor,
//                    videoDecoder, videoEncoder,
//                    audioDecoder, audioEncoder,
//                    muxer, inputSurface, outputSurface);
//        } finally {
//            try {
//                if (videoExtractor != null)
//                    videoExtractor.release();
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (audioExtractor != null)
//                    audioExtractor.release();
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (videoDecoder != null) {
//                    videoDecoder.stop();
//                    videoDecoder.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (outputSurface != null) {
//                    outputSurface.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (videoEncoder != null) {
//                    videoEncoder.stop();
//                    videoEncoder.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (audioDecoder != null) {
//                    audioDecoder.stop();
//                    audioDecoder.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (audioEncoder != null) {
//                    audioEncoder.stop();
//                    audioEncoder.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (muxer != null) {
//                    muxer.stop();
//                    muxer.release();
//                }
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//            try {
//                if (inputSurface != null)
//                    inputSurface.release();
//            } catch(Exception e) {
//                if (exception == null)
//                    exception = e;
//            }
//        }
//        if (exception != null)
//            throw exception;
//    }
//
//    private MediaExtractor createExtractor() throws IOException {
//        MediaExtractor extractor;
//        extractor = new MediaExtractor();
//        extractor.setDataSource(mInputFile);
//        return extractor;
//    }
//
//    private MediaCodec createVideoDecoder(MediaFormat inputFormat, Surface surface) throws IOException {
//        MediaCodec decoder = MediaCodec.createDecoderByType(getMimeTypeFor(inputFormat));
//        decoder.configure(inputFormat, surface, null, 0);
//        decoder.start();
//        return decoder;
//    }
//
//    private MediaCodec createVideoEncoder(MediaCodecInfo codecInfo, MediaFormat format,
//                                          AtomicReference<Surface> surfaceReference) throws IOException {
//        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());
//        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        surfaceReference.set(encoder.createInputSurface());
//        encoder.start();
//        return encoder;
//    }
//
//    private MediaCodec createAudioDecoder(MediaFormat inputFormat) throws IOException {
//        MediaCodec decoder = MediaCodec.createDecoderByType(getMimeTypeFor(inputFormat));
//        decoder.configure(inputFormat, null, null, 0);
//        decoder.start();
//        return decoder;
//    }
//
//    private MediaCodec createAudioEncoder(MediaCodecInfo codecInfo, MediaFormat format) throws IOException {
//        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());
//        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        encoder.start();
//        return encoder;
//    }
//
//    private int getAndSelectVideoTrackIndex(MediaExtractor extractor) {
//        for (int index = 0; index < extractor.getTrackCount(); ++index) {
//            if (isVideoFormat(extractor.getTrackFormat(index))) {
//                extractor.selectTrack(index);
//                return index;
//            }
//        }
//        return -1;
//    }
//    private int getAndSelectAudioTrackIndex(MediaExtractor extractor) {
//        for (int index = 0; index < extractor.getTrackCount(); ++index) {
//            if (isAudioFormat(extractor.getTrackFormat(index))) {
//                extractor.selectTrack(index);
//                return index;
//            }
//        }
//        return -1;
//    }
//
//    private void changeResolution(MediaExtractor videoExtractor, MediaExtractor audioExtractor,
//                                  MediaCodec videoDecoder, MediaCodec videoEncoder,
//                                  MediaCodec audioDecoder, MediaCodec audioEncoder,
//                                  MediaMuxer muxer,
//                                  InputSurface inputSurface, OutputSurface outputSurface) {
//        ByteBuffer[] videoDecoderInputBuffers = null;
//        ByteBuffer[] videoDecoderOutputBuffers = null;
//        ByteBuffer[] videoEncoderOutputBuffers = null;
//        MediaCodec.BufferInfo videoDecoderOutputBufferInfo = null;
//        MediaCodec.BufferInfo videoEncoderOutputBufferInfo = null;
//
//        videoDecoderInputBuffers = videoDecoder.getInputBuffers();
//        videoDecoderOutputBuffers = videoDecoder.getOutputBuffers();
//        videoEncoderOutputBuffers = videoEncoder.getOutputBuffers();
//        videoDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
//        videoEncoderOutputBufferInfo = new MediaCodec.BufferInfo();
//
//        ByteBuffer[] audioDecoderInputBuffers = null;
//        ByteBuffer[] audioDecoderOutputBuffers = null;
//        ByteBuffer[] audioEncoderInputBuffers = null;
//        ByteBuffer[] audioEncoderOutputBuffers = null;
//        MediaCodec.BufferInfo audioDecoderOutputBufferInfo = null;
//        MediaCodec.BufferInfo audioEncoderOutputBufferInfo = null;
//
//        audioDecoderInputBuffers = audioDecoder.getInputBuffers();
//        audioDecoderOutputBuffers =  audioDecoder.getOutputBuffers();
//        audioEncoderInputBuffers = audioEncoder.getInputBuffers();
//        audioEncoderOutputBuffers = audioEncoder.getOutputBuffers();
//        audioDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
//        audioEncoderOutputBufferInfo = new MediaCodec.BufferInfo();
//
//        MediaFormat decoderOutputVideoFormat = null;
//        MediaFormat decoderOutputAudioFormat = null;
//        MediaFormat encoderOutputVideoFormat = null;
//        MediaFormat encoderOutputAudioFormat = null;
//        int outputVideoTrack = -1;
//        int outputAudioTrack = -1;
//
//        boolean videoExtractorDone = false;
//        boolean videoDecoderDone = false;
//        boolean videoEncoderDone = false;
//
//        boolean audioExtractorDone = false;
//        boolean audioDecoderDone = false;
//        boolean audioEncoderDone = false;
//
//        int pendingAudioDecoderOutputBufferIndex = -1;
//        boolean muxing = false;
//        while ((!videoEncoderDone) || (!audioEncoderDone)) {
//            while (!videoExtractorDone
//                    && (encoderOutputVideoFormat == null || muxing)) {
//                int decoderInputBufferIndex = videoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
//                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
//                    break;
//
//                ByteBuffer decoderInputBuffer = videoDecoderInputBuffers[decoderInputBufferIndex];
//                int size = videoExtractor.readSampleData(decoderInputBuffer, 0);
//                long presentationTime = videoExtractor.getSampleTime();
//
//                if (size >= 0) {
//                    videoDecoder.queueInputBuffer(
//                            decoderInputBufferIndex,
//                            0,
//                            size,
//                            presentationTime,
//                            videoExtractor.getSampleFlags());
//                }
//                videoExtractorDone = !videoExtractor.advance();
//                if (videoExtractorDone)
//                    videoDecoder.queueInputBuffer(decoderInputBufferIndex,
//                            0, 0, 0,  MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                break;
//            }
//
//            while (!audioExtractorDone
//                    && (encoderOutputAudioFormat == null || muxing)) {
//                int decoderInputBufferIndex = audioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
//                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
//                    break;
//
//                ByteBuffer decoderInputBuffer = audioDecoderInputBuffers[decoderInputBufferIndex];
//                int size = audioExtractor.readSampleData(decoderInputBuffer, 0);
//                long presentationTime = audioExtractor.getSampleTime();
//
//                if (size >= 0)
//                    audioDecoder.queueInputBuffer(decoderInputBufferIndex, 0, size,
//                            presentationTime, audioExtractor.getSampleFlags());
//
//                audioExtractorDone = !audioExtractor.advance();
//                if (audioExtractorDone)
//                    audioDecoder.queueInputBuffer(decoderInputBufferIndex, 0, 0,
//                            0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//
//                break;
//            }
//
//            while (!videoDecoderDone
//                    && (encoderOutputVideoFormat == null || muxing)) {
//                int decoderOutputBufferIndex =
//                        videoDecoder.dequeueOutputBuffer(
//                                videoDecoderOutputBufferInfo, TIMEOUT_USEC);
//                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
//                    break;
//
//                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    videoDecoderOutputBuffers = videoDecoder.getOutputBuffers();
//                    break;
//                }
//                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    decoderOutputVideoFormat = videoDecoder.getOutputFormat();
//                    break;
//                }
//
//                ByteBuffer decoderOutputBuffer =
//                        videoDecoderOutputBuffers[decoderOutputBufferIndex];
//                if ((videoDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        != 0) {
//                    videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
//                    break;
//                }
//
//                boolean render = videoDecoderOutputBufferInfo.size != 0;
//                videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, render);
//                if (render) {
//                    outputSurface.awaitNewImage();
//                    outputSurface.drawImage();
//                    inputSurface.setPresentationTime(
//                            videoDecoderOutputBufferInfo.presentationTimeUs * 1000);
//                    inputSurface.swapBuffers();
//                }
//                if ((videoDecoderOutputBufferInfo.flags
//                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    videoDecoderDone = true;
//                    videoEncoder.signalEndOfInputStream();
//                }
//                break;
//            }
//
//            while (!audioDecoderDone && pendingAudioDecoderOutputBufferIndex == -1
//                    && (encoderOutputAudioFormat == null || muxing)) {
//                int decoderOutputBufferIndex =
//                        audioDecoder.dequeueOutputBuffer(
//                                audioDecoderOutputBufferInfo, TIMEOUT_USEC);
//                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
//                    break;
//
//                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    audioDecoderOutputBuffers = audioDecoder.getOutputBuffers();
//                    break;
//                }
//                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    decoderOutputAudioFormat = audioDecoder.getOutputFormat();
//                    break;
//                }
//                ByteBuffer decoderOutputBuffer =
//                        audioDecoderOutputBuffers[decoderOutputBufferIndex];
//                if ((audioDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        != 0) {
//                    audioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
//                    break;
//                }
//                pendingAudioDecoderOutputBufferIndex = decoderOutputBufferIndex;
//                break;
//            }
//
//            while (pendingAudioDecoderOutputBufferIndex != -1) {
//                int encoderInputBufferIndex = audioEncoder.dequeueInputBuffer(TIMEOUT_USEC);
//                ByteBuffer encoderInputBuffer = audioEncoderInputBuffers[encoderInputBufferIndex];
//                int size = audioDecoderOutputBufferInfo.size;
//                long presentationTime = audioDecoderOutputBufferInfo.presentationTimeUs;
//
//                if (size >= 0) {
//                    ByteBuffer decoderOutputBuffer =
//                            audioDecoderOutputBuffers[pendingAudioDecoderOutputBufferIndex]
//                                    .duplicate();
//                    decoderOutputBuffer.position(audioDecoderOutputBufferInfo.offset);
//                    decoderOutputBuffer.limit(audioDecoderOutputBufferInfo.offset + size);
//                    encoderInputBuffer.position(0);
//                    encoderInputBuffer.put(decoderOutputBuffer);
//                    audioEncoder.queueInputBuffer(
//                            encoderInputBufferIndex,
//                            0,
//                            size,
//                            presentationTime,
//                            audioDecoderOutputBufferInfo.flags);
//                }
//                audioDecoder.releaseOutputBuffer(pendingAudioDecoderOutputBufferIndex, false);
//                pendingAudioDecoderOutputBufferIndex = -1;
//                if ((audioDecoderOutputBufferInfo.flags
//                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
//                    audioDecoderDone = true;
//
//                break;
//            }
//
//            while (!videoEncoderDone
//                    && (encoderOutputVideoFormat == null || muxing)) {
//                int encoderOutputBufferIndex = videoEncoder.dequeueOutputBuffer(
//                        videoEncoderOutputBufferInfo, TIMEOUT_USEC);
//                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
//                    break;
//                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    videoEncoderOutputBuffers = videoEncoder.getOutputBuffers();
//                    break;
//                }
//                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    encoderOutputVideoFormat = videoEncoder.getOutputFormat();
//                    break;
//                }
//
//                ByteBuffer encoderOutputBuffer =
//                        videoEncoderOutputBuffers[encoderOutputBufferIndex];
//                if ((videoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        != 0) {
//                    videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                    break;
//                }
//                if (videoEncoderOutputBufferInfo.size != 0) {
//                    muxer.writeSampleData(
//                            outputVideoTrack, encoderOutputBuffer, videoEncoderOutputBufferInfo);
//                }
//                if ((videoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
//                        != 0) {
//                    videoEncoderDone = true;
//                }
//                videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                break;
//            }
//
//            while (!audioEncoderDone
//                    && (encoderOutputAudioFormat == null || muxing)) {
//                int encoderOutputBufferIndex = audioEncoder.dequeueOutputBuffer(
//                        audioEncoderOutputBufferInfo, TIMEOUT_USEC);
//                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    break;
//                }
//                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    audioEncoderOutputBuffers = audioEncoder.getOutputBuffers();
//                    break;
//                }
//                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    encoderOutputAudioFormat = audioEncoder.getOutputFormat();
//                    break;
//                }
//
//                ByteBuffer encoderOutputBuffer =
//                        audioEncoderOutputBuffers[encoderOutputBufferIndex];
//                if ((audioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        != 0) {
//                    audioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                    break;
//                }
//                if (audioEncoderOutputBufferInfo.size != 0)
//                    muxer.writeSampleData(
//                            outputAudioTrack, encoderOutputBuffer, audioEncoderOutputBufferInfo);
//                if ((audioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
//                        != 0)
//                    audioEncoderDone = true;
//
//                audioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//
//                break;
//            }
//            if (!muxing && (encoderOutputAudioFormat != null)
//                    && (encoderOutputVideoFormat != null)) {
//                outputVideoTrack = muxer.addTrack(encoderOutputVideoFormat);
//                outputAudioTrack = muxer.addTrack(encoderOutputAudioFormat);
//                muxer.start();
//                muxing = true;
//            }
//        }
//    }
//
//    private static boolean isVideoFormat(MediaFormat format) {
//        return getMimeTypeFor(format).startsWith("video/");
//    }
//    private static boolean isAudioFormat(MediaFormat format) {
//        return getMimeTypeFor(format).startsWith("audio/");
//    }
//    private static String getMimeTypeFor(MediaFormat format) {
//        return format.getString(MediaFormat.KEY_MIME);
//    }
//
//    private static MediaCodecInfo selectCodec(String mimeType) {
//        int numCodecs = MediaCodecList.getCodecCount();
//        for (int i = 0; i < numCodecs; i++) {
//            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
//            if (!codecInfo.isEncoder()) {
//                continue;
//            }
//            String[] types = codecInfo.getSupportedTypes();
//            for (int j = 0; j < types.length; j++) {
//                if (types[j].equalsIgnoreCase(mimeType)) {
//                    return codecInfo;
//                }
//            }
//        }
//        return null;
//    }

//    private Context mContext;
//    private String TAG = "MediaConverter";
//    private FFmpeg ffmpeg;
//
//    public MediaConverter(Context context) {
//        mContext = context;
//    }
//
//    private void loadFFMpegBinary() {
//        try {
//            if (ffmpeg == null) {
//                Log.d(TAG, "ffmpeg : null");
//                ffmpeg = FFmpeg.getInstance(mContext);
//            }
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    showUnsupportedExceptionDialog();
//                }
//
//                @Override
//                public void onSuccess() {
//                    Log.d(TAG, "ffmpeg : correct Loaded");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            showUnsupportedExceptionDialog();
//        } catch (Exception e) {
//            Log.d(TAG, "EXception not supported :  " + e);
//        }
//    }
//
//    public void execFFmpegBinary(final String[] command) {
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onFailure(String s) {
//                    Log.d(TAG, "FAILED with output : " + s);
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    Log.d(TAG, "SUCCESS with output : " + s);
//                //Perform action on success
//                }
//
//                @Override
//                public void onProgress(String s) {
//                    Log.d(TAG, "progress : " + s);
//                }
//
//                @Override
//                public void onStart() {
//                    Log.d(TAG, "Started command : ffmpeg " + command);
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.d(TAG, "Finished command : ffmpeg " + command);
//
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        }
//    }

    private void showUnsupportedExceptionDialog() {
//        Toast.makeText(mContext, "Error converting file", Toast.LENGTH_SHORT).show();
    }

//    public interface CompletionHandler {
//        void videoEncodingDidComplete(Error error);
//    }
//
//    // Constants
//
//    private static final String TAG = "VideoConverter";
//    private static final boolean VERBOSE = true;           // lots of logging
//
//    // parameters for the encoder
//    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
//    private static final int FRAME_RATE = 15;               // 15fps
//    private static final int CAPTURE_RATE = 15;               // 15fps
//    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
//    private static final int CHANNEL_COUNT = 1;
//    private static final int SAMPLE_RATE = 128000;
//    private static final int TIMEOUT_USEC = 10000;
//
//    // size of a frame, in pixels
//    private int mWidth = -1;
//    private int mHeight = -1;
//
//    // bit rate, in bits per second
//    private int mBitRate = -1;
//
//    // encoder / muxer state
//    private MediaCodec mDecoder;
//    private MediaCodec mEncoder;
//    private MediaMuxer mMuxer;
//    private int mTrackIndex;
//    private boolean mMuxerStarted;
//
//    /**
//     * Starts encoding process
//     */
//    public void convertVideo(String mediaFilePath, String destinationFilePath, CompletionHandler handler) {
//
//        // TODO: Make configurable
////        mWidth = 480;
////        mHeight = 360;
//        mWidth = 512;
//        mHeight = 288;
//        mBitRate = 500000;
//
//        try {
//
//            if ((mWidth % 16) != 0 || (mHeight % 16) != 0) {
//                Log.e(TAG, "Width or Height not multiple of 16");
//                Error e = new Error("Width and height must be a multiple of 16");
//                handler.videoEncodingDidComplete(e);
//                return;
//            }
//
//            // prep the decoder and the encoder
//            prepareEncoderDecoder(destinationFilePath);
//
//            // load file
//            File file = new File(mediaFilePath);
//            byte[] fileData = readContentIntoByteArray(file);
//
//            // fill up the input buffer
//            fillInputBuffer(fileData);
//
//            // encode buffer
//            encode();
//
//        } catch (Exception ex) {
//            Log.e(TAG, ex.toString());
//            ex.printStackTrace();
//
//        } finally {
//
//            // release encoder and muxer
//            releaseEncoder();
//            Log.e("Conversion", "Finished");
//        }
//    }
//
//    /**
//     * Configures encoder and muxer state
//     */
//
//    private void prepareEncoderDecoder(String outputPath) throws Exception {
//
//        // create decoder to read in the file data
//        mDecoder = MediaCodec.createDecoderByType(MIME_TYPE);
//
//        // create encoder to encode the file data into a new format
//        MediaCodecInfo info = selectCodec(MIME_TYPE);
//        int colorFormat = selectColorFormat(info, MIME_TYPE);
//
//        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
//
//        mEncoder = MediaCodec.createByCodecName(info.getName());
//        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mEncoder.start();
//
//        // Create a MediaMuxer for saving the data
//        mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//
//        mTrackIndex = -1;
//        mMuxerStarted = false;
//    }
//
//    /**
//     * Releases encoder resources.  May be called after partial / failed initialization.
//     */
//    private void releaseEncoder() {
//
//        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
//
//        if (mEncoder != null) {
//            mEncoder.stop();
//            mEncoder.release();
//            mEncoder = null;
//        }
//
//        if (mMuxer != null) {
////            mMuxer.stop();
////            mMuxer.release();
////            mMuxer = null;
//        }
//    }
//
//    private void fillInputBuffer(byte[] data) {
//
//        boolean inputDone = false;
//        int processedDataSize = 0;
//        int frameIndex = 0;
//
//        Log.d(TAG, "[fillInputBuffer] Buffer load start");
//
//        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
//
//        while (!inputDone) {
//
//            int inputBufferIndex = mEncoder.dequeueInputBuffer(10000);
//            if (inputBufferIndex >= 0) {
//
//                if (processedDataSize >= data.length) {
//
//                    mEncoder.queueInputBuffer(inputBufferIndex, 0, 0, computePresentationTime(frameIndex), MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                    inputDone = true;
//                    Log.d(TAG, "[fillInputBuffer] Buffer load complete");
//
//                } else {
//
//                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//
//                    int limit = inputBuffer.capacity();
//                    int pos = frameIndex * limit;
//                    byte[] subData = new byte[limit];
//                    System.arraycopy(data, pos, subData, 0, limit);
//
//                    inputBuffer.clear();
//                    inputBuffer.put(subData);
//
//                    Log.d(TAG, "[encode] call queueInputBuffer");
//                    mDecoder.queueInputBuffer(inputBufferIndex, 0, subData.length, computePresentationTime(frameIndex), MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
//                    Log.d(TAG, "[encode] did call queueInputBuffer");
//
//                    Log.d(TAG, "[encode] Loaded frame " + frameIndex + " into buffer");
//
//                    frameIndex++;
//                }
//            }
//        }
//    }
//
//    private void encode() throws Exception {
//
//        // get buffer info
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//
//        // start encoding
//        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
//
//        while (true) {
//
//            int encoderStatus = mEncoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
//
//            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
//
//                // no output available yet
//                if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
//                break;
//
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//
//                // not expected for an encoder
//                encoderOutputBuffers = mEncoder.getOutputBuffers();
//
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//
//                // should happen before receiving buffers, and should only happen once
//                if (!mMuxerStarted) {
//
//                    MediaFormat newFormat = mEncoder.getOutputFormat();
//                    Log.d(TAG, "encoder output format changed: " + newFormat);
//
//                    // now that we have the Magic Goodies, start the muxer
//                    mTrackIndex = mMuxer.addTrack(newFormat);
//                    mMuxer.start();
//                    mMuxerStarted = true;
//                }
//
//            } else if (encoderStatus > 0) {
//
//                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
//
//                if (encodedData == null) {
//                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
//                }
//
//                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
//                    bufferInfo.size = 0;
//                }
//
//                if (bufferInfo.size != 0) {
//
//                    if (!mMuxerStarted) {
//                        throw new RuntimeException("muxer hasn't started");
//                    }
//
//                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
//                    encodedData.position(bufferInfo.offset);
//                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
//
//                    mMuxer.writeSampleData(mTrackIndex, encodedData, bufferInfo);
//                    if (VERBOSE) Log.d(TAG, "sent " + bufferInfo.size + " bytes to muxer");
//                }
//
//                mEncoder.releaseOutputBuffer(encoderStatus, false);
//
//                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    if (VERBOSE) Log.d(TAG, "end of stream reached");
//                    break;      // out of while
//                }
//            }
//        }
//    }
//
//    private byte[] readContentIntoByteArray(File file) throws Exception
//    {
//        FileInputStream fileInputStream = null;
//        byte[] bFile = new byte[(int) file.length()];
//
//        //convert file into array of bytes
//        fileInputStream = new FileInputStream(file);
//        fileInputStream.read(bFile);
//        fileInputStream.close();
//
//        return bFile;
//    }
//
//    /**
//     * Returns the first codec capable of encoding the specified MIME type, or null if no
//     * match was found.
//     */
//    private static MediaCodecInfo selectCodec(String mimeType) {
//        int numCodecs = MediaCodecList.getCodecCount();
//        for (int i = 0; i < numCodecs; i++) {
//            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
//            if (!codecInfo.isEncoder()) {
//                continue;
//            }
//            String[] types = codecInfo.getSupportedTypes();
//            for (int j = 0; j < types.length; j++) {
//                if (types[j].equalsIgnoreCase(mimeType)) {
//                    return codecInfo;
//                }
//            }
//        }
//        return null;
//    }
//
//    private static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
//        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
//        for (int i = 0; i < capabilities.colorFormats.length; i++) {
//            int colorFormat = capabilities.colorFormats[i];
//            if (isRecognizedFormat(colorFormat)) {
//                return colorFormat;
//            }
//        }
//
//        return 0;   // not reached
//    }
//
//    private static boolean isRecognizedFormat(int colorFormat) {
//        switch (colorFormat) {
//            // these are the formats we know how to handle for this test
//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
//            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    /**
//     * Generates the presentation time for frame N, in microseconds.
//     */
//    private static long computePresentationTime(int frameIndex) {
//        return 132 + frameIndex * 1000000 / FRAME_RATE;
//    }

}
