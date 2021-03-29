package com.example.igordev.financeiro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igordev.financeiro.adapter.CategoriaAdapter;
import com.example.igordev.financeiro.adapter.HelperCallback;
import com.example.igordev.financeiro.dao.EnumTask;
import com.example.igordev.financeiro.dao.FinanceiroDao;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.util.AboutBox;
import com.example.igordev.financeiro.util.Animador;
import com.example.igordev.financeiro.util.Configuracao;
import com.example.igordev.financeiro.util.FormatarPeriodo;
import com.example.igordev.financeiro.util.ProgressDialogClose;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.igordev.financeiro.dao.EnumTask.DELETE;
import static com.example.igordev.financeiro.dao.EnumTask.GRAPH;
import static com.example.igordev.financeiro.dao.EnumTask.REFRESH;

public class MainActivity extends AppCompatActivity implements CategoriaAdapter.GerenciarCategoriaListener {

    private String[] meses;
    private ArrayList<Categoria> categoriasGraph;
    private Dao<Categoria, Integer> daoCategoria;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerViewCategoria;
    private CategoriaAdapter categoriaAdapter;
    private FormatarPeriodo periodo = null;
    private GridLayoutManager layoutManager;
    private HelperCallback helperCallback;
    private ItemTouchHelper itemTouchHelper;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                // Listener para atualizar as configurações após alteração
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreference, String Key) {
                    if (Key.equals("settings_dia")) {
                        Configuracao.DIA_FECHAMENTO_MES = Integer.parseInt(sharedPreference.getString(Key, "1"));

                        Calendar cal = Calendar.getInstance();
                        periodo = new FormatarPeriodo(Configuracao.DIA_FECHAMENTO_MES,
                                cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));

                        new DAOTask(REFRESH, periodo).execute();

                    } else if (Key.equals("settings_notificacao")) {
                        Configuracao.NOTIFICACAO_ATIVA = sharedPreference.getBoolean(Key, true);
                    }
                }
            };

    private void configureRecycler(List<Categoria> categorias, FormatarPeriodo formatarPeriodo) {
        if (categoriaAdapter != null) {
            categoriaAdapter.setCategorias(categorias, formatarPeriodo);
        } else {
            categoriaAdapter = new CategoriaAdapter(categorias, formatarPeriodo);
            recyclerViewCategoria.setAdapter(categoriaAdapter);

            helperCallback = new HelperCallback(categoriaAdapter);
            itemTouchHelper = new ItemTouchHelper(helperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerViewCategoria);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //shortcut dinâmico
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(getActivity(), "url_short_id")
                    .setShortLabel(getString(R.string.shortcut_url_label))
                    .setLongLabel(getString(R.string.shortcut_url_longl))
                    .setIcon(Icon.createWithResource(getActivity(), android.R.drawable.ic_dialog_info))
                    .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://igordev.com.br"))).build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
        }

        if (savedInstanceState != null) {
            periodo = (FormatarPeriodo) savedInstanceState.getSerializable("periodo");
        }

        //inicialização de variáveis
        this.meses = getResources().getStringArray(R.array.meses);
        recyclerViewCategoria = (RecyclerView) findViewById(R.id.listViewCategoria);

        //carrega os Cards do Recycler de acordo com a orientação da tela
        onConfigurationChanged(getResources().getConfiguration());
        Configuracao.carregar(getActivity());
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(
                preferenceChangeListener
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //não esqueça de alterar o AndroidManifest.xml
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        recyclerViewCategoria.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (periodo == null) {
            Calendar cal = Calendar.getInstance();
            periodo = new FormatarPeriodo(Configuracao.DIA_FECHAMENTO_MES,
                    cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        }
        new DAOTask(REFRESH, periodo).execute();

    }

    //Salvar a data que está configurada
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("periodo", periodo);
    }

    public void fabAddCategoriaClick(View v) {
        Animador.animaView(v, getResources().getInteger(R.integer.velocidade_animacao));
        Intent intent = new Intent(getActivity(), CategoriaActivity.class);
        startActivity(intent);

    }

    @Override
    public void editarCategoria(Categoria categoria) {
        Intent intent = new Intent(getActivity(), CategoriaActivity.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }

    @Override
    public void excluirCategoria(final Categoria categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.categoria_title_dlg_excluir)
                .setMessage(R.string.categoria_message_dlg_excluir)
                .setPositiveButton(R.string.sim_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DAOTask(DELETE, categoria).execute();
                        String log = String.format(getResources().getString(R.string.categoria_excluida), categoria.getNome());
                        Log.i(getResources().getString(R.string.app_name), log);
                        Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.nao_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DAOTask(REFRESH).execute();
                    }
                }).setCancelable(false).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcao_mes:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View dataView = LayoutInflater.from(getActivity()).inflate(R.layout.data_layout, null);
                final NumberPicker mes = (NumberPicker) dataView.findViewById(R.id.picker_mes);
                final NumberPicker ano = (NumberPicker) dataView.findViewById(R.id.picker_ano);
                mes.setValue(Calendar.getInstance().get(Calendar.MONTH));
                ano.setValue(Calendar.getInstance().get(Calendar.YEAR));

                builder
                        .setTitle(getResources().getString(R.string.meses_dialog))
                        .setView(dataView)
                        .setPositiveButton(getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                periodo.formatar(Configuracao.DIA_FECHAMENTO_MES, mes.getValue(), ano.getValue());
                                new DAOTask(REFRESH, periodo).execute();
                            }
                        }).show();
                break;
            case R.id.graph_d:
                new DAOTask(GRAPH, 'D').execute();
                break;
            case R.id.graph_r:
                new DAOTask(GRAPH, 'R').execute();
                break;
            case R.id.opcao_set:
                Intent preferencesIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(preferencesIntent);
                break;
            case R.id.opcao_about:
                AboutBox.aboutShow(getActivity());
                break;
        }

        return true;
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private class DAOTask extends AsyncTask<String, Integer, List<Categoria>> {

        final private EnumTask enumTask;
        final private Categoria categoria;
        final private int dia = Configuracao.DIA_FECHAMENTO_MES;
        final private FormatarPeriodo formatarPeriodo;
        private char tipoLancamento = '\u0000';

        public DAOTask(EnumTask enumTask, Categoria categoria, FormatarPeriodo formatarPeriodo) {
            this.enumTask = enumTask;
            this.categoria = categoria;
            this.formatarPeriodo = formatarPeriodo;
        }

        public DAOTask(EnumTask enumTask, Categoria categoria) {
            this.enumTask = enumTask;
            this.categoria = categoria;

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            this.formatarPeriodo = new FormatarPeriodo(dia, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        }

        public DAOTask(EnumTask enumTask, FormatarPeriodo formatarPeriodo) {
            this(enumTask, null, formatarPeriodo);
        }

        public DAOTask(EnumTask enumTask, char tipoLancamento) {
            this(enumTask, (Categoria) null);
            this.tipoLancamento = tipoLancamento;
        }

        public DAOTask(EnumTask enumTask) {
            this(enumTask, (Categoria) null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!getActivity().isFinishing()) {
                progressDialog = ProgressDialog.show(getActivity(),
                        getResources().getString(R.string.progess_aguarde),
                        getResources().getString(R.string.progress_message), true, true); //indeterminado, cancelável
            }

        }

        @Override
        protected List<Categoria> doInBackground(String... params) {
            try (FinanceiroDao<Categoria> categoriaDao = new FinanceiroDao<>(getActivity(), Categoria.class)) {
                daoCategoria = categoriaDao.getDao();
                switch (enumTask) {
                    case REFRESH:
                        return daoCategoria.queryForAll();
                    case DELETE:
                        daoCategoria.delete(categoria);
                        return daoCategoria.queryForAll();
                    case GRAPH:
                        QueryBuilder<Categoria, Integer> qbCategoria =
                                daoCategoria.queryBuilder();
                        qbCategoria.where().eq("tipoLancamento", tipoLancamento);
                        return daoCategoria.query(qbCategoria.prepare());
                }
            } catch (SQLException e) {
                Log.i(getResources().getString(R.string.app_name), getResources().getString(R.string.erro_iniciar_bd));
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Categoria> categorias) {
            String dataInicio = DateFormat.getDateInstance().format(periodo.getDataInicial().getTime());
            String dataFinal = DateFormat.getDateInstance().format(periodo.getDataFinal().getTime());

            TextView textViewMes = (TextView) getActivity().findViewById(R.id.textViewMes);
            textViewMes.setText(
                    String.format(getResources().getString(R.string.categoria_periodo), meses[periodo.getDataInicial().get(Calendar.MONTH)], dataInicio, dataFinal));
            TextView textViewBalanco = (TextView) getActivity().findViewById(R.id.textViewBalanco);

            double despesa = 0.0, receita = 0.0, balanco = 0.0;
            for (Categoria c : categorias) {
                if (c.getTipoLancamento() == 'D') {
                    despesa += c.getTotalCategoria(formatarPeriodo);
                } else {
                    receita += c.getTotalCategoria(formatarPeriodo);
                }
            }
            balanco = receita - despesa;
            textViewBalanco.setText(String.format(getResources().getString(R.string.categoria_valor_balanco),
                    DecimalFormat.getCurrencyInstance().format(balanco)));

            switch (enumTask) {
                case REFRESH:
                    configureRecycler(categorias, formatarPeriodo);
                    break;
                case DELETE:
                    categoriaAdapter.removerCategoria(categoria);
                    break;
                case GRAPH:
                    MainActivity.this.categoriasGraph = new ArrayList<>(categorias);
                    Intent intent = new Intent(getActivity(), GraphActivity.class);
                    intent.putExtra("categorias", categoriasGraph);
                    intent.putExtra("periodo", periodo);
                    startActivity(intent);
                    break;
            }
            ProgressDialogClose.closeProgress(progressDialog);
        }
    }
}