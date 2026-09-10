package com.miraloqueyoobservo.calculadorapropinas.models;

public class TrabajadorDia {
    private int id;
    private int idDia;
    private int idTrabajador;
    private String nombreTrabajador;
    private double horas;
    private double monto;

    public TrabajadorDia() {}

    public TrabajadorDia(int idDia, int idTrabajador, String nombreTrabajador, double horas) {
        this.idDia = idDia;
        this.idTrabajador = idTrabajador;
        this.nombreTrabajador = nombreTrabajador;
        this.horas = horas;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdDia() { return idDia; }
    public void setIdDia(int idDia) { this.idDia = idDia; }
    public int getIdTrabajador() { return idTrabajador; }
    public void setIdTrabajador(int idTrabajador) { this.idTrabajador = idTrabajador; }
    public String getNombreTrabajador() { return nombreTrabajador; }
    public void setNombreTrabajador(String nombreTrabajador) { this.nombreTrabajador = nombreTrabajador; }
    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}