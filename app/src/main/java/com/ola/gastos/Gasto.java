package com.ola.gastos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
public class Gasto implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String tipoGasto;
    private double monto;
    private int mes;
    private int anio;

    public Gasto(String tipoGasto, double monto, int mes, int anio) {
        this.tipoGasto = tipoGasto;
        this.monto = monto;
        this.mes = mes;
        this.anio = anio;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoGasto() {
        return tipoGasto;
    }

    public double getMonto() {
        return monto;
    }

    public int getMes() {
        return mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
