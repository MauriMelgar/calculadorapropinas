package com.miraloqueyoobservo.calculadorapropinas.models;

import java.util.Date;
import java.util.List;

public class ResumenSemanal {
    private Date lunes;
    private Date domingo;
    private double totalPropinas;
    private double totalRepartir;
    private double totalHoras;
    private List<ResumenTrabajador> trabajadores;
    private int cantidadDias;

    public ResumenSemanal() {}

    public Date getLunes() { return lunes; }
    public void setLunes(Date lunes) { this.lunes = lunes; }
    public Date getDomingo() { return domingo; }
    public void setDomingo(Date domingo) { this.domingo = domingo; }
    public double getTotalPropinas() { return totalPropinas; }
    public void setTotalPropinas(double totalPropinas) { this.totalPropinas = totalPropinas; }
    public double getTotalRepartir() { return totalRepartir; }
    public void setTotalRepartir(double totalRepartir) { this.totalRepartir = totalRepartir; }
    public double getTotalHoras() { return totalHoras; }
    public void setTotalHoras(double totalHoras) { this.totalHoras = totalHoras; }
    public List<ResumenTrabajador> getTrabajadores() { return trabajadores; }
    public void setTrabajadores(List<ResumenTrabajador> trabajadores) { this.trabajadores = trabajadores; }
    public int getCantidadDias() { return cantidadDias; }
    public void setCantidadDias(int cantidadDias) { this.cantidadDias = cantidadDias; }

    // Clase interna para el resumen por trabajador
    public static class ResumenTrabajador {
        private int idTrabajador;
        private String nombre;
        private double totalHoras;
        private double totalMonto;
        private int diasTrabajados;

        public ResumenTrabajador() {}

        public int getIdTrabajador() { return idTrabajador; }
        public void setIdTrabajador(int idTrabajador) { this.idTrabajador = idTrabajador; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public double getTotalHoras() { return totalHoras; }
        public void setTotalHoras(double totalHoras) { this.totalHoras = totalHoras; }
        public double getTotalMonto() { return totalMonto; }
        public void setTotalMonto(double totalMonto) { this.totalMonto = totalMonto; }
        public int getDiasTrabajados() { return diasTrabajados; }
        public void setDiasTrabajados(int diasTrabajados) { this.diasTrabajados = diasTrabajados; }
    }
}