package com.example.igordev.financeiro.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.igordev.financeiro.ListaLancamentoActivity;
import com.example.igordev.financeiro.R;
import com.example.igordev.financeiro.dao.EnumTask;
import com.example.igordev.financeiro.dao.FinanceiroDao;
import com.example.igordev.financeiro.model.Lancamento;
import com.example.igordev.financeiro.util.Configuracao;
import com.example.igordev.financeiro.util.FormatarPeriodo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.example.igordev.financeiro.dao.EnumTask.FIND_ID;
import static com.example.igordev.financeiro.dao.EnumTask.REFRESH;

/**
 * Created by igordev on 14/07/17.
 */

public class NotificacaoReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "notification_id";

    Dao<Lancamento, Integer> daoLancamento;
    private int notificationId;

    public static void ativarNotificacao(Context context, Lancamento lancamento) {

        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(lancamento.getVencimento());

        Intent notificationIntent = new Intent(context, NotificacaoReceiver.class);
        notificationIntent.putExtra(NotificacaoReceiver.NOTIFICATION_ID, UUID.randomUUID().hashCode());
        notificationIntent.putExtra("lancamentoId", lancamento.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, lancamento.getId(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long futureInMillis = vencimento.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    public static void desativarNotificacao(Context context, Lancamento lancamento) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService((Context.ALARM_SERVICE));
        Intent alarmIntent = new Intent(context, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, lancamento.getId(),
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Configuracao.carregar(context);

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            new DAOTask(context, REFRESH).execute();
        } else if (Configuracao.NOTIFICACAO_ATIVA) {
            notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
            int lancamentoId = intent.getIntExtra("lancamentoId", -1);
            new DAOTask(context, FIND_ID, lancamentoId).execute();
        }
    }

    @SuppressWarnings("deprecation")
    private void notificar(Context context, Lancamento lancamento) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Por default, as notificações na versão "O" aparecerão no ícone do aplicativo
        //Canal de notificação Android 8.0 api 26

        String channel_id = "fina_plus_channel";
        String channel_name = context.getString(R.string.app_name) + "_CHANNEL";

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(lancamento.getCategoria().getImagem())
                .setContentTitle(context.getResources().getString(R.string.notificacao_title))
                .setContentText(String.format(context.getResources().getString(
                        R.string.notificacao_text), lancamento.getDescricao()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel_id);
        } else {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        Intent resultIntent = new Intent(context, ListaLancamentoActivity.class);
        resultIntent.putExtra("categoria", lancamento.getCategoria());
        resultIntent.putExtra("periodo", new FormatarPeriodo());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ListaLancamentoActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(UUID.randomUUID().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);
    }

    private class DAOTask extends AsyncTask<String, Integer, List<Lancamento>> {
        private EnumTask enumTask;
        private Context context;
        private Calendar calendar;
        private int idToFind;

        public DAOTask(Context context, EnumTask enumTask) {
            this.calendar = Calendar.getInstance();
            this.calendar.add(Calendar.MINUTE, 5);
            this.calendar.set(Calendar.SECOND, 0);
            this.calendar.set(Calendar.MILLISECOND, 0);

            this.context = context;
            this.enumTask = enumTask;
        }

        public DAOTask(Context context, EnumTask enumTask, int idToFind) {
            this.context = context;
            this.enumTask = enumTask;
            this.idToFind = idToFind;
        }

        @Override
        protected List<Lancamento> doInBackground(String... strings) {
            try (FinanceiroDao<Lancamento> dao = new FinanceiroDao<>(context, Lancamento.class)) {
                daoLancamento = dao.getDao();
                switch (enumTask) {
                    case REFRESH:
                        QueryBuilder<Lancamento, Integer> queryBuilder =
                                daoLancamento.queryBuilder();
                        Where<Lancamento, Integer> where = queryBuilder.where();
                        where.ge("vencimento", calendar.getTime());
                        where.and();
                        where.lt("valorPago", new ColumnArg("valor"));
                        where.and();
                        where.eq("notificacao", true);
                        return daoLancamento.query(queryBuilder.prepare());
                    case FIND_ID:
                        List<Lancamento> lancamentos = new ArrayList<>(1);
                        lancamentos.add(daoLancamento.queryForId(idToFind));
                        return lancamentos;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Lancamento> lancamentos) {
            switch (enumTask) {
                case REFRESH:
                    for (Lancamento lancamento : lancamentos) {
                        ativarNotificacao(context, lancamento);
                    }
                    break;
                case FIND_ID:
                    if (lancamentos.get(0) != null) notificar(context, lancamentos.get(0));
                    break;
            }
        }
    }
}
