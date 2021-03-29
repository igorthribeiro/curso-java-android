package com.example.igordev.financeiro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.igordev.financeiro.dao.DatabaseHelper;
import com.example.igordev.financeiro.dao.EnumTask;
import com.example.igordev.financeiro.dao.FinanceiroDao;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.util.Animador;
import com.example.igordev.financeiro.util.ProgressDialogClose;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class CategoriaActivity extends AppCompatActivity {

    private Dao<Categoria, Integer> daoCategoria;
    private ImageView imageViewIcon;
    private RadioButton radioButtonReceita;
    private RadioButton radioButtonDespesa;
    private EditText editTextDescricao;
    private EditText editTextMeta;
    private int codigoIcone = DatabaseHelper.ICONES[0];
    private int categoriaId = -1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        //mapear componentes da tela
        imageViewIcon = (ImageView) findViewById(R.id.imageViewIcon);
        radioButtonReceita = (RadioButton) findViewById(R.id.radioButtonReceita);
        radioButtonDespesa = (RadioButton) findViewById(R.id.radioButtonDespesa);
        editTextDescricao = (EditText) findViewById(R.id.editTextDescricao);
        editTextMeta = (EditText) findViewById(R.id.editTextMeta);

        Categoria savedCategoria = (Categoria) getIntent().getSerializableExtra("categoria");
        if (savedCategoria != null) {
            categoriaId = savedCategoria.getId();
            codigoIcone = savedCategoria.getImagem();
            imageViewIcon.setImageResource(savedCategoria.getImagem());
            if (savedCategoria.getTipoLancamento() == 'R') {
                radioButtonReceita.setChecked(true);
            } else {
                radioButtonDespesa.setChecked(true);
            }
            editTextDescricao.setText(savedCategoria.getNome());
            editTextMeta.setText(String.valueOf(savedCategoria.getMeta()));
        } else {
            categoriaId = -1;
        }

        progressDialog = new ProgressDialog(getActivity());
    }

    public void onClickImagem(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(getResources().getString(R.string.categoria_tit_dialog))
                .setSingleChoiceItems(R.array.categoria_icones, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        codigoIcone = DatabaseHelper.ICONES[which];
                        imageViewIcon.setImageResource(DatabaseHelper.ICONES[which]);
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok_button), null).show();
    }

    public void onSaveCategoria(View v) {
        Animador.animaView(v, getResources().getInteger(R.integer.velocidade_animacao));
        if (editTextDescricao.getText().toString().equals("") ||
                editTextMeta.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.categoria_obrigatorio), Toast.LENGTH_SHORT).show();
            return;
        }

        char tipoDespesa = (radioButtonReceita.isChecked() ? 'R' : 'D');
        Categoria categoria = new Categoria(codigoIcone, tipoDespesa,
                editTextDescricao.getText().toString(), Double.parseDouble(editTextMeta.getText().toString()));
        new DAOTask(EnumTask.SAVE, categoria).execute();
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private class DAOTask extends AsyncTask<String, Integer, Categoria> {

        final private EnumTask enumTask;
        final private Categoria categoria;

        public DAOTask(EnumTask enumTask, Categoria categoria) {
            this.enumTask = enumTask;
            this.categoria = categoria;
        }

        public DAOTask(EnumTask enumTask) {
            this(enumTask, null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.progess_aguarde),
                    getResources().getString(R.string.progress_message), true, true); //indeterminado, cancelável

        }

        @Override
        protected Categoria doInBackground(String... params) {

            try (FinanceiroDao<Categoria> categoriaDao = new FinanceiroDao<>(getActivity(), Categoria.class)) {
                daoCategoria = categoriaDao.getDao();
                switch (enumTask) {
                    case SAVE:
                        if (categoriaId == -1) {
                            daoCategoria.create(categoria);
                        } else {
                            categoria.setId(categoriaId);
                            daoCategoria.update(categoria);
                        }
                }
            } catch (SQLException e) {
                Log.i(getResources().getString(R.string.app_name),
                        getResources().getString(R.string.erro_iniciar_bd));
                throw new RuntimeException(e);
            }

            return categoria;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Categoria categoria) {
            String mensagemFormatada = String.format(getResources().getString(R.string.categoria_gravada), categoria.getNome());
            Log.i(getResources().getString(R.string.app_name), mensagemFormatada);
            Toast.makeText(getActivity(), mensagemFormatada, Toast.LENGTH_SHORT).show();
            getActivity().finish(); //fecha a tela depois de salvar
            ProgressDialogClose.closeProgress(progressDialog);
        }
    }
}