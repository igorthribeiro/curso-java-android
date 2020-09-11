package br.com.igordev.agenda.ui.edita.contato;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.com.igordev.agenda.R;
import br.com.igordev.agenda.dao.AgendaDaoSQLite;
import br.com.igordev.agenda.modelo.Contato;
import br.com.igordev.agenda.retrofit.RetrofitConfig;
import br.com.igordev.agenda.retrofit.cep.Cep;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditaContatoFragment extends Fragment {

    private static final int RESULT_CAMERA = 1000;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 2000;
    private String mCurrentPhotoPath;
    private ImageView imageViewFoto;
    private EditText edtNome, edtEmail, edtTelefone, edtCep, edtEndereco;
    private Button buttonCep, buttonSMS;
    private FloatingActionButton fabSalvar, fabExcluir;
    private Contato contato;


    private EditaContatoViewModel mViewModel;

    public static EditaContatoFragment newInstance() {
        return new EditaContatoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edita_contato_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EditaContatoViewModel.class);
        // TODO: Use the ViewModel
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAMERA) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                return;
            }
            Bitmap img = getImagemReduzida(mCurrentPhotoPath);
            //gira a foto depois de capiturada
            img = rodarImagem(mCurrentPhotoPath, img);
            imageViewFoto.setImageBitmap(img);
        }
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

        //configura o evento de click para a foto
        imageViewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

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
                verificaPermissao();
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
                if (contato == null) contato = new Contato();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setTitle(R.string.titulo_dialolgo_excluir)
                        .setMessage(R.string.confirma_excluir)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ExcluirContatoTask().execute(contato);
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //não faz nada
                            }
                        }).setCancelable(false).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            contato = (Contato) getArguments().getSerializable("contato");
            if (contato != null) {
                preencheCampos(contato);
            }
        }
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

    //use os dois métodos a seguir para rodar a foto
    private Bitmap rodarImagem(String caminhoImagem, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        try {
            ExifInterface ei = new ExifInterface(caminhoImagem);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException ex) {
            Log.e(getClass().getSimpleName(), "Erro ao girar imagem.");
        }
        return rotatedBitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    //reduz a resolução da foto
    public Bitmap getImagemReduzida(String caminhoImagem) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(caminhoImagem, bmOptions);
        int fator = Math.min(bmOptions.outWidth/200, bmOptions.outHeight/200);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = fator;
        return BitmapFactory.decodeFile(caminhoImagem, bmOptions);
    }

    //converte em um array de bytes
    private byte[] bitmapToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return bitMapData;
    }

    protected void enviarSMS(String telefone) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(telefone,
                null, "Olá. Adicionei seu número.", null, null);
        Toast.makeText(getActivity(), R.string.mensagem_enviada, Toast.LENGTH_LONG).show();
    }

    private void verificaPermissao() {
        //verifica se o aplicativo tem permissão
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //verifica se a permissão já foi negada no passado
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                Toast.makeText(getActivity(), R.string.erro_sms, Toast.LENGTH_LONG).show();
            } else {
                //abre a caixa de diálogo de solicitação da permissão
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            enviarSMS(edtTelefone.getText().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0].equalsIgnoreCase  (Manifest.permission.SEND_SMS) && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //tudo OK, pode enviar
                    enviarSMS(edtTelefone.getText().toString());
                } else {
                    Log.d(getClass().getSimpleName(), "Sem permissão para enviar SMS.");
                }
            }
        }
    }

    //classe assíncrona para salva o contato
    private class SalvaContatoTask extends AsyncTask<Contato, Integer, Long> {

        AgendaDaoSQLite agendaDaoSQLite = new AgendaDaoSQLite(getActivity());

        @Override
        protected Long doInBackground(Contato... contatos) {
            /*
            O if verifica
                - se o id é nulo, a operação foi de cadastro: grava(contato[0])
                - se possui id, o contato foi alterado: altera(contato[0])
             */
            if (contatos[0].getId() == null) {
                return agendaDaoSQLite.grava(contatos[0]);
            } else {
                agendaDaoSQLite.atualiza(contatos[0]);
                return contatos[0].getId();
            }
        }

        @Override
        protected void onPostExecute(Long id) {
            Toast.makeText(getActivity(), R.string.sucesso_cadastro, Toast.LENGTH_LONG).show();
            contato.setId(id);
            agendaDaoSQLite.close();
            //volta para lista após o salvamento
            NavHostFragment.findNavController(EditaContatoFragment.this)
                    .navigate(R.id.action_nav_edita_contato_to_nav_lista_contato);
        }
    }

    //classe assíncrona para excluir um contato
    private class ExcluirContatoTask extends  AsyncTask<Contato, Integer, Integer> {

        AgendaDaoSQLite agendaDaoSQLite = new AgendaDaoSQLite(getActivity());

        @Override
        protected Integer doInBackground(Contato...contatos) {
            return agendaDaoSQLite.exclui(contatos[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //volta para lista após a exclusão
            NavHostFragment.findNavController(EditaContatoFragment.this)
                    .navigate(R.id.action_nav_edita_contato_to_nav_lista_contato);
        }
    }


}