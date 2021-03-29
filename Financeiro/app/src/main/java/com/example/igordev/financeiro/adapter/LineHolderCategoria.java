package com.example.igordev.financeiro.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igordev.financeiro.R;

/**
 * Created by igordev on 19/07/17.
 */

public class LineHolderCategoria extends RecyclerView.ViewHolder {

    public ImageView imageViewIcon;
    public TextView textViewCategoria;
    public TextView textViewPago;
    public TextView textViewAPagar;
    public TextView textViewDifMeta;

    public LineHolderCategoria(View itemView) {
        super(itemView);
        imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
        textViewCategoria = (TextView) itemView.findViewById(R.id.textViewCategoria);
        textViewPago = (TextView) itemView.findViewById(R.id.textViewPago);
        textViewAPagar = (TextView) itemView.findViewById(R.id.textViewAPagar);
        textViewDifMeta = (TextView) itemView.findViewById(R.id.textViewDifMeta);
    }
}
