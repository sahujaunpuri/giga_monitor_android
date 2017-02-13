//package br.inatel.icc.gigasecurity.gigamonitor.config.general;
//
//import com.xm.MyConfig;
//import com.xm.Systime;
//
//import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigAbstract;
//
///**
// * File: GeneralDateConfig.java
// * Creation date: 13/10/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class GeneralDateConfig.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class GeneralDateConfig extends ConfigAbstract<Systime> {
//    private int year, monthOfYear, dayOfMonth, dayOfWeek, hour, minute, second;
//    private boolean dst;
//
//    @Override
//    public void fillFromNetSdkConfig(Systime config) {
//        setYear(config.year);
//        setMonthOfYear(config.month);
//        setDayOfMonth(config.day);
//        setDayOfWeek(config.wday);
//        setHour(config.hour);
//        setMinute(config.minute);
//        setSecond(config.second);
//        setDst(config.isdst == 1);
//    }
//
//    @Override
//    public Systime getAsNetSdkConfig() {
//        Systime systime = new Systime();
//        systime.year = getYear();
//        systime.month = getMonthOfYear();
//        systime.day = getDayOfMonth();
//        systime.wday = getDayOfWeek();
//        systime.hour = getHour();
//        systime.minute = getMinute();
//        systime.second = getSecond();
//        systime.isdst = dst ? 1 : 0;
//        return systime;
//    }
//
//    @Override
//    public long getConfigKey() {
//        return MyConfig.SdkConfigType.E_SDK_CONFIG_SYS_TIME;
//    }
//
//    @Override
//    public Class<Systime> getConfigType() {
//        return Systime.class;
//    }
//
//    public int getYear() {
//        return year;
//    }
//
//    public GeneralDateConfig setYear(int year) {
//        this.year = year;
//        return this;
//    }
//
//    public int getMonthOfYear() {
//        return monthOfYear;
//    }
//
//    public GeneralDateConfig setMonthOfYear(int monthOfYear) {
//        this.monthOfYear = monthOfYear;
//        return this;
//    }
//
//    public int getDayOfMonth() {
//        return dayOfMonth;
//    }
//
//    public GeneralDateConfig setDayOfMonth(int dayOfMonth) {
//        this.dayOfMonth = dayOfMonth;
//        return this;
//    }
//
//    public int getDayOfWeek() {
//        return dayOfWeek;
//    }
//
//    public GeneralDateConfig setDayOfWeek(int dayOfWeek) {
//        this.dayOfWeek = dayOfWeek;
//        return this;
//    }
//
//    public int getHour() {
//        return hour;
//    }
//
//    public GeneralDateConfig setHour(int hour) {
//        this.hour = hour;
//        return this;
//    }
//
//    public int getMinute() {
//        return minute;
//    }
//
//    public GeneralDateConfig setMinute(int minute) {
//        this.minute = minute;
//        return this;
//    }
//
//    public int getSecond() {
//        return second;
//    }
//
//    public GeneralDateConfig setSecond(int second) {
//        this.second = second;
//        return this;
//    }
//
//    public boolean isDst() {
//        return dst;
//    }
//
//    public GeneralDateConfig setDst(boolean dst) {
//        this.dst = dst;
//        return this;
//    }
//}
