package com.example.igordev.financeiro.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.example.igordev.financeiro.R;

/**
 * Created by igordev on 11/07/17.
 */

public class MesNumberPicker extends NumberPicker {

    static private String[] meses;

    public MesNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.meses = context.getResources().getStringArray(R.array.meses);
        this.setMinValue(0);
        this.setMaxValue(meses.length -1);
        this.setDisplayedValues(meses);
    }
}
