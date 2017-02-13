//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.xm.MyConfig;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//
//public class DeviceRemoteControlActivity extends ActionBarActivity {
//
//    public ImageView ivEsc, ivQuad, ivMenu, ivLeft, ivRight, ivUp, ivDown, ivOk;
//    public DeviceManager mManager;
//    public Device mDevice;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device_remote_control);
//
//        initComponents();
//
//
//        ivEsc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_ESC);
//            }
//        });
//
//        ivQuad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_SPLIT);
//            }
//        });
//
//
//        ivMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_MENU);
//            }
//        });
//
//        ivOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_RET);
//            }
//        });
//
//        ivUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_UP);
//            }
//        });
//
//        ivDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_DOWN);
//            }
//        });
//
//        ivRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_RIGHT);
//            }
//        });
//
//        ivLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.remoteControl(mDevice.getLoginID(), MyConfig.NetKeyBoardValue.SDK_NET_KEY_LEFT);
//            }
//        });
//
//    }
//
//    private void initComponents() {
//        ivEsc   = (ImageView) findViewById(R.id.iv_control_esc);
//        ivQuad  = (ImageView) findViewById(R.id.iv_control_quad);
//        ivMenu  = (ImageView) findViewById(R.id.iv_control_menu);
//        ivLeft  = (ImageView) findViewById(R.id.iv_controle_left);
//        ivRight = (ImageView) findViewById(R.id.iv_control_right);
//        ivUp    = (ImageView) findViewById(R.id.iv_control_up);
//        ivDown  = (ImageView) findViewById(R.id.iv_control_down);
//        ivOk    = (ImageView) findViewById(R.id.iv_control_ok);
//
//        mManager = DeviceManager.getInstance();
//        mDevice = (Device) getIntent().getExtras().getSerializable("device");
//    }
//
//}
