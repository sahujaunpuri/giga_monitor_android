package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.managers.CustomGridLayoutManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.ListComponent;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;

/**
 * Created by filipecampos on 28/04/2016.
 */
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.MyViewHolder>  {

    public Device mDevice;
    public Context mContext;
    private static int numQuad, msvSelected = -1,positionSelected = -1;
    public DeviceManager mDeviceManager;
    private DeviceExpandableListAdapter.ChildViewHolder childViewHolder;
    private long clickTime, lastClickTime = 0;
    public boolean doubleClick = false;
    public final Handler handler = new Handler(Looper.getMainLooper());
    public final ListComponent listComponent;

    public ChannelRecyclerViewAdapter(Context mContext, Device mDevice, int numQuad, DeviceExpandableListAdapter.ChildViewHolder chieldViewHolder, ListComponent listComponent) {
        this.mContext = mContext;
        this.mDevice = mDevice;
        this.numQuad = numQuad;
        this.childViewHolder = chieldViewHolder;
        this.mDeviceManager  = DeviceManager.getInstance();
        this.listComponent = listComponent;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout frameLayout;

        public MyViewHolder(View view) {
            super(view);
            frameLayout     = (FrameLayout) view.findViewById(R.id.frame_layout_channel_recycler_view);
        }
    }


    @Override
    public ChannelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_recycler_view_layout, parent, false);

        MyViewHolder newMyViewHolder = new MyViewHolder(itemView);
        return newMyViewHolder;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
    }



    @Override
    public void onBindViewHolder(final ChannelRecyclerViewAdapter.MyViewHolder myViewHolder, final int position) {

        listComponent.reOrderSurfaceViewComponents();


        final SurfaceViewComponent currentSurfaceView = listComponent.surfaceViewComponents.get(position);
        currentSurfaceView.mRecyclerAdapter = this;

        myViewHolder.frameLayout.removeAllViews();
        ViewGroup parent = (ViewGroup) currentSurfaceView.getParent();

        if(parent != null) {
            parent.removeAllViews();
        }


        myViewHolder.frameLayout.addView(currentSurfaceView);
        myViewHolder.frameLayout.addView(currentSurfaceView.progressBar);

        currentSurfaceView.progressBar.setVisibility(View.VISIBLE);

        if(!currentSurfaceView.isConnected)
            currentSurfaceView.onPlayLive();

        listComponent.changeSurfaceViewSize(currentSurfaceView, myViewHolder.frameLayout);

        //TODO click listener pela GLView - onInterceptTouchEvent()
//        currentSurfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickTime = System.currentTimeMillis();
//                if(clickTime - lastClickTime <= 300) {
//                    doubleClick = true;
//                    currentSurfaceView.progressBar.setVisibility(View.VISIBLE);
//
//                    if(listComponent.numQuad == 1) {
//                        listComponent.numQuad = listComponent.lastNumQuad;
//                    } else {
//                        listComponent.numQuad = 1;
//                        GridLayoutManager lm = (GridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
//                        listComponent.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
//                    }
//                    childViewHolder.gridLayoutManager.setSpanCount(listComponent.numQuad);
//                    childViewHolder.mRecyclerAdapter.notifyDataSetChanged();
//
//                    listComponent.changeSurfaceViewSize(listComponent.surfaceViewComponents.get(msvSelected), myViewHolder.frameLayout);
//
//                    if(listComponent.numQuad == 1) {
//                        Log.d("teste", "onClick: " + msvSelected);
//                        childViewHolder.recyclerViewChannels.scrollToPosition(msvSelected);
//                        listComponent.lastFirstVisibleItem = msvSelected;
//                        listComponent.lastLastVisibleItem = msvSelected;
//                    } else {
//                        Log.d("teste", "onClick: " + listComponent.lastFirstItemBeforeSelectChannel);
//                        childViewHolder.recyclerViewChannels.scrollToPosition(listComponent.lastFirstItemBeforeSelectChannel);
//                        listComponent.lastFirstVisibleItem = listComponent.lastFirstItemBeforeSelectChannel;
//                        listComponent.lastLastVisibleItem = listComponent.lastFirstItemBeforeSelectChannel + listComponent.numQuad;
//                    }
//
//                } else {
//                    if (childViewHolder.layoutMenu.getVisibility() == View.GONE) {
//                        msvSelected = listComponent.getChannelSelected(position);
//                        positionSelected = position;
//                        SurfaceViewComponent selectedSurfaceView = listComponent.surfaceViewComponents.get(positionSelected);
//
//                        int channelPosition = msvSelected + 1;
//
//                        String title = "Canal " + channelPosition;
//                        childViewHolder.tvChnNumber.setText(title);
//
//                        if (selectedSurfaceView.isHD()) {
//                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
//                        } else {
//                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
//                        }
//
//                        if (selectedSurfaceView.isPlaying) {
//                            childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause));
//                        } else {
//                            childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_off));
//                        }
//
//                        if (selectedSurfaceView.isREC()) {
//                            childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
//                        } else {
//                            childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
//                        }
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(!doubleClick) {
//                                            childViewHolder.layoutMenu.setVisibility(View.VISIBLE);
//                                        } else {
//                                            doubleClick = false;
//                                        }
//                                    }
//                                });
//                            }
//                        }).start();
//
//
//                    } else {
//                        childViewHolder.layoutMenu.setVisibility(View.GONE);
//                    }
//
//                }
//
//                lastClickTime = clickTime;
//            }
//        });
//
        childViewHolder.ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurfaceViewComponent selectedSurfaceView = listComponent.surfaceViewComponents.get(positionSelected);
                if (selectedSurfaceView.isConnected) {
                    if (selectedSurfaceView.isPlaying) {
                        selectedSurfaceView.onPause();
                        childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                    } else {
                        selectedSurfaceView.onResume();
                        childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                    }
                }
            }
        });

        childViewHolder.ivHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SurfaceViewComponent selectedSurfaceView = listComponent.surfaceViewComponents.get(positionSelected);
                if(selectedSurfaceView.isREC){
                    Toast.makeText(mContext, "Finalize a gravação.", Toast.LENGTH_SHORT).show();
                } else if (positionSelected != -1) {
//                    selectedSurfaceView.onStop();

                    selectedSurfaceView.progressBar.setVisibility(View.VISIBLE);
                    if (!selectedSurfaceView.isHD()) {
                        selectedSurfaceView.setStreamType(0);
                        childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
                        Log.d("HD", "onClick: HD ON");
                    } else if(positionSelected != -1){
                        selectedSurfaceView.setStreamType(1);
                        childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
                        Log.d("HD", "onClick: HD OFF");
                    }
                    selectedSurfaceView.restartVideo();


                }
            }
        });

        childViewHolder.ivSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listComponent.surfaceViewComponents.get(positionSelected).takeSnapshot();
                startAnimationBlink();
            }
        });

        childViewHolder.ivSnapvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!listComponent.surfaceViewComponents.get(positionSelected).isPlaying) {
                    Toast.makeText(mContext, "Vídeo pausado", Toast.LENGTH_SHORT).show();
                } else if(!listComponent.surfaceViewComponents.get(positionSelected).isREC()){
                    listComponent.surfaceViewComponents.get(positionSelected).setREC(true);
                    mDeviceManager.channelOnRec = true;
                    listComponent.surfaceViewComponents.get(positionSelected).startRecord();
                    childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
                } else {
                    listComponent.surfaceViewComponents.get(positionSelected).setREC(false);
                    listComponent.surfaceViewComponents.get(positionSelected).stopRecord();
                    childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
                }
            }
        });

        childViewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSurfaceView.isFavorite()){
                    //unfavorite
                    currentSurfaceView.isFavorite = false;
                    childViewHolder.ivFavorite.clearColorFilter();
                } else {
                    //addfavorite
                    currentSurfaceView.isFavorite = true;
                    childViewHolder.ivFavorite.setColorFilter(Color.parseColor("#FFFF00"));
                }
            }
        });

        childViewHolder.ivSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSurfaceView.isSendAudioEnabled){
                    //disable
                    currentSurfaceView.isSendAudioEnabled = false;
                    childViewHolder.ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_off_white_36dp));
                } else{
                    //enable
                    currentSurfaceView.isSendAudioEnabled = true;
                    childViewHolder.ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_white_36dp));
                }
            }
        });

        childViewHolder.ivReceiveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSurfaceView.isReceiveAudioEnabled){
                    //disable
                    currentSurfaceView.isReceiveAudioEnabled = false;
                    childViewHolder.ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
                }else {
                    //enable
                    currentSurfaceView.isReceiveAudioEnabled = true;
                    childViewHolder.ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDevice.getChannelNumber();
    }

    private void startAnimationBlink() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final Animation mAnimationBlink = AnimationUtils.loadAnimation(mContext, R.anim.blink);

                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceListActivity.layoutMain.startAnimation(mAnimationBlink);
                    }
                });

            }
        }).start();
    }

    public void disableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(false);
    }

    public void enableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(true);
    }

    public void singleQuad(SurfaceViewComponent surfaceViewComponent, int position){
        listComponent.resetScale();
        msvSelected = listComponent.getChannelSelected(position);
        surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);

        if(listComponent.numQuad == 1) {
            listComponent.numQuad = listComponent.lastNumQuad;
        } else {
            listComponent.numQuad = 1;
            GridLayoutManager lm = (GridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
            listComponent.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
        }
        childViewHolder.gridLayoutManager.setSpanCount(listComponent.numQuad);
        childViewHolder.mRecyclerAdapter.notifyDataSetChanged();

        if(listComponent.numQuad == 1) {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(listComponent.getChannelSelected(position), 0);
            listComponent.lastFirstVisibleItem = listComponent.getChannelSelected(position);
            listComponent.lastLastVisibleItem = listComponent.getChannelSelected(position);
        } else {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(listComponent.lastFirstItemBeforeSelectChannel, 0);
//            childViewHolder.recyclerViewChannels.scrollToPosition(listComponent.lastFirstItemBeforeSelectChannel);
            listComponent.lastFirstVisibleItem = listComponent.lastFirstItemBeforeSelectChannel;
            listComponent.lastLastVisibleItem = listComponent.lastFirstItemBeforeSelectChannel + listComponent.numQuad;
        }


    }

    public void openOverlayMenu(SurfaceViewComponent surfaceViewComponent, int channelPosition) {
        positionSelected = channelPosition;

        if (childViewHolder.layoutMenu.getVisibility() == View.GONE) {
//            msvSelected = listComponent.getChannelSelected(channelPosition);
//            positionSelected = position;
//            SurfaceViewComponent selectedSurfaceView = listComponent.surfaceViewComponents.get(positionSelected);

//            int channelPosition = msvSelected + 1;

            String title = "Canal " + (channelPosition + 1);
            childViewHolder.tvChnNumber.setText(title);

            if (surfaceViewComponent.isHD()) {
                childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
            } else {
                childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
            }

            if (surfaceViewComponent.isPlaying) {
                childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_36dp));
            } else {
                childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
            }

            if (surfaceViewComponent.isREC()) {
                childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
            } else {
                childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
            }

            childViewHolder.layoutMenu.setVisibility(View.VISIBLE);

        } else {
            childViewHolder.layoutMenu.setVisibility(View.GONE);
        }
    }




}
