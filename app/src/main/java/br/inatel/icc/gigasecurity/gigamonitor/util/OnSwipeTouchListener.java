package br.inatel.icc.gigasecurity.gigamonitor.util;


import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by filipecampos on 27/07/2015.
 */
public class OnSwipeTouchListener extends Activity implements OnGestureListener {

    private Context mContext;

    public OnSwipeTouchListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1.getX() < e2.getX()) {
            Toast.makeText(mContext, "right", Toast.LENGTH_SHORT).show();
        }

        if (e1.getX() > e2.getX()) {
            Toast.makeText(mContext, "left", Toast.LENGTH_SHORT).show();
        }

        if (e1.getY() < e2.getY()) {
            Toast.makeText(mContext, "up", Toast.LENGTH_SHORT).show();
        }

        if (e1.getY() > e2.getY()) {
            Toast.makeText(mContext, "down", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
