package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xm.video.MySurfaceView;

import java.io.File;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceSearchListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.enums.PlayState;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.ListComponent;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

/**
 * Created by filipecampos on 28/04/2016.
 */
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.MyViewHolder> {

    public Device mDevice;
    public Context mContext;
    public static int numQuad, msvSelected = -1,positionSelected = -1;
    public DeviceManager mDeviceManager;
    public PlayState playState;
    public DeviceExpandableListAdapter.ChildViewHolder childViewHolder;
    public long clickTime, lastClickTime = 0;
    public static MyViewHolder myViewHolderSelected;
    public boolean doubleClick = false;
    public final Handler handler = new Handler(Looper.getMainLooper());
    public final ListComponent listComponent;
    public long clickRecTime;


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
    public void onBindViewHolder(final ChannelRecyclerViewAdapter.MyViewHolder myViewHolder, final int position) {

        listComponent.reOrderSurfaceViewComponents();

        myViewHolder.frameLayout.removeAllViews();
        ViewGroup parent = (ViewGroup) listComponent.surfaceViewComponents.get(position).mySurfaceView.getParent();

        if(parent != null) {
            parent.removeAllViews();
        }

        myViewHolder.frameLayout.addView(listComponent.surfaceViewComponents.get(position).mySurfaceView);
        myViewHolder.frameLayout.addView(listComponent.surfaceViewComponents.get(position).progressBar);

        listComponent.surfaceViewComponents.get(position).chnInfo.ChannelNo = listComponent.surfaceViewComponents.get(position).mySurfaceViewChannelId;

        listComponent.changeSurfaceViewSize(listComponent.surfaceViewComponents.get(position), myViewHolder.frameLayout);

        startDeviceVideo(mDevice, listComponent.surfaceViewComponents.get(position));

        listComponent.surfaceViewComponents.get(position).mySurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickTime = System.currentTimeMillis();

                if(clickTime - lastClickTime <= 300) {
                    doubleClick = true;

                    if(listComponent.numQuad == 1) {
                        listComponent.numQuad = listComponent.lastNumQuad;
                    } else {
                        listComponent.numQuad = 1;

                        GridLayoutManager lm = (GridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
                        listComponent.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
                    }

                    childViewHolder.gridLayoutManager = new GridLayoutManager(mContext, listComponent.numQuad, GridLayoutManager.HORIZONTAL, false);
                    childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
                    //childViewHolder.recyclerViewChannels.setLayoutManager(new GridLayoutManager(mContext, listComponent.numQuad, GridLayoutManager.HORIZONTAL, false));
                    childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, mDevice, listComponent.numQuad, childViewHolder, listComponent);
                    childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);

                    listComponent.changeSurfaceViewSize(listComponent.surfaceViewComponents.get(msvSelected), myViewHolder.frameLayout);

                    if(listComponent.numQuad == 1) {
                        childViewHolder.recyclerViewChannels.scrollToPosition(msvSelected);
                        listComponent.lastFirstVisibleItem = msvSelected;
                        listComponent.lastLastVisibleItem = msvSelected;
                    } else {
                        childViewHolder.recyclerViewChannels.scrollToPosition(listComponent.lastFirstItemBeforeSelectChannel);
                        listComponent.lastFirstVisibleItem = listComponent.lastFirstItemBeforeSelectChannel;
                        listComponent.lastLastVisibleItem = listComponent.lastFirstItemBeforeSelectChannel + listComponent.numQuad;
                    }

                } else {

                    if (childViewHolder.layoutMenu.getVisibility() == View.GONE) {
                        msvSelected = listComponent.getChannelSelected(position);
                        positionSelected = position;

                        myViewHolderSelected = myViewHolder;

                        int channelPosition =msvSelected + 1;

                        childViewHolder.tvChnNumber.setText("Canal " + channelPosition);

                        if (listComponent.surfaceViewComponents.get(positionSelected).chnInfo.nStream == 0) {
                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
                        } else {
                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
                        }

                        if (listComponent.surfaceViewComponents.get(positionSelected).isPlaying) {
                            childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause));
                        } else {
                            childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_off));
                        }

                        if (!listComponent.surfaceViewComponents.get(positionSelected).isREC()) {
                            childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
                        } else {
                            childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(600);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!doubleClick) {
                                            childViewHolder.layoutMenu.setVisibility(View.VISIBLE);
                                        } else {
                                            doubleClick = false;
                                        }
                                    }
                                });
                            }
                        }).start();


                    } else {
                        childViewHolder.layoutMenu.setVisibility(View.GONE);
                    }

                }

                lastClickTime = clickTime;
            }
        });

        childViewHolder.ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listComponent.surfaceViewComponents.get(positionSelected).connected) {
                    if (listComponent.surfaceViewComponents.get(positionSelected).isPlaying) {
                        listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView.onPause();
                        listComponent.surfaceViewComponents.get(positionSelected).isPlaying = false;
                        childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_off));
                    } else {
                        listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView.onPlay();
                        listComponent.surfaceViewComponents.get(positionSelected).isPlaying = true;
                        childViewHolder.ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause));
                    }
                }
            }
        });

        childViewHolder.ivHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listComponent.surfaceViewComponents.get(positionSelected).isREC()) {
                    if (positionSelected != -1) {
                        if (listComponent.surfaceViewComponents.get(positionSelected).chnInfo.nStream == 1) {
                            listComponent.surfaceViewComponents.get(positionSelected).chnInfo.nStream = 0;
                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
                        } else {
                            listComponent.surfaceViewComponents.get(positionSelected).chnInfo.nStream = 1;
                            childViewHolder.ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
                        }

                        mDeviceManager.stopDeviceVideo2(listComponent.surfaceViewComponents.get(positionSelected).realPlayHandleID, listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView, new DeviceManager.StopDeviceVideoListener() {
                            @Override
                            public void onSuccessStopDevice() {

                                mDeviceManager.startDeviceVideo2(mDevice.getLoginID(), listComponent.surfaceViewComponents.get(positionSelected).mySurfaceViewID, listComponent.surfaceViewComponents.get(positionSelected).chnInfo, new DeviceManager.StartDeviceVideoListener() {
                                    @Override
                                    public void onSuccessStartDevice(final long handleID) {

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView.onPlay();
                                                listComponent.surfaceViewComponents.get(positionSelected).realPlayHandleID = handleID;
                                                listComponent.surfaceViewComponents.get(positionSelected).isPlaying = true;
                                                listComponent.surfaceViewComponents.get(positionSelected).progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onErrorStartDevice() {
                                        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listComponent.surfaceViewComponents.get(positionSelected).progressBar.setVisibility(View.VISIBLE);
                                                startDeviceVideo(mDevice, listComponent.surfaceViewComponents.get(positionSelected));
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onErrorStopDevice() {
                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext, "Finalize a gravação para mudar para HD", Toast.LENGTH_LONG).show();
                    // Toast.makeText(mContext, "Gravando no canal "+mDeviceManager.getChannelOnRec(), Toast.LENGTH_LONG).show();
                }
            }
        });

        childViewHolder.ivSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSnapshot(listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView);
                startAnimationBlink();
            }
        });

        childViewHolder.ivSnapvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listComponent.surfaceViewComponents.get(positionSelected).isREC()) {
                    if (mDeviceManager.startSnapvideo(listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView,msvSelected) != null) {
                        clickRecTime = System.currentTimeMillis();
                        listComponent.surfaceViewComponents.get(positionSelected).setREC(true);
                        childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
                    } else {
                        Toast.makeText(mContext, "Gravando no canal "+mDeviceManager.getChannelOnRec(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    long duration = System.currentTimeMillis() - clickRecTime;
                    if (duration > 1000) {
                        if (mDeviceManager.stopSnapvideo(listComponent.surfaceViewComponents.get(positionSelected).mySurfaceView, mContext) != null) {
                            Toast.makeText(mContext, "Gravação salva com sucesso", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Não foi possível salvar o video", Toast.LENGTH_LONG).show();
                        }
                        childViewHolder.ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
                        listComponent.surfaceViewComponents.get(positionSelected).setREC(false);
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDevice.getChannelNumber();
    }

    private void startDeviceVideo(final Device mDevice, final SurfaceViewComponent surfaceViewComponent) {

        if(surfaceViewComponent.realPlayHandleID == 0) {

            surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);

            surfaceViewComponent.mySurfaceView.setOnPlayStateListener(new MySurfaceView.OnPlayStateListener() {
                @Override
                public void onPlayState(int i, int i1) {

                    if(i1 == 2 || i1 == 3) {
                        surfaceViewComponent.connected = true;
                        surfaceViewComponent.progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
                    }
/*
                    if(i1 == 2) {

                        surfaceViewComponent.progressBar.setVisibility(View.GONE);
                    } else {
                       // surfaceViewComponent.connected = false;
                        surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
                   }
  */
                }
            });

            mDeviceManager.startDeviceVideo2(mDevice.getLoginID(), surfaceViewComponent.mySurfaceViewID,
                    surfaceViewComponent.chnInfo, new DeviceManager.StartDeviceVideoListener() {
                @Override
                public void onSuccessStartDevice(final long handleID) {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceViewComponent.mySurfaceView.onPlay();
                            surfaceViewComponent.realPlayHandleID = handleID;
                            surfaceViewComponent.isPlaying = true;
                            surfaceViewComponent.progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                @Override
                public void onErrorStartDevice() {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
                            startDeviceVideo(mDevice, surfaceViewComponent);
                        }
                    });
                }
            });


        } else {
            surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
            mDeviceManager.startDeviceVideo2(mDevice.getLoginID(), surfaceViewComponent.mySurfaceViewID, surfaceViewComponent.chnInfo, new DeviceManager.StartDeviceVideoListener() {
                @Override
                public void onSuccessStartDevice(final long handleID) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            surfaceViewComponent.mySurfaceView.onPlay();
                            surfaceViewComponent.realPlayHandleID = handleID;
                            surfaceViewComponent.isPlaying = true;
                            surfaceViewComponent.progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                @Override
                public void onErrorStartDevice() {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
                            startDeviceVideo(mDevice, surfaceViewComponent);
                        }
                    });
                }
            });

        }

    }

    private void startAnimationBlink() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final Animation mAnimationBlink = AnimationUtils.loadAnimation(mContext, R.animator.blink);

                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceListActivity.layoutMain.startAnimation(mAnimationBlink);
                    }
                });

            }
        }).start();
    }

    private void takeSnapshot(MySurfaceView mySurfaceView) {
        File pictureFile = mDeviceManager.takeSnapshot(mContext, mySurfaceView);

        if (pictureFile == null) {
            Log.d("Error take SNAPSHOT:", "Error creating media file, check storage permissions");
            return;
        }

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, pictureFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());

        mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }
}
