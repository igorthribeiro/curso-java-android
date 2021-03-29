package com.example.igordev.financeiro.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.igordev.financeiro.ListaLancamentoActivity;
import com.example.igordev.financeiro.R;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.util.FormatarPeriodo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by igordev on 06/07/17.
 */

public class CategoriaAdapter extends RecyclerView.Adapter<LineHolderCategoria> {

    private FormatarPeriodo formatarPeriodo;

    private Context context;
    private GerenciarCategoriaListener listenerGerenciar;

    private List<Categoria> categorias;

    public CategoriaAdapter(List<Categoria> categorias, FormatarPeriodo formatarPeriodo) {
        this.categorias = categorias;
        this.formatarPeriodo = formatarPeriodo;
    }

    @Override
    public LineHolderCategoria onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderCategoria(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_categoria, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderCategoria holder, int position) {
        final Categoria categoria = getItem(position);

        char signal = categoria.getTipoLancamento() == 'R' ? '+' : '-';
        holder.textViewCategoria.setText(signal + "$ " +categoria.getNome());
        holder.textViewPago.setText(DecimalFormat.getCurrencyInstance().format(categoria.getTotalPago(formatarPeriodo)));
        holder.textViewAPagar.setText(DecimalFormat.getCurrencyInstance().format(categoria.getTotalAPagar(formatarPeriodo)));
        holder.textViewDifMeta.setText(DecimalFormat.getCurrencyInstance().format(categoria.getDiferencaMeta(formatarPeriodo)));
        holder.imageViewIcon.setImageResource(categoria.getImagem());
        holder.imageViewIcon.setTag(categoria.getImagem());

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListaLancamentoActivity.class);
                intent.putExtra("categoria", categoria);
                intent.putExtra("periodo", formatarPeriodo);
                context.startActivity(intent);

            }
        };

        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setTitle(categoria.getNome())
                        .setItems(context.getResources().getStringArray(R.array.categoria_opcoes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        listenerGerenciar.editarCategoria(categoria);
                                        break;
                                    case 1:
                                        listenerGerenciar.excluirCategoria(categoria);
                                        break;
                                    case 2:
                                        onClickListener.onClick(view);
                                        break;
                                }
                            }
                        }).show();
                return true; //evento tratado
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categorias != null) {
            return categorias.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (categorias != null) {
            return categorias.get(position).getId();
        }
        return -1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.context = recyclerView.getContext();
        this.listenerGerenciar = (GerenciarCategoriaListener) recyclerView.getContext();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.listenerGerenciar = null;
    }

    public void setCategorias(List<Categoria> categorias, FormatarPeriodo formatarPeriodo) {
        this.categorias = categorias;
        this.formatarPeriodo = formatarPeriodo;
        notifyDataSetChanged();
    }

    public void onItemDismiss(int adapterPosition) {
        listenerGerenciar.excluirCategoria(getItem(adapterPosition));
    }

    private Categoria getItem(int position) {
        if (categorias != null) {
            return categorias.get(position);
        }
        return null;
    }

    public void removerCategoria(Categoria categoria) {
        int index = categorias.indexOf(categoria);
        removerItem(index);
    }

    public void removerItem(int index) {
        categorias.remove(index);
        notifyItemRemoved(index);
    }

    public interface GerenciarCategoriaListener {
        void editarCategoria(Categoria categoria);
        void excluirCategoria(Categoria categoria);
    }
}
