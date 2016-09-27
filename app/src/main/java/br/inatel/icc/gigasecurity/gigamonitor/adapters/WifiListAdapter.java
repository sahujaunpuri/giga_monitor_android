package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.basic.G;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.javaclass.SDK_WifiStatusInfo;
import java.util.List;
import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.wifi.WifiAPListConfig;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * DeviceListAdapter
 * Created by rinaldo.bueno on 27/08/2014.
 */
public class WifiListAdapter extends ArrayAdapter<WifiAPListConfig.WifiItem> {

    private List<WifiAPListConfig.WifiItem> mList;
    private Device mDevice;
    private DeviceManager mManager;
    private long loginId;
    private WifiAPListConfig.WifiItem wifi;
    private static String TAG = WifiListAdapter.class.getSimpleName();
    private TextView name, status;

    public WifiListAdapter(Context context, WifiAPListConfig WifiAPListConfig) {
        super(context, R.layout.wifi_config_item, R.id.tv_wifi_item, WifiAPListConfig.getWifiList());
        this.mList = WifiAPListConfig.getWifiList();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        name = (TextView) view.findViewById(R.id.tv_wifi_item);
        TextView encrypt = (TextView) view.findViewById(R.id.tv_wifi_encrypt_item);
        TextView health = (TextView) view.findViewById(R.id.tv_wifi_health_item);
        status = (TextView) view.findViewById(R.id.tv_wifi_status_item);


         wifi = mList.get(position);

        if (wifi != null) {
            name.setText(wifi.getSSID());
            name.setTextColor(Color.BLACK);
            encrypt.setText(wifi.getAuth() + "/" + wifi.getEncrypType());
            health.setText(String.valueOf(wifi.getRSSI()));
            Log.d(TAG, "GO TO STATUS");
            status.setText(getWifiStatus());
            Log.d(TAG, "PASSED STATUS");

        }
        return view;
    }

    public String getWifiStatus() {

        String wifiStatus = null;

        if (wifi.getSSID().equals(name.getText())) {

            //Code implemented with the test code

            SDK_WifiStatusInfo wifiStatusInfo = new SDK_WifiStatusInfo();
            DevConfig devConfig = new DevConfig(loginId);
            byte[] buf = devConfig.getConfig(MyConfig.SdkConfigType.E_SDK_WIFI_STATUS, -1, G.Sizeof(wifiStatusInfo));

            if(buf != null) {

                G.BytesToObj(wifiStatusInfo, buf);
                wifiStatus = String.valueOf(wifiStatusInfo.st_0_connectStatus); //wifi status int value

            } else {

                wifiStatus = "Can't show\nWifi status";
                //The sdk can't get the wifi status
            }


            /*
            * The result or is always "Connected" or always "Not Connected", the value isEnable its comming
             * false for everyone in the list.
             */

            //WifiConfig config = new WifiConfig();

            //SDK_WifiStatusInfo statusInfo = new SDK_WifiStatusInfo();

            //if(config.isEnable() && statusInfo.st_0_connectStatus == 0){

            //    wifiStatus = "Connected";
            //} else {

            //    wifiStatus ="Not Connected";
            //}
        }
        return wifiStatus;
    }

    public class DevConfig {
        private NetSdk mNetSdk;
        private long loginId;
        public DevConfig(long loginid) {
            mNetSdk = NetSdk.getInstance();
            this.loginId = loginid;
        }
        public byte[] getConfig(int commd,int chnid,int bufsize) {
            boolean bret = false;
            if(bufsize <= 0)
                return null;
            byte[] buf = new byte[bufsize];
            bret = mNetSdk.H264DVRGetDevConfig2(loginId, commd, chnid,buf,5000, 5000);
            if(bret)
                return buf;
            else {
                Log.d("test",  "error:" + mNetSdk.GetLastError());
                return null;
            }
        }
        public boolean setConfig(int commd,int chnid,byte[] config) {
            boolean bret = false;
            bret = mNetSdk.H264DVRSetDevConfig2(loginId, commd, chnid, config, 5000, 5000);
            if(!bret) {
                Log.d("test", "error:" + mNetSdk.GetLastError());
            }
            return bret;
        }
    }
}