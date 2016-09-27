package br.inatel.icc.gigasecurity.gigamonitor.config;

/**
 * File: SetConfigOverNet.java
 * Creation date: 02/02/2015
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class SetConfigOverNet.java
 * Copyright 2015, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class SetConfigOverNet extends ConfigAbstract<Boolean> {

    @Override
    public Boolean getConfig(Long loginID) throws Exception {
        return super.getConfig(loginID);
    }

    @Override
    public void fillFromNetSdkConfig(Boolean config) {

    }

    @Override
    public Boolean getAsNetSdkConfig() {
        return null;
    }

    @Override
    public long getConfigKey() {
        return 0;
    }

    @Override
    public Class<Boolean> getConfigType() {
        return null;
    }
}
