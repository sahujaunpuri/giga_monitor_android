package br.inatel.icc.gigasecurity.gigamonitor.listeners;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;

/**
 * Created by zappts on 3/7/17.
 */

public class RecyclerTouch implements RecyclerView.OnItemTouchListener {
    final String TAG = "RecyclerTouch";
    private ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(DeviceListActivity.mContext, new ScaleListener());
    private  GestureDetector mClickListener = new GestureDetector(DeviceListActivity.mContext, new SimpleGestureDetector());
    private RecyclerView mView;
    private ChannelRecyclerViewAdapter mAdapter;
    private boolean isScaling = false;
    private float mScaleFactor = 1.F;


    @Override
    public boolean onInterceptTouchEvent (RecyclerView rv, MotionEvent e){
        boolean result;
        mAdapter = (ChannelRecyclerViewAdapter) rv.getAdapter();
        mView = rv;
//        rv.requestDisallowInterceptTouchEvent(true);
//        mClickListener.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);

//        final int action = e.getActionMasked();
//        Log.d(TAG, "onTouchEvent: " + action);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN: {
//                Log.d(TAG, "onTouchEvent: DOWN");
//            }
//            break;
//            case MotionEvent.ACTION_MOVE: {
//                Log.d(TAG, "onTouchEvent: MOVE");
//            }
//            break;
//            case MotionEvent.ACTION_CANCEL:{
//                Log.d(TAG, "onTouchEvent: CANCEL");
//            }
//            break;
//            case MotionEvent.ACTION_UP: {
//                Log.d(TAG, "onTouchEvent: UP");
//            }
//            break;
//        }

        return false;
    }

    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){
    }

    @Override
    public void onTouchEvent (RecyclerView rv, MotionEvent e){
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.d(TAG, "onScaleBegin: ");
            isScaling = true;
            mAdapter.disableListScrolling();
            mView.requestDisallowInterceptTouchEvent(true);
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector){
            Log.d(TAG, "onScaleEnd: ");
            isScaling = false;
            mAdapter.enableListScrolling();
            mView.requestDisallowInterceptTouchEvent(false);
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.min(1.F, Math.max(4.F, mScaleFactor));
            Log.d(TAG, "onScale: SCALE ");

            return false;
        }
    }

    private class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            //open menu
            Log.d(TAG, "onSingleTapConfirmed: ");
            mView.requestDisallowInterceptTouchEvent(false);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            //switch to quad 1
            Log.d(TAG, "onDoubleTap: ");
            mView.requestDisallowInterceptTouchEvent(false);
            return true;
        }
    }
}
