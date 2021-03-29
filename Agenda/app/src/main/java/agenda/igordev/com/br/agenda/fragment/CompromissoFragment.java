package agenda.igordev.com.br.agenda.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import agenda.igordev.com.br.agenda.R;
import agenda.igordev.com.br.agenda.action.AcaoSim;
import agenda.igordev.com.br.agenda.dialog.DialogDatePicker;
import agenda.igordev.com.br.agenda.modelo.Compromisso;
import agenda.igordev.com.br.agenda.util.DialogoSimNao;
import br.com.igordev.eminjectlib.dao.Em;
import br.com.igordev.eminjectlib.dao.EmFragment;
import br.com.igordev.eminjectlib.dao.EntityManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompromissoFragment extends EmFragment {


    @EntityManager
    Em<Compromisso> em;

    private EditText editTextCurto, editTextLongo, editTextData, editTextHora;
    private FloatingActionButton fabSalvar, fabExcluir;
    private Compromisso compromisso;
    private SimpleDateFormat dfData = new SimpleDateFormat("dd/MM/yyyy");


    public CompromissoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compromisso, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextCurto = view.findViewById(R.id.editTextCurto);
        editTextLongo = view.findViewById(R.id.editTextLongo);
        editTextData = view.findViewById(R.id.editTextData);
        editTextHora = view.findViewById(R.id.editTextHora);
        fabSalvar = view.findViewById(R.id.fabSalvar);
        fabExcluir = view.findViewById(R.id.fabExcluir);

        //verifica rotação da tela
        if (savedInstanceState != null) {
            compromisso = (Compromisso) savedInstanceState.getSerializable("compromisso");
        }


        if (compromisso == null) {
            compromisso = new Compromisso();
        } else {
            preencheCampos(compromisso);
        }

        fabSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCurto.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), R.string.erro_cadastro_compromisso, Toast.LENGTH_LONG).show();
                    return;
                }
                compromisso.setTextoCurso(editTextCurto.getText().toString());
                compromisso.setTextoLongo(editTextLongo.getText().toString());
                try {
                    compromisso.setData(dfData.parse(editTextData.getText().toString()));
                } catch (ParseException e) {
                    Log.e(getClass().getSimpleName(), "Erro de formatação de data.");
                }
                compromisso.setHora(editTextHora.getText().toString());
                new SalvaCompromissoTask().execute(compromisso);
            }
        });

        fabExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoSimNao.cria(getActivity(), R.string.titulo_dialolgo_excluir, R.string.confirma_excluir, new AcaoSim() {
                    @Override
                    public void executar() {
                        new ExcluirCompromissoTask().execute(compromisso);
                    }
                }, null);
            }
        });

        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDatePicker dialogDatePicker = new DialogDatePicker();
                dialogDatePicker.setEditText(editTextData);
                dialogDatePicker.show(getFragmentManager(), "dataPicker");
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("compromisso", compromisso);
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

    private void preencheCampos(Compromisso compromisso) {
        editTextCurto.setText(compromisso.getTextoCurso());
        editTextLongo.setText(compromisso.getTextoLongo());
        editTextData.setText(dfData.format(compromisso.getData()));
        editTextHora.setText(compromisso.getHora());
    }

    private class SalvaCompromissoTask extends AsyncTask<Compromisso, Integer, Long> {

        @Override
        protected Long doInBackground(Compromisso... compromissos) {
            em.post(compromissos[0]);
            return compromissos[0].getId();
        }

        @Override
        protected void onPostExecute(Long id) {
            Toast.makeText(getActivity(), R.string.sucesso_cadastro, Toast.LENGTH_LONG).show();
            compromisso.setId(id);
            //volta para lista após o salvamento
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private class ExcluirCompromissoTask extends AsyncTask<Compromisso, Integer, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }

        @Override
        protected Integer doInBackground(Compromisso... compromissos) {
            em.delete(compromissos[0]);
            return compromissos[0].getId().intValue();
        }
    }
}
