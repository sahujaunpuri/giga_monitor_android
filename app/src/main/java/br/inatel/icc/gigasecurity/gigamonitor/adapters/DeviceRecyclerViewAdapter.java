//package br.inatel.icc.gigasecurity.gigamonitor.adapters;
//
//import android.content.Context;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
///**
// * Created by filipecampos on 28/04/2016.
// */
//public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.MyViewHolder> {
//
//    public ArrayList<Device> mDevices;
//    public Context mContext;
//    public ChannelRecyclerViewAdapter channelRecyclerViewAdapter;
//    public DeviceManager mDeviceManager;
//
//    public DeviceRecyclerViewAdapter(Context mContext, ArrayList<Device> mDevices) {
//        this.mContext = mContext;
//        this.mDevices = mDevices;
//        this.mDeviceManager      = DeviceManager.getInstance();
//    }
//
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView tvDeviceName, tvMessage, tvChnNumber;
//        public ImageView ivMore, ivQuad, ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo;
//        public LinearLayout layoutMenu;
//        public ProgressBar pbLogin;
//        public RecyclerView channelRecyclerView;
//        public static int numQuad = 1;
//
//        public MyViewHolder(View view) {
//            super(view);
//
//            tvDeviceName        = (TextView) view.findViewById(R.id.tv_device_name_recycler);
//            tvMessage           = (TextView) view.findViewById(R.id.tv_message_connecting);
//            tvChnNumber         = (TextView) view.findViewById(R.id.tv_channel_number_recycler);
//            ivMore              = (ImageView) view.findViewById(R.id.iv_more_recycler);
//            ivQuad              = (ImageView) view.findViewById(R.id.iv_quad_recycler);
//            ivHQ                = (ImageView) view.findViewById(R.id.iv_hq);
//            ivPlayPause         = (ImageView) view.findViewById(R.id.iv_play_pause);
//            ivSnapshot          = (ImageView) view.findViewById(R.id.iv_snapshot);
//            ivSnapvideo         = (ImageView) view.findViewById(R.id.iv_snapvideo);
//            layoutMenu          = (LinearLayout) view.findViewById(R.id.layout_menu);
//            pbLogin             = (ProgressBar) view.findViewById(R.id.pb_recycle_device);
//            channelRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_channels);
//
//
//            //channelRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }
//    }
//
//
//    @Override
//    public DeviceRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_devices, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final DeviceRecyclerViewAdapter.MyViewHolder myViewHolder, int position) {
//        final Device mDevice = mDevices.get(position);
//
//        myViewHolder.tvDeviceName.setText(mDevice.deviceName);
//
//        if(mDevice.getLoginID() == 0) {
//            loginDevice(mDevice, myViewHolder, position);
//        } else {
//            //channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(mContext, mDevice, 1, myViewHolder);
//            myViewHolder.channelRecyclerView.setLayoutManager(new GridLayoutManager(mContext, myViewHolder.numQuad, GridLayoutManager.HORIZONTAL, false));
//            myViewHolder.channelRecyclerView.setAdapter(channelRecyclerViewAdapter);
//        }
//
//        myViewHolder.ivQuad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(myViewHolder.numQuad ==1) {
//                    myViewHolder.numQuad = 2;
//                } else if(myViewHolder.numQuad == 2) {
//                    myViewHolder.numQuad = 3;
//                } else if(myViewHolder.numQuad == 3) {
//                    myViewHolder.numQuad = 4;
//                } else if (myViewHolder.numQuad == 4) {
//                    myViewHolder.numQuad = 2;
//                }
//
//
//                myViewHolder.channelRecyclerView.setLayoutManager(new GridLayoutManager(mContext, myViewHolder.numQuad, GridLayoutManager.HORIZONTAL, false));
//                //channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(mContext, mDevice, myViewHolder.numQuad, myViewHolder);
//                myViewHolder.channelRecyclerView.setAdapter(channelRecyclerViewAdapter);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDevices.size();
//    }
//
//    private void loginDevice(final Device mDevice, final MyViewHolder myViewHolder, final int position) {
//
//        /*mDeviceManager.loginDevice(mDevice, LoginMethod.TRY_ALL, new DeviceManager.LoginDeviceListener() {
//            @Override
//            public void onLoginSuccess(long loginID) {
//
//                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        myViewHolder.tvMessage.setVisibility(View.GONE);
//                        myViewHolder.pbLogin.setVisibility(View.GONE);
//                        myViewHolder.channelRecyclerView.setVisibility(View.VISIBLE);
//                        myViewHolder.ivMore.setVisibility(View.VISIBLE);
//
//                        if (mDevice.getChannelNumber() > 1) {
//                            myViewHolder.ivQuad.setVisibility(View.VISIBLE);
//                            myViewHolder.numQuad = 2;
//                        } else {
//                            myViewHolder.ivQuad.setVisibility(View.INVISIBLE);
//                            myViewHolder.numQuad = 1;
//                        }
//
//
//                        //channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(mContext, mDevice, myViewHolder.numQuad, myViewHolder);
//                        myViewHolder.channelRecyclerView.setLayoutManager(new GridLayoutManager(mContext, myViewHolder.numQuad, GridLayoutManager.HORIZONTAL, false));
//                        myViewHolder.channelRecyclerView.setAdapter(channelRecyclerViewAdapter);
//                    }
//                });
//            }
//
//            @Override
//            public void onLoginError(long loginID) {
//                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        myViewHolder.ivQuad.setVisibility(View.INVISIBLE);
//                        myViewHolder.ivMore.setVisibility(View.INVISIBLE);
//                        myViewHolder.channelRecyclerView.setVisibility(View.GONE);
//                        myViewHolder.tvMessage.setVisibility(View.VISIBLE);
//                        myViewHolder.tvMessage.setText("Erro ao conectar com o dispositivo.");
//                    }
//                });
//            }
//        });*/
//    }
//
//
//
//}
