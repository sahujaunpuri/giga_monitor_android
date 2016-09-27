package br.inatel.icc.gigasecurity.gigamonitor.model;

/**
 * File: LoginMethod.java
 * Creation date: 11/12/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class LoginMethod.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public enum LoginMethod {

    TRY_ALL(0),
    LAN(1),
    CLOUD(2);

    private int value;

    LoginMethod(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public static LoginMethod fromIndex(int index) {
        LoginMethod frt;
        switch (index) {
            case (0): default :
                frt = TRY_ALL;
                break;
            case (1):
                frt = LAN;
                break;
            case (2):
                frt = CLOUD;
                break;
        }
        return frt;
    }
}
