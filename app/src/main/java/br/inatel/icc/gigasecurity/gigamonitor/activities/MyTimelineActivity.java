//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.ListView;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.ListAdapter;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
//
///**
// * Created by zappts on 3/22/17.
// */
//
//public class MyTimelineActivity extends ActionBarActivity {
//    private TableLayout tableLayout;
//    private ListView listView;
//    private ListAdapter mAdapter;
//    private DeviceManager mDeviceManager;
//    public Context mContext;
//    public ArrayList<DeviceChannelsManager> favoritesViews = new ArrayList<DeviceChannelsManager>();
//    public ArrayList<TextView> text = new ArrayList<TextView>();
//    public int nFavorites = 0;
//
//    private LoginDeviceListener loginListener = new LoginDeviceListener() {
//        @Override
//        public void onLoginSuccess(Device device) {
//            for(DeviceChannelsManager deviceChannelsManager : favoritesViews){
//                deviceChannelsManager.onPlayLive();
//            }
//        }
//
//        @Override
//        public void onLoginError(long error, Device device) {
//
//        }
//
//        @Override
//        public void onLogout() {
//
//        }
//    };
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_timeline);
//
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
//                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
//
//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//
//        listView = (ListView) findViewById(R.id.list_view);
//        mDeviceManager = DeviceManager.getInstance();
//        mDeviceManager.currentContext = this;
//        mContext = this;
//
//
//        createFavorites();
//
//        mAdapter = new ListAdapter(this, R.layout.list_row, favoritesViews);
//        listView.setAdapter(mAdapter);
//
//
//
//
//    }
//
//    private void createFavorites(){
//        for(Device device : mDeviceManager.mFavoriteDevices){
//            mDeviceManager.loginDevice(device, loginListener);
//            for(int channelNumber : mDeviceManager.favoritesMap.get(device.getSerialNumber())){
//                createSurfaceView(device.getSerialNumber(), channelNumber);
//            }
//        }
//    }
//
//    private void createSurfaceView(String serialNumber, int channelNumber){
//
//        DeviceChannelsManager deviceChannelsManager = new DeviceChannelsManager(mContext);
//
//        deviceChannelsManager.mySurfaceViewChannelId = channelNumber;
//        deviceChannelsManager.mySurfaceViewOrderId = nFavorites++;
//        deviceChannelsManager.deviceSn = serialNumber;
//
//        if(mDeviceManager.isFavorite(serialNumber, channelNumber))
//            deviceChannelsManager.setFavorite(true);
//
//
////        int surfaceViewHeight = (int) ((Resources.getSystem().getDisplayMetrics().heightPixels / 3.5));
////        int surfaceViewWidth = (int) (surfaceViewHeight * 1.77777777778);
////
////        deviceChannelsManager.setViewSize(surfaceViewWidth, surfaceViewHeight, 1);
//
////        deviceChannelsManager.menu.updateIcons();
//        favoritesViews.add(deviceChannelsManager);
//    }
//
//}
