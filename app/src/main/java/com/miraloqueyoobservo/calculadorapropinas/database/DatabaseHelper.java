package com.miraloqueyoobservo.calculadorapropinas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miraloqueyoobservo.calculadorapropinas.models.DiaPropina;
import com.miraloqueyoobservo.calculadorapropinas.models.Trabajador;
import com.miraloqueyoobservo.calculadorapropinas.models.TrabajadorDia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "propinas.db";
    private static final int DATABASE_VERSION = 1;

    // Tablas
    private static final String TABLE_TRABAJADORES = "trabajadores";
    private static final String TABLE_DIAS = "dias_propina";
    private static final String TABLE_TRABAJADORES_DIA = "trabajadores_dia";

    // Columnas comunes
    private static final String COLUMN_ID = "id";

    // Tabla trabajadores
    private static final String COLUMN_NOMBRE = "nombre";

    // Tabla dias_propina
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_TOTAL_PROPINAS = "total_propinas";
    private static final String COLUMN_DESCUENTO = "descuento";
    private static final String COLUMN_TOTAL_REPARTIR = "total_repartir";

    // Tabla trabajadores_dia
    private static final String COLUMN_ID_DIA = "id_dia";
    private static final String COLUMN_ID_TRABAJADOR = "id_trabajador";
    private static final String COLUMN_HORAS = "horas";
    private static final String COLUMN_MONTO = "monto";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla trabajadores
        String createTrabajadores = "CREATE TABLE " + TABLE_TRABAJADORES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOMBRE + " TEXT NOT NULL UNIQUE)";
        db.execSQL(createTrabajadores);

        // Crear tabla dias_propina
        String createDias = "CREATE TABLE " + TABLE_DIAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FECHA + " INTEGER NOT NULL, "
                + COLUMN_TOTAL_PROPINAS + " REAL NOT NULL, "
                + COLUMN_DESCUENTO + " REAL NOT NULL, "
                + COLUMN_TOTAL_REPARTIR + " REAL NOT NULL)";
        db.execSQL(createDias);

        // Crear tabla trabajadores_dia
        String createTrabajadoresDia = "CREATE TABLE " + TABLE_TRABAJADORES_DIA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID_DIA + " INTEGER NOT NULL, "
                + COLUMN_ID_TRABAJADOR + " INTEGER NOT NULL, "
                + COLUMN_HORAS + " REAL NOT NULL, "
                + COLUMN_MONTO + " REAL NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_ID_DIA + ") REFERENCES " + TABLE_DIAS + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ID_TRABAJADOR + ") REFERENCES " + TABLE_TRABAJADORES + "(" + COLUMN_ID + "))";
        db.execSQL(createTrabajadoresDia);

        // Insertar trabajadores por defecto
        insertDefaultTrabajadores(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRABAJADORES_DIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRABAJADORES);
        onCreate(db);
    }

    private void insertDefaultTrabajadores(SQLiteDatabase db) {
        String[] trabajadores = {"Luz", "Edu", "Victor", "Aylen", "Rubén", "Clau", "Felipe", "Dani"};
        for (String nombre : trabajadores) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOMBRE, nombre);
            db.insert(TABLE_TRABAJADORES, null, values);
        }
    }

    // CRUD para Trabajadores
    public long addTrabajador(Trabajador trabajador) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, trabajador.getNombre());
        return db.insert(TABLE_TRABAJADORES, null, values);
    }

    public List<Trabajador> getAllTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRABAJADORES, null, null, null, null, null, COLUMN_NOMBRE);

        if (cursor.moveToFirst()) {
            do {
                Trabajador trabajador = new Trabajador();
                trabajador.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                trabajador.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                trabajadores.add(trabajador);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trabajadores;
    }

    public void deleteTrabajador(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRABAJADORES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // CRUD para Dias
    public long addDia(DiaPropina dia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FECHA, dia.getFecha().getTime());
        values.put(COLUMN_TOTAL_PROPINAS, dia.getTotalPropinas());
        values.put(COLUMN_DESCUENTO, dia.getDescuento());
        values.put(COLUMN_TOTAL_REPARTIR, dia.getTotalRepartir());
        return db.insert(TABLE_DIAS, null, values);
    }

    public List<DiaPropina> getAllDias() {
        List<DiaPropina> dias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIAS, null, null, null, null, null, COLUMN_FECHA + " DESC");

        if (cursor.moveToFirst()) {
            do {
                DiaPropina dia = new DiaPropina();
                dia.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                dia.setFecha(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))));
                dia.setTotalPropinas(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PROPINAS)));
                dia.setDescuento(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESCUENTO)));
                dia.setTotalRepartir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_REPARTIR)));
                dias.add(dia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dias;
    }

    public DiaPropina getDiaById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIAS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            DiaPropina dia = new DiaPropina();
            dia.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            dia.setFecha(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))));
            dia.setTotalPropinas(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PROPINAS)));
            dia.setDescuento(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESCUENTO)));
            dia.setTotalRepartir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_REPARTIR)));
            cursor.close();
            return dia;
        }
        cursor.close();
        return null;
    }

    // CRUD para TrabajadoresDia
    public long addTrabajadorDia(TrabajadorDia trabajadorDia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DIA, trabajadorDia.getIdDia());
        values.put(COLUMN_ID_TRABAJADOR, trabajadorDia.getIdTrabajador());
        values.put(COLUMN_HORAS, trabajadorDia.getHoras());
        values.put(COLUMN_MONTO, trabajadorDia.getMonto());
        return db.insert(TABLE_TRABAJADORES_DIA, null, values);
    }

    public List<TrabajadorDia> getTrabajadoresByDia(int idDia) {
        List<TrabajadorDia> trabajadoresDia = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT td.*, t." + COLUMN_NOMBRE +
                " FROM " + TABLE_TRABAJADORES_DIA + " td" +
                " JOIN " + TABLE_TRABAJADORES + " t ON td." + COLUMN_ID_TRABAJADOR + " = t." + COLUMN_ID +
                " WHERE td." + COLUMN_ID_DIA + " = ?" +
                " ORDER BY t." + COLUMN_NOMBRE;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idDia)});

        if (cursor.moveToFirst()) {
            do {
                TrabajadorDia td = new TrabajadorDia();
                td.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                td.setIdDia(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_DIA)));
                td.setIdTrabajador(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TRABAJADOR)));
                td.setNombreTrabajador(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                td.setHoras(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HORAS)));
                td.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)));
                trabajadoresDia.add(td);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trabajadoresDia;
    }

    public void deleteTrabajadoresByDia(int idDia) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRABAJADORES_DIA, COLUMN_ID_DIA + "=?", new String[]{String.valueOf(idDia)});
    }

    public void deleteDia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Primero eliminar los trabajadores del día
        db.delete(TABLE_TRABAJADORES_DIA, COLUMN_ID_DIA + "=?", new String[]{String.valueOf(id)});
        // Luego eliminar el día
        db.delete(TABLE_DIAS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Obtener todos los días con sus trabajadores para calcular semanas
    public List<DiaPropina> getDiasConTrabajadores() {
        return getAllDias();
    }

    // Obtener el total de horas y monto de un trabajador en un rango de fechas
    public double[] getTotalTrabajadorEnRango(int idTrabajador, long fechaInicio, long fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        double[] resultado = new double[3]; // [0] = totalHoras, [1] = totalMonto, [2] = diasTrabajados

        String query = "SELECT SUM(td." + COLUMN_HORAS + ") as total_horas, " +
                "SUM(td." + COLUMN_MONTO + ") as total_monto, " +
                "COUNT(DISTINCT td." + COLUMN_ID_DIA + ") as dias " +
                "FROM " + TABLE_TRABAJADORES_DIA + " td " +
                "JOIN " + TABLE_DIAS + " d ON td." + COLUMN_ID_DIA + " = d." + COLUMN_ID + " " +
                "WHERE td." + COLUMN_ID_TRABAJADOR + " = ? " +
                "AND d." + COLUMN_FECHA + " >= ? " +
                "AND d." + COLUMN_FECHA + " <= ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(idTrabajador),
                String.valueOf(fechaInicio),
                String.valueOf(fechaFin)
        });

        if (cursor.moveToFirst()) {
            resultado[0] = cursor.getDouble(cursor.getColumnIndexOrThrow("total_horas"));
            resultado[1] = cursor.getDouble(cursor.getColumnIndexOrThrow("total_monto"));
            resultado[2] = cursor.getInt(cursor.getColumnIndexOrThrow("dias"));
        }
        cursor.close();
        return resultado;
    }

    // Obtener el total de propinas en un rango de fechas
    public double[] getTotalSemana(long fechaInicio, long fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        double[] resultado = new double[3]; // [0] = totalPropinas, [1] = totalRepartir, [2] = cantidadDias

        String query = "SELECT SUM(" + COLUMN_TOTAL_PROPINAS + ") as total, " +
                "SUM(" + COLUMN_TOTAL_REPARTIR + ") as total_rep, " +
                "COUNT(*) as dias " +
                "FROM " + TABLE_DIAS + " " +
                "WHERE " + COLUMN_FECHA + " >= ? " +
                "AND " + COLUMN_FECHA + " <= ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(fechaInicio),
                String.valueOf(fechaFin)
        });

        if (cursor.moveToFirst()) {
            resultado[0] = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            resultado[1] = cursor.getDouble(cursor.getColumnIndexOrThrow("total_rep"));
            resultado[2] = cursor.getInt(cursor.getColumnIndexOrThrow("dias"));
        }
        cursor.close();
        return resultado;
    }

    // Obtener todos los trabajadores que trabajaron en un rango de fechas
    public List<Trabajador> getTrabajadoresEnRango(long fechaInicio, long fechaFin) {
        List<Trabajador> trabajadores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT t." + COLUMN_ID + ", t." + COLUMN_NOMBRE + " " +
                "FROM " + TABLE_TRABAJADORES + " t " +
                "JOIN " + TABLE_TRABAJADORES_DIA + " td ON t." + COLUMN_ID + " = td." + COLUMN_ID_TRABAJADOR + " " +
                "JOIN " + TABLE_DIAS + " d ON td." + COLUMN_ID_DIA + " = d." + COLUMN_ID + " " +
                "WHERE d." + COLUMN_FECHA + " >= ? " +
                "AND d." + COLUMN_FECHA + " <= ? " +
                "ORDER BY t." + COLUMN_NOMBRE;

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(fechaInicio),
                String.valueOf(fechaFin)
        });

        if (cursor.moveToFirst()) {
            do {
                Trabajador t = new Trabajador();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                t.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                trabajadores.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trabajadores;
    }

}