package br.inatel.icc.gigasecurity.gigamonitor.config;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * File: ConfigBasic.java
 * Creation date: 01/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class ConfigBasic.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class ConfigBasic<E> extends ConfigAbstract<E>{

    private long configKey;
    Class<E> klass;

    public ConfigBasic(long configType, Class<E> klass ) {
        this.configKey = configType;
        this.klass = klass;
    }

    public boolean setConfig(Long loginID, E config) throws Exception {
        return DeviceManager.getInstance().setConfig(loginID, configKey,config);
    }

    @Override
    public void fillFromNetSdkConfig(E config) {

    }

    @Override
    public E getAsNetSdkConfig() {
        return null;
    }

    public long getConfigKey() { return configKey; }
    @Override public Class<E> getConfigType() { return null; }

}
