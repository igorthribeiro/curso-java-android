package com.example.igordev.financeiro.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Created by igordev on 11/07/17.
 */

public class AnoNumberPicker extends NumberPicker {
    public AnoNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setMinValue(attrs.getAttributeIntValue(null,"min_year",0));
        this.setMaxValue(attrs.getAttributeIntValue(null,"max_year",0));
    }
}
