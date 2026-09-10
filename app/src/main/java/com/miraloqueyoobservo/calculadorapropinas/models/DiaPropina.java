package com.miraloqueyoobservo.calculadorapropinas.models;

import java.util.Date;

public class DiaPropina {
    private int id;
    private Date fecha;
    private double totalPropinas;
    private double descuento;
    private double totalRepartir;

    public DiaPropina() {}

    public DiaPropina(Date fecha, double totalPropinas) {
        this.fecha = fecha;
        this.totalPropinas = totalPropinas;
        this.descuento = totalPropinas * 0.10;
        this.totalRepartir = totalPropinas * 0.90;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public double getTotalPropinas() { return totalPropinas; }
    public void setTotalPropinas(double totalPropinas) {
        this.totalPropinas = totalPropinas;
        this.descuento = totalPropinas * 0.10;
        this.totalRepartir = totalPropinas * 0.90;
    }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public double getTotalRepartir() { return totalRepartir; }
    public void setTotalRepartir(double totalRepartir) { this.totalRepartir = totalRepartir; }
}