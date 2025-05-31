package com.example.saludapp.Model;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.saludapp.Helpers.NotificacionHelper;
import com.example.saludapp.R;

public class RecordatorioReceiver extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        String nombre = intent.getStringExtra("nombre");
        String dosis = intent.getStringExtra("dosis");
        String tipo = intent.getStringExtra("tipo");

        if (nombre == null || dosis == null || tipo == null) {
            // Si falta algún dato no hacemos nada
            return;
        }

        String channelId;

        switch (tipo.toLowerCase()) {
            case "pastilla":
                channelId = NotificacionHelper.CHANNEL_PASTILLA;
                break;
            case "jarabe":
                channelId = NotificacionHelper.CHANNEL_JARABE;
                break;
            case "ampolla":
                channelId = NotificacionHelper.CHANNEL_AMPOLLA;
                break;
            case "cápsula":
            case "capsula": // por si usan sin tilde
                channelId = NotificacionHelper.CHANNEL_CAPSULA;
                break;
            default:
                channelId = NotificacionHelper.CHANNEL_PASTILLA; // canal por defecto
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_medicamento)  // Asegúrate de tener este ícono en drawable
                .setContentTitle("Hora de tomar " + nombre)
                .setContentText("Dosis: " + dosis)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(nombre.hashCode(), builder.build());
    }
}
