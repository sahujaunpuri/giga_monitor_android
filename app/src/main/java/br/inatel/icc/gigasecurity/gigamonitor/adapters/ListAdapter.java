//package br.inatel.icc.gigasecurity.gigamonitor.adapters;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import java.util.ArrayList;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
//
///**
// * Created by zappts on 3/23/17.
// */
//
//public class ListAdapter extends ArrayAdapter<DeviceChannelsManager> {
//    private DeviceManager mDeviceManager = DeviceManager.getInstance();
//    private ArrayList<DeviceChannelsManager> channels;
//
//    private static class ViewHolder {
//        private DeviceChannelsManager deviceChannelsManager;
//        private ProgressBar progressBar;
//        private FrameLayout frameLayout;
//    }
//
//
//    public ListAdapter(Context context, int resource, ArrayList<DeviceChannelsManager> objects) {
//        super(context, resource, objects);
//        this.channels = objects;
//    }
//
//    @Override
//    public void add(DeviceChannelsManager object) {
//        super.add(object);
//    }
//
//    @Override
//    public void remove(DeviceChannelsManager object) {
//        super.remove(object);
//    }
//
//    @NonNull
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        View v = convertView;
//
//        if(v == null){
//            final DeviceChannelsManager deviceChannelsManager = getItem(position);
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            v = inflater.inflate(R.layout.list_row, null);
//            FrameLayout  layout = (FrameLayout) v.findViewById(R.id.frame_layout_list);
//
////            v = inflater.inflate(R.layout.overlay_menu_layout, null);
//
//            layout.addView(deviceChannelsManager);
////            layout.addView(v);
//
//            viewHolder = new ViewHolder();
//            viewHolder.deviceChannelsManager = deviceChannelsManager;
//            viewHolder.progressBar = deviceChannelsManager.progressBar;
//            viewHolder.frameLayout = layout;
//            deviceChannelsManager.progressBar.setVisibility(View.VISIBLE);
//            v.setTag(viewHolder);
//        } else{
//            viewHolder = (ViewHolder) v.getTag();
//        }
//
//        return v;
//    }
//
//}
