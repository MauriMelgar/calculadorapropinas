package com.miraloqueyoobservo.calculadorapropinas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.adapters.TrabajadoresDiaAdapter;
import com.miraloqueyoobservo.calculadorapropinas.database.DatabaseHelper;
import com.miraloqueyoobservo.calculadorapropinas.models.DiaPropina;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;
import com.miraloqueyoobservo.calculadorapropinas.models.TrabajadorDia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NuevoDiaActivity extends AppCompatActivity {

    private TextView tvFecha, tvDescuento, tvTotalRepartir;
    private EditText etTotalPropinas;
    private Button btnSeleccionarFecha, btnAgregarTrabajador, btnCalcular;
    private RecyclerView rvTrabajadoresDia;
    private TrabajadoresDiaAdapter adapter;
    private DatabaseHelper dbHelper;
    private Date fechaSeleccionada;
    private List<Trabajador> trabajadoresDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_dia);

        dbHelper = new DatabaseHelper(this);
        trabajadoresDisponibles = dbHelper.getAllTrabajadores();

        // Inicializar vistas
        tvFecha = findViewById(R.id.tvFecha);
        tvDescuento = findViewById(R.id.tvDescuento);
        tvTotalRepartir = findViewById(R.id.tvTotalRepartir);
        etTotalPropinas = findViewById(R.id.etTotalPropinas);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnAgregarTrabajador = findViewById(R.id.btnAgregarTrabajador);
        btnCalcular = findViewById(R.id.btnCalcular);
        rvTrabajadoresDia = findViewById(R.id.rvTrabajadoresDia);

        // Configurar RecyclerView
        rvTrabajadoresDia.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrabajadoresDiaAdapter(position -> {
            // Eliminar trabajador
            adapter.removeTrabajador(position);
            calcular();
        });
        rvTrabajadoresDia.setAdapter(adapter);

        // Configurar fecha por defecto (hoy)
        fechaSeleccionada = new Date();
        actualizarFecha();

        // Listeners
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());

        btnAgregarTrabajador.setOnClickListener(v -> mostrarDialogoTrabajadores());

        etTotalPropinas.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcular();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnCalcular.setOnClickListener(v -> guardarDia());
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaSeleccionada);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    fechaSeleccionada = c.getTime();
                    actualizarFecha();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void actualizarFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        tvFecha.setText(sdf.format(fechaSeleccionada));
    }

    private void mostrarDialogoTrabajadores() {
        // Crear lista de trabajadores disponibles (los que no están seleccionados)
        List<Trabajador> disponibles = new ArrayList<>();
        List<Trabajador> seleccionados = adapter.getTrabajadores();

        for (Trabajador t : trabajadoresDisponibles) {
            boolean yaSeleccionado = false;
            for (Trabajador seleccionado : seleccionados) {
                if (seleccionado.getId() == t.getId()) {
                    yaSeleccionado = true;
                    break;
                }
            }
            if (!yaSeleccionado) {
                disponibles.add(t);
            }
        }

        if (disponibles.isEmpty()) {
            Toast.makeText(this, "Todos los trabajadores ya están agregados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar diálogo con lista de trabajadores disponibles
        String[] nombres = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) {
            nombres[i] = disponibles.get(i).getNombre();
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar Trabajador")
                .setItems(nombres, (dialog, which) -> {
                    Trabajador seleccionado = disponibles.get(which);
                    adapter.addTrabajador(seleccionado);
                    calcular();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void calcular() {
        String totalStr = etTotalPropinas.getText().toString();
        if (TextUtils.isEmpty(totalStr)) {
            tvDescuento.setText("0");
            tvTotalRepartir.setText("0");
            return;
        }

        try {
            double total = Double.parseDouble(totalStr);
            double descuento = total * 0.10;
            double totalRepartir = total * 0.90;

            tvDescuento.setText(String.format(Locale.getDefault(), "$%,.0f", descuento));
            tvTotalRepartir.setText(String.format(Locale.getDefault(), "$%,.0f", totalRepartir));

        } catch (NumberFormatException e) {
            tvDescuento.setText("0");
            tvTotalRepartir.setText("0");
        }
    }

    private void guardarDia() {
        // Validar fecha
        if (fechaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar total
        String totalStr = etTotalPropinas.getText().toString();
        if (TextUtils.isEmpty(totalStr)) {
            Toast.makeText(this, "Ingrese el total de propinas", Toast.LENGTH_SHORT).show();
            return;
        }

        double total;
        try {
            total = Double.parseDouble(totalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Total inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (total <= 0) {
            Toast.makeText(this, "El total debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar trabajadores
        List<Trabajador> trabajadores = adapter.getTrabajadores();
        if (trabajadores.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un trabajador", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar horas de cada trabajador
        double totalHoras = 0;
        for (int i = 0; i < trabajadores.size(); i++) {
            double horas = adapter.getHoras(i);
            if (horas <= 0) {
                Toast.makeText(this, "Ingrese horas válidas para " + trabajadores.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                return;
            }
            totalHoras += horas;
        }

        // Calcular valor por hora
        double totalRepartir = total * 0.90;
        double valorHora = totalRepartir / totalHoras;

        // Guardar en base de datos
        DiaPropina dia = new DiaPropina(fechaSeleccionada, total);
        long idDia = dbHelper.addDia(dia);

        if (idDia == -1) {
            Toast.makeText(this, "Error al guardar el día", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar trabajadores del día
        for (int i = 0; i < trabajadores.size(); i++) {
            double horas = adapter.getHoras(i);
            double monto = horas * valorHora;

            TrabajadorDia td = new TrabajadorDia(
                    (int) idDia,
                    trabajadores.get(i).getId(),
                    trabajadores.get(i).getNombre(),
                    horas
            );
            td.setMonto(monto);
            dbHelper.addTrabajadorDia(td);
        }

        // Mostrar resumen
        StringBuilder resumen = new StringBuilder();
        resumen.append("Total propinas: $").append(String.format("%.0f", total)).append("\n");
        resumen.append("Descuento 10%: $").append(String.format("%.0f", total * 0.10)).append("\n");
        resumen.append("Total a repartir: $").append(String.format("%.0f", totalRepartir)).append("\n");
        resumen.append("Valor por hora: $").append(String.format("%.0f", valorHora)).append("\n\n");

        for (int i = 0; i < trabajadores.size(); i++) {
            double horas = adapter.getHoras(i);
            double monto = horas * valorHora;
            resumen.append(trabajadores.get(i).getNombre())
                    .append(": ").append(horas).append("h → $")
                    .append(String.format("%.0f", monto)).append("\n");
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Resumen del Día")
                .setMessage(resumen.toString())
                .setPositiveButton("Aceptar", (dialog, which) -> finish())
                .show();
    }
}