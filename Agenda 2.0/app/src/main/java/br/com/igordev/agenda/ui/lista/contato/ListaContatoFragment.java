package br.com.igordev.agenda.ui.lista.contato;

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
import br.com.igordev.agenda.adapter.ListaContatoAdapter;
import br.com.igordev.agenda.dao.AgendaDaoSQLite;
import br.com.igordev.agenda.modelo.Contato;

public class ListaContatoFragment extends Fragment {

    private ListaContatoViewModel mViewModel;

    public static ListaContatoFragment newInstance() {
        return new ListaContatoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lista_contato_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListaContatoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //atribuição click itens do ListView
        ListView listViewContatos = view.findViewById(R.id.listViewContatos);
        listViewContatos.setOnItemClickListener(onItemClick);

        //Atribuição click fabNovo
        FloatingActionButton fabNovo = view.findViewById(R.id.fabNovo);
        fabNovo.setOnClickListener(onNovoClick);
    }


    private View.OnClickListener onNovoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavHostFragment.findNavController(ListaContatoFragment.this)
                    .navigate(R.id.action_nav_lista_contato_to_nav_edita_contato);
        }
    };

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //envia para o fragment o contato escolhido na lista
            Contato contato = (Contato) parent.getItemAtPosition(position);
            Bundle dados = new Bundle();
            dados.putSerializable("contato", contato);

            NavHostFragment.findNavController(ListaContatoFragment.this)
                    .navigate(R.id.action_nav_lista_contato_to_nav_edita_contato, dados);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        new ListaContatoTask().execute();
    }

    //classe assíncrona para listar os contatos cadastrados
    private class ListaContatoTask extends AsyncTask<String, Integer, List<Contato>> {

        @Override
        protected List<Contato> doInBackground(String... strings) {
            AgendaDaoSQLite agendaDaoSQLite = new AgendaDaoSQLite(getActivity());
            return agendaDaoSQLite.listaContatos();
        }

        @Override
        protected void onPostExecute(List<Contato> contatos) {
            ListView listViewContatos = getActivity().findViewById(R.id.listViewContatos);
            listViewContatos.setAdapter(new ListaContatoAdapter(getActivity(), contatos));
        }
    }

}