package agenda.igordev.com.br.agenda.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import agenda.igordev.com.br.agenda.R;

public class LineHolderCompromisso extends RecyclerView.ViewHolder {

    TextView textViewData, textViewCompromisso;

    public LineHolderCompromisso(View itemView) {
        super(itemView);
        this.textViewData = itemView.findViewById(R.id.textViewData);
        this.textViewCompromisso = itemView.findViewById(R.id.textViewCompromisso);
    }
}