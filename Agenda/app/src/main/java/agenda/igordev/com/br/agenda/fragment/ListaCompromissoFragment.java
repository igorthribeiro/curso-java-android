package agenda.igordev.com.br.agenda.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import agenda.igordev.com.br.agenda.R;
import agenda.igordev.com.br.agenda.adapter.AdapterListaCompromisso;
import agenda.igordev.com.br.agenda.modelo.Compromisso;
import br.com.igordev.eminjectlib.dao.Em;
import br.com.igordev.eminjectlib.dao.EmFragment;
import br.com.igordev.eminjectlib.dao.EntityManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaCompromissoFragment extends EmFragment {

    private RecyclerView recyclerView;
    private AdapterListaCompromisso adapter;

    @EntityManager
    Em<Compromisso> em;

    public ListaCompromissoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_compromisso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.recyclerView);

        FloatingActionButton fabNovo = view.findViewById(R.id.fabNovo);
        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompromissoFragment compromissoFragment = new CompromissoFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameMain, compromissoFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        new ListaCompromissoTask().execute();
    }

    private void configuraRecycler(List<Compromisso> compromissos) {
        adapter = new AdapterListaCompromisso(getActivity(), compromissos);
        recyclerView.setAdapter(adapter);
    }

    private class ListaCompromissoTask extends AsyncTask<String, Integer, List<Compromisso>> {
        @Override
        protected void onPostExecute(List<Compromisso> compromissos) {
            configuraRecycler(compromissos);
        }

        @Override
        protected List<Compromisso> doInBackground(String... strings) {
            List<Compromisso> lista = em.list();
            Collections.sort(lista, (new Comparator<Compromisso>() {
                @Override
                public int compare(Compromisso o1, Compromisso o2) {
                    return o1.getData().compareTo(o2.getData());
                }
            }));
            return lista;
        }
    }

}
