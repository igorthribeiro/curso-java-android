package com.example.igordev.financeiro.model;

import com.example.igordev.financeiro.util.FormatarPeriodo;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by igordev on 05/07/17.
 */

@DatabaseTable
public class Categoria  implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private int imagem;
    @DatabaseField
    private char tipoLancamento;
    @DatabaseField(index = true)
    private String nome;
    @DatabaseField
    private double meta;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Lancamento> lancamentos;

    public Categoria(int imagem, char tipoLancamento, String nome, double meta) {
        this.imagem = imagem;
        this.tipoLancamento = tipoLancamento;
        this.nome = nome;
        this.meta = meta;
    }

    public Categoria() {

    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Categoria) {
            Categoria categoria = (Categoria) obj;
            return this.getId() == categoria.getId();
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getMeta() {
        return meta;
    }

    public void setMeta(double meta) {
        this.meta = meta;
    }

    public double getTotalPago(FormatarPeriodo formatarPeriodo) {
        double total = 0.0;
        Calendar cal = Calendar.getInstance();
        for (Lancamento l : getLancamentos()) {
            cal.setTime(l.getVencimento());
            if (cal.after(formatarPeriodo.getDataInicial()) &&
                    cal.before(formatarPeriodo.getDataFinal())) {
                total += l.getValorPago();
            }
        }
        return total;
    }

    public double getTotalAPagar(FormatarPeriodo formatarPeriodo) {
        double total = 0.0;
        Calendar cal = Calendar.getInstance();
        for (Lancamento l : getLancamentos()) {
            cal.setTime(l.getVencimento());
            if ((cal.after(formatarPeriodo.getDataInicial()) && cal.before(formatarPeriodo.getDataFinal()))
                    && l.getValorPago() < l.getValor()) {
                total += l.getValor() - l.getValorPago();
            }
        }
        return total;
    }

    public double getTotalCategoria(FormatarPeriodo formatarPeriodo) {
        double total = 0.0;
        Calendar cal = Calendar.getInstance();
        for (Lancamento l : getLancamentos()) {
            cal.setTime(l.getVencimento());
            if ((cal.after(formatarPeriodo.getDataInicial()) && cal.before(formatarPeriodo.getDataFinal()))
                    && l.getValorPago() < l.getValor()) {
                total += l.getValor();
            }
        }
        return total;
    }

    public double getDiferencaMeta(FormatarPeriodo formatarPeriodo) {
        return getMeta() - (getTotalPago(formatarPeriodo) + getTotalAPagar(formatarPeriodo));
    }

    public ForeignCollection<Lancamento> getLancamentos() {
        return lancamentos;
    }

    public char getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(char tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}
