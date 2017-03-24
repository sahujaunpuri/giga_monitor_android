package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

/**
 * Created by zappts on 3/23/17.
 */

public class ListAdapter extends ArrayAdapter<SurfaceViewComponent> {
    private DeviceManager mDeviceManager = DeviceManager.getInstance();
    private ArrayList<SurfaceViewComponent> channels;

    private static class ViewHolder {
        private SurfaceViewComponent surfaceViewComponent;
        private ProgressBar progressBar;
        private FrameLayout frameLayout;
    }


    public ListAdapter(Context context, int resource, ArrayList<SurfaceViewComponent> objects) {
        super(context, resource, objects);
        this.channels = objects;
    }

    @Override
    public void add(SurfaceViewComponent object) {
        super.add(object);
    }

    @Override
    public void remove(SurfaceViewComponent object) {
        super.remove(object);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View v = convertView;

        if(v == null){
            final SurfaceViewComponent surfaceViewComponent = getItem(position);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_row, null);
            FrameLayout  layout = (FrameLayout) v.findViewById(R.id.frame_layout_list);

            layout.addView(surfaceViewComponent);

            viewHolder = new ViewHolder();
            viewHolder.surfaceViewComponent = surfaceViewComponent;
            viewHolder.progressBar = surfaceViewComponent.progressBar;
            viewHolder.frameLayout = layout;
            surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
            v.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) v.getTag();
        }

        return v;
    }

}
