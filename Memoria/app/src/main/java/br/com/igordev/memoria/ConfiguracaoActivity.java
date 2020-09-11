package br.com.igordev.memoria;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class ConfiguracaoActivity extends AppCompatActivity {

    //declaração do FloatActionButton
    FloatingActionButton fabRecarregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        fabRecarregar = findViewById(R.id.fabRecarregar);
        fabRecarregar.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fecha a tela e retorna para activity anterior
                finish();
            }
        });
    }
}
