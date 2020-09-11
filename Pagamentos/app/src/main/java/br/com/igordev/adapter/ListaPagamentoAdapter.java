package br.com.igordev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.com.igordev.R;
import br.com.igordev.dominio.Pagamento;

public class ListaPagamentoAdapter extends BaseAdapter {

    private Context context;
    private List<Pagamento> pagamentos;

    public ListaPagamentoAdapter(Context context, List<Pagamento> pagamentos) {
        this.context = context;
        this.pagamentos = pagamentos;
    }

    @Override
    public int getCount() {
        return pagamentos.size();
    }

    @Override
    public Object getItem(int position) {
        return pagamentos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pagamentos.get(position).getCodigo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_pagamento_adapter, parent, false);
        TextView txtData =  view.findViewById(R.id.textViewData);
        TextView txtDescricao = view.findViewById(R.id.textViewDescricao);
        TextView txtValor = view.findViewById(R.id.textViewValor);
        TextView txtPago = view.findViewById(R.id.textViewPago);

        Pagamento pagamento = (Pagamento) getItem(position);

        txtData.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy - EEEE").format(pagamento.getDataVencimento()));
        txtDescricao.setText(pagamento.getDescricao());
        txtValor.setText(NumberFormat.getCurrencyInstance().format(pagamento.getValor()));

        if (pagamento.getPago().equals(true)) {
            txtPago.setText("PAGO");
            view.setBackgroundColor(Color.rgb(236, 236, 236));
        }

        return view;
    }
}
