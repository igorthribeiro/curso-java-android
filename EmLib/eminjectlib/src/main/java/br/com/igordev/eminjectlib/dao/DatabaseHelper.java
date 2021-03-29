package br.com.igordev.eminjectlib.dao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public abstract class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "database.db";

    private Context context;

    protected DatabaseHelper(Context context) throws PackageManager.NameNotFoundException {
        super(context, DATABASE_NAME, null, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        //cria as novas tabelas;
        atualizaTabelas();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        //cria as novas tabelas
        atualizaTabelas();
        oldVersion++;
        for (int i = oldVersion; i <= newVersion; i++) {
            Log.i(getClass().getSimpleName(), "Looking for upgrade commands entry: upgrade_for_version_" + i);
            int upgradeCommandsId = context.getResources().getIdentifier("upgrade_for_version_" + i, "array", context.getPackageName());
            if (upgradeCommandsId == 0) {
                Log.i(getClass().getSimpleName(), "SQL upgrade commands for version: " + i + " not found.");
            } else {
                Log.i(getClass().getSimpleName(),"SQL upgrade commands for version " + i + " found.");
                String[] upgradeCommandArray = context.getResources().getStringArray(upgradeCommandsId);
                EmPrepare.updateTables(database, upgradeCommandArray);
            }
        }
    }

    private void atualizaTabelas() {
        try {
            int tableArrayId =  context.getResources().getIdentifier("entities", "array", context.getPackageName());
            if (tableArrayId == 0) {
                throw new RuntimeException("No entity found for create table.\n\tCreate entities.xml resource file and add entity array <string-array name=\"entities\">");
            }
            String[] tableArray = context.getResources().getStringArray(tableArrayId);
            EmPrepare.createTables(connectionSource, tableArray);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
