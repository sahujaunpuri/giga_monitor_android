//package br.inatel.icc.gigasecurity.gigamonitor.model;
//
//import java.io.Serializable;
//
///**
// * File: SystimeGiga.java
// * Creation date: 04/12/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class SystimeGiga.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class SystimeGiga implements Serializable {
//    public int year;
//    public int month;
//    public int day;
//    public int wday;
//    public int hour;
//    public int minute;
//    public int second;
//    public int isdst;
//
//    public SystimeGiga(com.xm.Systime sdkSystime) {
//        this.year = sdkSystime.year;
//        this.month = sdkSystime.month;
//        this.day = sdkSystime.day;
//        this.wday = sdkSystime.wday;
//        this.hour = sdkSystime.hour;
//        this.minute = sdkSystime.minute;
//        this.second = sdkSystime.second;
//        this.isdst = sdkSystime.isdst;
//    }
//
//    public com.xm.Systime getSdkSystime() {
//        com.xm.Systime s = new com.xm.Systime();
//        s.year = this.year;
//        s.month = this.month;
//        s.day = this.day;
//        s.wday = this.wday;
//        s.hour = this.hour;
//        s.minute = this.minute;
//        s.second = this.second;
//        s.isdst = this.isdst;
//        return s;
//    }
//}