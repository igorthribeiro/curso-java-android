package com.example.igordev.financeiro.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by igordev on 06/07/17.
 */

public class FinanceiroDao<T> extends DatabaseHelper implements AutoCloseable {

    //A classe passada como parâmetro deve ser do mesmo tipo de T
    //Class t => raw type (Object)
    //Class<T> t => Força o mesmo tipo de T. (List != List<String>)
    final private Class<T> t;

    public FinanceiroDao(Context context, Class<T> t) {
        super(context);
        this.t = t;
    }

    public Dao<T, Integer> getDao() throws SQLException {
        Dao<T, Integer> dao = null;
        if (dao == null) {
            dao = getDao(t);
        }
        return dao;
    }
}
