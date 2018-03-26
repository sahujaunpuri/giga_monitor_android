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

    // Variáveis de exibição e controle
    DragSortListView channelListView;
    ChannelsAdapter adapter;
    ArrayList<Channels> arrayOfChannels;
    ArrayList<SurfaceViewComponent> arrayOfSurfaceViewComponents;
    int [] channelOrder = new int[4];
    TextView tvBack, tvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_order);
        getSupportActionBar().hide();

        tvBack = (TextView) findViewById(R.id.text_view_back);
        tvDone = (TextView) findViewById(R.id.text_view_apply);

        mManager = DeviceManager.getInstance();
        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);

        channelsManager = mManager.findChannelManagerByDevice(mDevice);

        numberOfChannels = mDevice.getChannelNumber();

        channelListView = (DragSortListView) findViewById(R.id.list);

        arrayOfChannels = new ArrayList<Channels>();

        arrayOfSurfaceViewComponents = new ArrayList<SurfaceViewComponent>(numberOfChannels);

        adapter = new ChannelsAdapter(this, arrayOfChannels);

        for (int i = 0; i < numberOfChannels; i++) {
            arrayOfSurfaceViewComponents.add(i, channelsManager.surfaceViewComponents.get(i));
            adapter.add(new Channels(arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId+1, arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId+1, "Canal " + (arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId+1)));
            Log.e("CHANNEL Id, i:", ""+arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId+", "+i);
        }

        channelListView.setAdapter(adapter);

        TextView channelNumberTextView = (TextView) findViewById(R.id.number_of_channels);

        channelNumberTextView.setText("Número de Canais: " + numberOfChannels);

        setListeners();

        channelListView.setDropListener(onDrop);

        // Keep channel number while change channel order
        for (int i = 0; i < numberOfChannels; i++) {
            int position = channelsManager.inverseMatrix[1][i];
            adapter.getItem(i).setNewPosition(position+1);
        }

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
                        int position = channelsManager.inverseMatrix[1][i];
                        adapter.getItem(i).setNewPosition(position+1);
                    }
                }
            };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_back:
                exitOnly();
                break;
            case R.id.text_view_apply:
                exitAndSave();
                break;
            default:
                break;
        }
    }

    private void exitAndSave() {

        // Change surface view order as draggable list shows
        int originalPosition, channelAntes, channelDepois;
        for (int i = 0; i < numberOfChannels; i++) {
            originalPosition = arrayOfChannels.get(i).getOriginalPosition()-1;

            channelAntes = arrayOfSurfaceViewComponents.get(originalPosition).mySurfaceViewNewChannelId;
            channelDepois = i;

            Log.e("Channel ID antes e dps:", ""+channelAntes+", "+channelDepois);

            arrayOfSurfaceViewComponents.get(originalPosition).mySurfaceViewNewChannelId = i;
            channelOrder[i] = originalPosition;
        }
        finish();
    }

    private void exitOnly() {
        finish();
    }

    private void setListeners() {
        tvBack.setOnClickListener(this);
        tvDone.setOnClickListener(this);
    }

}

