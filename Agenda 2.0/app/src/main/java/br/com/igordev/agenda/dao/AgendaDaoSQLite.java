package br.com.igordev.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.igordev.agenda.dao.helper.DatabaseOpenHelper;
import br.com.igordev.agenda.modelo.Compromisso;
import br.com.igordev.agenda.modelo.Contato;

public class AgendaDaoSQLite {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public AgendaDaoSQLite(Context context) {
        this.databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
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
        return contentValues;
    }

    private ContentValues carregaDados(Compromisso compromisso) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("textoCurto", compromisso.getTextoCurto());
        contentValues.put("textoLongo", compromisso.getTextoLongo());
        contentValues.put("data", compromisso.getData());
        contentValues.put("hora", compromisso.getHora());
        return contentValues;
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

    private Compromisso ormCompromisso(Cursor c) {
        Compromisso compromisso = new Compromisso();
        compromisso.setId(c.getLong(c.getColumnIndex("id")));
        compromisso.setTextoCurto(c.getString(c.getColumnIndex("textoCurto")));
        compromisso.setTextoLongo(c.getString(c.getColumnIndex("textoLongo")));
        compromisso.setData(c.getString(c.getColumnIndex("data")));
        compromisso.setHora(c.getString(c.getColumnIndex("hora")));
        return compromisso;
    }

    public long grava(Contato contato) {
        return database.insert("Contato", null, carregaDados(contato));
    }

    public long grava(Compromisso compromisso) {
        return database.insert("Compromisso", null, carregaDados(compromisso));
    }

    public int atualiza(Contato contato) {
        return database.update("Contato", carregaDados(contato), "id=" + contato.getId(),
                null);
    }

    public int atualiza(Compromisso compromisso) {
        return database.update("Compromisso", carregaDados(compromisso), "id=" + compromisso.getId(),
                null);
    }

    public int exclui(Contato contato) {
        return database.delete("Contato", "id=" + contato.getId(), null);
    }

    public int exclui(Compromisso compromisso) {
        return database.delete("Compromisso", "id=" + compromisso.getId(), null);
    }

    public Contato buscaContatoPorId(Long id) {
        Cursor c = database.query("Contato", null, "id=" + id, null, null, null, null);
        c.moveToFirst();
        Contato contato = ormContato(c);
        c.close();
        return  contato;
    }

    public Compromisso buscaCompromissoPorId(Long id) {
        Cursor c = database.query("Compromisso", null, "id=" + id, null, null, null, null);
        c.moveToFirst();
        Compromisso compromisso = ormCompromisso(c);
        c.close();
        return  compromisso;
    }

    public List<Contato> listaContatos() {
        List<Contato> contatos = new ArrayList<>();
        Cursor c = database.query("Contato", null, null, null, null, null, null);
        while (c.moveToNext()) {
            contatos.add(ormContato(c));
        }
        c.close();
        return  contatos;
    }

    public List<Compromisso> listaCompromissos() {
        List<Compromisso> compromissos = new ArrayList<>();
        Cursor c = database.query("Compromisso", null, null, null, null, null, null);
        while (c.moveToNext()) {
            compromissos.add(ormCompromisso(c));
        }
        c.close();
        return  compromissos;
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }



}
