package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    int aux;

    // Variáveis de exibição e controle
    DragSortListView channelListView;
    ChannelsAdapter adapter;
    ArrayList<Channels> arrayOfChannels;

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

        Log.e("New IDs antes: ", ""+currentSurfaceView0.mySurfaceViewNewChannelId+", "+currentSurfaceView1.mySurfaceViewNewChannelId+", "+currentSurfaceView2.mySurfaceViewNewChannelId+", "+currentSurfaceView3.mySurfaceViewNewChannelId+", ");

        aux = currentSurfaceView3.mySurfaceViewNewChannelId;
        currentSurfaceView3.mySurfaceViewNewChannelId = currentSurfaceView0.mySurfaceViewNewChannelId;
        currentSurfaceView0.mySurfaceViewNewChannelId = aux;

        Log.e("New IDs depois: ", ""+currentSurfaceView0.mySurfaceViewNewChannelId+", "+currentSurfaceView1.mySurfaceViewNewChannelId+", "+currentSurfaceView2.mySurfaceViewNewChannelId+", "+currentSurfaceView3.mySurfaceViewNewChannelId+", ");

/*        currentSurfaceView0.mySurfaceViewNewChannelId = 1;
        currentSurfaceView1.mySurfaceViewNewChannelId = 3;
        currentSurfaceView2.mySurfaceViewNewChannelId = 0;
        currentSurfaceView3.mySurfaceViewNewChannelId = 0;

        channelsManager.reOrderSurfaceViewComponents();

        int [][] inverseMatrix = new int[][]{
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, // 1x1
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, // 2x2
                {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 11, 14}, // 3x3
                {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15} // 4x4
        };

        channelsManager.setInverseMatrix(inverseMatrix);*/

        //Layout e talz
        numberOfChannels = mDevice.getChannelNumber();

        channelListView = (DragSortListView) findViewById(R.id.list);

        arrayOfChannels = new ArrayList<>();

        adapter = new ChannelsAdapter(this, arrayOfChannels);

        for (int i = 0; i < numberOfChannels; i++) {
            adapter.add(new Channels(i, i, "Canal " + i));
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

/*                    adapter = new ChannelsAdapter(DeviceChannelOrderActivity.this, arrayOfChannels);
                    channelListView.setAdapter(adapter);*/

                    // Keep channel number while change channel order
                    for (int i = 0; i < numberOfChannels; i++) {
                        adapter.getItem(i).setNewPosition(i);
                    }
                }
            };

    @Override
    public void onClick(View view) {

    }
}

