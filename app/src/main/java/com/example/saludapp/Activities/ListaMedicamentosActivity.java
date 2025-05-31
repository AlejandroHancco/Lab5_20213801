package com.example.saludapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludapp.Adapter.MedicamentoAdapter;
import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListaMedicamentosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentoAdapter adaptador;
    private ArrayList<Medicamento> lista;
    private TextView tvMensajeVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_medicamentos_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Botón atrás
            getSupportActionBar().setTitle("Lista de medicamentos");
        }

        recyclerView = findViewById(R.id.recyclerMedicamentos);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fabAgregar).setOnClickListener(v -> {
            startActivity(new Intent(this, FormularioMedicamentoActivity.class));
        });

        cargarMedicamentos();
        actualizarVista();
    }

    private void cargarMedicamentos() {
        SharedPreferences prefs = getSharedPreferences("Medicamentos", MODE_PRIVATE);
        String json = prefs.getString("lista", null);
        Gson gson = new Gson();
        Type tipoLista = new TypeToken<ArrayList<Medicamento>>() {}.getType();
        lista = (json != null) ? gson.fromJson(json, tipoLista) : new ArrayList<>();
    }

    private void guardarMedicamentos() {
        SharedPreferences prefs = getSharedPreferences("Medicamentos", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString("lista", gson.toJson(lista));
        editor.apply();
    }

    private void actualizarVista() {
        if (lista.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvMensajeVacio.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvMensajeVacio.setVisibility(View.GONE);

            adaptador = new MedicamentoAdapter(this, lista, posicion -> {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar")
                        .setMessage("¿Eliminar este medicamento?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            lista.remove(posicion);
                            guardarMedicamentos();
                            actualizarVista();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
            recyclerView.setAdapter(adaptador);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMedicamentos();
        actualizarVista();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Cierra esta actividad y vuelve a la anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
