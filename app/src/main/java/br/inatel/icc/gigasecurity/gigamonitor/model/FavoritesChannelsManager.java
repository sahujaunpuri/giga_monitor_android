//package br.inatel.icc.gigasecurity.gigamonitor.model;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.os.Environment;
//import android.os.Message;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.basic.G;
//import com.lib.EUIMSG;
//import com.lib.FunSDK;
//import com.lib.IFunSDKResult;
//import com.lib.MsgContent;
//import com.lib.sdk.struct.H264_DVR_FILE_DATA;
//import com.video.opengl.GLSurfaceView20;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Locale;
//
//import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
//import br.inatel.icc.gigasecurity.gigamonitor.task.AudioRecordThread;
//import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
///**
// * Created by filipecampos on 02/05/2016.
// */
//public class FavoritesChannelsManager extends ChannelsManager implements IFunSDKResult {
//    String TAG = "DeviceChannelsManager";
//
//    public ArrayList<SurfaceViewComponent> surfaceViewComponents;
//    public ArrayList<GLSurfaceView20> mySurfaceViews;
//    public ArrayList<Integer> handlers;
//    public ArrayList<Integer> deviceIds;
//    public DeviceManager mDeviceManager;
//    public Context mContext;
//    public int numQuad, lastNumQuad;
//    public int lastFirstVisibleItem;
//    public int lastLastVisibleItem;
//    public int lastFirstItemBeforeSelectChannel;
//    public int channelNumber;
//
//    public FrameLayout.LayoutParams pbParam;
//    public FrameLayout.LayoutParams surfaceViewLayout;
//
//    public ChannelRecyclerViewAdapter mRecyclerAdapter;
//    private int mUserID = -1;
//    public PlaybackListener currentPlaybackListener;
//    public AudioRecordThread mRecordThread;
//    public int recCounter = 0;
//    private int talkHandler;
//    public int playType = 0; //0 - live, 1 - playback live
//    public boolean collapased = false;
//
//    private int[][] inverseMatrix = new int[][]{
//            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
//            {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15},
//            {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 14, 10, 13, 15, 11},
//            {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15}
//    };
//
//    public FavoritesChannelsManager(int channelNumber){
//        if(mUserID == -1){
//            mUserID = FunSDK.RegUser(this);
//        }
//        this.surfaceViewComponents = new ArrayList<>();
//        this.mySurfaceViews = new ArrayList<GLSurfaceView20>();
//        this.handlers = new ArrayList<>();
//        this.deviceIds = new ArrayList<>();
//        this.numQuad = 1;
//        this.lastNumQuad = 1;
//        this.mDeviceManager = DeviceManager.getInstance();
//        this.mContext = mDeviceManager.currentContext;
//        this.channelNumber = channelNumber;
//
//        this.lastFirstVisibleItem = 0;
//        this.lastLastVisibleItem = 0;
//
//
//        surfaceViewLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        pbParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//    }
//
//    public void createComponents(){
//        //componentes temporarios substituidos quando o dispositivo estiver logado
//        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : mDeviceManager.favoritesMap.entrySet()){
//            deviceIds.add(entry.getKey());
//            for(Integer i : entry.getValue()){
//                GLSurfaceView20 mySurfaceView = new GLSurfaceView20(mContext);
//                mySurfaceView.setLayoutParams(surfaceViewLayout);
//                mySurfaceViews.add(mySurfaceView);
//
//                SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext, this, i);
//
//                surfaceViewComponent.mySurfaceViewChannelId = i;
//                surfaceViewComponent.mySurfaceViewOrderId = i;
//                surfaceViewComponent.deviceConnection = mDeviceManager.findDeviceById(entry.getKey()).connectionString;
//                surfaceViewComponent.deviceId = entry.getKey();
//
//                if(mDeviceManager.isFavorite(entry.getKey(), i))
//                    surfaceViewComponent.setFavorite(true);
//
//                surfaceViewComponent.setLayoutParams(surfaceViewLayout);
//
//                surfaceViewComponents.add(surfaceViewComponent);
//            }
//        }
//
//        this.reOrderSurfaceViewComponents();
//        changeSurfaceViewSize();
//    }
//
//    @Override
//    public void onStartVideo(final SurfaceViewComponent svc) {
//        svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, svc.deviceConnection, svc.mySurfaceViewChannelId, svc.streamType, mySurfaceViews.get(deviceIds.indexOf(svc.deviceId)), svc.mySurfaceViewOrderId);
//    }
//}
