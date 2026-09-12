package com.miraloqueyoobservo.calculadorapropinas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.R;
import com.miraloqueyoobservo.calculadorapropinas.models.ResumenSemanal;

import java.util.List;
import java.util.Locale;

public class ResumenTrabajadorAdapter extends RecyclerView.Adapter<ResumenTrabajadorAdapter.ViewHolder> {
    private List<ResumenSemanal.ResumenTrabajador> trabajadores;

    public ResumenTrabajadorAdapter(List<ResumenSemanal.ResumenTrabajador> trabajadores) {
        this.trabajadores = trabajadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resumen_trabajador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResumenSemanal.ResumenTrabajador t = trabajadores.get(position);

        holder.tvNombreResumen.setText(t.getNombre());
        holder.tvHorasResumen.setText(String.format(Locale.getDefault(),
                "%.1f h en %d día(s)", t.getTotalHoras(), t.getDiasTrabajados()));
        holder.tvMontoResumen.setText(String.format(Locale.getDefault(),
                "$%,.0f", t.getTotalMonto()));
    }

    @Override
    public int getItemCount() {
        return trabajadores != null ? trabajadores.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreResumen, tvHorasResumen, tvMontoResumen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreResumen = itemView.findViewById(R.id.tvNombreResumen);
            tvHorasResumen = itemView.findViewById(R.id.tvHorasResumen);
            tvMontoResumen = itemView.findViewById(R.id.tvMontoResumen);
        }
    }
}