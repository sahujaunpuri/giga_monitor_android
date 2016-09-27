package br.inatel.icc.gigasecurity.gigamonitor.model;

/**
 * File: SDKFileType.java
 * Creation date: 05/11/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class SDKFileType.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public enum SDKFileType {

    SDK_RECORD_ALL(0),
    SDK_RECORD_ALARM(1), //Íâ²¿±¨¾¯Â¼Ïñ
    SDK_RECORD_DETECT(2),	  //ÊÓÆµÕì²âÂ¼Ïñ
    SDK_RECORD_REGULAR(3),	  //ÆÕÍ¨Â¼Ïñ
    SDK_RECORD_MANUAL(4),	  //ÊÖ¶¯Â¼Ïñ
    SDK_PIC_ALL(10),
    SDK_PIC_ALARM(11),		  //Íâ²¿±¨¾¯Â¼Ïñ
    SDK_PIC_DETECT(12),		  //ÊÓÆµÕì²âÂ¼Ïñ
    SDK_PIC_REGULAR(13),      //ÆÕÍ¨Â¼Ïñ
    SDK_PIC_MANUAL(14),       //ÊÖ¶¯Â¼Ïñ
    SDK_TYPE_NUM(15);

    private int value;

    SDKFileType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
