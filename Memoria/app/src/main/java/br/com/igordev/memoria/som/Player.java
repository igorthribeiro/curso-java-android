package br.com.igordev.memoria.som;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import br.com.igordev.memoria.R;

public class Player {

    public static final int SOM_ACERTO = R.raw.acerto;
    public static final int SOM_ERRO = R.raw.erro;

    private float volumeSom;
    private static final int MAX_STREAMS = 8;
    private int somAcerto, somErro;
    private Map<Integer, Integer> sons;
    private MediaPlayer mediaPlayer;

    private AudioAttributes audioAttributes;
    private SoundPool soundPool;


    public Player(Context context) {
        //verifica a necessidade de criação do AudioAttributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        //carrega os recursos de som
        somErro = soundPool.load(context, SOM_ERRO, 1);
        somAcerto = soundPool.load(context, SOM_ACERTO, 1);

        //mapear sons do jogo associando a uma constante
        sons = new HashMap<>();
        sons.put(SOM_ACERTO, somAcerto);
        sons.put(SOM_ERRO, somErro);

        //fundo musical
        mediaPlayer = MediaPlayer.create(context, R.raw.bk_music);
        mediaPlayer.setLooping(true);
    }


    public void iniciaMusica() {
        mediaPlayer.start();
    }

    public void pausaMusica() {
        mediaPlayer.pause();
    }

    public void paraMuscica() {
        mediaPlayer.stop();
    }

    public void reset() {
        mediaPlayer.reset();
    }


    public void tocaSom(int som) {
    /*
      Parâmetros do SoundPool.play(A, B, C, D, E, F) :

      A => o som que irá tocar
      B => o volume esquerdo (entre 0f e 1f)
      C => o volume direito (entre 0f e 1f)
      D => a prioridade (colocar 1)
      E => o loop (0 - som loop / -1 loop infinito)
      F => velocidade (entre 0.5f e 2.0f)

     */
        if (sons.containsKey(som)) {
            soundPool.play(sons.get(som), volumeSom, volumeSom, 1, 0, 1);
        }
    }

    public void setVolumeSom(float volumeSom) {
        this.volumeSom = volumeSom;
        mediaPlayer.setVolume(volumeSom, volumeSom); //1 = 100% do volume
    }

}
