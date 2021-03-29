package agenda.igordev.com.br.agenda.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //executado na primeira vez que a tabela for chamada
    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptCreate = "create table Contato (" +
                "id integer primary key autoincrement," +
                "nome text not null," +
                "endereco text," +
                "cep text," +
                "email text, " +
                "telefone text, " +
                "foto blob);";

        db.execSQL(scriptCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //executado sempre que o banco mudar de versao
    }
}
