//package br.inatel.icc.gigasecurity.gigamonitor.util;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.media.MediaCodec;
//import android.media.MediaCodecInfo;
//import android.media.MediaCodecList;
//import android.media.MediaExtractor;
//import android.media.MediaFormat;
//import android.media.MediaMetadataRetriever;
//import android.media.MediaMuxer;
//import android.os.Environment;
//import android.util.Log;
//import android.view.Surface;
//import android.view.SurfaceView;
//import android.widget.Toast;
//
////import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
////import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
////import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
////import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
////import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.concurrent.atomic.AtomicReference;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//
///**
// * Created by zappts on 04/08/17.
// */
//
//public class ExtractDecodeEditEncodeMuxTest extends Thread {
//
//    private static final String TAG = ExtractDecodeEditEncodeMuxTest.class.getSimpleName();
//    private static final boolean VERBOSE = false; // lots of logging
//
//    /** How long to wait for the next buffer to become available. */
//    private static final int TIMEOUT_USEC = 10000;
//
//    /** Where to output the test files. */
//    private static final File OUTPUT_FILENAME_DIR = Environment.getExternalStorageDirectory();
//    // parameters for the video encoder
//    // H.264 Advanced Video Coding
//    private static final String OUTPUT_VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
//    private static final int OUTPUT_VIDEO_BIT_RATE = 2000000;   // 2Mbps
//    private static final int OUTPUT_VIDEO_FRAME_RATE = 15;      // 15fps
//    private static final int OUTPUT_VIDEO_IFRAME_INTERVAL = 10; // 10 seconds between I-frames
//    private static final int OUTPUT_VIDEO_COLOR_FORMAT =
//    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
//
//    // parameters for the audio encoder
//    // Advanced Audio Coding
//    private static final String OUTPUT_AUDIO_MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC;
//    private static final int OUTPUT_AUDIO_CHANNEL_COUNT = 2;    // Must match the input stream.
//    private static final int OUTPUT_AUDIO_BIT_RATE = 128 * 1024;
//    private static final int OUTPUT_AUDIO_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectHE;
//    private static final int OUTPUT_AUDIO_SAMPLE_RATE_HZ = 44100; // Must match the input stream.
//        /**
// 83     * Used for editing the frames.
// 84     *
// 85     * <p>Swaps green and blue channels by storing an RBGA color in an RGBA buffer.
// 86     */
//    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
//            "precision mediump float;\n" +
//            "varying vec2 vTextureCoord;\n" +
//            "uniform samplerExternalOES sTexture;\n" +
//            "void main() {\n" +
//            "  gl_FragColor = texture2D(sTexture, vTextureCoord).rbga;\n" +
//            "}\n";
//
//        /** Whether to copy the video from the test video. */
//    private boolean mCopyVideo;
//    /** Whether to copy the audio from the test video. */
//    private boolean mCopyAudio;
//    /** Whether to verify the audio format. */
//    private boolean mVerifyAudioFormat;
//    /** Width of the output frames. */
//    private int mWidth = -1;
//    /** Height of the output frames. */
//    private int mHeight = -1;
//    /** The raw resource used as the input file. */
//    private int mSourceResId;
//    /** The destination file for the encoded output. */
//    private String mOutputFile;
//
//    public void testExtractDecodeEditEncodeMuxQCIF() throws Throwable {
//         setSize(176, 144);
//         setSource(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//         setCopyVideo();
//         TestWrapper.runTest(this);
//    }
//
//    public void testExtractDecodeEditEncodeMuxQVGA() throws Throwable {
//        setSize(320, 240);
//        setSource(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//        setCopyVideo();
//        TestWrapper.runTest(this);
//    }
//    public void testExtractDecodeEditEncodeMux720p() throws Throwable {
//        setSize(1280, 720);
//        setSource(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//        setCopyVideo();
//        TestWrapper.runTest(this);
//    }
//
//    public void testExtractDecodeEditEncodeMuxAudio() throws Throwable {
//        setSize(1280, 720);
//        setSource(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//        setCopyAudio();
//        setVerifyAudioFormat();
//        TestWrapper.runTest(this);
//    }
//
//    public void testExtractDecodeEditEncodeMuxAudioVideo() throws Throwable {
//        setSize(1280, 720);
//        setSource(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//        setCopyAudio();
//        setCopyVideo();
//        setVerifyAudioFormat();
//        TestWrapper.runTest(this);
//    }
//
//    /** Wraps testExtractDecodeEditEncodeMux() */
//    private static class TestWrapper implements Runnable {
//    private Throwable mThrowable;
//    private ExtractDecodeEditEncodeMuxTest mTest;
//    private TestWrapper(ExtractDecodeEditEncodeMuxTest test) {
//        mTest = test;
//    }
//
//    @Override
//    public void run() {
//       try {
//          mTest.extractDecodeEditEncodeMux();
//       } catch (Throwable th) {
//          mThrowable = th;
//       }
//    }
//
//        169        /**
// 170         * Entry point.
// 171         */
//        172        public static void runTest(ExtractDecodeEditEncodeMuxTest test) throws Throwable {
//            173            test.setOutputFile();
//            174            TestWrapper wrapper = new TestWrapper(test);
//            175            Thread th = new Thread(wrapper, "codec test");
//            176            th.start();
//            177            th.join();
//            178            if (wrapper.mThrowable != null) {
//                179                throw wrapper.mThrowable;
//                180            }
//            181        }
//182    }
//183
//        184    /**
// 185     * Sets the test to copy the video stream.
// 186     */
//        187    private void setCopyVideo() {
//        188        mCopyVideo = true;
//        189    }
//190
//        191    /**
// 192     * Sets the test to copy the video stream.
// 193     */
//        194    private void setCopyAudio() {
//        195        mCopyAudio = true;
//        196    }
//197
//        198    /**
// 199     * Sets the test to verify the output audio format.
// 200     */
//        201    private void setVerifyAudioFormat() {
//        202        mVerifyAudioFormat = true;
//        203    }
//204
//        205    /**
// 206     * Sets the desired frame size.
// 207     */
//        208    private void setSize(int width, int height) {
//        209        if ((width % 16) != 0 || (height % 16) != 0) {
//            210            Log.w(TAG, "WARNING: width or height not multiple of 16");
//            211        }
//        212        mWidth = width;
//        213        mHeight = height;
//        214    }
//215
//        216    /**
// 217     * Sets the raw resource used as the source video.
// 218     */
//        219    private void setSource(int resId) {
//        220        mSourceResId = resId;
//        221    }
//222
//        223    /**
// 224     * Sets the name of the output file based on the other parameters.
// 225     *
// 226     * <p>Must be called after {@link #setSize(int, int)} and {@link #setSource(int)}.
// 227     */
//        228    private void setOutputFile() {
//        229        StringBuilder sb = new StringBuilder();
//        230        sb.append(OUTPUT_FILENAME_DIR.getAbsolutePath());
//        231        sb.append("/cts-media-");
//        232        sb.append(getClass().getSimpleName());
//        233        assertTrue("should have called setSource() first", mSourceResId != -1);
//        234        sb.append('-');
//        235        sb.append(mSourceResId);
//        236        if (mCopyVideo) {
//            237            assertTrue("should have called setSize() first", mWidth != -1);
//            238            assertTrue("should have called setSize() first", mHeight != -1);
//            239            sb.append('-');
//            240            sb.append("video");
//            241            sb.append('-');
//            242            sb.append(mWidth);
//            243            sb.append('x');
//            244            sb.append(mHeight);
//            245        }
//        246        if (mCopyAudio) {
//            247            sb.append('-');
//            248            sb.append("audio");
//            249        }
//        250        sb.append(".mp4");
//        251        mOutputFile = sb.toString();
//        252    }
//253
//        254    /**
// 255     * Tests encoding and subsequently decoding video from frames generated into a buffer.
// 256     * <p>
// 257     * We encode several frames of a video test pattern using MediaCodec, then decode the output
// 258     * with MediaCodec and do some simple checks.
// 259     */
//        260    private void extractDecodeEditEncodeMux() throws Exception {
//        261        // Exception that may be thrown during release.
//        262        Exception exception = null;
//        263
//        264        MediaCodecList mcl = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
//        265
//        266        // We avoid the device-specific limitations on width and height by using values
//        267        // that are multiples of 16, which all tested devices seem to be able to handle.
//        268        MediaFormat outputVideoFormat =
//                269                MediaFormat.createVideoFormat(OUTPUT_VIDEO_MIME_TYPE, mWidth, mHeight);
//        270
//        271        // Set some properties. Failing to specify some of these can cause the MediaCodec
//        272        // configure() call to throw an unhelpful exception.
//        273        outputVideoFormat.setInteger(
//                274                MediaFormat.KEY_COLOR_FORMAT, OUTPUT_VIDEO_COLOR_FORMAT);
//        275        outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, OUTPUT_VIDEO_BIT_RATE);
//        276        outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, OUTPUT_VIDEO_FRAME_RATE);
//        277        outputVideoFormat.setInteger(
//                278                MediaFormat.KEY_I_FRAME_INTERVAL, OUTPUT_VIDEO_IFRAME_INTERVAL);
//        279        if (VERBOSE) Log.d(TAG, "video format: " + outputVideoFormat);
//        280
//        281        String videoEncoderName = mcl.findEncoderForFormat(outputVideoFormat);
//        282        if (videoEncoderName == null) {
//            283            // Don't fail CTS if they don't have an AVC codec (not here, anyway).
//            284            Log.e(TAG, "Unable to find an appropriate codec for " + outputVideoFormat);
//            285            return;
//            286        }
//        287        if (VERBOSE) Log.d(TAG, "video found codec: " + videoEncoderName);
//        288
//        289        MediaFormat outputAudioFormat =
//                290                MediaFormat.createAudioFormat(
//                291                        OUTPUT_AUDIO_MIME_TYPE, OUTPUT_AUDIO_SAMPLE_RATE_HZ,
//                292                        OUTPUT_AUDIO_CHANNEL_COUNT);
//        293        outputAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, OUTPUT_AUDIO_BIT_RATE);
//        294        outputAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, OUTPUT_AUDIO_AAC_PROFILE);
//        295
//        296        String audioEncoderName = mcl.findEncoderForFormat(outputAudioFormat);
//        297        if (audioEncoderName == null) {
//            298            // Don't fail CTS if they don't have an AAC codec (not here, anyway).
//            299            Log.e(TAG, "Unable to find an appropriate codec for " + outputAudioFormat);
//            300            return;
//            301        }
//        302        if (VERBOSE) Log.d(TAG, "audio found codec: " + audioEncoderName);
//        303
//        304        MediaExtractor videoExtractor = null;
//        305        MediaExtractor audioExtractor = null;
//        306        OutputSurface outputSurface = null;
//        307        MediaCodec videoDecoder = null;
//        308        MediaCodec audioDecoder = null;
//        309        MediaCodec videoEncoder = null;
//        310        MediaCodec audioEncoder = null;
//        311        MediaMuxer muxer = null;
//        312
//        313        InputSurface inputSurface = null;
//        314
//        315        try {
//            316            if (mCopyVideo) {
//                317                videoExtractor = createExtractor();
//                318                int videoInputTrack = getAndSelectVideoTrackIndex(videoExtractor);
//                319                assertTrue("missing video track in test video", videoInputTrack != -1);
//                320                MediaFormat inputFormat = videoExtractor.getTrackFormat(videoInputTrack);
//                321
//                322                // Create a MediaCodec for the desired codec, then configure it as an encoder with
//                323                // our desired properties. Request a Surface to use for input.
//                324                AtomicReference<Surface> inputSurfaceReference = new AtomicReference<Surface>();
//                325                videoEncoder = createVideoEncoder(
//                        326                        videoEncoderName, outputVideoFormat, inputSurfaceReference);
//                327                inputSurface = new InputSurface(inputSurfaceReference.get());
//                328                inputSurface.makeCurrent();
//                329                // Create a MediaCodec for the decoder, based on the extractor's format.
//                330                outputSurface = new OutputSurface();
//                331                outputSurface.changeFragmentShader(FRAGMENT_SHADER);
//                332                videoDecoder = createVideoDecoder(mcl, inputFormat, outputSurface.getSurface());
//                333            }
//            334
//            335            if (mCopyAudio) {
//                336                audioExtractor = createExtractor();
//                337                int audioInputTrack = getAndSelectAudioTrackIndex(audioExtractor);
//                338                assertTrue("missing audio track in test video", audioInputTrack != -1);
//                339                MediaFormat inputFormat = audioExtractor.getTrackFormat(audioInputTrack);
//                340
//                341                // Create a MediaCodec for the desired codec, then configure it as an encoder with
//                342                // our desired properties. Request a Surface to use for input.
//                343                audioEncoder = createAudioEncoder(audioEncoderName, outputAudioFormat);
//                344                // Create a MediaCodec for the decoder, based on the extractor's format.
//                345                audioDecoder = createAudioDecoder(mcl, inputFormat);
//                346            }
//            347
//            348            // Creates a muxer but do not start or add tracks just yet.
//            349            muxer = createMuxer();
//            350
//            351            doExtractDecodeEditEncodeMux(
//                    352                    videoExtractor,
//                    353                    audioExtractor,
//                    354                    videoDecoder,
//                    355                    videoEncoder,
//                    356                    audioDecoder,
//                    357                    audioEncoder,
//                    358                    muxer,
//                    359                    inputSurface,
//                    360                    outputSurface);
//            361        } finally {
//            362            if (VERBOSE) Log.d(TAG, "releasing extractor, decoder, encoder, and muxer");
//            363            // Try to release everything we acquired, even if one of the releases fails, in which
//            364            // case we save the first exception we got and re-throw at the end (unless something
//            365            // other exception has already been thrown). This guarantees the first exception thrown
//            366            // is reported as the cause of the error, everything is (attempted) to be released, and
//            367            // all other exceptions appear in the logs.
//            368            try {
//                369                if (videoExtractor != null) {
//                    370                    videoExtractor.release();
//                    371                }
//                372            } catch(Exception e) {
//                373                Log.e(TAG, "error while releasing videoExtractor", e);
//                374                if (exception == null) {
//                    375                    exception = e;
//                    376                }
//                377            }
//            378            try {
//                379                if (audioExtractor != null) {
//                    380                    audioExtractor.release();
//                    381                }
//                382            } catch(Exception e) {
//                383                Log.e(TAG, "error while releasing audioExtractor", e);
//                384                if (exception == null) {
//                    385                    exception = e;
//                    386                }
//                387            }
//            388            try {
//                389                if (videoDecoder != null) {
//                    390                    videoDecoder.stop();
//                    391                    videoDecoder.release();
//                    392                }
//                393            } catch(Exception e) {
//                394                Log.e(TAG, "error while releasing videoDecoder", e);
//                395                if (exception == null) {
//                    396                    exception = e;
//                    397                }
//                398            }
//            399            try {
//                400                if (outputSurface != null) {
//                    401                    outputSurface.release();
//                    402                }
//                403            } catch(Exception e) {
//                404                Log.e(TAG, "error while releasing outputSurface", e);
//                405                if (exception == null) {
//                    406                    exception = e;
//                    407                }
//                408            }
//            409            try {
//                410                if (videoEncoder != null) {
//                    411                    videoEncoder.stop();
//                    412                    videoEncoder.release();
//                    413                }
//                414            } catch(Exception e) {
//                415                Log.e(TAG, "error while releasing videoEncoder", e);
//                416                if (exception == null) {
//                    417                    exception = e;
//                    418                }
//                419            }
//            420            try {
//                421                if (audioDecoder != null) {
//                    422                    audioDecoder.stop();
//                    423                    audioDecoder.release();
//                    424                }
//                425            } catch(Exception e) {
//                426                Log.e(TAG, "error while releasing audioDecoder", e);
//                427                if (exception == null) {
//                    428                    exception = e;
//                    429                }
//                430            }
//            431            try {
//                432                if (audioEncoder != null) {
//                    433                    audioEncoder.stop();
//                    434                    audioEncoder.release();
//                    435                }
//                436            } catch(Exception e) {
//                437                Log.e(TAG, "error while releasing audioEncoder", e);
//                438                if (exception == null) {
//                    439                    exception = e;
//                    440                }
//                441            }
//            442            try {
//                443                if (muxer != null) {
//                    444                    muxer.stop();
//                    445                    muxer.release();
//                    446                }
//                447            } catch(Exception e) {
//                448                Log.e(TAG, "error while releasing muxer", e);
//                449                if (exception == null) {
//                    450                    exception = e;
//                    451                }
//                452            }
//            453            try {
//                454                if (inputSurface != null) {
//                    455                    inputSurface.release();
//                    456                }
//                457            } catch(Exception e) {
//                458                Log.e(TAG, "error while releasing inputSurface", e);
//                459                if (exception == null) {
//                    460                    exception = e;
//                    461                }
//                462            }
//            463        }
//        464        if (exception != null) {
//            465            throw exception;
//            466        }
//        467
//        468        MediaExtractor mediaExtractor = null;
//        469        try {
//            470            mediaExtractor = new MediaExtractor();
//            471            mediaExtractor.setDataSource(mOutputFile);
//            472
//            473            assertEquals("incorrect number of tracks", (mCopyAudio ? 1 : 0) + (mCopyVideo ? 1 : 0),
//                    474                    mediaExtractor.getTrackCount());
//            475            if (mVerifyAudioFormat) {
//                476                boolean foundAudio = false;
//                477                for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
//                    478                    MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
//                    479                    if (isAudioFormat(trackFormat)) {
//                        480                        foundAudio = true;
//                        481                        int expectedSampleRate = OUTPUT_AUDIO_SAMPLE_RATE_HZ;
//                        482
//                        483                        // SBR mode halves the sample rate in the format.
//                        484                        if (OUTPUT_AUDIO_AAC_PROFILE ==
//                                485                                MediaCodecInfo.CodecProfileLevel.AACObjectHE) {
//                            486                            expectedSampleRate /= 2;
//                            487                        }
//                        488                        assertEquals("sample rates should match", expectedSampleRate,
//                                489                                trackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
//                        490                    }
//                    491                }
//                492
//                493                assertTrue("output should have an audio track", foundAudio || !mCopyAudio);
//                494            }
//            495        } catch (IOException e) {
//            496            throw new IllegalStateException("exception verifying output file", e);
//            497        } finally {
//            498            if (mediaExtractor != null) {
//                499                mediaExtractor.release();
//                500            }
//            501        }
//        502
//        503        // TODO: Check the generated output file's video format and sample data.
//        504    }
//505
//        506    /**
// 507     * Creates an extractor that reads its frames from {@link #mSourceResId}.
// 508     */
//        509    private MediaExtractor createExtractor() throws IOException {
//        510        MediaExtractor extractor;
//        511        AssetFileDescriptor srcFd = getContext().getResources().openRawResourceFd(mSourceResId);
//        512        extractor = new MediaExtractor();
//        513        extractor.setDataSource(srcFd.getFileDescriptor(), srcFd.getStartOffset(),
//                514                srcFd.getLength());
//        515        return extractor;
//        516    }
//517
//        518    /**
// 519     * Creates a decoder for the given format, which outputs to the given surface.
// 520     *
// 521     * @param inputFormat the format of the stream to decode
// 522     * @param surface into which to decode the frames
// 523     */
//        524    private MediaCodec createVideoDecoder(
//525            MediaCodecList mcl, MediaFormat inputFormat, Surface surface) throws IOException {
//        526        MediaCodec decoder = MediaCodec.createByCodecName(mcl.findDecoderForFormat(inputFormat));
//        527        decoder.configure(inputFormat, surface, null, 0);
//        528        decoder.start();
//        529        return decoder;
//        530    }
//531
//        532    /**
// 533     * Creates an encoder for the given format using the specified codec, taking input from a
// 534     * surface.
// 535     *
// 536     * <p>The surface to use as input is stored in the given reference.
// 537     *
// 538     * @param codecInfo of the codec to use
// 539     * @param format of the stream to be produced
// 540     * @param surfaceReference to store the surface to use as input
// 541     */
//        542    private MediaCodec createVideoEncoder(
//543            String codecName,
//544            MediaFormat format,
//545            AtomicReference<Surface> surfaceReference)
//546            throws IOException {
//        547        MediaCodec encoder = MediaCodec.createByCodecName(codecName);
//        548        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        549        // Must be called before start() is.
//        550        surfaceReference.set(encoder.createInputSurface());
//        551        encoder.start();
//        552        return encoder;
//        553    }
//554
//        555    /**
// 556     * Creates a decoder for the given format.
// 557     *
// 558     * @param inputFormat the format of the stream to decode
// 559     */
//        560    private MediaCodec createAudioDecoder(
//561            MediaCodecList mcl, MediaFormat inputFormat) throws IOException {
//        562        MediaCodec decoder = MediaCodec.createByCodecName(mcl.findDecoderForFormat(inputFormat));
//        563        decoder.configure(inputFormat, null, null, 0);
//        564        decoder.start();
//        565        return decoder;
//        566    }
//567
//        568    /**
// 569     * Creates an encoder for the given format using the specified codec.
// 570     *
// 571     * @param codecInfo of the codec to use
// 572     * @param format of the stream to be produced
// 573     */
//        574    private MediaCodec createAudioEncoder(String codecName, MediaFormat format)
//575            throws IOException {
//        576        MediaCodec encoder = MediaCodec.createByCodecName(codecName);
//        577        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        578        encoder.start();
//        579        return encoder;
//        580    }
//581
//        582    /**
// 583     * Creates a muxer to write the encoded frames.
// 584     *
// 585     * <p>The muxer is not started as it needs to be started only after all streams have been added.
// 586     */
//        587    private MediaMuxer createMuxer() throws IOException {
//        588        return new MediaMuxer(mOutputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//        589    }
//590
//        591    private int getAndSelectVideoTrackIndex(MediaExtractor extractor) {
//        592        for (int index = 0; index < extractor.getTrackCount(); ++index) {
//            593            if (VERBOSE) {
//                594                Log.d(TAG, "format for track " + index + " is "
//                        595                        + getMimeTypeFor(extractor.getTrackFormat(index)));
//                596            }
//            597            if (isVideoFormat(extractor.getTrackFormat(index))) {
//                598                extractor.selectTrack(index);
//                599                return index;
//                600            }
//            601        }
//        602        return -1;
//        603    }
//604
//        605    private int getAndSelectAudioTrackIndex(MediaExtractor extractor) {
//        606        for (int index = 0; index < extractor.getTrackCount(); ++index) {
//            607            if (VERBOSE) {
//                608                Log.d(TAG, "format for track " + index + " is "
//                        609                        + getMimeTypeFor(extractor.getTrackFormat(index)));
//                610            }
//            611            if (isAudioFormat(extractor.getTrackFormat(index))) {
//                612                extractor.selectTrack(index);
//                613                return index;
//                614            }
//            615        }
//        616        return -1;
//        617    }
//618
//        619    /**
// 620     * Does the actual work for extracting, decoding, encoding and muxing.
// 621     */
//        622    private void doExtractDecodeEditEncodeMux(
//623            MediaExtractor videoExtractor,
//624            MediaExtractor audioExtractor,
//625            MediaCodec videoDecoder,
//626            MediaCodec videoEncoder,
//627            MediaCodec audioDecoder,
//628            MediaCodec audioEncoder,
//629            MediaMuxer muxer,
//630            InputSurface inputSurface,
//631            OutputSurface outputSurface) {
//        632        ByteBuffer[] videoDecoderInputBuffers = null;
//        633        ByteBuffer[] videoDecoderOutputBuffers = null;
//        634        ByteBuffer[] videoEncoderOutputBuffers = null;
//        635        MediaCodec.BufferInfo videoDecoderOutputBufferInfo = null;
//        636        MediaCodec.BufferInfo videoEncoderOutputBufferInfo = null;
//        637        if (mCopyVideo) {
//            638            videoDecoderInputBuffers = videoDecoder.getInputBuffers();
//            639            videoDecoderOutputBuffers = videoDecoder.getOutputBuffers();
//            640            videoEncoderOutputBuffers = videoEncoder.getOutputBuffers();
//            641            videoDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
//            642            videoEncoderOutputBufferInfo = new MediaCodec.BufferInfo();
//            643        }
//        644        ByteBuffer[] audioDecoderInputBuffers = null;
//        645        ByteBuffer[] audioDecoderOutputBuffers = null;
//        646        ByteBuffer[] audioEncoderInputBuffers = null;
//        647        ByteBuffer[] audioEncoderOutputBuffers = null;
//        648        MediaCodec.BufferInfo audioDecoderOutputBufferInfo = null;
//        649        MediaCodec.BufferInfo audioEncoderOutputBufferInfo = null;
//        650        if (mCopyAudio) {
//            651            audioDecoderInputBuffers = audioDecoder.getInputBuffers();
//            652            audioDecoderOutputBuffers =  audioDecoder.getOutputBuffers();
//            653            audioEncoderInputBuffers = audioEncoder.getInputBuffers();
//            654            audioEncoderOutputBuffers = audioEncoder.getOutputBuffers();
//            655            audioDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
//            656            audioEncoderOutputBufferInfo = new MediaCodec.BufferInfo();
//            657        }
//        658        // We will get these from the decoders when notified of a format change.
//        659        MediaFormat decoderOutputVideoFormat = null;
//        660        MediaFormat decoderOutputAudioFormat = null;
//        661        // We will get these from the encoders when notified of a format change.
//        662        MediaFormat encoderOutputVideoFormat = null;
//        663        MediaFormat encoderOutputAudioFormat = null;
//        664        // We will determine these once we have the output format.
//        665        int outputVideoTrack = -1;
//        666        int outputAudioTrack = -1;
//        667        // Whether things are done on the video side.
//        668        boolean videoExtractorDone = false;
//        669        boolean videoDecoderDone = false;
//        670        boolean videoEncoderDone = false;
//        671        // Whether things are done on the audio side.
//        672        boolean audioExtractorDone = false;
//        673        boolean audioDecoderDone = false;
//        674        boolean audioEncoderDone = false;
//        675        // The audio decoder output buffer to process, -1 if none.
//        676        int pendingAudioDecoderOutputBufferIndex = -1;
//        677
//        678        boolean muxing = false;
//        679
//        680        int videoExtractedFrameCount = 0;
//        681        int videoDecodedFrameCount = 0;
//        682        int videoEncodedFrameCount = 0;
//        683
//        684        int audioExtractedFrameCount = 0;
//        685        int audioDecodedFrameCount = 0;
//        686        int audioEncodedFrameCount = 0;
//        687
//        688        while ((mCopyVideo && !videoEncoderDone) || (mCopyAudio && !audioEncoderDone)) {
//            689            if (VERBOSE) {
//                690                Log.d(TAG, String.format(
//                        691                        "loop: "
//                        692
//                        693                        + "V(%b){"
//                        694                        + "extracted:%d(done:%b) "
//                        695                        + "decoded:%d(done:%b) "
//                        696                        + "encoded:%d(done:%b)} "
//                        697
//                        698                        + "A(%b){"
//                        699                        + "extracted:%d(done:%b) "
//                        700                        + "decoded:%d(done:%b) "
//                        701                        + "encoded:%d(done:%b) "
//                        702                        + "pending:%d} "
//                        703
//                        704                        + "muxing:%b(V:%d,A:%d)",
//                        705
//                        706                        mCopyVideo,
//                        707                        videoExtractedFrameCount, videoExtractorDone,
//                        708                        videoDecodedFrameCount, videoDecoderDone,
//                        709                        videoEncodedFrameCount, videoEncoderDone,
//                        710
//                        711                        mCopyAudio,
//                        712                        audioExtractedFrameCount, audioExtractorDone,
//                        713                        audioDecodedFrameCount, audioDecoderDone,
//                        714                        audioEncodedFrameCount, audioEncoderDone,
//                        715                        pendingAudioDecoderOutputBufferIndex,
//                        716
//                        717                        muxing, outputVideoTrack, outputAudioTrack));
//                718            }
//            719
//            720            // Extract video from file and feed to decoder.
//            721            // Do not extract video if we have determined the output format but we are not yet
//            722            // ready to mux the frames.
//            723            while (mCopyVideo && !videoExtractorDone
//            724                    && (encoderOutputVideoFormat == null || muxing)) {
//                725                int decoderInputBufferIndex = videoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
//                726                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    727                    if (VERBOSE) Log.d(TAG, "no video decoder input buffer");
//                    728                    break;
//                    729                }
//                730                if (VERBOSE) {
//                    731                    Log.d(TAG, "video decoder: returned input buffer: " + decoderInputBufferIndex);
//                    732                }
//                733                ByteBuffer decoderInputBuffer = videoDecoderInputBuffers[decoderInputBufferIndex];
//                734                int size = videoExtractor.readSampleData(decoderInputBuffer, 0);
//                735                long presentationTime = videoExtractor.getSampleTime();
//                736                if (VERBOSE) {
//                    737                    Log.d(TAG, "video extractor: returned buffer of size " + size);
//                    738                    Log.d(TAG, "video extractor: returned buffer for time " + presentationTime);
//                    739                }
//                740                if (size >= 0) {
//                    741                    videoDecoder.queueInputBuffer(
//                            742                            decoderInputBufferIndex,
//                            743                            0,
//                            744                            size,
//                            745                            presentationTime,
//                            746                            videoExtractor.getSampleFlags());
//                    747                }
//                748                videoExtractorDone = !videoExtractor.advance();
//                749                if (videoExtractorDone) {
//                    750                    if (VERBOSE) Log.d(TAG, "video extractor: EOS");
//                    751                    videoDecoder.queueInputBuffer(
//                            752                            decoderInputBufferIndex,
//                            753                            0,
//                            754                            0,
//                            755                            0,
//                            756                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                    757                }
//                758                videoExtractedFrameCount++;
//                759                // We extracted a frame, let's try something else next.
//                760                break;
//                761            }
//            762
//            763            // Extract audio from file and feed to decoder.
//            764            // Do not extract audio if we have determined the output format but we are not yet
//            765            // ready to mux the frames.
//            766            while (mCopyAudio && !audioExtractorDone
//            767                    && (encoderOutputAudioFormat == null || muxing)) {
//                768                int decoderInputBufferIndex = audioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
//                769                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    770                    if (VERBOSE) Log.d(TAG, "no audio decoder input buffer");
//                    771                    break;
//                    772                }
//                773                if (VERBOSE) {
//                    774                    Log.d(TAG, "audio decoder: returned input buffer: " + decoderInputBufferIndex);
//                    775                }
//                776                ByteBuffer decoderInputBuffer = audioDecoderInputBuffers[decoderInputBufferIndex];
//                777                int size = audioExtractor.readSampleData(decoderInputBuffer, 0);
//                778                long presentationTime = audioExtractor.getSampleTime();
//                779                if (VERBOSE) {
//                    780                    Log.d(TAG, "audio extractor: returned buffer of size " + size);
//                    781                    Log.d(TAG, "audio extractor: returned buffer for time " + presentationTime);
//                    782                }
//                783                if (size >= 0) {
//                    784                    audioDecoder.queueInputBuffer(
//                            785                            decoderInputBufferIndex,
//                            786                            0,
//                            787                            size,
//                            788                            presentationTime,
//                            789                            audioExtractor.getSampleFlags());
//                    790                }
//                791                audioExtractorDone = !audioExtractor.advance();
//                792                if (audioExtractorDone) {
//                    793                    if (VERBOSE) Log.d(TAG, "audio extractor: EOS");
//                    794                    audioDecoder.queueInputBuffer(
//                            795                            decoderInputBufferIndex,
//                            796                            0,
//                            797                            0,
//                            798                            0,
//                            799                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                    800                }
//                801                audioExtractedFrameCount++;
//                802                // We extracted a frame, let's try something else next.
//                803                break;
//                804            }
//            805
//            806            // Poll output frames from the video decoder and feed the encoder.
//            807            while (mCopyVideo && !videoDecoderDone
//            808                    && (encoderOutputVideoFormat == null || muxing)) {
//                809                int decoderOutputBufferIndex =
//                        810                        videoDecoder.dequeueOutputBuffer(
//                        811                                videoDecoderOutputBufferInfo, TIMEOUT_USEC);
//                812                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    813                    if (VERBOSE) Log.d(TAG, "no video decoder output buffer");
//                    814                    break;
//                    815                }
//                816                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    817                    if (VERBOSE) Log.d(TAG, "video decoder: output buffers changed");
//                    818                    videoDecoderOutputBuffers = videoDecoder.getOutputBuffers();
//                    819                    break;
//                    820                }
//                821                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    822                    decoderOutputVideoFormat = videoDecoder.getOutputFormat();
//                    823                    if (VERBOSE) {
//                        824                        Log.d(TAG, "video decoder: output format changed: "
//                                825                                + decoderOutputVideoFormat);
//                        826                    }
//                    827                    break;
//                    828                }
//                829                if (VERBOSE) {
//                    830                    Log.d(TAG, "video decoder: returned output buffer: "
//                            831                            + decoderOutputBufferIndex);
//                    832                    Log.d(TAG, "video decoder: returned buffer of size "
//                            833                            + videoDecoderOutputBufferInfo.size);
//                    834                }
//                835                ByteBuffer decoderOutputBuffer =
//                        836                        videoDecoderOutputBuffers[decoderOutputBufferIndex];
//                837                if ((videoDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        838                        != 0) {
//                    839                    if (VERBOSE) Log.d(TAG, "video decoder: codec config buffer");
//                    840                    videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
//                    841                    break;
//                    842                }
//                843                if (VERBOSE) {
//                    844                    Log.d(TAG, "video decoder: returned buffer for time "
//                            845                            + videoDecoderOutputBufferInfo.presentationTimeUs);
//                    846                }
//                847                boolean render = videoDecoderOutputBufferInfo.size != 0;
//                848                videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, render);
//                849                if (render) {
//                    850                    if (VERBOSE) Log.d(TAG, "output surface: await new image");
//                    851                    outputSurface.awaitNewImage();
//                    852                    // Edit the frame and send it to the encoder.
//                    853                    if (VERBOSE) Log.d(TAG, "output surface: draw image");
//                    854                    outputSurface.drawImage();
//                    855                    inputSurface.setPresentationTime(
//                            856                            videoDecoderOutputBufferInfo.presentationTimeUs * 1000);
//                    857                    if (VERBOSE) Log.d(TAG, "input surface: swap buffers");
//                    858                    inputSurface.swapBuffers();
//                    859                    if (VERBOSE) Log.d(TAG, "video encoder: notified of new frame");
//                    860                }
//                861                if ((videoDecoderOutputBufferInfo.flags
//                862                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    863                    if (VERBOSE) Log.d(TAG, "video decoder: EOS");
//                    864                    videoDecoderDone = true;
//                    865                    videoEncoder.signalEndOfInputStream();
//                    866                }
//                867                videoDecodedFrameCount++;
//                868                // We extracted a pending frame, let's try something else next.
//                869                break;
//                870            }
//            871
//            872            // Poll output frames from the audio decoder.
//            873            // Do not poll if we already have a pending buffer to feed to the encoder.
//            874            while (mCopyAudio && !audioDecoderDone && pendingAudioDecoderOutputBufferIndex == -1
//            875                    && (encoderOutputAudioFormat == null || muxing)) {
//                876                int decoderOutputBufferIndex =
//                        877                        audioDecoder.dequeueOutputBuffer(
//                        878                                audioDecoderOutputBufferInfo, TIMEOUT_USEC);
//                879                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    880                    if (VERBOSE) Log.d(TAG, "no audio decoder output buffer");
//                    881                    break;
//                    882                }
//                883                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    884                    if (VERBOSE) Log.d(TAG, "audio decoder: output buffers changed");
//                    885                    audioDecoderOutputBuffers = audioDecoder.getOutputBuffers();
//                    886                    break;
//                    887                }
//                888                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    889                    decoderOutputAudioFormat = audioDecoder.getOutputFormat();
//                    890                    if (VERBOSE) {
//                        891                        Log.d(TAG, "audio decoder: output format changed: "
//                                892                                + decoderOutputAudioFormat);
//                        893                    }
//                    894                    break;
//                    895                }
//                896                if (VERBOSE) {
//                    897                    Log.d(TAG, "audio decoder: returned output buffer: "
//                            898                            + decoderOutputBufferIndex);
//                    899                }
//                900                if (VERBOSE) {
//                    901                    Log.d(TAG, "audio decoder: returned buffer of size "
//                            902                            + audioDecoderOutputBufferInfo.size);
//                    903                }
//                904                ByteBuffer decoderOutputBuffer =
//                        905                        audioDecoderOutputBuffers[decoderOutputBufferIndex];
//                906                if ((audioDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        907                        != 0) {
//                    908                    if (VERBOSE) Log.d(TAG, "audio decoder: codec config buffer");
//                    909                    audioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
//                    910                    break;
//                    911                }
//                912                if (VERBOSE) {
//                    913                    Log.d(TAG, "audio decoder: returned buffer for time "
//                            914                            + audioDecoderOutputBufferInfo.presentationTimeUs);
//                    915                }
//                916                if (VERBOSE) {
//                    917                    Log.d(TAG, "audio decoder: output buffer is now pending: "
//                            918                            + pendingAudioDecoderOutputBufferIndex);
//                    919                }
//                920                pendingAudioDecoderOutputBufferIndex = decoderOutputBufferIndex;
//                921                audioDecodedFrameCount++;
//                922                // We extracted a pending frame, let's try something else next.
//                923                break;
//                924            }
//            925
//            926            // Feed the pending decoded audio buffer to the audio encoder.
//            927            while (mCopyAudio && pendingAudioDecoderOutputBufferIndex != -1) {
//                928                if (VERBOSE) {
//                    929                    Log.d(TAG, "audio decoder: attempting to process pending buffer: "
//                            930                            + pendingAudioDecoderOutputBufferIndex);
//                    931                }
//                932                int encoderInputBufferIndex = audioEncoder.dequeueInputBuffer(TIMEOUT_USEC);
//                933                if (encoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    934                    if (VERBOSE) Log.d(TAG, "no audio encoder input buffer");
//                    935                    break;
//                    936                }
//                937                if (VERBOSE) {
//                    938                    Log.d(TAG, "audio encoder: returned input buffer: " + encoderInputBufferIndex);
//                    939                }
//                940                ByteBuffer encoderInputBuffer = audioEncoderInputBuffers[encoderInputBufferIndex];
//                941                int size = audioDecoderOutputBufferInfo.size;
//                942                long presentationTime = audioDecoderOutputBufferInfo.presentationTimeUs;
//                943                if (VERBOSE) {
//                    944                    Log.d(TAG, "audio decoder: processing pending buffer: "
//                            945                            + pendingAudioDecoderOutputBufferIndex);
//                    946                }
//                947                if (VERBOSE) {
//                    948                    Log.d(TAG, "audio decoder: pending buffer of size " + size);
//                    949                    Log.d(TAG, "audio decoder: pending buffer for time " + presentationTime);
//                    950                }
//                951                if (size >= 0) {
//                    952                    ByteBuffer decoderOutputBuffer =
//                            953                            audioDecoderOutputBuffers[pendingAudioDecoderOutputBufferIndex]
//                    954                                    .duplicate();
//                    955                    decoderOutputBuffer.position(audioDecoderOutputBufferInfo.offset);
//                    956                    decoderOutputBuffer.limit(audioDecoderOutputBufferInfo.offset + size);
//                    957                    encoderInputBuffer.position(0);
//                    958                    encoderInputBuffer.put(decoderOutputBuffer);
//                    959
//                    960                    audioEncoder.queueInputBuffer(
//                            961                            encoderInputBufferIndex,
//                            962                            0,
//                            963                            size,
//                            964                            presentationTime,
//                            965                            audioDecoderOutputBufferInfo.flags);
//                    966                }
//                967                audioDecoder.releaseOutputBuffer(pendingAudioDecoderOutputBufferIndex, false);
//                968                pendingAudioDecoderOutputBufferIndex = -1;
//                969                if ((audioDecoderOutputBufferInfo.flags
//                970                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    971                    if (VERBOSE) Log.d(TAG, "audio decoder: EOS");
//                    972                    audioDecoderDone = true;
//                    973                }
//                974                // We enqueued a pending frame, let's try something else next.
//                975                break;
//                976            }
//            977
//            978            // Poll frames from the video encoder and send them to the muxer.
//            979            while (mCopyVideo && !videoEncoderDone
//            980                    && (encoderOutputVideoFormat == null || muxing)) {
//                981                int encoderOutputBufferIndex = videoEncoder.dequeueOutputBuffer(
//                        982                        videoEncoderOutputBufferInfo, TIMEOUT_USEC);
//                983                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    984                    if (VERBOSE) Log.d(TAG, "no video encoder output buffer");
//                    985                    break;
//                    986                }
//                987                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    988                    if (VERBOSE) Log.d(TAG, "video encoder: output buffers changed");
//                    989                    videoEncoderOutputBuffers = videoEncoder.getOutputBuffers();
//                    990                    break;
//                    991                }
//                992                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    993                    if (VERBOSE) Log.d(TAG, "video encoder: output format changed");
//                    994                    if (outputVideoTrack >= 0) {
//                        995                        fail("video encoder changed its output format again?");
//                        996                    }
//                    997                    encoderOutputVideoFormat = videoEncoder.getOutputFormat();
//                    998                    break;
//                    999                }
//                1000                assertTrue("should have added track before processing output", muxing);
//                1001                if (VERBOSE) {
//                    1002                    Log.d(TAG, "video encoder: returned output buffer: "
//                            1003                            + encoderOutputBufferIndex);
//                    1004                    Log.d(TAG, "video encoder: returned buffer of size "
//                            1005                            + videoEncoderOutputBufferInfo.size);
//                    1006                }
//                1007                ByteBuffer encoderOutputBuffer =
//                        1008                        videoEncoderOutputBuffers[encoderOutputBufferIndex];
//                1009                if ((videoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        1010                        != 0) {
//                    1011                    if (VERBOSE) Log.d(TAG, "video encoder: codec config buffer");
//                    1012                    // Simply ignore codec config buffers.
//                    1013                    videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                    1014                    break;
//                    1015                }
//                1016                if (VERBOSE) {
//                    1017                    Log.d(TAG, "video encoder: returned buffer for time "
//                            1018                            + videoEncoderOutputBufferInfo.presentationTimeUs);
//                    1019                }
//                1020                if (videoEncoderOutputBufferInfo.size != 0) {
//                    1021                    muxer.writeSampleData(
//                            1022                            outputVideoTrack, encoderOutputBuffer, videoEncoderOutputBufferInfo);
//                    1023                }
//                1024                if ((videoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
//                        1025                        != 0) {
//                    1026                    if (VERBOSE) Log.d(TAG, "video encoder: EOS");
//                    1027                    videoEncoderDone = true;
//                    1028                }
//                1029                videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                1030                videoEncodedFrameCount++;
//                1031                // We enqueued an encoded frame, let's try something else next.
//                1032                break;
//                1033            }
//            1034
//            1035            // Poll frames from the audio encoder and send them to the muxer.
//            1036            while (mCopyAudio && !audioEncoderDone
//            1037                    && (encoderOutputAudioFormat == null || muxing)) {
//                1038                int encoderOutputBufferIndex = audioEncoder.dequeueOutputBuffer(
//                        1039                        audioEncoderOutputBufferInfo, TIMEOUT_USEC);
//                1040                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                    1041                    if (VERBOSE) Log.d(TAG, "no audio encoder output buffer");
//                    1042                    break;
//                    1043                }
//                1044                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    1045                    if (VERBOSE) Log.d(TAG, "audio encoder: output buffers changed");
//                    1046                    audioEncoderOutputBuffers = audioEncoder.getOutputBuffers();
//                    1047                    break;
//                    1048                }
//                1049                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    1050                    if (VERBOSE) Log.d(TAG, "audio encoder: output format changed");
//                    1051                    if (outputAudioTrack >= 0) {
//                        1052                        fail("audio encoder changed its output format again?");
//                        1053                    }
//                    1054
//                    1055                    encoderOutputAudioFormat = audioEncoder.getOutputFormat();
//                    1056                    break;
//                    1057                }
//                1058                assertTrue("should have added track before processing output", muxing);
//                1059                if (VERBOSE) {
//                    1060                    Log.d(TAG, "audio encoder: returned output buffer: "
//                            1061                            + encoderOutputBufferIndex);
//                    1062                    Log.d(TAG, "audio encoder: returned buffer of size "
//                            1063                            + audioEncoderOutputBufferInfo.size);
//                    1064                }
//                1065                ByteBuffer encoderOutputBuffer =
//                        1066                        audioEncoderOutputBuffers[encoderOutputBufferIndex];
//                1067                if ((audioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
//                        1068                        != 0) {
//                    1069                    if (VERBOSE) Log.d(TAG, "audio encoder: codec config buffer");
//                    1070                    // Simply ignore codec config buffers.
//                    1071                    audioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                    1072                    break;
//                    1073                }
//                1074                if (VERBOSE) {
//                    1075                    Log.d(TAG, "audio encoder: returned buffer for time "
//                            1076                            + audioEncoderOutputBufferInfo.presentationTimeUs);
//                    1077                }
//                1078                if (audioEncoderOutputBufferInfo.size != 0) {
//                    1079                    muxer.writeSampleData(
//                            1080                            outputAudioTrack, encoderOutputBuffer, audioEncoderOutputBufferInfo);
//                    1081                }
//                1082                if ((audioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
//                        1083                        != 0) {
//                    1084                    if (VERBOSE) Log.d(TAG, "audio encoder: EOS");
//                    1085                    audioEncoderDone = true;
//                    1086                }
//                1087                audioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
//                1088                audioEncodedFrameCount++;
//                1089                // We enqueued an encoded frame, let's try something else next.
//                1090                break;
//                1091            }
//            1092
//            1093            if (!muxing
//            1094                    && (!mCopyAudio || encoderOutputAudioFormat != null)
//            1095                    && (!mCopyVideo || encoderOutputVideoFormat != null)) {
//                1096                if (mCopyVideo) {
//                    1097                    Log.d(TAG, "muxer: adding video track.");
//                    1098                    outputVideoTrack = muxer.addTrack(encoderOutputVideoFormat);
//                    1099                }
//                1100                if (mCopyAudio) {
//                    1101                    Log.d(TAG, "muxer: adding audio track.");
//                    1102                    outputAudioTrack = muxer.addTrack(encoderOutputAudioFormat);
//                    1103                }
//                1104                Log.d(TAG, "muxer: starting");
//                1105                muxer.start();
//                1106                muxing = true;
//                1107            }
//            1108        }
//        1109
//        1110        // Basic sanity checks.
//        1111        if (mCopyVideo) {
//            1112            assertEquals("encoded and decoded video frame counts should match",
//                    1113                    videoDecodedFrameCount, videoEncodedFrameCount);
//            1114            assertTrue("decoded frame count should be less than extracted frame count",
//                    1115                    videoDecodedFrameCount <= videoExtractedFrameCount);
//            1116        }
//        1117        if (mCopyAudio) {
//            1118            assertEquals("no frame should be pending", -1, pendingAudioDecoderOutputBufferIndex);
//            1119        }
//        1120    }
//1121
//        1122    private static boolean isVideoFormat(MediaFormat format) {
//        1123        return getMimeTypeFor(format).startsWith("video/");
//        1124    }
//1125
//        1126    private static boolean isAudioFormat(MediaFormat format) {
//        1127        return getMimeTypeFor(format).startsWith("audio/");
//        1128    }
//1129
//        1130    private static String getMimeTypeFor(MediaFormat format) {
//        1131        return format.getString(MediaFormat.KEY_MIME);
//        1132    }
//
//}
