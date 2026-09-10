package com.miraloqueyoobservo.calculadorapropinas.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.R;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrabajadoresDiaAdapter extends RecyclerView.Adapter<TrabajadoresDiaAdapter.ViewHolder> {
    private List<Trabajador> trabajadores = new ArrayList<>();
    private Map<Integer, Double> horasMap = new HashMap<>();
    private OnTrabajadorRemoveListener removeListener;

    public interface OnTrabajadorRemoveListener {
        void onRemove(int position);
    }

    public TrabajadoresDiaAdapter(OnTrabajadorRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trabajador_dia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trabajador trabajador = trabajadores.get(position);
        holder.tvNombre.setText(trabajador.getNombre());

        // Limpiar listener anterior
        if (holder.horasWatcher != null) {
            holder.etHoras.removeTextChangedListener(holder.horasWatcher);
        }

        // Dejar el campo VACÍO (no "0.0")
        Double horas = horasMap.get(position);
        if (horas == null || horas == 0.0) {
            holder.etHoras.setText("");
        } else {
            // Mostrar sin decimales innecesarios
            if (horas == horas.intValue()) {
                holder.etHoras.setText(String.valueOf(horas.intValue()));
            } else {
                holder.etHoras.setText(String.valueOf(horas));
            }
        }

        // Crear y asignar un nuevo TextWatcher para esta posición
        final int pos = position;
        holder.horasWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().trim();
                if (texto.isEmpty()) {
                    horasMap.put(pos, 0.0);
                } else {
                    try {
                        double valor = Double.parseDouble(texto);
                        horasMap.put(pos, valor);
                    } catch (NumberFormatException e) {
                        horasMap.put(pos, 0.0);
                    }
                }
            }
        };
        holder.etHoras.addTextChangedListener(holder.horasWatcher);

        holder.btnEliminar.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trabajadores.size();
    }

    public void setTrabajadores(List<Trabajador> trabajadores) {
        this.trabajadores = trabajadores;
        horasMap.clear();
        notifyDataSetChanged();
    }

    public void addTrabajador(Trabajador trabajador) {
        trabajadores.add(trabajador);
        horasMap.put(trabajadores.size() - 1, 0.0);
        notifyItemInserted(trabajadores.size() - 1);
    }

    public void removeTrabajador(int position) {
        trabajadores.remove(position);
        Map<Integer, Double> newMap = new HashMap<>();
        for (int i = 0; i < trabajadores.size(); i++) {
            Double horas = horasMap.get(i + 1);
            if (horas != null) {
                newMap.put(i, horas);
            }
        }
        horasMap = newMap;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, trabajadores.size());
    }

    public List<Trabajador> getTrabajadores() {
        return trabajadores;
    }

    public double getHoras(int position) {
        Double horas = horasMap.get(position);
        return horas != null ? horas : 0.0;
    }

    public double getTotalHoras() {
        double total = 0;
        for (int i = 0; i < trabajadores.size(); i++) {
            total += getHoras(i);
        }
        return total;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        EditText etHoras;
        ImageButton btnEliminar;
        TextWatcher horasWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTrabajador);
            etHoras = itemView.findViewById(R.id.etHorasTrabajador);
            btnEliminar = itemView.findViewById(R.id.btnEliminarTrabajador);
        }
    }
}