package br.inatel.icc.gigasecurity.gigamonitor.config;

import java.lang.reflect.ParameterizedType;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * File: AbstractConfig.java
 * Creation date: 06/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class AbstractConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public abstract class Config2Abstract<E> extends ConfigAbstract<E> {

    private String TAG = Config2Abstract.class.getSimpleName()+":"+ ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Override
    public E getConfig(Long loginID) throws Exception {
        final boolean success = DeviceManager.getInstance().getConfig2(loginID, getConfigKey(), getConfigEntity());
        if(success){
            return getConfigEntity();
        }
        return null;
    }

    @Override
    public boolean setConfig(Long loginID) throws Exception {
        return DeviceManager.getInstance().setConfig2(loginID, (int) getConfigKey(), getConfigEntity());
    }


}


