package com.miraloqueyoobservo.calculadorapropinas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.adapters.RegistrosAdapter;
import com.miraloqueyoobservo.calculadorapropinas.database.DatabaseHelper;
import com.miraloqueyoobservo.calculadorapropinas.models.DiaPropina;
import com.miraloqueyoobservo.calculadorapropinas.models.TrabajadorDia;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RegistrosActivity extends AppCompatActivity {

    private RecyclerView rvRegistros;
    private RegistrosAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        dbHelper = new DatabaseHelper(this);
        rvRegistros = findViewById(R.id.rvRegistros);
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));

        cargarRegistros();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRegistros();
    }

    private void cargarRegistros() {
        List<DiaPropina> registros = dbHelper.getAllDias();

        if (registros.isEmpty()) {
            Toast.makeText(this, "No hay registros disponibles", Toast.LENGTH_SHORT).show();
        }

        if (adapter == null) {
            adapter = new RegistrosAdapter(registros, this::mostrarDetalleDia);
            rvRegistros.setAdapter(adapter);
        } else {
            adapter.setRegistros(registros);
        }
    }

    private void mostrarDetalleDia(DiaPropina dia) {
        List<TrabajadorDia> trabajadores = dbHelper.getTrabajadoresByDia(dia.getId());

        if (trabajadores.isEmpty()) {
            Toast.makeText(this, "No hay trabajadores en este registro", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular total de horas
        double totalHoras = 0;
        for (TrabajadorDia td : trabajadores) {
            totalHoras += td.getHoras();
        }

        double valorHora = totalHoras > 0 ? dia.getTotalRepartir() / totalHoras : 0;

        // Construir el mensaje
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        StringBuilder sb = new StringBuilder();
        sb.append("📅 ").append(sdf.format(dia.getFecha())).append("\n\n");
        sb.append("💰 Total propinas: $").append(String.format(Locale.getDefault(), "%,.0f", dia.getTotalPropinas())).append("\n");
        sb.append("🏢 Descuento 10%: $").append(String.format(Locale.getDefault(), "%,.0f", dia.getDescuento())).append("\n");
        sb.append("💵 Total a repartir: $").append(String.format(Locale.getDefault(), "%,.0f", dia.getTotalRepartir())).append("\n");
        sb.append("⏱ Total horas: ").append(totalHoras).append(" h\n");
        sb.append("📊 Valor por hora: $").append(String.format(Locale.getDefault(), "%,.0f", valorHora)).append("\n\n");
        sb.append("👥 Desglose por trabajador:\n");
        sb.append("─────────────────────────\n");

        for (TrabajadorDia td : trabajadores) {
            sb.append("• ").append(td.getNombreTrabajador())
                    .append(": ").append(td.getHoras()).append(" h → $")
                    .append(String.format(Locale.getDefault(), "%,.0f", td.getMonto()))
                    .append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalle del Día")
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .setNeutralButton("Eliminar", (dialog, which) -> confirmarEliminar(dia))
                .show();
    }

    private void confirmarEliminar(DiaPropina dia) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Registro")
                .setMessage("¿Estás seguro de eliminar este registro? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteDia(dia.getId());
                    cargarRegistros();
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}