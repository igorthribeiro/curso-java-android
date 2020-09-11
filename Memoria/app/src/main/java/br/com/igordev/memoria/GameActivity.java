package br.com.igordev.memoria;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.igordev.memoria.adapter.ImagensAdapter;
import br.com.igordev.memoria.som.Player;
import br.com.igordev.memoria.tempo.ContagemRegressiva;

public class GameActivity extends AppCompatActivity implements ContagemRegressiva.ContagemListener {

    private static final int CONFIGURACAO_REQUEST = 200;

    private int paresDificuldade;
    private GridView gridView;
    private AssetManager assetManager;
    private Handler handler;
    private int paresAcertados;
    private ImageView figuraImagem1, figuraImagem2;
    private TextView textView;
    private ContagemRegressiva contagemRegressiva;
    private Player player;

    private ImageView.OnClickListener itemClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (figuraImagem2 != null) {
                //não faz nada
                return;
            }

            view.setOnClickListener(null); //bloqueada para clique
            ImageView imageView = (ImageView) view;
            viraCarta(imageView, "img/" + imageView.getTag().toString());

            if (figuraImagem1 == null) {
                figuraImagem1 = imageView;
            } else {
                figuraImagem2 = imageView;
                verificaAcerto();
            }
        }
    };


    private void viraCarta(ImageView imageView, String figura) {
        try {
            InputStream stream = assetManager.open(figura);
            Drawable drawable = Drawable.createFromStream(stream, figura);
            imageView.setImageDrawable(drawable);

            //extra: acessibilidade
            Pattern p = Pattern.compile("^(img|imgdef)\\/(.*)(.png)$");
            Matcher m = p.matcher(figura);
            if (m.matches()) {
                int codigoImagem = getResources().getIdentifier(m.group(2), "string", getPackageName());
                imageView.setContentDescription(getResources().getString(codigoImagem));
            }

        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Erro ao virar a carta: " + e.getMessage());
        }
    }


    private void verificaAcerto() {
        //comparação das imagens
        if (figuraImagem1.getTag().toString().equals(figuraImagem2.getTag().toString())) {

            //toca som acerto
            player.tocaSom(Player.SOM_ACERTO);

            //o jogador acertou: incrementa pares acertatos
            paresAcertados++;

            //executa a animação para o acerto
            animaAcerto();
            Log.d(getClass().getSimpleName(), "Acertou - pares: " + paresAcertados);

            //verifica o fim da partida (acerto todos os pares)
            if (paresAcertados == paresDificuldade) {
                contagemRegressiva.cancel();
                Toast.makeText(this, R.string.fim_jogo, Toast.LENGTH_LONG).show();
            }

        } else { //o jogador errou

            //toca som erro
            player.tocaSom(Player.SOM_ERRO);

            //animação para o erro
            animaErro();

            //handler que vira as cartas
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //vira a carta e reatribui o evento de click
                    viraCarta(figuraImagem1, "imgdef/wildcard.png");
                    figuraImagem1.setOnClickListener(itemClick);
                    viraCarta(figuraImagem2, "imgdef/wildcard.png");
                    figuraImagem2.setOnClickListener(itemClick);

                    //extra: acessibilidade
                    for (int i = 0; i < gridView.getChildCount(); i++) {
                        ImageView img = gridView.getChildAt(i).findViewById(R.id.imageView);
                        if (img.getTag().equals(figuraImagem1.getTag()) || img.getTag().equals(figuraImagem2.getTag())) {
                            gridView.getChildAt(i).setContentDescription(getResources().getString(R.string.wildcard));
                        }
                    }
                }
            }, 350);
        }

        //handler para limpar a jogada
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //limpa a jogada
                figuraImagem1 = figuraImagem2 = null;
            }
        }, 710);
    }

    private void animaErro() {
        ObjectAnimator animC1 = ObjectAnimator.ofFloat(figuraImagem1, "scaleX", 1f, 0f, 1f);
        ObjectAnimator animC2 = ObjectAnimator.ofFloat(figuraImagem2, "scaleX", 1f, 0f, 1f);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animC1, animC2);
        animSetXY.setDuration(700);
        animSetXY.start();
    }

    private void animaAcerto() {
        figuraImagem1.animate().setDuration(700).alpha(0.3f);
        figuraImagem2.animate().setDuration(700).alpha(0.3f);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player = new Player(this);

        iniciarPartida();

    }

    private List<String> carregaImagens(int paresDificuldade) {
        assetManager = getAssets();
        try {
            //listamos todos os arquivos da pasta /img
            List<String> lista = new ArrayList(Arrays.asList(assetManager.list("img")));
            //embaralhamos as imagens
            Collections.shuffle(lista);
            //removemos os pares desnecessários para o jogo
            List<String> listaFinal = new ArrayList<>(lista.subList(0, paresDificuldade));

            List<String> quadroImagens = new ArrayList<>(paresDificuldade * 2);
            //adicionamos 2x pois precisaremos dos pares
            quadroImagens.addAll(listaFinal);
            quadroImagens.addAll(listaFinal);

            //embaralhamos e retornamos a lista
            Collections.shuffle(quadroImagens);

            return quadroImagens;
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Erro ao obter a lista de imagens: " + e.getMessage());
        }

        return null;
    }


    private void ocultaImagens() {
        //pega o todal de imagens no quadro
        int elementosNoGrid = gridView.getChildCount();
        try {
            InputStream stream = assetManager.open("imgdef/wildcard.png");
            Drawable imagemPadrao = Drawable.createFromStream(stream, "wildcard");
            for (int i = 0; i < elementosNoGrid; i++) {
                //busca a imagem pelo indice
                ImageView img = gridView.getChildAt(i).findViewById(R.id.imageView);
                img.setImageDrawable(imagemPadrao);
                img.setOnClickListener(itemClick);

                //extra: acessibilidade
                gridView.getChildAt(i).setContentDescription(img.getContentDescription());
            }

            textView = findViewById(R.id.txtTime);
            contagemRegressiva = new ContagemRegressiva(30, textView, this);
            contagemRegressiva.start();

        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Erro ao obter a imagem padrão: " + e.getMessage());
        }
    }

    public void iniciarPartida() {
        //zerar os acerto
        paresAcertados = 0;

        //carrega as preferências do usuário
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);


        //definir dificultade
        paresDificuldade = Integer.parseInt(sp.getString("prefPares", null));
        //configurar o volume do jogo
        float volumeSom = sp.getBoolean("prefAudio", true) ? 1 : 0;

        //monta o quadro de imagens a partir do Adapter
        gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new ImagensAdapter(this, carregaImagens(paresDificuldade)));

        //extra: acessibilidade
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.callOnClick();
                view.setContentDescription(imageView.getContentDescription());
            }
        });

        //após 10 segundo, as imagens serão escondidas
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ocultaImagens();
            }
        }, 10000);

        //limpa a jogada e cencela a contagem se existir
        figuraImagem1 = figuraImagem2 = null;
        if (contagemRegressiva != null) {
            contagemRegressiva.cancel();
        }

        //inicia a música de fundo
        player.setVolumeSom(volumeSom);
        player.iniciaMusica();

    }


    @Override
    public void onTimeOver() {
        //localiza todas as cartas e remove o evento de click
        int elementosNoGrid = gridView.getChildCount();
        for (int i = 0; i < elementosNoGrid; i++) {
            ImageView img = gridView.getChildAt(i).findViewById(R.id.imageView);
            img.setOnClickListener(null);
        }
        textView.setText("00:00:00");
        Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        player.iniciaMusica();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.reset();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pausaMusica();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jogo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.jogoConfigura:
                Intent intent = new Intent(this, ConfiguracaoActivity.class);
                startActivityForResult(intent, CONFIGURACAO_REQUEST);
                break;
            case R.id.jogoReinicia:
                iniciarPartida();
                break;
            case R.id.paraMusica:
                player.pausaMusica();
                break;
            case R.id.tocaMusica:
                player.iniciaMusica();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIGURACAO_REQUEST) {
            iniciarPartida();
        }
    }
}
