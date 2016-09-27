package br.inatel.icc.gigasecurity.gigamonitor.model;

import java.io.Serializable;

/**
 * File: NetDDNSConfig.java
 * Creation date: 14/10/2014
 * Author: Denis Vilela
 * <p/>
 * Purpose: Declaration of class NetDDNSConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class NetDDNSConfig implements Serializable {
    public int ddnsType;
    public boolean enabled;
    public String domainName;
    public String userName;
    public String password;
}
