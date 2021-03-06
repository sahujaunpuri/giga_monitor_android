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

public class FavoriteDeviceChannelOrderActivity extends ActionBarActivity implements View.OnClickListener {

    // Variáveis do DVR
    public DeviceManager mManager;
    public Device mDevice;
    int numberOfChannels;
    ChannelsManager channelsManager;

    // Variáveis de exibição e controle
    DragSortListView channelListView;
    ChannelsAdapter adapter;
    ArrayList<Channel> arrayOfChannels;
    ArrayList<SurfaceViewComponent> arrayOfSurfaceViewComponents;
    int [] channelOrder = new int[36];
    TextView tvBack, tvDone,tvRestoreDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_order);
        getSupportActionBar().hide();

        tvBack = (TextView) findViewById(R.id.text_view_back);
        tvDone = (TextView) findViewById(R.id.text_view_apply);
        tvRestoreDefault = (TextView) findViewById(R.id.restore_defaults);

        mManager = DeviceManager.getInstance();
        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);

        channelOrder = mDevice.getChannelOrder();

        channelsManager = mManager.findChannelManagerByDevice(mDevice);

        numberOfChannels = mDevice.getChannelNumber();

        channelListView = (DragSortListView) findViewById(R.id.list);

        arrayOfChannels = new ArrayList<Channel>();

        arrayOfSurfaceViewComponents = new ArrayList<SurfaceViewComponent>(numberOfChannels);

        adapter = new ChannelsAdapter(this, arrayOfChannels);

        // Ordenação de plot. Valor do array é o número do canal e posição do array é o lugar do plot (ordem do DVR).

        int [][] inverseMatrix = channelsManager.inverseMatrix;

        numberOfChannels = DeviceManager.getInstance().favoritesList.size();

        // montar lista de exibição
        for (int i = 0; i < numberOfChannels; i++) {
            arrayOfSurfaceViewComponents.add(i, channelsManager.surfaceViewComponents.get(inverseMatrix[channelsManager.numQuad-1][i]));
            int channelId = arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId;
            adapter.add(new Channel("Canal " + (channelId+1), channelId, channelId));
        }

        channelListView.setAdapter(adapter);

        TextView channelNumberTextView = (TextView) findViewById(R.id.number_of_channels);

        String numeroDeCanais = ("Canais: " + numberOfChannels);

        channelNumberTextView.setText(numeroDeCanais);

        setListeners();

        channelListView.setDropListener(onDrop);

        for (int i = 0; i < numberOfChannels; i++) {
            adapter.getItem(i).setChannelNewGrid(i);
        }
    }

    //onDrop change position of the list item.
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    Channel item = adapter.getItem(from);

                    arrayOfChannels.remove(from);
                    arrayOfChannels.add(to, item);

                    adapter.notifyDataSetChanged();

                    // Keep channel number while change channel order
                    for (int i = 0; i < numberOfChannels; i++) {
                        adapter.getItem(i).setChannelNewGrid(i);
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
            case R.id.restore_defaults:
                restoreDefaults();
                break;
            default:
                break;
        }
    }

    private void exitAndSave() {
        for (int i = 0; i < numberOfChannels; i++) {
            arrayOfSurfaceViewComponents.get(i).mySurfaceViewNewChannelId = arrayOfChannels.get(i).getChannelOldGrid();
            channelOrder[i] = arrayOfChannels.get(i).getChannelOldGrid();
        }
        mDevice.setChannelOrder(channelOrder);
        finish();
    }

    private void restoreDefaults() {

        for (int i = 0; i < numberOfChannels; i++) {
            adapter.getItem(i).setChannelNewGrid(i);
            adapter.getItem(i).setChannelId("Canal "+(i+1));
            adapter.getItem(i).setChannelOldGrid(i);
        }
        adapter.notifyDataSetChanged();
    }

    private void exitOnly() {
        finish();
    }

    private void setListeners() {
        tvBack.setOnClickListener(this);
        tvDone.setOnClickListener(this);
        tvRestoreDefault.setOnClickListener(this);
    }
}
