package com.example.saludapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_medicamentos_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Muestra el botón atrás
            getSupportActionBar().setTitle("Lista de medicamentos");  // Título
        }
        recyclerView = findViewById(R.id.recyclerMedicamentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarMedicamentos();

        adaptador = new MedicamentoAdapter(this, lista, posicion -> {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setMessage("¿Eliminar este medicamento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        lista.remove(posicion);
                        guardarMedicamentos();
                        adaptador.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        recyclerView.setAdapter(adaptador);

        findViewById(R.id.btnAgregar).setOnClickListener(v -> {
            startActivity(new Intent(this, FormularioMedicamentoActivity.class));
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        cargarMedicamentos();
        adaptador = new MedicamentoAdapter(this, lista, posicion -> {
            lista.remove(posicion);
            guardarMedicamentos();
            adaptador.notifyDataSetChanged();
        });
        recyclerView.setAdapter(adaptador);
    }
}

