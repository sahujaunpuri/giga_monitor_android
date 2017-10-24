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
    private ImageView topLeftArrow, leftArrow, rightArrow, topRightArrow, upArrow, downLeftArrow, downArrow, downRightArrow, zoomIn, zoomOut, ivTouch, focusIn, focusOut, irisUp, irisDown/*, ptz*/;
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
        topRightArrow = (ImageView) findViewById(R.id.arrow_top_right);
        topRightArrow.setOnTouchListener(this);
        rightArrow = (ImageView) findViewById(R.id.arrow_right);
        rightArrow.setOnTouchListener(this);
        upArrow = (ImageView) findViewById(R.id.arrow_up);
        upArrow.setOnTouchListener(this);
        downLeftArrow = (ImageView) findViewById(R.id.arrow_down_left);
        downLeftArrow.setOnTouchListener(this);
        downArrow = (ImageView) findViewById(R.id.arrow_down);
        downArrow.setOnTouchListener(this);
        downRightArrow = (ImageView) findViewById(R.id.arrow_down_right);
        downRightArrow.setOnTouchListener(this);
        zoomIn = (ImageView) findViewById(R.id.zoom_in);
        zoomIn.setOnTouchListener(this);
        zoomOut = (ImageView) findViewById(R.id.zoom_out);
        zoomOut.setOnTouchListener(this);
        ivTouch = (ImageView) findViewById(R.id.touch_icon);
        focusIn = (ImageView) findViewById(R.id.focus_in);
        focusIn.setOnTouchListener(this);
        focusOut = (ImageView) findViewById(R.id.focus_out);
        focusOut.setOnTouchListener(this);
        irisUp = (ImageView) findViewById(R.id.iris_up);
        irisUp.setOnTouchListener(this);
        irisDown = (ImageView) findViewById(R.id.iris_down);
        irisDown.setOnTouchListener(this);
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

    public void setPartialVisibility(int visibility){
        zoomIn.setVisibility(visibility);
        zoomOut.setVisibility(visibility);
        irisUp.setVisibility(visibility);
        irisDown.setVisibility(visibility);
        focusIn.setVisibility(visibility);
        focusOut.setVisibility(visibility);
        ivTouch.setVisibility(visibility);
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
        if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN || motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.arrow_top_left:
                    int topLeftCommand = EPTZCMD.PAN_LEFTTOP;
                    manageImageTouch(motionEvent, topLeftCommand);
                    return true;
                case R.id.arrow_left:
                    int leftCommand = EPTZCMD.PAN_LEFT;
                    manageImageTouch(motionEvent, leftCommand);
                    return true;
                case R.id.arrow_top_right:
                    int topRightCommand = EPTZCMD.PAN_RIGTHTOP;
                    manageImageTouch(motionEvent, topRightCommand);
                    return true;
                case R.id.arrow_right:
                    int rightCommand = EPTZCMD.PAN_RIGHT;
                    manageImageTouch(motionEvent, rightCommand);
                    return true;
                case R.id.arrow_up:
                    int upCommand = EPTZCMD.TILT_UP;
                    manageImageTouch(motionEvent, upCommand);
                    return true;
                case R.id.arrow_down_left:
                    int downLeftCommand = EPTZCMD.PAN_LEFTDOWN;
                    manageImageTouch(motionEvent, downLeftCommand);
                    return true;
                case R.id.arrow_down:
                    int downCommand = EPTZCMD.TILT_DOWN;
                    manageImageTouch(motionEvent, downCommand);
                    return true;
                case R.id.arrow_down_right:
                    int downRightCommand = EPTZCMD.PAN_RIGTHDOWN;
                    manageImageTouch(motionEvent, downRightCommand);
                    return true;
                case R.id.zoom_in:
                    int zoomInCommand = EPTZCMD.ZOOM_IN;
                    Log.d("ptz", "onTouch: in " + motionEvent);
                    manageImageTouch(motionEvent, zoomInCommand);
                    return true;
                case R.id.zoom_out:
                    int zoomOutCommand = EPTZCMD.ZOOM_OUT;
                    Log.d("ptz", "onTouch: out " + motionEvent);
                    manageImageTouch(motionEvent, zoomOutCommand);
                    return true;
                case R.id.focus_in:
                    int focusInCommand = EPTZCMD.FOCUS_NEAR;
                    manageImageTouch(motionEvent, focusInCommand);
                    return true;
                case R.id.focus_out:
                    int focusOutCommand = EPTZCMD.FOCUS_FAR;
                    manageImageTouch(motionEvent, focusOutCommand);
                    return true;
                case R.id.iris_up:
                    int irisUpCommand = EPTZCMD.IRIS_OPEN;
                    manageImageTouch(motionEvent, irisUpCommand);
                    return true;
                case R.id.iris_down:
                    int irisDownCommand = EPTZCMD.IRIS_CLOSE;
                    manageImageTouch(motionEvent, irisDownCommand);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}
