package br.inatel.icc.gigasecurity.gigamonitor.managers;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by zappts on 3/15/17.
 */

public class CustomGridLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollHorizontally();
    }


}
