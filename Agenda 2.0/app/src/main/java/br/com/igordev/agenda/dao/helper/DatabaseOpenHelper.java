package br.com.igordev.agenda.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public DatabaseOpenHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptCreate;
        //contato
        scriptCreate  = "create table Contato (" +
                "id integer primary key autoincrement," +
                "nome text not null," +
                "endereco text," +
                "cep text," +
                "email text, " +
                "telefone text, " +
                "foto blob);";
        db.execSQL(scriptCreate);

        //compromisso
        scriptCreate  = "create table Compromisso (" +
                "id integer primary key autoincrement," +
                "textoCurto text not null," +
                "textoLongo text," +
                "data text," +
                "hora text);";
        db.execSQL(scriptCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //executado sempre que o banco mudar de versao
    }
}
