package com.miraloqueyoobservo.calculadorapropinas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.R;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;

import java.util.List;

public class TrabajadoresAdapter extends RecyclerView.Adapter<TrabajadoresAdapter.ViewHolder> {
    private List<Trabajador> trabajadores;
    private OnTrabajadorClickListener listener;

    public interface OnTrabajadorClickListener {
        void onTrabajadorClick(Trabajador trabajador);
        void onTrabajadorLongClick(Trabajador trabajador);
    }

    public TrabajadoresAdapter(List<Trabajador> trabajadores, OnTrabajadorClickListener listener) {
        this.trabajadores = trabajadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trabajador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trabajador trabajador = trabajadores.get(position);
        holder.tvNombre.setText(trabajador.getNombre());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrabajadorClick(trabajador);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTrabajadorLongClick(trabajador);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return trabajadores != null ? trabajadores.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreItemTrabajador);
        }
    }
}