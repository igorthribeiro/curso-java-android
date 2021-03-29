package com.example.igordev.financeiro.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by igordev on 28/07/17.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView textView;
    private Calendar calendar;

    public void setTextViewAndDate(TextView textView, Calendar calendar) {
        this.textView = textView;
        this.calendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //pega a data atual
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //cria uma instância nova de DatePickerDialog e a retorna
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        c.set(Calendar.SECOND,0);
        setTextView(textView, c.getTime(), calendar);
    }

    private void setTextView(TextView textViewDate, Date date, Calendar calendar) {
        String data = DateFormat.getDateInstance().format(date);
        textViewDate.setText(data);
        calendar.setTime(date);
    }
}