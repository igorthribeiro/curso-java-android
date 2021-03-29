package com.example.igordev.financeiro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igordev.financeiro.adapter.LancamentoAdapter;
import com.example.igordev.financeiro.dao.EnumTask;
import com.example.igordev.financeiro.dao.FinanceiroDao;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.model.Lancamento;
import com.example.igordev.financeiro.receiver.NotificacaoReceiver;
import com.example.igordev.financeiro.util.Animador;
import com.example.igordev.financeiro.util.FormatarPeriodo;
import com.example.igordev.financeiro.util.ProgressDialogClose;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.igordev.financeiro.dao.EnumTask.DELETE;
import static com.example.igordev.financeiro.dao.EnumTask.REFRESH;
import static com.example.igordev.financeiro.dao.EnumTask.SAVE;

public class ListaLancamentoActivity extends AppCompatActivity {

    Categoria savedCategoria;
    private String[] meses;
    private List<Lancamento> lancamentos;
    private Dao<Lancamento, Integer> daoLancamento;
    private ProgressDialog progressDialog;
    private FormatarPeriodo periodo;
    private String colunaOrdenacao = "vencimento";
    private MenuItem menuItemDescricao, menuItemVencimento, menuItemValor;

    private AdView adView;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            editarLancamento(lancamentos.get(position));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Lancamento lancamento = lancamentos.get(position);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setTitle(lancamento.getDescricao())
                    .setItems(getResources().getStringArray(R.array.lancamento_opcoes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    editarLancamento(lancamento);
                                    break;
                                case 1:
                                    excluirLancamento(lancamento);
                                    break;
                                case 2:
                                    liquidarLancamento(lancamento);
                                    break;
                                case 3:
                                    estornarLancamento(lancamento);
                                    break;
                            }
                        }
                    }).show();
            return true; //evento tratado
        }
    };

    private void editarLancamento(Lancamento lancamento) {
        Intent intent = new Intent(getActivity(), LancamentoActivity.class);
        intent.putExtra("lancamento", lancamento);
        startActivity(intent);
    }

    private void excluirLancamento(final Lancamento lancamento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.lancamento_title_dlg_excluir)
                .setMessage(R.string.lancamento_message_dlg_excluir)
                .setPositiveButton(R.string.sim_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ListaLancamentoActivity.DAOTask(DELETE, lancamento).execute();
                        String log = String.format(getResources().getString(R.string.lancamento_excluido), lancamento.getDescricao());
                        Log.i(getResources().getString(R.string.app_name), log);
                        Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(R.string.nao_button, null).show();
    }

    private void liquidarLancamento(Lancamento lancamento) {
        lancamento.setValorPago(lancamento.getValor());
        lancamento.setPagamento(new Date());
        new DAOTask(SAVE, lancamento).execute();
        String mensagem = String.format(getResources().getString(R.string.lancamento_liquidado), lancamento.getDescricao());
        Log.i(getResources().getString(R.string.app_name), mensagem);
        Toast.makeText(getActivity(), mensagem, Toast.LENGTH_SHORT).show();
    }

    private void estornarLancamento(Lancamento lancamento) {
        lancamento.setValorPago(0);
        lancamento.setPagamento(null);
        new DAOTask(SAVE, lancamento).execute();
        String mensagem = String.format(getResources().getString(R.string.lancamento_estornado), lancamento.getDescricao());
        Log.i(getResources().getString(R.string.app_name), mensagem);
        Toast.makeText(getActivity(), mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_lancamento);

        savedCategoria = (Categoria) getIntent().getSerializableExtra("categoria");
        periodo = (FormatarPeriodo) getIntent().getSerializableExtra("periodo");

        FloatingActionButton fabAddLancamento = (FloatingActionButton) findViewById(R.id.fabAddLancamento);
        if (savedCategoria == null || periodo == null) {
            periodo = new FormatarPeriodo();
            fabAddLancamento.setVisibility(View.INVISIBLE);
        } else {
            fabAddLancamento.setVisibility(View.VISIBLE);
        }

        this.meses = getResources().getStringArray(R.array.meses);

        //Carregar AdView
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544/6300978111");
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F5E373C416A52BE194724C132A0B0346").build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new DAOTask(EnumTask.REFRESH).execute();
    }

    public void fabAddLancamentoClick(View v) {
        Animador.animaView(v, getResources().getInteger(R.integer.velocidade_animacao));
        Intent intent = new Intent(getActivity(), LancamentoActivity.class);
        intent.putExtra("categoria", savedCategoria);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ordem, menu);
        menuItemDescricao = menu.findItem(R.id.ordem_descricao);
        menuItemVencimento = menu.findItem(R.id.ordem_vencimento);
        menuItemValor = menu.findItem(R.id.ordem_valor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menuItemDescricao.setChecked(false);
        menuItemVencimento.setChecked(false);
        menuItemValor.setChecked(false);

        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.ordem_descricao:
                colunaOrdenacao = "descricao";
                break;
            case R.id.ordem_vencimento:
                colunaOrdenacao = "vencimento";
                break;
            case R.id.ordem_valor:
                colunaOrdenacao = "valor";
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        new DAOTask(REFRESH).execute();
        return true;
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private class DAOTask extends AsyncTask<String, Integer, List<Lancamento>> {

        final private EnumTask enumTask;
        final private Lancamento lancamento;

        public DAOTask(EnumTask enumTask, Lancamento lancamento) {
            this.enumTask = enumTask;
            this.lancamento = lancamento;
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
        protected List<Lancamento> doInBackground(String... params) {
            List<Lancamento> lancamentos = null;

            try (FinanceiroDao<Lancamento> dao = new FinanceiroDao<>(getActivity(), Lancamento.class)) {
                daoLancamento = dao.getDao();

                QueryBuilder<Lancamento, Integer> queryBuilder =
                        daoLancamento.queryBuilder();
                Where<Lancamento, Integer> where = queryBuilder.where();
                where.between("vencimento",
                        periodo.getDataInicial().getTime(), periodo.getDataFinal().getTime());
                where.and();
                if (savedCategoria != null) {
                    where.eq("categoria_id", savedCategoria.getId());
                } else {
                    where.lt("valorPago", new ColumnArg("valor"));
                }

                switch (enumTask) {
                    case REFRESH:
                        break;
                    case DELETE:
                        daoLancamento.delete(lancamento);
                        NotificacaoReceiver.desativarNotificacao(getActivity(), lancamento);
                        break;
                    case SAVE:
                        daoLancamento.update(lancamento);
                        if (lancamento.isNotificacao() &&
                                (lancamento.getValorPago() < lancamento.getValor())) {
                            NotificacaoReceiver.ativarNotificacao(getActivity(), lancamento);
                        } else {
                            NotificacaoReceiver.desativarNotificacao(getActivity(), lancamento);
                        }
                        break;
                }
                queryBuilder.orderBy(colunaOrdenacao, true);
                lancamentos = daoLancamento.query(queryBuilder.prepare());

            } catch (SQLException e) {
                Log.i(getResources().getString(R.string.app_name), getResources().getString(R.string.erro_iniciar_bd));
                throw new RuntimeException(e);
            }
            return lancamentos;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Lancamento> lancamentos) {
            ListaLancamentoActivity.this.lancamentos = lancamentos;
            ListView listViewLancamento = (ListView) getActivity().findViewById(R.id.listViewLancamento);
            LancamentoAdapter lancamentoAdapter = new LancamentoAdapter(getActivity(), lancamentos);
            listViewLancamento.setAdapter(lancamentoAdapter);
            listViewLancamento.setOnItemClickListener(onItemClickListener);
            listViewLancamento.setOnItemLongClickListener(onItemLongClickListener);

            String dataInicio = DateFormat.getDateInstance().format(periodo.getDataInicial().getTime());
            String dataFinal = DateFormat.getDateInstance().format(periodo.getDataFinal().getTime());

            TextView textViewMes = (TextView) getActivity().findViewById(R.id.textViewMes);
            textViewMes.setText(
                    String.format(getResources().getString(R.string.lancamento_periodo),
                            meses[periodo.getDataInicial().get(Calendar.MONTH)], dataInicio, dataFinal));
            TextView textViewSaldo = (TextView) getActivity().findViewById(R.id.textViewSaldo);

            double pago = 0.0, aPagar = 0.0;
            for (Lancamento l : lancamentos) {
                pago += l.getValorPago();
                double diferenca = l.getValorPago() - l.getValor();
                aPagar += (diferenca >= 0) ? 0 : diferenca;
            }
            textViewSaldo.setText(String.format(getResources().getString(R.string.lancamento_valor_saldo),
                    DecimalFormat.getCurrencyInstance().format(pago),
                    DecimalFormat.getCurrencyInstance().format(aPagar)));

            ProgressDialogClose.closeProgress(progressDialog);
        }
    }
}