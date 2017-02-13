//package br.inatel.icc.gigasecurity.gigamonitor.config.wifi;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.WifiListAdapter;
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
//public class WifiSearchActivity extends ActionBarActivity {
//
//    private ListView mListView;
//    private Device mDevice;
//    private WifiAPListConfig mWifiConfig;
//    private AlertDialog mDialog;
//
//    private ArrayList<WifiConfig> mConfigs;
//
//    private DeviceManager mManager;
//
//    private static final int SEARCH_ACTIVITY_REQUEST = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wifi_search);
//
//        mListView = (ListView) findViewById(R.id.list_view_wifi);
//
//        mManager = DeviceManager.getInstance();
//
//        if (savedInstanceState == null) {
//            mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//            searchAP();
//        } else {
//            mDevice = (Device) savedInstanceState.getSerializable("device");
//            mWifiConfig = mDevice.getWifiConfig().getWifiAPListConfig();
//            initData();
//
//        }
//
//
//    }
//    private void initData() {
//        mListView.setAdapter(new WifiListAdapter(this, mWifiConfig));
//        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
////                mListView.setItemChecked(i, true);
////                view.setSelected(true);
//
//                WifiAPListConfig.WifiItem item = (WifiAPListConfig.WifiItem) mListView.getItemAtPosition(i);
//                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//
//                final LayoutInflater inflater = getLayoutInflater();
//
//                builder.setTitle(item.getSSID());
//                builder.setView(inflater.inflate(R.layout.dialog_wifi_password, null))
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                savePasswordAndSetWifi(i);
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                WifiSearchActivity.this.getDialog().cancel();
//                            }
//                        });
//
//                mDialog = builder.create();
//                mDialog.show();
//
////                Intent output = new Intent();
////                output.putExtra("wifiConfig", mWifiConfig.getWifiList().get(i));
//////                setResult(RESULT_OK, output);
//////                finish();
//            }
//        });
//
//    }
//
//    private void savePasswordAndSetWifi(int i) {
//        EditText etPassword = (EditText) getDialog().findViewById(R.id.edit_text_wifi_password);
//        mDevice.getWifiConfig().setKeys(etPassword.getText().toString());
//        WifiAPListConfig.WifiItem item = (WifiAPListConfig.WifiItem) mListView.getItemAtPosition(i);
//        setToWifi(item);
//    }
//
//
//    private void searchAP() {
//        mWifiConfig = mDevice.getWifiConfig().getWifiAPListConfig();
//        mWifiConfig.getConfigTask(this, mDevice.getLoginID(), new ConfigAbstract.ConfigGetTaskListener() {
//            @Override
//            public void onPreFinish(Boolean success) {
//                if(!success){
//                    String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);
//
//                    Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                initData();
//
//            }
//
//            @Override
//            public void onFinish(Boolean success) {}
//        });
//    }
//
//
//    private void setToWifi(WifiAPListConfig.WifiItem item) {
//
//        mDevice.getWifiConfig().fillFromWifiItem(item);
//        mDevice.getWifiConfig().setConfigTask(getDialog().getContext(), mDevice.getLoginID(), new ConfigAbstract.ConfigSetTaskListener() {
//            @Override
//            public void onFinish(Boolean success) {
//                if(!success){
//                    String labelUnableConfig = getResources().getString(R.string.label_unable_config, true);
//
//                    Toast.makeText(getApplicationContext(), labelUnableConfig, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                initData();
//            }
//        });
//    }
//
//    public void startEditWifiActivity() {
//        Intent intent = new Intent(this, WifiConfigActivity.class);
//        Bundle extras = new Bundle();
//        extras.putSerializable("device", mDevice);
//        intent.putExtras(extras);
//        startActivity(intent);
//    }
//
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        outState.putSerializable("device", mDevice);
//        super.onSaveInstanceState(outState);
//    }
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.wifi_search, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_edit:
//                startEditWifiActivity();;
//                return true;
//            case R.id.action_refresh:
//                searchAP();
//                return true;
//            case android.R.id.home:
//                finish();
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    public AlertDialog getDialog() {
//        return mDialog;
//    }
//}
