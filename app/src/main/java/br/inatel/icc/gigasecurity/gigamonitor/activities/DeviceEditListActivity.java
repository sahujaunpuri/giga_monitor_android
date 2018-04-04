package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceEditAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class DeviceEditListActivity extends ActionBarActivity implements View.OnClickListener {
    private TextView tvBack;
    private ImageView ivDone, ivAdd;
    private final String TAG = "DeviceEdit";
    private DeviceEditAdapter mAdapter;
    private ArrayList<Device> mDevices;
    private DeviceManager mDeviceManager;
    private ArrayList<Integer> devicesRemoved = new ArrayList<Integer>();

    public static DragSortListView lv;
    private boolean deleted, moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device_list);

        this.deleted = false;
        this.moved = false;

        mDeviceManager = DeviceManager.getInstance();
        mDevices = (ArrayList<Device>) mDeviceManager.getDevices().clone();

        initComponents();
        setListeners();

        mAdapter = new DeviceEditAdapter(this, mDevices);
        lv.setAdapter(mAdapter);

        getSupportActionBar().hide();
    }

    private void initComponents() {
        lv = (DragSortListView) findViewById(R.id.lv_drag_sort_list_view) ;
        tvBack = (TextView) findViewById(R.id.text_view_back);
        ivAdd  = (ImageView) findViewById(R.id.image_view_add);
        ivDone = (ImageView) findViewById(R.id.image_view_done);
        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragScrollProfile(ssProfile);
    }

    private void startDeviceListActivity() {
        Intent i = new Intent(this, DeviceListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void setListeners() {
        tvBack.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivDone.setOnClickListener(this);
    }


    //onDrop change position of the list item.
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    Device item = (Device) mAdapter.getItem(from);

                    mAdapter.notifyDataSetChanged();
                    mDevices.remove(from);
                    mDevices.add(to, item);

                    mAdapter = new DeviceEditAdapter(DeviceEditListActivity.this, mDevices);
                    lv.setAdapter(mAdapter);
//                    mAdapter.notifyDataSetChanged();

                    moved = true;
                }
            };

    //onRemove remove item of the list.
    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    devicesRemoved.add(((Device)mAdapter.getItem(which)).getId());
                    mDevices.remove(mAdapter.getItem(which));


//                    mAdapter.notifyDataSetChanged();
                    mAdapter = new DeviceEditAdapter(DeviceEditListActivity.this, mDevices);
                    lv.setAdapter(mAdapter);


                    deleted = true;
                }
            };

    private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) mAdapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    @Override
    public void onResume(){
        super.onResume();
        mDevices = mDeviceManager.getDevices();
        mAdapter = new DeviceEditAdapter(DeviceEditListActivity.this, mDevices);
        lv.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_edit_device_list, menu);
        return true;
    }

    private void exitAndSave(){
        if(deleted) {
            for (int id : devicesRemoved) {
                mDeviceManager.removeDeviceFromFavorite(id);
            }
        }
        mDeviceManager.invalidateExpandableList();
        mDeviceManager.updateDevices(mDevices);
        mDeviceManager.updateSurfaceViewManagers();
        startDeviceListActivity();
    }

    private void startInitialActivity() {
        Intent i = new Intent(this, InitialActivity.class);

        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            startInitialActivity();
            return true;
        }
        if (id == R.id.action_finish) {
            if(!deleted && !moved)
                finish();
            else {
                exitAndSave();
            }
            return true;
        }
        if (id == android.R.id.home) {
            if (deleted || moved) {
                showBackConfirmation();
            } else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBackConfirmation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.exit_config_dialog, null);
        dialog.setView(view);
        final AlertDialog alert = dialog.create();
        Button cancel = (Button) view.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        Button dontSave = (Button) view.findViewById(R.id.dont_save);
        dontSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        alert.show();
        Button ok = (Button) view.findViewById(R.id.ok_action);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAndSave();
            }
        });
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_back:
                if (deleted || moved) {
                    showBackConfirmation();
                } else {
                    finish();
                }
                break;
            case R.id.image_view_add:
                startInitialActivity();
                break;
            case R.id.image_view_done:
                exitAndSave();
                break;
            default:
                break;
        }
    }
}
