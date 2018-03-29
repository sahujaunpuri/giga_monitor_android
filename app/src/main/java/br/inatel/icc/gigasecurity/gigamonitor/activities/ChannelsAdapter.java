package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.inatel.icc.gigasecurity.gigamonitor.R;

/**
 * Created by zappts on 21/03/18.
 */

public class ChannelsAdapter extends ArrayAdapter<Channel> {

    public ChannelsAdapter(Context context, List<Channel> order) {
        super(context, 0, order);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.channel_list_item, parent, false);
        }

        Channel currentChannel = getItem(position);

        TextView positionView = (TextView) listItemView.findViewById(R.id.channel_position);
        positionView.setText(Integer.toString(currentChannel.getChannelNewGrid()+1));

        TextView nameView = (TextView) listItemView.findViewById(R.id.channel_name);
        nameView.setText(currentChannel.getChannelId());

        return listItemView;
    }

}



