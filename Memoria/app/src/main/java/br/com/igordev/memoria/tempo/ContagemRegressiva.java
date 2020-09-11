package br.com.igordev.memoria.tempo;

import android.os.CountDownTimer;
import android.widget.TextView;

public class ContagemRegressiva extends CountDownTimer {

    private TextView textView;
    private ContagemListener contagemListener;

    public ContagemRegressiva(long tempoMinutosJogo, TextView textView,
                              ContagemListener contagemListener) {
        super(tempoMinutosJogo * 1000 * 60, 1000); //converte em milissegundos
        this.textView = textView;
        //anexa o listener
        this.contagemListener = contagemListener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long segundo = (millisUntilFinished / 1000) % 60;
        long minuto = (millisUntilFinished / (1000 * 60)) % 60;
        long hora = (millisUntilFinished / (1000 * 60 * 60)) % 24;

        String diferencaTempo = String
                .format("%02d:%02d:%02d", hora, minuto, segundo);

        textView.setText(diferencaTempo);
    }

    @Override
    public void onFinish() {
        contagemListener.onTimeOver();
    }

    public interface ContagemListener {
        void onTimeOver();
    }

}
