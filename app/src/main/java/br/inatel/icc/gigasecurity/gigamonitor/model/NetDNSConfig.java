package br.inatel.icc.gigasecurity.gigamonitor.model;

import com.xm.javaclass.SDK_NetDNSConfig;

import java.io.Serializable;

/**
 * File: NetDNSConfig.java
 * Temporary file until the SDK will be ready
 */
public class NetDNSConfig implements Serializable{

    SDK_NetDNSConfig sdkNetDNSConfig;
    public String PrimaryDNS = String.valueOf(sdkNetDNSConfig.st_0_PrimaryDNS);
    public String SecondaryDNS = String.valueOf(sdkNetDNSConfig.st_1_SecondaryDNS);
}
