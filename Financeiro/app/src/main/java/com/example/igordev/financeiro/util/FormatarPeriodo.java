package com.example.igordev.financeiro.util;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by igordev on 09/07/17.
 */

public class FormatarPeriodo implements Serializable {

    private Calendar dataInicial = Calendar.getInstance();
    private Calendar dataFinal = Calendar.getInstance();

    public FormatarPeriodo(int dia, int mes, int ano) {
        this.formatar(dia, mes, ano);
    }

    public FormatarPeriodo() {
        this.dataInicial.set(Calendar.HOUR_OF_DAY, 0);
        this.dataInicial.set(Calendar.MINUTE, 0);
        this.dataInicial.set(Calendar.SECOND, 0);
        this.dataInicial.set(Calendar.MILLISECOND, 0);

        this.dataFinal.set(Calendar.HOUR_OF_DAY, 23);
        this.dataFinal.set(Calendar.MINUTE, 59);
        this.dataFinal.set(Calendar.SECOND, 59);
        this.dataFinal.set(Calendar.MILLISECOND, 999);

    }

    public void formatar(int dia, int mes, int ano) {
        Calendar calendar = Calendar.getInstance();

        dataInicial.set(Calendar.DAY_OF_MONTH, dia);
        dataInicial.set(Calendar.MONTH, mes);
        dataInicial.set(Calendar.YEAR, ano);
        dataFinal.setTime(dataInicial.getTime());

        if (calendar.get(Calendar.DAY_OF_MONTH) >= dia) {
            dataFinal.add(Calendar.MONTH, 1);
        } else {
            dataInicial.add(Calendar.MONTH, -1);
        }
    }

    public Calendar getDataInicial() {
        return dataInicial;
    }

    public Calendar getDataFinal() {
        return dataFinal;
    }
}
