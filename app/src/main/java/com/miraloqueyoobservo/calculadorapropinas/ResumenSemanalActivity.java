package com.miraloqueyoobservo.calculadorapropinas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.adapters.ResumenTrabajadorAdapter;
import com.miraloqueyoobservo.calculadorapropinas.adapters.SemanasAdapter;
import com.miraloqueyoobservo.calculadorapropinas.database.DatabaseHelper;
import com.miraloqueyoobservo.calculadorapropinas.models.DiaPropina;
import com.miraloqueyoobservo.calculadorapropinas.models.ResumenSemanal;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResumenSemanalActivity extends AppCompatActivity {

    private RecyclerView rvSemanas;
    private SemanasAdapter adapter;
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_semanal);

        dbHelper = new DatabaseHelper(this);
        rvSemanas = findViewById(R.id.rvSemanas);
        rvSemanas.setLayoutManager(new LinearLayoutManager(this));

        cargarSemanas();
    }

    private void cargarSemanas() {
        List<DiaPropina> dias = dbHelper.getAllDias();

        if (dias.isEmpty()) {
            Toast.makeText(this, "No hay registros para agrupar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Agrupar por semana usando el lunes como clave
        Map<Long, ResumenSemanal> semanasMap = new LinkedHashMap<>();

        for (DiaPropina dia : dias) {
            Date lunes = getLunes(dia.getFecha());
            Date domingo = getDomingo(lunes);
            long key = lunes.getTime();

            ResumenSemanal semana = semanasMap.get(key);
            if (semana == null) {
                semana = new ResumenSemanal();
                semana.setLunes(lunes);
                semana.setDomingo(domingo);
                semana.setTotalPropinas(0);
                semana.setTotalRepartir(0);
                semana.setTotalHoras(0);
                semana.setCantidadDias(0);
                semanasMap.put(key, semana);
            }

            semana.setTotalPropinas(semana.getTotalPropinas() + dia.getTotalPropinas());
            semana.setTotalRepartir(semana.getTotalRepartir() + dia.getTotalRepartir());
            semana.setCantidadDias(semana.getCantidadDias() + 1);
        }

        // Convertir a lista
        List<ResumenSemanal> semanas = new ArrayList<>(semanasMap.values());

        // Ordenar de más reciente a más antigua
        semanas.sort((a, b) -> b.getLunes().compareTo(a.getLunes()));

        adapter = new SemanasAdapter(semanas, this::mostrarDetalleSemana);
        rvSemanas.setAdapter(adapter);
    }

    /**
     * Calcula el lunes de la semana a la que pertenece la fecha dada.
     * Si la fecha es domingo, el lunes de esa semana es el lunes anterior.
     */
    private Date getLunes(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // Si es domingo, retroceder al lunes anterior
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -6);
        } else {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getDomingo(Date lunes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lunes);
        cal.add(Calendar.DAY_OF_YEAR, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private void mostrarDetalleSemana(ResumenSemanal semana) {
        // Obtener trabajadores de esa semana
        List<Trabajador> trabajadores = dbHelper.getTrabajadoresEnRango(
                semana.getLunes().getTime(), semana.getDomingo().getTime());

        if (trabajadores.isEmpty()) {
            Toast.makeText(this, "No hay trabajadores en esta semana", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular el resumen por trabajador
        List<ResumenSemanal.ResumenTrabajador> resumenList = new ArrayList<>();
        double totalHoras = 0;
        double granTotalMonto = 0;

        for (Trabajador t : trabajadores) {
            double[] datos = dbHelper.getTotalTrabajadorEnRango(
                    t.getId(),
                    semana.getLunes().getTime(),
                    semana.getDomingo().getTime());

            ResumenSemanal.ResumenTrabajador rt = new ResumenSemanal.ResumenTrabajador();
            rt.setIdTrabajador(t.getId());
            rt.setNombre(t.getNombre());
            rt.setTotalHoras(datos[0]);
            rt.setTotalMonto(datos[1]);
            rt.setDiasTrabajados((int) datos[2]);
            resumenList.add(rt);

            totalHoras += datos[0];
            granTotalMonto += datos[1];
        }

        // Ordenar de mayor a menor monto
        resumenList.sort((a, b) -> Double.compare(b.getTotalMonto(), a.getTotalMonto()));

        // Construir el diálogo con título e info general
        String titulo = "Semana del " + dateFormat.format(semana.getLunes()) +
                " al " + dateFormat.format(semana.getDomingo());

        String infoGeneral = String.format(Locale.getDefault(),
                "Total propinas: $%,.0f\nA repartir: $%,.0f\nTotal horas: %.1f h\nTotal repartido: $%,.0f",
                semana.getTotalPropinas(), semana.getTotalRepartir(),
                totalHoras, granTotalMonto);

        // Inflar la vista del diálogo
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_resumen_semanal, null);
        android.widget.TextView tvTitulo = dialogView.findViewById(R.id.tvTituloDialogo);
        android.widget.TextView tvInfo = dialogView.findViewById(R.id.tvInfoDialogo);
        RecyclerView rv = dialogView.findViewById(R.id.rvResumenTrabajadores);

        tvTitulo.setText(titulo);
        tvInfo.setText(infoGeneral);

        rv.setLayoutManager(new LinearLayoutManager(this));
        ResumenTrabajadorAdapter adapterDialog = new ResumenTrabajadorAdapter(resumenList);
        rv.setAdapter(adapterDialog);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }
}