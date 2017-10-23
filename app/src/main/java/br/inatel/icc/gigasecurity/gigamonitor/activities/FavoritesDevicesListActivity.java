package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;

public class FavoritesDevicesListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_devices_list);

        ListView listView = (ListView)findViewById(R.id.listview_fav_devices);
        FavDeviceAdapter mCustomAdapter = new FavDeviceAdapter(50);
        listView.setAdapter(mCustomAdapter);
    }

    class FavDeviceAdapter extends BaseAdapter {

        int mDevices;
        Context mContext;


        public FavDeviceAdapter(int size){
            this.mDevices = size;
        }

        @Override
        public int getCount() {
            return this.mDevices;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list_item_fav_devices, null);
            TextView mTextView = (TextView) convertView.findViewById(R.id.tv_item_edit_device);
            mTextView.setText("Channel name");
            if (position % 10 == 0) {
                mTextView.setText("Fav Device");
            }


            return convertView;
        }
    }

}
