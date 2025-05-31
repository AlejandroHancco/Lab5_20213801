package com.example.saludapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludapp.Model.Medicamento;
import com.example.saludapp.R;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {
    private List<Medicamento> lista;
    private Context contexto;
    private OnEliminarClickListener eliminarClickListener;

    public interface OnEliminarClickListener {
        void onEliminarClick(int posicion);
    }

    public MedicamentoAdapter(Context contexto, List<Medicamento> lista, OnEliminarClickListener listener) {
        this.contexto = contexto;
        this.lista = lista;
        this.eliminarClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento med = lista.get(position);
        holder.txtNombre.setText(med.getNombre());
        holder.txtTipo.setText(med.getTipo() + " - " + med.getDosis());
        holder.txtFrecuencia.setText("Cada " + med.getFrecuenciaHoras() + " horas");
        holder.txtFechaInicio.setText(med.getFechaHoraInicio());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtTipo, txtFrecuencia, txtFechaInicio;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtFrecuencia = itemView.findViewById(R.id.txtFrecuencia);
            txtFechaInicio = itemView.findViewById(R.id.txtFechaInicio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

            btnEliminar.setOnClickListener(v -> {
                if (eliminarClickListener != null) {
                    eliminarClickListener.onEliminarClick(getAdapterPosition());
                }
            });
        }
    }
}
