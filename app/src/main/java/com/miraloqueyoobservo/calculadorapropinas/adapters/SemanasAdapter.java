package com.miraloqueyoobservo.calculadorapropinas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.R;
import com.miraloqueyoobservo.calculadorapropinas.models.ResumenSemanal;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SemanasAdapter extends RecyclerView.Adapter<SemanasAdapter.ViewHolder> {
    private List<ResumenSemanal> semanas;
    private OnSemanaClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));

    public interface OnSemanaClickListener {
        void onSemanaClick(ResumenSemanal semana);
    }

    public SemanasAdapter(List<ResumenSemanal> semanas, OnSemanaClickListener listener) {
        this.semanas = semanas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semana, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResumenSemanal semana = semanas.get(position);

        String rango = "Semana: " + dateFormat.format(semana.getLunes()) +
                "  →  " + dateFormat.format(semana.getDomingo());

        String info = String.format(Locale.getDefault(),
                "%d día(s) trabajado(s)  •  Total: $%,.0f  •  A repartir: $%,.0f",
                semana.getCantidadDias(),
                semana.getTotalPropinas(),
                semana.getTotalRepartir());

        holder.tvRangoSemana.setText(rango);
        holder.tvInfoSemana.setText(info);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSemanaClick(semana);
            }
        });
    }

    @Override
    public int getItemCount() {
        return semanas != null ? semanas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRangoSemana, tvInfoSemana;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRangoSemana = itemView.findViewById(R.id.tvRangoSemana);
            tvInfoSemana = itemView.findViewById(R.id.tvInfoSemana);
        }
    }
}