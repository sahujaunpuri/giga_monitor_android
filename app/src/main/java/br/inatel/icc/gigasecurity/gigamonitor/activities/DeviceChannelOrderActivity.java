package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 20/03/18. - Alexandre
 */

public class DeviceChannelOrderActivity extends ActionBarActivity{

    public DeviceManager mManager;
    public Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_order);

        mManager = DeviceManager.getInstance();
        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);

        int numberOfChannels = mDevice.getChannelNumber();

        ListView channelListView = (ListView) findViewById(R.id.list);

        ArrayList<ChannelsOrder> arrayOfChannels = new ArrayList<>();

        ChannelsAdapter adapter = new ChannelsAdapter(this, arrayOfChannels);

        for (int i = 0; i < numberOfChannels; i++){
            adapter.add(new ChannelsOrder(i, "Channel " + i));
        }

        channelListView.setAdapter(adapter);

        TextView channelNumberTextView = (TextView) findViewById(R.id.number_of_channels);

        channelNumberTextView.setText("Number of Channels: " + numberOfChannels);

    }

}

