package agenda.igordev.com.br.agenda.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import agenda.igordev.com.br.agenda.R;
import agenda.igordev.com.br.agenda.action.AcaoSim;
import agenda.igordev.com.br.agenda.dao.ContatoDaoSQLite;
import agenda.igordev.com.br.agenda.modelo.Contato;
import agenda.igordev.com.br.agenda.retrofit.cep.Cep;
import agenda.igordev.com.br.agenda.retrofit.RetrofitConfig;
import agenda.igordev.com.br.agenda.util.DialogoSimNao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatoFragment extends Fragment {

    private static final int RESULT_CAMERA = 1000;
    private String mCurrentPhotoPath;
    private ImageView imageViewFoto;
    private EditText edtNome, edtEmail, edtTelefone, edtCep, edtEndereco;
    private Button buttonCep, buttonSMS;
    private FloatingActionButton fabSalvar, fabExcluir;
    private Contato contato;

    public ContatoFragment() {
        // Required empty public constructor
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    private File createImageFile() throws IOException {
        //cria o arquivo para receber a foto
        String imageFileName = "jpeg_temp_foto";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* arquivo */
                ".jpg", /* extensão */
                storageDir /* diretório */
        );

        //armazena o caminho para o arquivo
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // procura um aplicativo para resolver a requisição da câmera
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // criação do arquivo
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(getClass().getSimpleName(), "Erro ao obter foto: " + ex.getMessage());
            }
            // Se o arquivo foi criado com sucesso, aciona a câmera
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "br.com.igordev.agenda.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_CAMERA);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contato, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //associação de componentes com a view
        imageViewFoto = view.findViewById(R.id.imageViewFoto);
        fabSalvar = view.findViewById(R.id.fabSalvar);
        fabExcluir = view.findViewById(R.id.fabExcluir);
        buttonCep = view.findViewById(R.id.buttonCep);
        buttonSMS = view.findViewById(R.id.buttonSMS);
        edtNome = view.findViewById(R.id.edtNome);
        edtEndereco = view.findViewById(R.id.edtEndereco);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtTelefone = view.findViewById(R.id.edtTelefone);
        edtCep = view.findViewById(R.id.edtCep);

        //verifica rotação da tela
        if (savedInstanceState != null) {
            contato = (Contato) savedInstanceState.getSerializable("contato");
        }

        //mostra os dados do contato na tela
        if (contato == null) {
            contato = new Contato();
        } else {
            preencheCampos(contato);
        }

        //configura o evento de click para a foto
        imageViewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //configura o evento de click para o botão salvar
        fabSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNome.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), R.string.erro_cadastro_contato, Toast.LENGTH_LONG).show();
                    return;
                }
                //setar campos
                contato.setFoto(bitmapToByteArray(imageViewFoto.getDrawable()));
                contato.setNome(edtNome.getText().toString());
                contato.setEndereco(edtEndereco.getText().toString());
                contato.setEmail(edtEmail.getText().toString());
                contato.setTelefone(edtTelefone.getText().toString());
                contato.setCep(edtCep.getText().toString());
                //execussão assincrona
                new SalvaContatoTask().execute(contato);
            }
        });

        //configura o evento de click para o botão excluir
        fabExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoSimNao.cria(getActivity(), R.string.titulo_dialolgo_excluir, R.string.confirma_excluir, new AcaoSim() {
                            @Override
                            public void executar() {
                                new ExcluirContatoTask().execute(contato);
                            }
                        }, null);
            }
        });

        //configura o evento de click para buscar o CEP
        buttonCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Call<Cep> cepCall = new RetrofitConfig().getCepService().buscaCep(edtCep.getText().toString());
                cepCall.enqueue(new Callback<Cep>() {
                    @Override
                    public void onResponse(Call<Cep> call, Response<Cep> response) {
                        Cep cep = response.body();
                        edtCep.setText(cep.getCep());
                        edtEndereco.setText(cep.getLogradouro() + " " + cep.getBairro() + " - " + cep.getUf().toUpperCase());
                    }

                    @Override
                    public void onFailure(Call<Cep> call, Throwable t) {
                        Log.e(getClass().getSimpleName(), "Erro no serviço de cep: " + t.getMessage());
                        Toast.makeText(getActivity(), R.string.erro_servico, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTelefone.getText().toString().equals("")) {
                    return;
                }
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(edtTelefone.getText().toString(),
                        null, "Olá. Adicionei seu número.", null, null);
                Toast.makeText(getActivity(), R.string.mensagem_enviada, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("contato", contato);
    }

    public void preencheCampos(Contato contato) {
        if (contato.getFoto() != null) {
            imageViewFoto.setImageBitmap(BitmapFactory.decodeByteArray(contato.getFoto(), 0, contato.getFoto().length));
        }
        edtNome.setText(contato.getNome());
        edtEndereco.setText(contato.getEndereco());
        edtEmail.setText(contato.getEmail());
        edtTelefone.setText(contato.getTelefone());
        edtCep.setText(contato.getCep());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAMERA) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                return;
            }
            Bitmap img = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageViewFoto.setImageBitmap(img);
        }
    }

    private byte[] bitmapToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return bitMapData;
    }


    private class SalvaContatoTask extends AsyncTask<Contato, Integer, Long> {

        ContatoDaoSQLite contatoDaoSQLite = new ContatoDaoSQLite(getActivity());

        @Override
        protected Long doInBackground(Contato... contatos) {
            if (contatos[0].getId() == null) {
                return contatoDaoSQLite.grava(contatos[0]);

            } else {
                contatoDaoSQLite.atualiza(contatos[0]);
                return contatos[0].getId();
            }
        }

        @Override
        protected void onPostExecute(Long id) {
            Toast.makeText(getActivity(), R.string.sucesso_cadastro, Toast.LENGTH_LONG).show();
            contato.setId(id);
            contatoDaoSQLite.close();
            //volta para lista após o salvamento
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private class ExcluirContatoTask extends  AsyncTask<Contato, Integer, Integer> {

        ContatoDaoSQLite contatoDaoSQLite = new ContatoDaoSQLite(getActivity());

        @Override
        protected Integer doInBackground(Contato...contatos) {
            return contatoDaoSQLite.exclui(contatos[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }
}