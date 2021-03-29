package com.example.igordev.financeiro.picker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by igordev on 28/07/17.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{

    private int resource;
    private TextView textView;
    private Calendar calendar;

    public void setTextViewAndTime(TextView textView, int resource, Calendar calendar) {
        this.textView = textView;
        this.resource = resource;
        this.calendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND,0);
        setTextView(textView, calendar.getTime(), calendar);
    }

    private void setTextView(TextView textView, Date time, Calendar calendar) {
        String hora = String.format(getResources().getString(resource),
                java.text.DateFormat.getTimeInstance().format(time));
        textView.setText(hora);
        calendar.setTime(time);
    }
}