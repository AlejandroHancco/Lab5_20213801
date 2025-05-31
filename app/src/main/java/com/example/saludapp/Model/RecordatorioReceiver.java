package com.example.saludapp.Model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class RecordatorioReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String nombre = intent.getStringExtra("nombre");
        String dosis = intent.getStringExtra("dosis");
        String tipo = intent.getStringExtra("tipo");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Canal de notificación
        String canalId = tipo.toLowerCase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId, tipo, NotificationManager.IMPORTANCE_HIGH);
            canal.enableVibration(true);
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("Recordatorio: " + nombre)
                .setContentText("Tomar: " + dosis)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(nombre.hashCode(), builder.build());
    }
}

