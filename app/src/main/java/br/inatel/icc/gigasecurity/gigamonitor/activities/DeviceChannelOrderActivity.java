package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by zappts on 20/03/18. - Alexandre
 */

public class DeviceChannelOrderActivity extends ActionBarActivity implements View.OnClickListener {

    // Variáveis do DVR
    public DeviceManager mManager;
    public Device mDevice;
    int numberOfChannels;
    ChannelsManager channelsManager;
    SurfaceViewComponent currentSurfaceView0;
    SurfaceViewComponent currentSurfaceView1;
    SurfaceViewComponent currentSurfaceView2;
    SurfaceViewComponent currentSurfaceView3;

    // Variáveis de exibição e controle
    DragSortListView channelListView;
    ChannelsAdapter adapter;
    ArrayList<Channels> arrayOfChannels;
    private boolean moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_order);
        getSupportActionBar().hide();

        mManager = DeviceManager.getInstance();
        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);

        // Alterar a ordem do grid

        channelsManager = mManager.findChannelManagerByDevice(mDevice);

        currentSurfaceView0 = channelsManager.surfaceViewComponents.get(0);
        currentSurfaceView1 = channelsManager.surfaceViewComponents.get(1);
        currentSurfaceView2 = channelsManager.surfaceViewComponents.get(2);
        currentSurfaceView3 = channelsManager.surfaceViewComponents.get(3);

        currentSurfaceView0.mySurfaceViewChannelId = 1;
        //currentSurfaceView0.mySurfaceViewOrderId = 2;
        currentSurfaceView1.mySurfaceViewChannelId = 3;
        //currentSurfaceView1.mySurfaceViewOrderId = 3;
        currentSurfaceView2.mySurfaceViewChannelId = 0;
        //currentSurfaceView2.mySurfaceViewOrderId = 0;
        currentSurfaceView3.mySurfaceViewChannelId = 0;
        //currentSurfaceView3.mySurfaceViewOrderId = 1;

        channelsManager.reOrderSurfaceViewComponents();

        //Layout e talz
        numberOfChannels = mDevice.getChannelNumber();

        channelListView = (DragSortListView) findViewById(R.id.list);

        arrayOfChannels = new ArrayList<>();

        adapter = new ChannelsAdapter(this, arrayOfChannels);

        for (int i = 0; i < numberOfChannels; i++) {
            adapter.add(new Channels(i, "Canal " + i));
        }

        channelListView.setAdapter(adapter);

        TextView channelNumberTextView = (TextView) findViewById(R.id.number_of_channels);

        channelNumberTextView.setText("Número de Canais: " + numberOfChannels);

        channelListView.setDropListener(onDrop);

    }

    //onDrop change position of the list item.
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    Channels item = adapter.getItem(from);

                    arrayOfChannels.remove(from);
                    arrayOfChannels.add(to, item);

                    adapter.notifyDataSetChanged();

                    //adapter = new ChannelsAdapter(DeviceChannelOrderActivity.this, arrayOfChannels);
                    //channelListView.setAdapter(adapter);

                    moved = true;

                    // Keep channel number while change channel order
                    for (int i = 0; i < numberOfChannels; i++) {
                        adapter.getItem(i).setChannel(i);
                    }
                }
            };

    @Override
    public void onClick(View view) {

    }
}

