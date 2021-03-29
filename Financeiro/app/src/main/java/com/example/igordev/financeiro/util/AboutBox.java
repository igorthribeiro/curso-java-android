package com.example.igordev.financeiro.util;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.igordev.financeiro.R;

/**
 * Created by igordev on 02/08/17.
 */

public class AboutBox {
    public static void aboutShow(Context context) {
        final AlertDialog.Builder builderSobre = new AlertDialog.Builder(context);
        final View sobreView = LayoutInflater.from(context).inflate(R.layout.sobre_layout, null);

        builderSobre
                .setTitle(context.getResources().getString(R.string.about_title))
                .setView(sobreView).setPositiveButton(context.getResources().getString(R.string.ok_button), null)
                .show();
    }

}
