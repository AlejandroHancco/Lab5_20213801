package com.example.saludapp.Helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.Model.RecordatorioReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificacionHelper {

    // Constantes para los canales
    public static final String CHANNEL_PASTILLA = "channel_pastilla";
    public static final String CHANNEL_JARABE = "channel_jarabe";
    public static final String CHANNEL_AMPOLLA = "channel_ampolla";
    public static final String CHANNEL_CAPSULA = "channel_capsula";


    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel pastilla = new NotificationChannel(
                    CHANNEL_PASTILLA,
                    "Pastilla",
                    NotificationManager.IMPORTANCE_HIGH);
            pastilla.enableVibration(true);
            pastilla.setVibrationPattern(new long[]{0, 500, 250, 500});
            pastilla.setLightColor(Color.GREEN);
            pastilla.setDescription("Notificaciones para pastillas");

            NotificationChannel jarabe = new NotificationChannel(
                    CHANNEL_JARABE,
                    "Jarabe",
                    NotificationManager.IMPORTANCE_HIGH);
            jarabe.enableVibration(true);
            jarabe.setVibrationPattern(new long[]{0, 300, 300, 300});
            jarabe.setLightColor(Color.BLUE);
            jarabe.setDescription("Notificaciones para jarabes");

            NotificationChannel ampolla = new NotificationChannel(
                    CHANNEL_AMPOLLA,
                    "Ampolla",
                    NotificationManager.IMPORTANCE_HIGH);
            ampolla.enableVibration(true);
            ampolla.setVibrationPattern(new long[]{0, 700, 200, 700});
            ampolla.setLightColor(Color.MAGENTA);
            ampolla.setDescription("Notificaciones para ampollas");

            NotificationChannel capsula = new NotificationChannel(
                    CHANNEL_CAPSULA,
                    "Cápsula",
                    NotificationManager.IMPORTANCE_HIGH);
            capsula.enableVibration(true);
            capsula.setVibrationPattern(new long[]{0, 400, 400, 400});
            capsula.setLightColor(Color.CYAN);
            capsula.setDescription("Notificaciones para cápsulas");

            // Registrar canales
            manager.createNotificationChannel(pastilla);
            manager.createNotificationChannel(jarabe);
            manager.createNotificationChannel(ampolla);
            manager.createNotificationChannel(capsula);
        }
    }

    /**
     * Programa una notificación repetitiva usando AlarmManager.
     */
    public static void programarNotificacion(Context context, Medicamento medicamento) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RecordatorioReceiver.class);
        intent.putExtra("nombre", medicamento.getNombre());
        intent.putExtra("dosis", medicamento.getDosis());
        intent.putExtra("tipo", medicamento.getTipo());

        PendingIntent pending = PendingIntent.getBroadcast(
                context,
                medicamento.getNombre().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fechaInicio = sdf.parse(medicamento.getFechaHoraInicio());
            if (fechaInicio == null) {
                return;
            }
            long tiempoInicio = fechaInicio.getTime();
            long intervalo = medicamento.getFrecuenciaHoras() * 3600 * 1000L;

            manager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    tiempoInicio,
                    intervalo,
                    pending
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
