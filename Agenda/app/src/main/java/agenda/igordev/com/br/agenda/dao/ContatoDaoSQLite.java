package agenda.igordev.com.br.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import agenda.igordev.com.br.agenda.dao.helper.DatabaseOpenHelper;
import agenda.igordev.com.br.agenda.modelo.Contato;

public class ContatoDaoSQLite {

    private static final String DATABASE_NAME = "datatabase.db";
    private static final int DATABASE_VERSION = 1;
    private final SQLiteDatabase database;
    private final DatabaseOpenHelper databaseOpenHelper;

    public ContatoDaoSQLite(Context context) {
        this.databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.database = databaseOpenHelper.getWritableDatabase();
    }

    private ContentValues carregaDados(Contato contato) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome", contato.getNome());
        contentValues.put("endereco", contato.getEndereco());
        contentValues.put("email", contato.getEmail());
        contentValues.put("telefone", contato.getTelefone());
        contentValues.put("cep", contato.getCep());
        contentValues.put("foto", contato.getFoto());
        return  contentValues;
    }

    public long grava(Contato contato) {
        return database.insert("Contato", null, carregaDados(contato));
    }

    public int atualiza(Contato contato) {
        return database.update("Contato", carregaDados(contato), "id=" + contato.getId(), null);
    }

    public int exclui(Contato contato) {
        return database.delete("Contato", "id=" + contato.getId(), null);
    }

    public Contato buscaPorId(Long id) {
        Cursor c = database.query("Contato", null, "id=" + id, null, null, null, null);
        c.moveToFirst();
        Contato contato = ormContato(c);
        c.close();
        return  contato;
    }

    public List<Contato> lista() {
        List<Contato> contatos = new ArrayList<>();
        Cursor c = database.query("Contato", null, null, null, null, null, null);
        while (c.moveToNext()) {
            contatos.add(ormContato(c));
        }
        c.close();
        return  contatos;
    }


    private Contato ormContato(Cursor c) {
        Contato contato = new Contato();
        contato.setId(c.getLong(c.getColumnIndex("id")));
        contato.setNome(c.getString(c.getColumnIndex("nome")));
        contato.setCep(c.getString(c.getColumnIndex("cep")));
        contato.setEndereco(c.getString(c.getColumnIndex("endereco")));
        contato.setEmail(c.getString(c.getColumnIndex("email")));
        contato.setTelefone(c.getString(c.getColumnIndex("telefone")));
        contato.setFoto(c.getBlob(c.getColumnIndex("foto")));
        return  contato;
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
