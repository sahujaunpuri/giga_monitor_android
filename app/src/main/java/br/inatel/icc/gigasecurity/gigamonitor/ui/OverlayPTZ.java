package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lib.EPTZCMD;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * Created by zappts on 03/07/17.
 */

public class OverlayPTZ extends RelativeLayout implements View.OnTouchListener  {

    private Context mContext;
    public SurfaceViewComponent surfaceViewComponent;
    private ImageView topLeftArrow, leftArrow, rightArrow, upArrow, downArrow, zoomIn, zoomOut, ptz;
    private DeviceManager deviceManager;

    public OverlayPTZ(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OverlayPTZ(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public OverlayPTZ(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ptz_overlay, this);

        topLeftArrow = (ImageView) findViewById(R.id.arrow_top_left);
        topLeftArrow.setOnTouchListener(this);
        leftArrow = (ImageView) findViewById(R.id.arrow_left);
        leftArrow.setOnTouchListener(this);
        rightArrow = (ImageView) findViewById(R.id.arrow_right);
        rightArrow.setOnTouchListener(this);
        upArrow = (ImageView) findViewById(R.id.arrow_up);
        upArrow.setOnTouchListener(this);
        downArrow = (ImageView) findViewById(R.id.arrow_down);
        downArrow.setOnTouchListener(this);
        zoomIn = (ImageView) findViewById(R.id.zoom_in);
        zoomIn.setOnTouchListener(this);
        zoomOut = (ImageView) findViewById(R.id.zoom_out);
        zoomOut.setOnTouchListener(this);
//        ptz = (ImageView) findViewById(R.id.iv_ptz);
//        ptz.setOnDragListener(this);

        deviceManager = DeviceManager.getInstance();

    }

//    public void setDeviceChannelsManager(ChannelsManager deviceChannelsManager) {
//        this.deviceChannelsManager = deviceChannelsManager;
//    }

    public void setSurfaceViewComponent(SurfaceViewComponent svc) {
        this.surfaceViewComponent = svc;
    }

    private void manageImageTouch(MotionEvent motionEvent, int command) {
        int action = motionEvent.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Finger", "Pressed");
                surfaceViewComponent.ptz(command, false);
            case MotionEvent.ACTION_UP:
                Log.i("Finger", "UP");
                surfaceViewComponent.ptz(command, true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.arrow_top_left:
                int topLeftCommand = EPTZCMD.PAN_LEFTTOP;
                manageImageTouch(motionEvent, topLeftCommand);
                return true;
            case R.id.arrow_left:
                int leftCommand = EPTZCMD.PAN_LEFT;
                manageImageTouch(motionEvent, leftCommand);
                return true;
            case R.id.arrow_right:
                int rightCommand = EPTZCMD.PAN_RIGHT;
                manageImageTouch(motionEvent, rightCommand);
                return true;
            case R.id.arrow_up:
                int upCommand = EPTZCMD.TILT_UP;
                manageImageTouch(motionEvent, upCommand);
                return true;
            case R.id.arrow_down:
                int downCommand = EPTZCMD.TILT_DOWN;
                manageImageTouch(motionEvent, downCommand);
                return true;
            case R.id.zoom_in:
                int zoomInCommand = EPTZCMD.ZOOM_IN;
                manageImageTouch(motionEvent, zoomInCommand);
                return true;
            case R.id.zoom_out:
                int zoomOutCommand = EPTZCMD.ZOOM_OUT;
                manageImageTouch(motionEvent, zoomOutCommand);
                return true;
            default:
                break;
        }
        return false;
    }
}
