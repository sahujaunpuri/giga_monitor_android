//package br.inatel.icc.gigasecurity.gigamonitor.model;
//
//import java.io.Serializable;
//
///**
// * File: FileDataGiga.java
// * Creation date: 03/12/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class FileDataGiga.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class FileDataGiga implements Serializable {
//    public String sFileName;
//    public int size;
//    public int filetype;
//    public int ch;
//    public SystimeGiga stBeginTime;
//    public SystimeGiga stEndTime;
//
//    public FileDataGiga(com.xm.FileData fileData) {
//        super();
//        this.sFileName = fileData.sFileName;
//        this.size = fileData.size;
//        this.filetype = fileData.filetype;
//        this.ch = fileData.ch;
//        this.stBeginTime = new SystimeGiga(fileData.stBeginTime);
//        this.stEndTime = new SystimeGiga(fileData.stEndTime);
//    }
//
//    public com.xm.FileData getSdkFileData() {
//        com.xm.FileData f = new com.xm.FileData();
//        f.sFileName = this.sFileName;
//        f.size = this.size;
//        f.filetype = this.filetype;
//        f.ch = this.ch;
//        f.stBeginTime = this.stBeginTime.getSdkSystime();
//        f.stEndTime =this.stEndTime.getSdkSystime();
//        return f;
//    }
//
//    public int getStartSecond() {
//        return 3600 * stBeginTime.hour + 60 * stBeginTime.minute + stBeginTime.second;
//    }
//
//    private int getEndSecond() {
//        return 3600 * stEndTime.hour + 60 * stEndTime.minute + stEndTime.second;
//    }
//
//    public int getTotalTime() {
//        return getEndSecond() - getStartSecond();
//    }
//}
