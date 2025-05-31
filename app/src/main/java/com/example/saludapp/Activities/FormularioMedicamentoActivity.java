package com.example.saludapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludapp.Helpers.NotificacionHelper;
import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class FormularioMedicamentoActivity extends AppCompatActivity {
    private EditText etNombre, etDosis, etFrecuencia, etFecha, etHora;
    private Spinner spTipo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_medicamento_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Mostrar botón atrás
            getSupportActionBar().setTitle("Registrar Medicamento");  // Título de la barra
        }
        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        spTipo = findViewById(R.id.spTipo);
        btnGuardar = findViewById(R.id.btnGuardar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipos_medicamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String dosis = etDosis.getText().toString();
            String tipo = spTipo.getSelectedItem().toString();
            int frecuencia = Integer.parseInt(etFrecuencia.getText().toString());
            String fechaHora = etFecha.getText().toString() + " " + etHora.getText().toString();

            Medicamento nuevo = new Medicamento(nombre, tipo, dosis, frecuencia, fechaHora);

            SharedPreferences prefs = getSharedPreferences("Medicamentos", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Medicamento>>() {}.getType();

            String jsonExistente = prefs.getString("lista", null);
            ArrayList<Medicamento> lista = (jsonExistente != null)
                    ? gson.fromJson(jsonExistente, listType)
                    : new ArrayList<>();

            lista.add(nuevo);
            editor.putString("lista", gson.toJson(lista));
            editor.apply();

            // programar notificación
            NotificacionHelper.programarNotificacion(this, nuevo);

            finish();
        });
    }
}
