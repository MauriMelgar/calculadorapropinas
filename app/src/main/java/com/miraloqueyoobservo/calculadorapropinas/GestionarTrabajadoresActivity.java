package com.miraloqueyoobservo.calculadorapropinas;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraloqueyoobservo.calculadorapropinas.adapters.TrabajadoresAdapter;
import com.miraloqueyoobservo.calculadorapropinas.database.DatabaseHelper;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;

import java.util.List;

public class GestionarTrabajadoresActivity extends AppCompatActivity {

    private EditText etNuevoTrabajador;
    private Button btnAgregarTrabajador;
    private RecyclerView rvTrabajadores;
    private TrabajadoresAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Trabajador> trabajadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_trabajadores);

        dbHelper = new DatabaseHelper(this);

        etNuevoTrabajador = findViewById(R.id.etNuevoTrabajador);
        btnAgregarTrabajador = findViewById(R.id.btnAgregarTrabajadorLista);
        rvTrabajadores = findViewById(R.id.rvTrabajadores);

        rvTrabajadores.setLayoutManager(new LinearLayoutManager(this));

        btnAgregarTrabajador.setOnClickListener(v -> agregarTrabajador());

        cargarTrabajadores();
    }

    private void cargarTrabajadores() {
        trabajadores = dbHelper.getAllTrabajadores();

        TrabajadoresAdapter.OnTrabajadorClickListener listener = new TrabajadoresAdapter.OnTrabajadorClickListener() {
            @Override
            public void onTrabajadorClick(Trabajador trabajador) {
                Toast.makeText(GestionarTrabajadoresActivity.this,
                        trabajador.getNombre(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTrabajadorLongClick(Trabajador trabajador) {
                new AlertDialog.Builder(GestionarTrabajadoresActivity.this)
                        .setTitle("Eliminar Trabajador")
                        .setMessage("¿Estás seguro de eliminar a " + trabajador.getNombre() + "?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            dbHelper.deleteTrabajador(trabajador.getId());
                            cargarTrabajadores();
                            Toast.makeText(GestionarTrabajadoresActivity.this,
                                    "Trabajador eliminado", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        };

        adapter = new TrabajadoresAdapter(trabajadores, listener);
        rvTrabajadores.setAdapter(adapter);
    }

    private void agregarTrabajador() {
        String nombre = etNuevoTrabajador.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Trabajador t : trabajadores) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                Toast.makeText(this, "El trabajador ya existe", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Trabajador nuevo = new Trabajador(nombre);
        long id = dbHelper.addTrabajador(nuevo);

        if (id != -1) {
            Toast.makeText(this, "Trabajador agregado", Toast.LENGTH_SHORT).show();
            etNuevoTrabajador.setText("");
            cargarTrabajadores();
        } else {
            Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
        }
    }
}