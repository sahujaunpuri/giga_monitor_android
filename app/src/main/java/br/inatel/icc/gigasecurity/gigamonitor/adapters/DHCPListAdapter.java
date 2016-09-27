package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.config.dhcp.DHCPConfig;

/**
 * DeviceListAdapter
 * Created by rinaldo.bueno on 27/08/2014.
 */
public class DHCPListAdapter extends ArrayAdapter<DHCPConfig.DHCPItem> {

    private List<DHCPConfig.DHCPItem> mList;

    public DHCPListAdapter(Context context, DHCPConfig dhcpConfig) {
        super(context, R.layout.dhcp_config_item, R.id.tv_dhcp, dhcpConfig.getDhcpList());
        this.mList = dhcpConfig.getDhcpList();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView name = (TextView) view.findViewById(R.id.tv_dhcp);

        final Switch enableSwitch = (Switch) view.findViewById(R.id.s_dhcp);

        DHCPConfig.DHCPItem dhpc = mList.get(position);

        if (dhpc != null) {
            name.setText(dhpc.getName());
            enableSwitch.setChecked(dhpc.isEnable());

            enableSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean isChecked = enableSwitch.isChecked();
                    DHCPConfig.DHCPItem dhpc = mList.get(position);
                    dhpc.setEnable(isChecked);
                }
            });

        }
        return view;
    }

}