package com.example.igordev.financeiro;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igordev.financeiro.dao.EnumTask;
import com.example.igordev.financeiro.dao.FinanceiroDao;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.model.Lancamento;
import com.example.igordev.financeiro.picker.DatePickerFragment;
import com.example.igordev.financeiro.picker.TimePickerFragment;
import com.example.igordev.financeiro.receiver.NotificacaoReceiver;
import com.example.igordev.financeiro.util.Animador;
import com.example.igordev.financeiro.util.ProgressDialogClose;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LancamentoActivity extends AppCompatActivity {

    private static final int RESULT_CAMERA = 1000;
    private final BitmapFactory.Options options;
    private Dao<Lancamento, Integer> daoLancamento;
    private Categoria categoria;
    private int lancamentoId = -1;
    private ImageView imageViewIcon;
    private TextView textViewCategoria;
    private EditText editTextDescricao;
    private TextView textViewVencimento;
    private Calendar dataVencimento = Calendar.getInstance();
    private TextView textViewPagamento;
    private Calendar dataPagamento = Calendar.getInstance();
    private EditText editTextValor;
    private EditText editTextValorPago;
    private Switch switchNotificar;
    private TextView textViewHoraNotificacao;
    private ProgressDialog progressDialog;
    //foto lançamento
    private Uri pathFotoLancamento;
    private List<Lancamento> lancamentos;

    {
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 4;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento);

        imageViewIcon = (ImageView) findViewById(R.id.imageViewIcon);
        textViewCategoria = (TextView) findViewById(R.id.textViewCategoria);
        editTextDescricao = (EditText) findViewById(R.id.editTextDescricao);
        textViewVencimento = (TextView) findViewById(R.id.textViewVencimento);
        textViewPagamento = (TextView) findViewById(R.id.textViewPagamento);
        editTextValor = (EditText) findViewById(R.id.editTextValor);
        editTextValorPago = (EditText) findViewById(R.id.editTextValorPago);
        switchNotificar = (Switch) findViewById(R.id.switchNotificar);
        textViewHoraNotificacao = (TextView) findViewById(R.id.textViewHoraNotificacao);
        progressDialog = new ProgressDialog(getActivity());

        Categoria savedCategoria;
        Lancamento savedLancamento = (Lancamento) getIntent().getSerializableExtra("lancamento");

        if (savedLancamento != null) {
            savedCategoria = savedLancamento.getCategoria();
            editTextDescricao.setText(savedLancamento.getDescricao());
            textViewVencimento.setText(DateFormat.getDateInstance().format(savedLancamento.getVencimento()));
            textViewHoraNotificacao.setText(
                    String.format(getResources().getString(R.string.lancamento_horario),
                            DateFormat.getTimeInstance().format(savedLancamento.getVencimento())));
            dataVencimento.setTime(savedLancamento.getVencimento());
            if (savedLancamento.getPagamento() != null) {
                textViewPagamento.setText(DateFormat.getDateInstance().format(savedLancamento.getPagamento()));
                dataPagamento.setTime(savedLancamento.getPagamento());
            } else {
                textViewPagamento.setText(null);
            }
            editTextValor.setText(String.valueOf(savedLancamento.getValor()));
            editTextValorPago.setText(String.valueOf(savedLancamento.getValorPago()));
            switchNotificar.setChecked(savedLancamento.isNotificacao());
            if (savedLancamento.getImagem() != null) {
                pathFotoLancamento = Uri.parse(savedLancamento.getImagem());
                carregarFotoLancamento();
            }
            lancamentoId = savedLancamento.getId();
        } else {
            savedCategoria = (Categoria) getIntent().getSerializableExtra("categoria");
            lancamentoId = -1;
            dataVencimento.set(Calendar.HOUR_OF_DAY, 12);
            dataVencimento.set(Calendar.MINUTE, 0);
            dataVencimento.set(Calendar.SECOND, 0);
            textViewHoraNotificacao.setText(
                    String.format(getResources().getString(R.string.lancamento_horario),
                            DateFormat.getTimeInstance().format(dataVencimento.getTime())));
        }

        if (savedCategoria != null) {
            categoria = savedCategoria;
            imageViewIcon.setImageResource(savedCategoria.getImagem());
            textViewCategoria.setText(savedCategoria.getNome());
        } else {
            String erro = getResources().getString(R.string.lancamento_lista_erro);
            Log.i(getResources().getString(R.string.app_name), erro);
            throw new RuntimeException(erro);
        }
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    public void onDatePickerClick(View v) {
        DatePickerFragment dialog = new DatePickerFragment();
        if (v.getId() == R.id.textViewVencimento) {
            dialog.setTextViewAndDate(textViewVencimento, dataVencimento);
        } else {
            dialog.setTextViewAndDate(textViewPagamento, dataPagamento);
        }
        dialog.show(getSupportFragmentManager(), "datePicker");
    }

    public void onTimePickerClick(View v) {
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.setTextViewAndTime(textViewHoraNotificacao, R.string.lancamento_horario, dataVencimento);
        dialog.show(getSupportFragmentManager(), "timePicker");
    }

    public void onSaveLancamento(View v) {
        Animador.animaView(v, getResources().getInteger(R.integer.velocidade_animacao));
        if (editTextDescricao.getText().toString().equals("") || editTextValor.getText().toString().equals("") ||
                textViewVencimento.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.lancamento_obrigatorio), Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(editTextValor.getText().toString());
        double valorPago = 0;
        if (!editTextValorPago.getText().toString().equals("")) {
            valorPago = Double.parseDouble(editTextValorPago.getText().toString());
        }
        Date pagamento;
        if (textViewPagamento.getText().toString().equals("")) {
            pagamento = null;
        } else {
            pagamento = dataPagamento.getTime();
        }

        Lancamento lancamento = new Lancamento(categoria, editTextDescricao.getText().toString(),
                dataVencimento.getTime(), pagamento, valor, valorPago, switchNotificar.isChecked(),
                pathFotoLancamento != null ? pathFotoLancamento.toString() : null);

        new DAOTask(EnumTask.SAVE, lancamento).execute();
    }

    public void onFotoClick(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_CAMERA);
            }
        } else {
            apagarFotosNaoUtilizadas();
            pathFotoLancamento = getImageUri();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            /*
            Para compartilhar um arquivo de forma segura, você deverá criar um caminho na área
            privada de armazenamento reservado para sua APP e compartilhar esse caminho com a outra APP.
            O reponsável pela criação desse caminho é o FileProvider.
             */

            File file  = new File(pathFotoLancamento.getPath());
            //Cria a subpasta "provider" no final do PackageName da APP e envia para câmera gravar o arquivo
            Uri fotoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(intent, RESULT_CAMERA);
        }
    }

    private Uri getImageUri() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = new File(Environment.getExternalStorageDirectory() + "/PH_FINA");
            if (!path.exists()) {
                path.mkdir();
            }
            File file = new File(path, "photo_lanc_" + System.currentTimeMillis() + ".jpg");
            return Uri.fromFile(file);
        }
        Toast.makeText(getActivity(), R.string.falha_sd, Toast.LENGTH_SHORT);
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAMERA) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                pathFotoLancamento = null;
            }
            carregarFotoLancamento();
        }
    }

    private void carregarFotoLancamento() {
        final ImageView imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);
        if (pathFotoLancamento != null) {
            Uri imagePath = pathFotoLancamento;
            Bitmap img = BitmapFactory.decodeFile(imagePath.getPath(), options);
            //para rodar a imagem para a posição certa
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            img = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            imageViewFoto.setImageBitmap(img);
        } else {
            imageViewFoto.setImageResource(R.drawable.nophoto);
        }
        imageViewFoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageViewFoto.setImageResource(R.drawable.nophoto);
                pathFotoLancamento = null;
                return true;
            }
        });
    }

    private void apagarFotosNaoUtilizadas() {
        new DAOTask(EnumTask.REFRESH).execute();
    }

    private class DAOTask extends AsyncTask<String, Integer, List<Lancamento>> { //versão plus mudar para List<Lancamento>

        final private EnumTask enumTask;
        final private Lancamento lancamento;
        private List<Lancamento> lancamentos = new ArrayList<>();

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

            try (FinanceiroDao<Lancamento> dao = new FinanceiroDao<>(getActivity(), Lancamento.class)) {
                daoLancamento = dao.getDao();
                switch (enumTask) {
                    case SAVE:
                        if (lancamentoId == -1) {
                            daoLancamento.create(lancamento);
                        } else {
                            lancamento.setId(lancamentoId);
                            daoLancamento.update(lancamento);
                        }
                        if (lancamento.isNotificacao() &&
                                (lancamento.getValorPago() < lancamento.getValor())) {
                            NotificacaoReceiver.ativarNotificacao(getActivity(), lancamento);
                        } else {
                            NotificacaoReceiver.desativarNotificacao(getActivity(), lancamento);
                        }
                        lancamentos.add(lancamento);
                        break;
                    case REFRESH:
                        QueryBuilder<Lancamento, Integer> queryBuilder =
                                daoLancamento.queryBuilder();
                        Where<Lancamento, Integer> where = queryBuilder.where();
                        where.isNotNull("imagem");
                        lancamentos = daoLancamento.query(queryBuilder.prepare());
                        break;

                }
            } catch (SQLException e) {
                Log.i(getResources().getString(R.string.app_name),
                        getResources().getString(R.string.erro_iniciar_bd));
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
            LancamentoActivity.this.lancamentos = lancamentos; //apagar fotos não utilizadas
            switch (enumTask) {
                case SAVE:
                    Lancamento lancamento = lancamentos.get(0);
                    String mensagemFormatada = String.format(getResources().getString(R.string.lancamento_gravado), lancamento.getDescricao());
                    Log.i(getResources().getString(R.string.app_name), mensagemFormatada);
                    Toast.makeText(getActivity(), mensagemFormatada, Toast.LENGTH_SHORT).show();
                    getActivity().finish(); //fecha a tela depois de salvar
                    break;
                case REFRESH:
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File sdCardDir = new File(Environment.getExternalStorageDirectory() + "/PH_FINA");
                        String[] files = sdCardDir.list();
                        List<String> fotos = new ArrayList<>();
                        for (Lancamento l : lancamentos) {
                            fotos.add(Uri.parse(l.getImagem()).getPath());
                        }
                        for (String f : files) {
                            String fileName = sdCardDir.getAbsolutePath() + "/" + f;
                            if (fotos.indexOf(fileName) == -1) {
                                File file = new File(fileName);
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                    break;
            }
            ProgressDialogClose.closeProgress(progressDialog);
        }
    }
}