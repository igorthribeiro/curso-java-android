package br.com.igordev.memoria.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import br.com.igordev.memoria.R;

public class ConfiguracaoFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_jogo);
    }
}
