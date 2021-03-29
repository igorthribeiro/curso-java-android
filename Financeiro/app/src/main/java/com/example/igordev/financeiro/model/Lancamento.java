package com.example.igordev.financeiro.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by igordev on 05/07/17.
 */

@DatabaseTable
public class Lancamento implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "categoria_id",
            columnDefinition = "integer references categoria(id) on delete cascade")
    private Categoria categoria;
    @DatabaseField(index = true)
    private String descricao;
    @DatabaseField(index = true)
    private Date vencimento;
    @DatabaseField
    private Date pagamento;
    @DatabaseField(index = true)
    private double valor;
    @DatabaseField
    private double valorPago;
    @DatabaseField
    private boolean notificacao;
    @DatabaseField
    private String imagem;

    public Lancamento() {

    }

    public Lancamento(Categoria categoria, String descricao, Date vencimento, Date pagamento, double valor, double valorPago,
                      boolean notificacao, String imagem) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.vencimento = vencimento;
        this.pagamento = pagamento;
        this.valor = valor;
        this.valorPago = valorPago;
        this.notificacao = notificacao;
        this.imagem = imagem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Date getPagamento() {
        return pagamento;
    }

    public void setPagamento(Date pagamento) {
        this.pagamento = pagamento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public boolean isNotificacao() {
        return notificacao;
    }

    public void setNotificacao(boolean notificacao) {
        this.notificacao = notificacao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
