package br.com.igordev.agenda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.igordev.agenda.R;
import br.com.igordev.agenda.modelo.Compromisso;

public class ListaCompromissoAdapter extends BaseAdapter {

    private Context context;
    private List<Compromisso> compromissos;

    public ListaCompromissoAdapter(Context context, List<Compromisso> compromissos) {
        this.context = context;
        this.compromissos = compromissos;
    }

    @Override
    public int getCount() {
        return compromissos.size();
    }

    @Override
    public Object getItem(int position) {
        return compromissos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return compromissos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_compromisso_adapter, parent, false);
        TextView textViewData = view.findViewById(R.id.textViewData);
        TextView textViewCompromisso = view.findViewById(R.id.textViewCompromisso);

        Compromisso compromisso = (Compromisso) getItem(position);

        textViewData.setText(compromissos.get(position).getData());
        textViewCompromisso.setText(compromissos.get(position).getTextoCurto());

        return view;

    }
}
