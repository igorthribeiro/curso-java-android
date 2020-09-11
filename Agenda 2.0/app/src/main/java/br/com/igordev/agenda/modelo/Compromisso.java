package br.com.igordev.agenda.modelo;

import java.io.Serializable;

public class Compromisso implements Serializable {
    private Long id;
    private String textoCurto;
    private String textoLongo;
    private String data;
    private String hora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoCurto() {
        return textoCurto;
    }

    public void setTextoCurto(String textoCurto) {
        this.textoCurto = textoCurto;
    }

    public String getTextoLongo() {
        return textoLongo;
    }

    public void setTextoLongo(String textoLongo) {
        this.textoLongo = textoLongo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
