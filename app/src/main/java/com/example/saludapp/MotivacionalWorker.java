package com.example.saludapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;

public class MotivacionalWorker extends Worker {

    public MotivacionalWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String mensaje = prefs.getString("mensaje_motivacional", "¡Tú puedes!");

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String canalId = "motivacional";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId, "Mensajes Motivacionales", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), canalId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("¡Motivación!")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(new Random().nextInt(), builder.build());

        return Result.success();
    }
}

