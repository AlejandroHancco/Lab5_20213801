package com.example.saludapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludapp.Helpers.NotificacionHelper;
import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Medicamento");
        }

        // Referencias
        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        spTipo = findViewById(R.id.spTipo);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipos_medicamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        // Selector de fecha
        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                etFecha.setText(fecha);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Selector de hora
        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etHora.setText(hora);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Guardar medicamento
        btnGuardar.setOnClickListener(v -> guardarMedicamento());
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String dosis = etDosis.getText().toString().trim();
        String tipo = spTipo.getSelectedItem().toString();
        String frecuenciaStr = etFrecuencia.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || dosis.isEmpty() || frecuenciaStr.isEmpty()
                || fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int frecuencia;
        try {
            frecuencia = Integer.parseInt(frecuenciaStr);
            if (frecuencia <= 0) {
                Toast.makeText(this, "La frecuencia debe ser mayor que cero.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Frecuencia inválida. Usa solo números.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaHora = fecha + " " + hora;
        Medicamento nuevo = new Medicamento(nombre, tipo, dosis, frecuencia, fechaHora);

        // Guardar en SharedPreferences
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

        NotificacionHelper.programarNotificacion(this, nuevo);
        Log.d("MedicamentosDebug", "Guardados: " + gson.toJson(lista));

        Toast.makeText(this, "Medicamento guardado correctamente.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
