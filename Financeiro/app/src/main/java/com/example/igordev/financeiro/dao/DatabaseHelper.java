package com.example.igordev.financeiro.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.igordev.financeiro.R;
import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.model.Lancamento;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by igordev on 05/07/17.
 */

public abstract class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final int[] ICONES = {
            R.drawable._im001, R.drawable._im002, R.drawable._im003, R.drawable._im004,
            R.drawable._im005, R.drawable._im006, R.drawable._im007, R.drawable._im008,
            R.drawable._im009, R.drawable._im010, R.drawable._im011, R.drawable._im012,
            R.drawable._im013, R.drawable._im014, R.drawable._im015, R.drawable._im016,
            R.drawable._im017, R.drawable._im018, R.drawable._im019, R.drawable._im020
    };
    private static final String DATABASE_NAME = "financeiro.db";
    private static final int DATABASE_VERSION = 2;

    private List<Integer> icones = new ArrayList<>();
    private Context context;
    private List<Class> tabelasBD = new ArrayList<>();

    {
        tabelasBD.add(Categoria.class);
        tabelasBD.add(Lancamento.class);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //necessário para habilitar as contraints e permitir operações
        //como delete cascade
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getSimpleName(), "onCreate");
            for (Class tabela : tabelasBD) {
                TableUtils.createTable(connectionSource, tabela);
                Log.i(context.getResources().getString(R.string.app_name),
                        String.format(context.getResources().getString(R.string.criacao_tabela_bd), tabela.getSimpleName()));
            }

            cargaInicial();

            Log.i(context.getResources().getString(R.string.app_name),
                    context.getResources().getString(R.string.fim_criacao_bd));
        } catch (SQLException e) {
            Log.i(context.getResources().getString(R.string.app_name),
                    context.getResources().getString(R.string.erro_criacao_bd), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Dao dao = getDao(Lancamento.class);
            switch (oldVersion) {
                case 1:
                    dao.executeRaw("alter table `lancamento` add column notificacao boolean;");
                    dao.executeRaw("alter table `lancamento` add column imagem text;");
            }
            Log.i(context.getResources().getString(R.string.app_name),
                    context.getResources().getString(R.string.fim_atualizacao_bd));
        } catch (SQLException e) {
            Log.i(context.getResources().getString(R.string.app_name),
                    context.getResources().getString(R.string.erro_atualizacao_bd), e);
            throw new RuntimeException(e);
        }
    }

    private void cargaInicial() throws SQLException {
        Dao dao = getDao(Categoria.class);

        for (int i = 1; i <= 10; i++) {
            //Categoria c = new Categoria(CategoriaActivity.ICONES[i], 'D', "Categoria Teste " + i, Math.round((Math.random() * 5001)));//5000

            Categoria c = new Categoria(ICONES[pegarIcone()], 'D', "Categoria Teste " + i, Math.round((Math.random() * 5001)));//5000
            dao.create(c);
            addLancamento(c);
        }

        for (int i = 11; i <= 15; i++) {
            //Categoria c = new Categoria(CategoriaActivity.ICONES[i], 'R', "Categoria Teste " + i, Math.round((Math.random() * 5001)));//5000

            Categoria c = new Categoria(ICONES[pegarIcone()], 'R', "Categoria Teste " + i, Math.round((Math.random() * 10001)));//10000
            dao.create(c);
            addLancamento(c);
        }

    }

    private int pegarIcone() {
        int icone;
        do {
            icone = (int) (Math.random() * 20);
            if (icones.indexOf(icone) == -1) {
                break;
            }
        } while (true);
        icones.add(icone);

        return icone;
    }


    private void addLancamento(Categoria categoria) throws SQLException{
        Dao dao = getDao(Lancamento.class);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date hoje = calendar.getTime();

        for (int i = 1; i <= 5; i++) {
            Lancamento l = new Lancamento(categoria, "Lançamento Teste " + i, hoje, null, Math.round((Math.random() * 2001)), 0.0, false, null);//2000
            dao.create(l);
        }
    }
}