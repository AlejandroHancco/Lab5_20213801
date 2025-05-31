package com.example.saludapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.saludapp.MotivacionalWorker;
import com.example.saludapp.R;

import java.util.concurrent.TimeUnit;

public class ConfiguracionesActivity extends AppCompatActivity {
    private EditText etNombre, etMensaje, etIntervalo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);

        // Referencias UI
        etNombre = findViewById(R.id.etNombreUsuario);
        etMensaje = findViewById(R.id.etMensajeMotivacional);
        etIntervalo = findViewById(R.id.etIntervaloMotivacion);
        btnGuardar = findViewById(R.id.btnGuardarConfiguracion);

        // Cargar datos previamente guardados
        SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre_usuario", ""));
        etMensaje.setText(prefs.getString("mensaje_motivacional", ""));
        etIntervalo.setText(prefs.getString("intervalo_motivacional", "6"));

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String mensaje = etMensaje.getText().toString().trim();
            String intervaloStr = etIntervalo.getText().toString().trim();

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(mensaje) || TextUtils.isEmpty(intervaloStr)) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int intervaloHoras;
            try {
                intervaloHoras = Integer.parseInt(intervaloStr);
                if (intervaloHoras < 1) throw new NumberFormatException(); // mínimo 1 hora
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Intervalo inválido. Ingresa un número mayor a 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar preferencias
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombre_usuario", nombre);
            editor.putString("mensaje_motivacional", mensaje);
            editor.putString("intervalo_motivacional", intervaloStr);
            editor.apply(); // O usa commit() si necesitas guardar antes de continuar

            // Programar notificaciones motivacionales
            programarMotivacion(intervaloHoras);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void programarMotivacion(int horas) {
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.cancelAllWorkByTag("motivacional");

        PeriodicWorkRequest trabajo = new PeriodicWorkRequest.Builder(
                MotivacionalWorker.class,
                horas, TimeUnit.HOURS)
                .addTag("motivacional")
                .build();

        workManager.enqueue(trabajo);
    }
}
