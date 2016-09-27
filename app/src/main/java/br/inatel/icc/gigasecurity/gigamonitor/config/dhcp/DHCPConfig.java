package br.inatel.icc.gigasecurity.gigamonitor.config.dhcp;

import com.basic.G;
import com.xm.MyConfig;
import com.xm.javaclass.SDK_NetDHCPConfig;
import com.xm.javaclass.SDK_NetDHCPConfigAll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.inatel.icc.gigasecurity.gigamonitor.config.Config2Abstract;

/**
 * File: WifiConfig.java
 * Creation date: 06/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class WifiConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class DHCPConfig extends Config2Abstract<SDK_NetDHCPConfigAll> implements Serializable{

    private List<DHCPItem> dhcpList = new ArrayList<>();
    public DHCPConfig() {
    }


    @Override
    public long getConfigKey() {
        return MyConfig.SdkConfigType.E_SDK_CONFIG_NET_DHCP;
    }

    @Override
    public Class<SDK_NetDHCPConfigAll> getConfigType() {
        return SDK_NetDHCPConfigAll.class;
    }


    @Override
    public void fillFromNetSdkConfig(SDK_NetDHCPConfigAll sdkNetWifiConfig){

        final SDK_NetDHCPConfig[] st_0_vNetDHCPConfig = sdkNetWifiConfig.st_0_vNetDHCPConfig;

        for (int i = 0; i < st_0_vNetDHCPConfig.length ; i++) {
            dhcpList.add(new DHCPItem(st_0_vNetDHCPConfig[i]));
        }
    }

    @Override
    public SDK_NetDHCPConfigAll getAsNetSdkConfig(){
        SDK_NetDHCPConfig[] array = new SDK_NetDHCPConfig[dhcpList.size()];

        for (int i = 0; i < array.length ; i++) {
            array[i] =  dhcpList.get(i).getAsNetSdkConfig();
        }

        mConfig.st_0_vNetDHCPConfig = array;

        return mConfig;
    }

    public List<DHCPItem> getDhcpList() {
        return dhcpList;
    }

    public class DHCPItem {
        String name;
        boolean enable;

        public DHCPItem(SDK_NetDHCPConfig sdkNetDHCPConfig ) {
            fillFromNetSdkConfig(sdkNetDHCPConfig);
        }

        public DHCPItem(String name, boolean enable) {
            this.name = name;
            this.enable = enable;
        }

        public void fillFromNetSdkConfig(SDK_NetDHCPConfig sdkNetDHCPConfig ){
            this.name = G.ToString(sdkNetDHCPConfig.st_1_ifName);
            this.enable = sdkNetDHCPConfig.st_0_bEnable;

        }

        public SDK_NetDHCPConfig getAsNetSdkConfig(){
            SDK_NetDHCPConfig sdkNetDHCPConfig = new SDK_NetDHCPConfig();
            sdkNetDHCPConfig.st_0_bEnable = enable;
            G.SetValue(sdkNetDHCPConfig.st_1_ifName, name);

            return sdkNetDHCPConfig;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
}
