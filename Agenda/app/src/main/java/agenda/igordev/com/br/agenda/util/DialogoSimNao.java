package agenda.igordev.com.br.agenda.util;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import agenda.igordev.com.br.agenda.action.AcaoNao;
import agenda.igordev.com.br.agenda.action.AcaoSim;


public class DialogoSimNao {

    public static void cria(Context context, int titulo, int mensagem, final AcaoSim acaoSim, final AcaoNao acaoNao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (acaoSim != null) acaoSim.executar();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (acaoNao != null) acaoNao.executar();
                    }
                }).setCancelable(false).show();
    }

}

