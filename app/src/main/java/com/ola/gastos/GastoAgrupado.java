package com.ola.gastos;

public class GastoAgrupado {
    private String tipo;
    private double monto;

    public GastoAgrupado(String tipo, double monto) {
        this.tipo = tipo;
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }
}
