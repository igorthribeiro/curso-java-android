package agenda.igordev.com.br.agenda.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;

import agenda.igordev.com.br.agenda.R;
import agenda.igordev.com.br.agenda.fragment.CompromissoFragment;
import agenda.igordev.com.br.agenda.modelo.Compromisso;

public class AdapterListaCompromisso extends RecyclerView.Adapter<LineHolderCompromisso> {


    private FragmentActivity context;
    private List<Compromisso> compromissos;


    public AdapterListaCompromisso(FragmentActivity context, List<Compromisso> compromissos) {
        this.context = context;
        this.compromissos = compromissos;
    }

    @NonNull
    @Override
    public LineHolderCompromisso onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LineHolderCompromisso(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_compromisso_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineHolderCompromisso holder, final int position) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        holder.textViewData.setText(df.format(compromissos.get(position).getData()));
        holder.textViewCompromisso.setText(compromissos.get(position).getTextoLongo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //atenção para a classe de compatibilidade
                CompromissoFragment compromissoFragment = new CompromissoFragment();
                compromissoFragment.setCompromisso(compromissos.get(position));

                FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameMain, compromissoFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (compromissos != null) {
            return compromissos.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return compromissos.get(position).getId();
    }

    public void setCompromissos(List<Compromisso> compromissos) {
        this.compromissos = compromissos;
        notifyDataSetChanged();
    }
}
