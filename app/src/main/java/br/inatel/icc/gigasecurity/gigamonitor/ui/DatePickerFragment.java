package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * File: DatePickerFragment.java
 * Creation date: 13/10/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class DatePickerFragment.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class DatePickerFragment extends DialogFragment {
    public static String KEY_SERIALIZABLE_CALENDAR = "calendar";

    private DatePickerDialog.OnDateSetListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c;

        if (getArguments() != null && getArguments().containsKey(KEY_SERIALIZABLE_CALENDAR)) {
            c = (Calendar) getArguments().getSerializable(KEY_SERIALIZABLE_CALENDAR);
        } else {
            c = Calendar.getInstance();
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener l) {
        mListener = l;
    }
}
