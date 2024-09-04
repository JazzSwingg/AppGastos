package com.ola.gastos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import android.app.AlertDialog;
import android.widget.NumberPicker;

public class AgregarGastoActivity extends AppCompatActivity {

    private Spinner spinnerTipoGasto;
    private EditText editTextMonto;
    private TextView textViewMesSeleccionado;
    private Calendar mesSeleccionado;
    private GastosDatabase db;
    private boolean mesFueSeleccionado = false; // Nueva variable para controlar si el mes fue seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_gasto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inicialización de las vistas y la base de datos
        spinnerTipoGasto = findViewById(R.id.spinnerTipoGasto);
        editTextMonto = findViewById(R.id.editTextMonto);
        textViewMesSeleccionado = findViewById(R.id.textViewMesSeleccionado);
        Button btnSeleccionarMes = findViewById(R.id.btnSeleccionarMes);
        db = Room.databaseBuilder(getApplicationContext(),
                        GastosDatabase.class, "gastos_database")
                .fallbackToDestructiveMigration()
                .build();

        mesSeleccionado = Calendar.getInstance();  // Se inicializa con la fecha actual

        // Configurar el botón para seleccionar el mes
        btnSeleccionarMes.setOnClickListener(v -> mostrarSelectorDeMes());

        // Formatear el campo de monto como CLP
        editTextMonto.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextMonto.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,\\.]", "");
                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    // Validar que el monto no sea 0 mientras se escribe
                    if (parsed == 0) {
                        Toast.makeText(AgregarGastoActivity.this, "El monto no puede ser 0.", Toast.LENGTH_SHORT).show();
                    }

                    NumberFormat formatter = NumberFormat.getInstance(new Locale("es", "CL"));
                    String formatted = formatter.format(parsed);

                    current = formatted;
                    editTextMonto.setText(formatted);
                    editTextMonto.setSelection(formatted.length());

                    editTextMonto.addTextChangedListener(this);
                }
            }
        });

        // Configurar el botón para guardar el gasto
        findViewById(R.id.btnGuardarGasto).setOnClickListener(v -> guardarGasto());
    }

    private void mostrarSelectorDeMes() {
        final Calendar calendarioActual = Calendar.getInstance();
        final int anioActual = calendarioActual.get(Calendar.YEAR);
        final int mesActual = calendarioActual.get(Calendar.MONTH);

        // Crear un diálogo para seleccionar el mes y año
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);
        builder.setView(dialogView);

        final NumberPicker monthPicker = dialogView.findViewById(R.id.pickerMonth);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.pickerYear);

        String[] displayedMonths = new String[]{
                getMonthName(mesActual - 1), // Mes anterior
                getMonthName(mesActual)      // Mes actual
        };

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(1);
        monthPicker.setDisplayedValues(displayedMonths);
        monthPicker.setValue(1); // Mes actual por defecto

        yearPicker.setMinValue(anioActual);
        yearPicker.setMaxValue(anioActual);
        yearPicker.setValue(anioActual);
        yearPicker.setEnabled(false);  // Deshabilitar el cambio de año

        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int mesSeleccionadoIndex = monthPicker.getValue();
            int mesSeleccionadoValue = mesSeleccionadoIndex == 0 ? mesActual - 1 : mesActual;

            if (mesSeleccionadoValue < 0) {
                mesSeleccionadoValue = 11; // Ajuste para diciembre del año anterior
                mesSeleccionado.set(Calendar.YEAR, anioActual - 1);
            } else {
                mesSeleccionado.set(Calendar.YEAR, anioActual);
            }

            mesSeleccionado.set(Calendar.MONTH, mesSeleccionadoValue);
            mesFueSeleccionado = true; // Marcar que el mes fue seleccionado

            textViewMesSeleccionado.setText("Mes seleccionado: " + (mesSeleccionadoValue + 1) + "/" + mesSeleccionado.get(Calendar.YEAR));
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private String getMonthName(int month) {
        String[] months = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return months[(month < 0 ? 11 : month)];  // Ajustar para manejar diciembre del año anterior
    }

    private void guardarGasto() {
        String tipoGasto = spinnerTipoGasto.getSelectedItem().toString();
        String montoStr = editTextMonto.getText().toString().replace(".", "").replace(",", "");

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el mes fue seleccionado
        if (!mesFueSeleccionado) {
            Toast.makeText(this, "Por favor, seleccione un mes.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar como número simple en la base de datos
        double monto = Double.parseDouble(montoStr);

        // Validar que el monto no sea 0
        if (monto == 0) {
            Toast.makeText(this, "El monto no puede ser 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        int mes = mesSeleccionado.get(Calendar.MONTH) + 1;
        int anio = mesSeleccionado.get(Calendar.YEAR);

        Gasto nuevoGasto = new Gasto(tipoGasto, monto, mes, anio);

        new Thread(() -> {
            db.gastoDao().insertarGasto(nuevoGasto);
            runOnUiThread(() -> {
                Toast.makeText(this, "Gasto de " + tipoGasto + " guardado", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
