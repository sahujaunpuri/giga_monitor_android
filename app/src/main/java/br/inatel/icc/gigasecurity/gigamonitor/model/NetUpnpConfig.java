package br.inatel.icc.gigasecurity.gigamonitor.model;

/**
 * File: Undsa.java
 * Creation date: 29/09/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class NetUpnpConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class NetUpnpConfig {
    private boolean enabled;
    private boolean state; // true = OK, false = NOK
    private int httpPort;
    private int mediaPort;
    private int mobilePort;

//    TODO finish after SDK correction
//    public NetUpnpConfig(SDK_NetUPNPConfig sdkConfig) {
//
//    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getMediaPort() {
        return mediaPort;
    }

    public void setMediaPort(int mediaPort) {
        this.mediaPort = mediaPort;
    }

    public int getMobilePort() {
        return mobilePort;
    }

    public void setMobilePort(int mobilePort) {
        this.mobilePort = mobilePort;
    }
}
