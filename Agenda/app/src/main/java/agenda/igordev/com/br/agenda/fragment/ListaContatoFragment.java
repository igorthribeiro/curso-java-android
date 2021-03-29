package agenda.igordev.com.br.agenda.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import agenda.igordev.com.br.agenda.R;
import agenda.igordev.com.br.agenda.adapter.AdapterListaContato;
import agenda.igordev.com.br.agenda.dao.ContatoDaoSQLite;
import agenda.igordev.com.br.agenda.modelo.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaContatoFragment extends Fragment {


    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ContatoFragment contatoFragment = new ContatoFragment();
            //envia para o fragment o contato escolhido na lista
            Contato contato = (Contato) parent.getItemAtPosition(position);
            contatoFragment.setContato(contato);

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameMain, contatoFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    private View.OnClickListener onNovoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //atenção para a classe de compatibilidade
            ContatoFragment contatoFragment = new ContatoFragment();

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameMain, contatoFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    public ListaContatoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_contato, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        new ListaContatoTask().execute();
    }

    //AsyncTask para listar os contatos cadastrados
    private class ListaContatoTask extends AsyncTask<String, Integer, List<Contato>> {

        @Override
        protected List<Contato> doInBackground(String... strings) {
            ContatoDaoSQLite contatoDaoSQLite = new ContatoDaoSQLite(getActivity());
            return contatoDaoSQLite.lista();
        }

        @Override
        protected void onPostExecute(List<Contato> contatos) {
            ListView listViewContatos = getActivity().findViewById(R.id.listViewContatos);
            listViewContatos.setAdapter(new AdapterListaContato(getActivity(), contatos));
        }
    }
}
