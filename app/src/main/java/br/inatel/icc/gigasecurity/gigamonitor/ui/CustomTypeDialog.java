package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by ZapptsDev on 25/01/18.
 */

public class CustomTypeDialog extends Dialog {
    private final OnDialogClickListener listener;
    DeviceManager mDeviceManager = DeviceManager.getInstance();
    List <Device> mDevices = new ArrayList<>();
    Device mDevice;
    TextView mTextViewDialog7, mTextViewCancel;
    LinearLayout mLinearLayoutButtonCloud3, mLinearLayoutCloud3, mLinearLayoutCloud3BtnReboot;
    ImageButton mImageButtonCloud3;
    ImageView mImageViewUpdate;
    Context context;
    JSONObject jsonObjectToSend = null;
    String TAG = "CustomTypeDialog";

    public CustomTypeDialog(final Context context, Device device, final OnDialogClickListener listener) {
        super(context);
        init();
        this.listener = listener;
        this.context = context;

        if (device != null) {
            mDevices.add(device);
        } else {
            mDevices = mDeviceManager.getDevices();
        }

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
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String message = "Não foi possível configurar os seguintes dispositivos: ";
                        if (mDeviceManager.devicesWithJsonError.size() > 0) {
                            for (Device device : mDeviceManager.devicesWithJsonError) {
                                message += device.getDeviceName() + " ";
                            }
                            mTextViewDialog7.setText(message);
                        } else {
                            mTextViewDialog7.setText("Dispositivo otimizado com sucesso!");
                        }
                        setCancelable(true);
                    }
                }, 9000);
            }
        });

        show();
    }

    public void setStreamingConfig(List <Device> devices) {
        for (Device device : devices) {
            if (!device.getSerialNumber().equals("Favoritos")) {
                this.mDevice = device;
                mDeviceManager.getJsonConfig(device,"Simplify.Encode", configListener);
            }
        }
    }

    public interface OnDialogClickListener {
        void onDialogImageRunClick();
    }

    private ConfigListener configListener = new ConfigListener() {
        @Override
        public void onReceivedConfig() {
                mDeviceManager.setJsonConfig(mDevice, "Simplify.Encode", mDevice.getSimplifyEncodeJson(), configListener);
        }

        @Override
        public void onSetConfig() {
            mDeviceManager.rebootDevice(mDevice);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 1000);

        }

        @Override
        public void onError() {
            if (!mDeviceManager.devicesWithJsonError.contains(mDevice)) {
                mDeviceManager.devicesWithJsonError.add(mDevice);
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        if (mDeviceManager.devicesWithJsonError.size() > 0) {
            String message = "Não foi possível configurar os seguintes dispositivos: ";
            for (Device device : mDeviceManager.devicesWithJsonError) {
                message += device.getDeviceName() + " ";
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
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
}
