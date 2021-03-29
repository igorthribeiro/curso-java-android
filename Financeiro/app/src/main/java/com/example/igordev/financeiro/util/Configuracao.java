package com.example.igordev.financeiro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.igordev.financeiro.R;

/**
 * Created by igordev on 04/08/17.
 */

public class Configuracao {

    public static int DIA_FECHAMENTO_MES;
    public static boolean NOTIFICACAO_ATIVA;

    public static void carregar(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.financeiro_settings, false);
        //carrega um objeto SharedPreferences para buscar as configuracoes armazenadas
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //seta os valores
        DIA_FECHAMENTO_MES = Integer.parseInt(sharedPreferences.getString("settings_dia", "1"));
        NOTIFICACAO_ATIVA = sharedPreferences.getBoolean("settings_notificacao", true);
    }
}
