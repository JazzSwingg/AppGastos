package com.ola.gastos;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class PorcentajeFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        // Formatear el valor para que se muestre como un porcentaje (sin decimales) con el símbolo %
        return String.format("%.0f%%", value);
    }
}
