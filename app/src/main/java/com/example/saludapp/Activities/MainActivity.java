package com.example.saludapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saludapp.Helpers.NotificacionHelper;
import com.example.saludapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private TextView textSaludo, textMotivacion;
    private ImageView imagenUsuario;
    private SharedPreferences prefs;
    private static final int PICK_IMAGEN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificacionHelper.createNotificationChannels(this);

        prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);

        textSaludo = findViewById(R.id.textSaludo);
        textMotivacion = findViewById(R.id.textMotivacion);
        imagenUsuario = findViewById(R.id.imagenUsuario);

        actualizarUI();

        findViewById(R.id.btnVerMedicamentos).setOnClickListener(view ->
                startActivity(new Intent(this, ListaMedicamentosActivity.class))
        );

        findViewById(R.id.btnConfiguraciones).setOnClickListener(view ->
                startActivity(new Intent(this, ConfiguracionesActivity.class))
        );

        imagenUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGEN);
        });
    }

    private void actualizarUI() {
        String nombre = prefs.getString("nombre_usuario", "Usuario");
        String mensaje = prefs.getString("mensaje_motivacional", "¡Tú puedes!");

        textSaludo.setText("¡Hola, " + nombre + "!");
        textMotivacion.setText(mensaje);

        // Imagen: cargar desde almacenamiento interno si ya fue guardada
        File archivo = new File(getFilesDir(), "foto_perfil.jpg");
        if (archivo.exists()) {
            imagenUsuario.setImageURI(Uri.fromFile(archivo));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE);
        String nombre = prefs.getString("nombre_usuario", "Usuario");
        String mensaje = prefs.getString("mensaje_motivacional", "¡Sigue adelante!");

        TextView tvNombre = findViewById(R.id.textSaludo);
        TextView tvMensaje = findViewById(R.id.textMotivacion);

        tvNombre.setText("Hola, " + nombre);
        tvMensaje.setText(mensaje);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGEN && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imagenUsuario.setImageURI(uri);

            // Guardar imagen en almacenamiento interno
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = openFileOutput("foto_perfil.jpg", MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
