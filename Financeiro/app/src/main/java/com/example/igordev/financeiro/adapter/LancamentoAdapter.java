package com.example.igordev.financeiro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igordev.financeiro.R;
import com.example.igordev.financeiro.model.Lancamento;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by igordev on 09/07/17.
 */

public class LancamentoAdapter extends BaseAdapter {
    private final Context context;
    private final List<Lancamento> lancamentos;

    public LancamentoAdapter(Context context, List<Lancamento> lancamentos) {
        this.context = context;
        this.lancamentos = lancamentos;
    }

    @Override
    public int getCount() {
        if (lancamentos != null) {
            return lancamentos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (lancamentos != null) {
            return lancamentos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (lancamentos != null) {
            return lancamentos.get(position).getId();
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_lancamento, parent, false);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
        TextView textViewLancamento = (TextView) view.findViewById(R.id.textViewLancamento);
        TextView textViewVencimento = (TextView) view.findViewById(R.id.textViewVencimento);
        TextView textViewPagamento = (TextView) view.findViewById(R.id.textViewPagamento);
        TextView textViewPago = (TextView) view.findViewById(R.id.textViewPago);

        Lancamento lancamento = (Lancamento) getItem(position);

        textViewLancamento.setText(lancamento.getDescricao());
        String dataPagamento = context.getResources().getString(R.string.lancamento_nao_pago);
        if (lancamento.getPagamento() != null) {
            dataPagamento = DateFormat.getDateInstance().format(lancamento.getPagamento());
        }
        String dataVencimento = DateFormat.getDateInstance().format(lancamento.getVencimento());
        textViewPagamento.setText(String.format(context.getResources().getString(R.string.lancamento_lista_pagamento), dataPagamento));
        textViewVencimento.setText(String.format(context.getResources().getString(R.string.lancamento_lista_vencimento), dataVencimento));
        String original = DecimalFormat.getCurrencyInstance().format(lancamento.getValor());
        String pago = DecimalFormat.getCurrencyInstance().format(lancamento.getValorPago());
        textViewPago.setText(
                String.format(context.getResources().getString(R.string.lancamento_pago_original), pago, original));
        if (lancamento.getValorPago() >= lancamento.getValor()) {
            imageViewIcon.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else {
            imageViewIcon.setImageResource(R.drawable.ic_schedule_black_24dp);
        }

        return view;
    }
}
