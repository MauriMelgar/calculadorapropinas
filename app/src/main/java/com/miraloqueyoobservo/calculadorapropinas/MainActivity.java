package com.miraloqueyoobservo.calculadorapropinas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevoDia, btnVerRegistros, btnGestionarTrabajadores, btnResumenSemanal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNuevoDia = findViewById(R.id.btnNuevoDia);
        btnVerRegistros = findViewById(R.id.btnVerRegistros);
        btnGestionarTrabajadores = findViewById(R.id.btnGestionarTrabajadores);
        btnResumenSemanal = findViewById(R.id.btnResumenSemanal);

        btnNuevoDia.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NuevoDiaActivity.class);
            startActivity(intent);
        });

        btnVerRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrosActivity.class);
            startActivity(intent);
        });

        btnGestionarTrabajadores.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GestionarTrabajadoresActivity.class);
            startActivity(intent);
        });

        btnResumenSemanal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResumenSemanalActivity.class);
            startActivity(intent);
        });
    }
}