package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by ZapptsDev on 25/01/18.
 */

public class CustomTypeDialog extends Dialog {
    private final OnDialogClickListener listener;
    DeviceManager mDeviceManager = DeviceManager.getInstance();
    ArrayList<Device> mDevices = mDeviceManager.getDevices();
    TextView mTextViewDialog7, mTextViewCancel;
    LinearLayout mLinearLayoutButtonCloud3, mLinearLayoutCloud3, mLinearLayoutCloud3BtnReboot;
    ImageButton mImageButtonCloud3;
    ImageView mImageViewUpdate;
    Context context;

    public CustomTypeDialog(final Context context, final OnDialogClickListener listener) {
        super(context);
        init();
        this.listener = listener;
        this.context = context;


        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mImageButtonCloud3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextViewDialog7.setVisibility(View.VISIBLE);
                mImageButtonCloud3.setVisibility(View.GONE);
                mTextViewCancel.setVisibility(View.GONE);
                setStreamingConfig(mDevices);
                listener.onDialogImageRunClick();
                newCloudConfigSuccess();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        mLinearLayoutButtonCloud3.setVisibility(View.GONE);
                        mLinearLayoutCloud3.setVisibility(View.GONE);
                    }
                }, 9000);

            }
        });

        show();
    }

    public void setStreamingConfig(ArrayList<Device> devices) {
        for (Device device : devices) {
            if (!device.getSerialNumber().equals("Favoritos")) {
                mDeviceManager.getJsonConfig(device,"Simplify.Encode", null);
                //                mDeviceManager.rebootDevice(device);
            }
        }
    }

    public void newCloudConfigSuccess () {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cloud2", false);
        editor.commit();
        Log.d("CustomTypeDialog", "NEW CLOUD CONFIG SUCCESS");
    }

    public void init () {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.alert_dialog_cloud3);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;

        mTextViewDialog7 = (TextView) findViewById(R.id.text_view_dialog_7);
        mTextViewCancel = (TextView) findViewById(R.id.text_view_custom_dialog_cancel);
        mLinearLayoutButtonCloud3 = (LinearLayout) findViewById(R.id.linear_layout_button_cloud_3);
        mImageButtonCloud3 = (ImageButton) findViewById(R.id.button_cloud_3);
        mImageViewUpdate = (ImageView) findViewById(R.id.image_view_update);
        mLinearLayoutCloud3 = (LinearLayout) findViewById(R.id.linear_layout_cloud_3);
        mLinearLayoutCloud3BtnReboot = (LinearLayout) findViewById(R.id.linear_layout_cloud_3_reboot_btn);
    }

    public interface OnDialogClickListener {
        void onDialogImageRunClick();
    }

}
