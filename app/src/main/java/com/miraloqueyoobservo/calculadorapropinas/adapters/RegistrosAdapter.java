package com.miraloqueyoobservo.calculadorapropinas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.R;
import com.miraloqueyoobservo.calculadorapropinas.models.DiaPropina;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.ViewHolder> {
    private List<DiaPropina> registros;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
    private OnRegistroClickListener listener;

    public interface OnRegistroClickListener {
        void onRegistroClick(DiaPropina dia);
    }

    public RegistrosAdapter(List<DiaPropina> registros, OnRegistroClickListener listener) {
        this.registros = registros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaPropina dia = registros.get(position);

        String fecha = dateFormat.format(dia.getFecha());
        String total = String.format(Locale.getDefault(), "Total recaudado: $%,.0f", dia.getTotalPropinas());
        String descuento = String.format(Locale.getDefault(), "Descuento 10%%: $%,.0f", dia.getDescuento());
        String repartir = String.format(Locale.getDefault(), "Total a repartir: $%,.0f", dia.getTotalRepartir());

        holder.tvFecha.setText(fecha);
        holder.tvTotal.setText(total + " | " + repartir);
        holder.tvDetalle.setText(descuento);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRegistroClick(dia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return registros != null ? registros.size() : 0;
    }

    public void setRegistros(List<DiaPropina> registros) {
        this.registros = registros;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvTotal, tvDetalle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaRegistro);
            tvTotal = itemView.findViewById(R.id.tvTotalRegistro);
            tvDetalle = itemView.findViewById(R.id.tvDetalleRegistro);
        }
    }
}