package com.ola.gastos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Mes implements Serializable {

    private String nombre;
    private List<Gasto> gastos;

    public Mes(String nombre) {
        this.nombre = nombre;
        this.gastos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarGasto(Gasto gasto) {
        gastos.add(gasto);
    }

    public double calcularTotal() {
        double total = 0;
        for (Gasto g : gastos) {
            total += g.getMonto();
        }
        return total;
    }

    public double calcularPorcentaje(String tipoGasto) {
        double totalGasto = calcularTotal();
        double gastoEspecifico = 0;

        for (Gasto g : gastos) {
            if (g.getTipoGasto().equals(tipoGasto)) {
                gastoEspecifico += g.getMonto();
            }
        }

        return (gastoEspecifico / totalGasto) * 100;
    }
}
