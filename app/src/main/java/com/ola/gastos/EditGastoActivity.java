package com.ola.gastos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EditGastoActivity extends AppCompatActivity {

    private Spinner spinnerTipoGasto;
    private EditText editTextMonto;
    private Gasto gasto;
    private GastosDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gasto);

        // Configurar el Toolbar con la flecha de "volver atrás"
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Manejar la acción al presionar la flecha de "volver atrás"
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        spinnerTipoGasto = findViewById(R.id.spinnerTipoGasto);
        editTextMonto = findViewById(R.id.editTextMonto);

        // Inicializar la base de datos
        db = GastosDatabase.getInstance(this);

        // Obtener los datos del gasto enviado por la actividad anterior
        Intent intent = getIntent();
        gasto = (Gasto) intent.getSerializableExtra("gasto");

        if (gasto != null) {
            // Configurar el spinner con los tipos de gasto
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.tipos_gasto, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoGasto.setAdapter(adapter);

            // Establecer los valores actuales del gasto
            editTextMonto.setText(formatCLP(gasto.getMonto()));
            int spinnerPosition = adapter.getPosition(gasto.getTipoGasto());
            spinnerTipoGasto.setSelection(spinnerPosition);
        }

        // Agregar TextWatcher para mantener el formato CLP y el símbolo '$'
        editTextMonto.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextMonto.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", ""); // Remover todo lo que no sea un número

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    // Formatear como CLP
                    String formatted = formatCLP(parsed);

                    current = formatted;
                    editTextMonto.setText(formatted);
                    editTextMonto.setSelection(formatted.length());

                    editTextMonto.addTextChangedListener(this);
                }
            }
        });

        // Guardar los cambios
        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        // Validar los datos
        String nuevoTipoGasto = spinnerTipoGasto.getSelectedItem().toString();
        String montoStr = editTextMonto.getText().toString().replaceAll("[^\\d]", "");

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto.", Toast.LENGTH_SHORT).show();
            return;
        }

        double nuevoMonto = Double.parseDouble(montoStr);

        // Validar que el monto no sea 0
        if (nuevoMonto == 0) {
            Toast.makeText(this, "El monto no puede ser 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el gasto con el nuevo valor
        gasto.setTipoGasto(nuevoTipoGasto);
        gasto.setMonto(nuevoMonto);

        // Guardar el gasto en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            db.gastoDao().actualizarGasto(gasto);

            // Enviar el resultado a la actividad principal
            Intent resultIntent = new Intent();
            resultIntent.putExtra("gastoActualizado", gasto);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Método para formatear el valor como CLP
    private String formatCLP(double amount) {
        NumberFormat formatCLP = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        return formatCLP.format(amount);
    }
}

