package com.example.igordev.financeiro.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.igordev.financeiro.R;

/**
 * Created by igordev on 28/07/17.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.financeiro_settings);
    }
}
