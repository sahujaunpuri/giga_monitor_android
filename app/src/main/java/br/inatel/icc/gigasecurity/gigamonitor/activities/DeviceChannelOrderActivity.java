package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 20/03/18. - Alexandre
 */

public class DeviceChannelOrderActivity extends ActionBarActivity implements View.OnClickListener{

    public DeviceManager mManager;
    public Device mDevice;
    DragSortListView channelListView;
    ChannelsAdapter adapter;
    ArrayList<Channels> arrayOfChannels;
    private boolean moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_order);

        mManager = DeviceManager.getInstance();
        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);

        int numberOfChannels = mDevice.getChannelNumber();

        channelListView = (DragSortListView) findViewById(R.id.list);

        arrayOfChannels = new ArrayList<>();

        adapter = new ChannelsAdapter(this, arrayOfChannels);

        for (int i = 0; i < numberOfChannels; i++){
            adapter.add(new Channels(i, "Channel " + i));
        }

        channelListView.setAdapter(adapter);

        TextView channelNumberTextView = (TextView) findViewById(R.id.number_of_channels);

        channelNumberTextView.setText("Number of Channels: " + numberOfChannels);

        channelListView.setDropListener(onDrop);

    }


    //onDrop change position of the list item.
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    Channels item = (Channels) adapter.getItem(from);

                    adapter.notifyDataSetChanged();
                    arrayOfChannels.remove(from);
                    arrayOfChannels.add(to, item);

                    adapter = new ChannelsAdapter(DeviceChannelOrderActivity.this, arrayOfChannels);
                    channelListView.setAdapter(adapter);
//                    mAdapter.notifyDataSetChanged();

                    moved = true;
                }
            };

    @Override
    public void onClick(View view) {

    }
}

