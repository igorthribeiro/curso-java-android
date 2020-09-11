package br.com.igordev.agenda.ui.lista.compromisso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.igordev.agenda.R;
import br.com.igordev.agenda.adapter.ListaCompromissoAdapter;
import br.com.igordev.agenda.dao.AgendaDaoSQLite;
import br.com.igordev.agenda.modelo.Compromisso;

public class ListaCompromissoFragment extends Fragment {

    private ListaCompromissoViewModel mViewModel;

    public static ListaCompromissoFragment newInstance() {
        return new ListaCompromissoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lista_compromisso_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ListaCompromissoTask().execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //atribuição click itens do ListView
        ListView listViewCompromissos = view.findViewById(R.id.listViewCompromissos);
        listViewCompromissos.setOnItemClickListener(onItemClick);

        //Atribuição click fabNovo
        FloatingActionButton fabNovo = view.findViewById(R.id.fabNovo);
        fabNovo.setOnClickListener(onNovoClick);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListaCompromissoViewModel.class);
        // TODO: Use the ViewModel
    }

    private View.OnClickListener onNovoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavHostFragment.findNavController(ListaCompromissoFragment.this)
                    .navigate(R.id.action_nav_lista_compromisso_to_nav_edita_compromisso);
        }
    };

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //envia para o fragment o contato escolhido na lista
            Compromisso compromisso = (Compromisso) parent.getItemAtPosition(position);
            Bundle dados = new Bundle();
            dados.putSerializable("compromisso", compromisso);

            NavHostFragment.findNavController(ListaCompromissoFragment.this)
                    .navigate(R.id.action_nav_lista_compromisso_to_nav_edita_compromisso, dados);
        }
    };

    //classe assíncrona para listar os compromissos cadastrados
    private class ListaCompromissoTask extends AsyncTask<String, Integer, List<Compromisso>> {

        @Override
        protected List<Compromisso> doInBackground(String... strings) {
            AgendaDaoSQLite agendaDaoSQLite = new AgendaDaoSQLite(getActivity());
            return agendaDaoSQLite.listaCompromissos();
        }

        @Override
        protected void onPostExecute(List<Compromisso> compromissos) {
            ListView listViewCompromissos = getActivity().findViewById(R.id.listViewCompromissos);
            listViewCompromissos.setAdapter(new ListaCompromissoAdapter(getActivity(), compromissos));
        }
    }

}