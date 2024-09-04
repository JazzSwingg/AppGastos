package com.ola.gastos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import android.graphics.Color;
import java.text.NumberFormat;
import java.util.Locale;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

public class VerGastosActivity extends AppCompatActivity implements GastosAdapter.OnGastoClickListener {

    private Spinner spinnerMes;
    private PieChart pieChart;
    private RecyclerView recyclerViewGastos;
    private GastosAdapter gastosAdapter;
    private List<Gasto> gastosList;
    private List<GastoAgrupado> gastosAgrupadosList;
    private GastosDatabase db;
    private static final int REQUEST_CODE_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_gastos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inicializar vistas
        spinnerMes = findViewById(R.id.spinnerMes);
        pieChart = findViewById(R.id.pieChart);
        recyclerViewGastos = findViewById(R.id.recyclerViewGastos);
        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar base de datos y listas
        db = GastosDatabase.getInstance(this);
        gastosList = new ArrayList<>();
        gastosAgrupadosList = new ArrayList<>();

        // Configurar el adaptador del RecyclerView
        gastosAdapter = new GastosAdapter(gastosList, this);
        recyclerViewGastos.setAdapter(gastosAdapter);

        cargarMesesConGastos();
    }

    private void cargarMesesConGastos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            List<Integer> mesesConGastos = db.gastoDao().obtenerMesesConGastos(anioActual);

            runOnUiThread(() -> {
                if (mesesConGastos.isEmpty()) {
                    Toast.makeText(this, "No hay gastos guardados para este año.", Toast.LENGTH_SHORT).show();
                    spinnerMes.setVisibility(View.GONE); // Ocultar el Spinner
                } else {
                    spinnerMes.setVisibility(View.VISIBLE); // Mostrar el Spinner si hay datos
                    List<String> mesesString = new ArrayList<>();
                    for (int mes : mesesConGastos) {
                        mesesString.add(getMonthName(mes));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mesesString);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMes.setAdapter(adapter);

                    spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int mesSeleccionado = mesesConGastos.get(position);
                            cargarGastosPorMes(mesSeleccionado);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // No hacer nada si no hay selección
                        }
                    });

                    // Cargar los datos iniciales
                    int mesInicial = mesesConGastos.get(0);
                    cargarGastosPorMes(mesInicial);
                }
            });
        });
    }

    private void cargarGastosPorMes(int mes) {
        Log.d("VerGastosActivity", "Cargando gastos para el mes: " + mes);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Gasto> gastos = db.gastoDao().obtenerGastosPorMes(mes);

            // Calcular el total de los gastos
            double totalGastos = 0;
            for (Gasto gasto : gastos) {
                totalGastos += gasto.getMonto();  // Sumar todos los gastos
            }

            final double total = totalGastos;  // Variable final para usar en runOnUiThread

            runOnUiThread(() -> {
                gastosList.clear();
                gastosList.addAll(gastos);
                gastosAdapter.notifyDataSetChanged();

                // Mostrar el total formateado en CLP
                NumberFormat formatCLP = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
                String totalFormateado = formatCLP.format(total);
                Toast.makeText(VerGastosActivity.this, "Total de gastos: " + totalFormateado, Toast.LENGTH_LONG).show();

                actualizarGraficoCircular();  // Actualizar el gráfico circular
            });
        });
    }

    @Override
    public void onEditGasto(Gasto gasto, int position) {
        // Lanzar la nueva actividad de edición
        Intent intent = new Intent(this, EditGastoActivity.class);
        intent.putExtra("gasto", gasto);  // Pasar el gasto seleccionado a la nueva actividad
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    // Recibir los resultados de la actividad de edición
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            // Obtener el gasto actualizado de la actividad de edición
            Gasto gastoActualizado = (Gasto) data.getSerializableExtra("gastoActualizado");

            // Actualizar la lista y la UI
            for (int i = 0; i < gastosList.size(); i++) {
                if (gastosList.get(i).getId() == gastoActualizado.getId()) {
                    gastosList.set(i, gastoActualizado);
                    gastosAdapter.notifyItemChanged(i);
                    actualizarGraficoCircular();
                    break;
                }
            }
        }
    }

    @Override
    public void onDeleteGasto(Gasto gasto, int position) {
        // Crear un AlertDialog para confirmar la eliminación
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este gasto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si el usuario confirma, se elimina el gasto
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.gastoDao().eliminarGasto(gasto);
                        runOnUiThread(() -> {
                            gastosList.remove(position);
                            gastosAdapter.notifyItemRemoved(position);
                            actualizarGraficoCircular(); // Actualizar el gráfico circular después de eliminar el gasto
                            Toast.makeText(VerGastosActivity.this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Si el usuario cancela, no se hace nada
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void actualizarGraficoCircular() {
        List<PieEntry> entries = new ArrayList<>();

        // Variables para agrupar los gastos
        double totalLuz = 0;
        double totalAgua = 0;
        double totalGas = 0;
        double totalGastos = 0;

        // Formateador para mostrar los valores en CLP
        NumberFormat formatCLP = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));

        // Calcular los totales de cada tipo de gasto
        for (Gasto gasto : gastosList) {
            switch (gasto.getTipoGasto()) {
                case "Luz":
                    totalLuz += gasto.getMonto();
                    break;
                case "Agua":
                    totalAgua += gasto.getMonto();
                    break;
                case "Gas":
                    totalGas += gasto.getMonto();
                    break;
            }
        }

        // Calcular el total de todos los gastos
        totalGastos = totalLuz + totalAgua + totalGas;

        // Añadir las entradas al gráfico circular con los porcentajes y el total en CLP
        if (totalLuz > 0) {
            float porcentajeLuz = (float) (totalLuz / totalGastos * 100);
            // Formatear el total como CLP y añadirlo a la etiqueta
            entries.add(new PieEntry(porcentajeLuz, "Luz: " + formatCLP.format(totalLuz)));
        }
        if (totalAgua > 0) {
            float porcentajeAgua = (float) (totalAgua / totalGastos * 100);
            // Formatear el total como CLP y añadirlo a la etiqueta
            entries.add(new PieEntry(porcentajeAgua, "Agua: " + formatCLP.format(totalAgua)));
        }
        if (totalGas > 0) {
            float porcentajeGas = (float) (totalGas / totalGastos * 100);
            // Formatear el total como CLP y añadirlo a la etiqueta
            entries.add(new PieEntry(porcentajeGas, "Gas: " + formatCLP.format(totalGas)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getColorsForCategories());
        dataSet.setDrawValues(true);  // Mostrar solo los porcentajes
        dataSet.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.BLACK);

        // Configurar el gráfico para mostrar los porcentajes con el símbolo %
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PorcentajeFormatter());  // Usar el formateador personalizado
        pieChart.setData(data);

        pieChart.setDrawEntryLabels(false);  // Desactivar las etiquetas de las entradas

        // Habilitar la leyenda y personalizarla
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setTextSize(12f);

        pieChart.getDescription().setEnabled(false);  // Desactivar la descripción del gráfico
        pieChart.invalidate();  // Refrescar el gráfico
    }


    private List<Integer> getColorsForCategories() {
        List<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.rgb("#FFEB3B"));  // Luz
        colors.add(ColorTemplate.rgb("#2196F3"));  // Agua
        colors.add(ColorTemplate.rgb("#FF9800"));  // Gas
        return colors;
    }

    private String getMonthName(int month) {
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return months[month - 1];
    }
}
