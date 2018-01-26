package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

public class DeviceRemoteControlActivity extends ActionBarActivity {

    public TextView tvCancel;
    public ImageView ivEsc, ivQuad, ivMenu, ivLeft, ivRight, ivUp, ivDown, ivOk;
    public DeviceManager mManager;
    public Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_remote_control);

        initComponents();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivEsc);
                mManager.remoteControl(mDevice, "Esc", "KeyDown");
                mManager.remoteControl(mDevice, "Esc", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_ESC);
            }
        });

        ivQuad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivQuad);
                mManager.remoteControl(mDevice, "Split", "KeyDown");
                mManager.remoteControl(mDevice, "Split", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_SPLIT);
            }
        });


        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivMenu);
                mManager.remoteControl(mDevice, "Menu", "KeyDown");
                mManager.remoteControl(mDevice, "Menu", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_MENU);
            }
        });

        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivOk);
                mManager.remoteControl(mDevice, "Enter", "KeyDown");
                mManager.remoteControl(mDevice, "Enter", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_RET);
            }
        });

        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivUp);
                mManager.remoteControl(mDevice, "Up", "KeyDown");
                mManager.remoteControl(mDevice, "Up", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_UP);
            }
        });

        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivDown);
                mManager.remoteControl(mDevice, "Down", "KeyDown");
                mManager.remoteControl(mDevice, "Down", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_DOWN);
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivRight);
                mManager.remoteControl(mDevice, "Right", "KeyDown");
                mManager.remoteControl(mDevice, "Right", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_RIGHT);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blink(ivLeft);
                mManager.remoteControl(mDevice, "Left", "KeyDown");
                mManager.remoteControl(mDevice, "Left", "KeyUp");
//                mManager.remoteControl(mDevice, SDKCONST.NetKeyBoardValue.SDK_NET_KEY_LEFT);
            }
        });

        getSupportActionBar().hide();

    }

    private void initComponents() {
        ivEsc     = (ImageView) findViewById(R.id.iv_control_esc);
        ivQuad    = (ImageView) findViewById(R.id.iv_control_quad);
        ivMenu    = (ImageView) findViewById(R.id.iv_control_menu);
        ivLeft    = (ImageView) findViewById(R.id.iv_controle_left);
        ivRight   = (ImageView) findViewById(R.id.iv_control_right);
        ivUp      = (ImageView) findViewById(R.id.iv_control_up);
        ivDown    = (ImageView) findViewById(R.id.iv_control_down);
        ivOk      = (ImageView) findViewById(R.id.iv_control_ok);
        tvCancel  = (TextView) findViewById(R.id.text_view_cancel);

        mManager = DeviceManager.getInstance();
//        mDevice = (Device) getIntent().getExtras().getSerializable("device");

        int devicePosition = (int) getIntent().getExtras().getSerializable("device");
        mDevice = mManager.getDevices().get(devicePosition);
    }

    private void blink(ImageView imageButton){
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(150);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        imageButton.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simple_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
