package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * Created by ZapptsDev on 25/01/18.
 */

public class CustomTypeDialog extends Dialog {
    private final OnDialogClickListener listener;

    public CustomTypeDialog(final Context context, final OnDialogClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.alert_dialog_cloud3);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.listener = listener;
        final DeviceManager mDeviceManager;


        getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;

        final TextView mTextViewDialog7 = (TextView) findViewById(R.id.text_view_dialog_7);
        final TextView mTextViewCancel = (TextView) findViewById(R.id.text_view_custom_dialog_cancel);
        final LinearLayout mLinearLayoutButtonCloud3 = (LinearLayout) findViewById(R.id.linear_layout_button_cloud_3);
        final ImageButton mImageButtonCloud3 = (ImageButton) findViewById(R.id.button_cloud_3);
        final ImageView mImageViewUpdate = (ImageView) findViewById(R.id.image_view_update);
        final LinearLayout mLinearLayoutCloud3 = (LinearLayout) findViewById(R.id.linear_layout_cloud_3);
        final LinearLayout mLinearLayoutCloud3BtnReboot = (LinearLayout) findViewById(R.id.linear_layout_cloud_3_reboot_btn);
        mDeviceManager = DeviceManager.getInstance();

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
                mDeviceManager.rebootAllDevices();
                listener.onDialogImageRunClick();

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLinearLayoutCloud3BtnReboot.setVisibility(View.VISIBLE);
                        mLinearLayoutButtonCloud3.setVisibility(View.GONE);
                        mLinearLayoutCloud3.setVisibility(View.GONE);
                    }
                }, 4000);

            }
        });

        mImageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();

                dismiss();
            }
        });

        show();
    }

    public interface OnDialogClickListener {
        void onDialogImageRunClick();
    }
}
