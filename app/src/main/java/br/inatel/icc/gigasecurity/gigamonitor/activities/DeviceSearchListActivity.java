package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceSearchAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.config.ethernet.EthernetConfigActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.core.Discovery;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;


public class DeviceSearchListActivity extends ActionBarActivity {

    private ListView mListView;

    private ArrayList<Device> mDevices;

    private Discovery mDiscoveryThread;

    private ProgressDialog mLoadingDialog;

    private AlertDialog mDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_device_search);

        mListView = (ListView) findViewById(R.id.list_view_device);

        if(getIntent().getExtras() != null) {
            mDevices = (ArrayList<Device>) getIntent().getExtras().getSerializable("devicesSearch");
        } else {
            mDevices = new ArrayList<Device>();
        }

        mListView.setAdapter(new DeviceSearchAdapter(mContext, mDevices));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                mListView.setItemChecked(i, true);
                view.setSelected(true);
                verifyOptionSelected(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    public void searchDevices() {
        if (mDiscoveryThread == null) {
            mDiscoveryThread = prepareDiscoveryThread();
        } else {
            stopAndRefresh();
        }

        mDiscoveryThread.start();

        String text = getResources().getString(R.string.searching_dialog_messsage);

        mLoadingDialog = ProgressDialog.show(mContext, "", text, true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        stopAndRefresh();
                        mLoadingDialog.dismiss();
                    }
                });
    }

    private void verifyOptionSelected(final int item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        ((DeviceSearchListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.setTitle("")
                        .setItems(new CharSequence[]{"Configurar rede", "Adicionar dispositivo", "Cancelar"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                startNetworkSettingsActivity(item);
                                                break;
                                            case 1:
                                                startAddDeviceActivity(item);
                                                break;
                                            case 2:
                                                builder.setCancelable(true);
                                                break;
                                        }
                                    }
                                });
                builder.show();
            }
        });
    }

    private void startNetworkSettingsActivity(int item) {

        Bundle extras = new Bundle();
        Device dev = new Device();
        dev.setHostname(mDevices.get(item).getHostname());
        dev.setGateway(mDevices.get(item).getGateway());
        dev.setIpAddress(mDevices.get(item).getIpAddress());
        dev.setSubmask(mDevices.get(item).getSubmask());
        dev.setHttpPort(mDevices.get(item).getHttpPort());
        dev.setMacAddress(mDevices.get(item).getMacAddress());
        dev.setMaxBPS(mDevices.get(item).getMaxBPS());
        dev.setMonMode(mDevices.get(item).getMonMode());
        dev.setSslPort(mDevices.get(item).getSslPort());
        dev.setTcpMaxConn(mDevices.get(item).getTcpMaxConn());
        dev.setTCPPort(mDevices.get(item).getTCPPort());
        dev.setTransferPlan(mDevices.get(item).getTransferPlan());
        dev.setUdpPort(mDevices.get(item).getUdpPort());
        dev.setMacAddress(mDevices.get(item).getMacAddress());

        extras.putSerializable("device", dev);
        extras.putInt("index", -3);

        Intent intent = new Intent(DeviceSearchListActivity.this, EthernetConfigActivity.class);
        intent.putExtras(extras);
//                startActivity(intent);
        startActivityForResult(intent, 1);
//                finish();
    }

    private void startAddDeviceActivity(int item) {
        Bundle extras = new Bundle();
        Device device = mDevices.get(item);
        device.setIpPriorityConnection(true);
        extras.putSerializable("device", device);
        extras.putInt("index", -2);

        Intent intent = new Intent(DeviceSearchListActivity.this, DeviceFormActivity.class);
        intent.putExtras(extras);
//                startActivity(intent);
        startActivityForResult(intent, 1);
//                finish();
    }

    private void stopAndRefresh() {
        mDiscoveryThread.interrupt();
        mDiscoveryThread = prepareDiscoveryThread();
    }

    public Discovery prepareDiscoveryThread() {
        return new Discovery(mContext, mDeviceReceiver);
    }

    private Discovery.DiscoveryReceiver mDeviceReceiver = new Discovery.DiscoveryReceiver() {
        @Override
        public void onReceiveDevices(ArrayList<Device> devices) {

            mDevices = new ArrayList<>(devices);

            DeviceSearchListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.setAdapter(new DeviceSearchAdapter(mContext, mDevices));
                    mLoadingDialog.dismiss();
                }
            });
        }

        @Override
        public void onFailedSearch() {
            DeviceSearchListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAndRefresh();
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home){
            finish();
        }

        if (id == R.id.action_refresh) {
            searchDevices();
        }

        return super.onOptionsItemSelected(item);
    }

    public AlertDialog getDialog() {
        return mDialog;
    }
}
