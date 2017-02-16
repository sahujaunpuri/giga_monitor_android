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
import br.inatel.icc.gigasecurity.gigamonitor.core.Discovery;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;


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

        if(getIntent().getExtras() != null){
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

                Bundle extras = new Bundle();
                extras.putSerializable("device", mDevices.get(i));

                Intent intent = new Intent(DeviceSearchListActivity.this, DeviceFormActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
                finish();

                /*String labelSetIP = getResources().getString(R.string.label_set_ip);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(getResources().getString(R.string.label_action))
                        .setItems(new CharSequence[] { getResources().getString(R.string.label_add), labelSetIP },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            Bundle extras = new Bundle();
                                            extras.putSerializable("device", mDevices.get(i));

                                            Intent i = new Intent(DeviceSearchListActivity.this, DeviceFormActivity.class);
                                            i.putExtras(extras);
                                            startActivity(i);

                                            finish();
                                        }else{
                                            showSetOverNetDialog(i, view);
                                        }
                                    }
                                });
                builder.show();*/

            }
        });

    }

    /*private void showSetOverNetDialog(final int i, View view) {
        final Device device = mDevices.get(i);
        final LayoutInflater inflater = getLayoutInflater();
        final View viewDialog = inflater.inflate(R.layout.dialog_set_over_net, null);

        final EditText etIpAddress = (EditText) viewDialog.findViewById(R.id.edit_text_set_over_net_ip_address);
        final EditText etGateway = (EditText) viewDialog.findViewById(R.id.edit_text_set_over_net_gateway);
        final EditText etMask = (EditText) viewDialog.findViewById(R.id.edit_text_set_over_net_mask);

        etIpAddress.setText(device.getIpAddress());
        etGateway.setText(device.getGateway());
        etMask.setText(device.getSubmask());

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        String title = getResources().getString(R.string.label_set_configurations);

        builder.setTitle(title);
        builder.setView(viewDialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setOverNetConfiguration(device);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeviceSearchListActivity.this.getDialog().cancel();
                    }
                });

        mDialog = builder.create();
        mDialog.show();
    }*/


    /*private void setOverNetConfiguration(final Device device) {
        final EditText etUsername = (EditText) getDialog().findViewById(R.id.edit_text_set_over_net_ip_user_name);
        final EditText etPassword = (EditText) getDialog().findViewById(R.id.edit_text_set_over_net_ip_password);
        final EditText etIpAddress = (EditText) getDialog().findViewById(R.id.edit_text_set_over_net_ip_address);
        final EditText etGateway = (EditText) getDialog().findViewById(R.id.edit_text_set_over_net_gateway);
        final EditText etMask = (EditText) getDialog().findViewById(R.id.edit_text_set_over_net_mask);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Setting Devices",
                "Please wait...", true, false);

        final DeviceSearchListActivity activity = this;

        DeviceManager.getInstance().setConfigOverNet(
                device,
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                etIpAddress.getText().toString(),
                etGateway.getText().toString(),
                etMask.getText().toString(),
                device.getMacAddress(),
                new DeviceManager.setConfigOverNetInterface() {
                    @Override
                    public void onSetConfigOverNetSuccess() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchDevices();
                                progressDialog.dismiss();

                                Toast.makeText(DeviceSearchListActivity.this, "Configurações definidas com sucesso!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onSetConfigOverNetError() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("Error SetConfigOverNet", " Error: ");
                                progressDialog.dismiss();

                                Toast.makeText(DeviceSearchListActivity.this, "Erro ao definir configurações.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
        );

    }*/

    public void searchDevices(){
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
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            searchDevices();
        }

        return super.onOptionsItemSelected(item);
    }

    public AlertDialog getDialog() {
        return mDialog;
    }
}
