package br.com.igordev.eminjectlib.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;

public final class EmPrepare {

    private EmPrepare() {
    }

    protected static void prepare(Context context, Object object) throws IllegalAccessException {
        Class clazz = object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(EntityManager.class)) {
                Class entidade = (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                f.setAccessible(true);
                f.set(object, new Em(context, entidade));
            }
        }
    }

    protected static void createTables(ConnectionSource connectionSource, String[] tables) throws SQLException, ClassNotFoundException {
        for (String table : tables) {
            TableUtils.createTableIfNotExists(connectionSource, Class.forName(table));
        }
    }

    protected static void updateTables(SQLiteDatabase database, String[] upgradeCommandArray) {
        for (String sqlCommand : upgradeCommandArray) {
            database.execSQL(sqlCommand);
            Log.i("EM upgrade","SQL command: " + sqlCommand );
        }
    }
}
