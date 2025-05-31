package com.example.saludapp.Helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.Model.RecordatorioReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificacionHelper {
    public static void programarNotificacion(Context context, Medicamento medicamento) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RecordatorioReceiver.class);
        intent.putExtra("nombre", medicamento.getNombre());
        intent.putExtra("dosis", medicamento.getDosis());
        intent.putExtra("tipo", medicamento.getTipo());

        PendingIntent pending = PendingIntent.getBroadcast(
                context, medicamento.getNombre().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Calcular tiempo de inicio
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fechaInicio = sdf.parse(medicamento.getFechaHoraInicio());
            long tiempoInicio = fechaInicio.getTime();

            long intervalo = medicamento.getFrecuenciaHoras() * 3600 * 1000L;

            manager.setRepeating(AlarmManager.RTC_WAKEUP, tiempoInicio, intervalo, pending);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
