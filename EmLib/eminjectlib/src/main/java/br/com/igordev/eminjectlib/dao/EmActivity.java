package br.com.igordev.eminjectlib.dao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class EmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Inject EntityManagers
        try {
            EmPrepare.prepare(this, this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
