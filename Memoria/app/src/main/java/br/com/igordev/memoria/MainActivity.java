package br.com.igordev.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //carrega os valores default das preferências
        PreferenceManager.setDefaultValues(this, R.xml.settings_jogo, false);
    }

    public void iniciarClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}
