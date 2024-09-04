package com.ola.gastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar botón para agregar un gasto
        MaterialButton btnAgregarGasto = findViewById(R.id.btnAgregarGasto);
        btnAgregarGasto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarGastoActivity.class);
            startActivity(intent);
        });

        // Configurar botón para ver los gastos realizados
        MaterialButton btnVerGastos = findViewById(R.id.btnVerGastos);
        btnVerGastos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VerGastosActivity.class);
            startActivity(intent);
        });
    }
}
