package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * File: TimePickerFragment.java
 * Creation date: 07/10/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class TimePickerFragment.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String KEY_SERIALIZABLE_CALENDAR = "calendar";

    private MyTimePickerDialog.OnTimeSetListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c;

        if (getArguments() != null && getArguments().containsKey(KEY_SERIALIZABLE_CALENDAR)) {
           c = (Calendar) getArguments().getSerializable(KEY_SERIALIZABLE_CALENDAR);
        } else {
           c = Calendar.getInstance();
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        // Create a new instance of TimePickerDialog and return it
        return new MyTimePickerDialog(getActivity(), mListener, hour, minute, second,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(MyTimePickerDialog.OnTimeSetListener l) {
        mListener = l;
    }
}
