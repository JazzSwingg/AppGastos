package com.ola.gastos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GastosAdapter extends RecyclerView.Adapter<GastosAdapter.GastoViewHolder> {

    private List<Gasto> gastosList;
    private OnGastoClickListener listener;

    public GastosAdapter(List<Gasto> gastosList, OnGastoClickListener listener) {
        this.gastosList = gastosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = gastosList.get(position);

        // Formatear el monto como CLP
        NumberFormat formatCLP = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        String montoFormateado = formatCLP.format(gasto.getMonto());

        // Mostrar el tipo de gasto y el monto formateado
        holder.textViewTipoGasto.setText(gasto.getTipoGasto());
        holder.textViewMonto.setText(montoFormateado);  // Mostrar el monto en formato CLP

        // Asignar listeners para los botones de editar y eliminar
        holder.btnEditar.setOnClickListener(v -> listener.onEditGasto(gasto, position));
        holder.btnBorrar.setOnClickListener(v -> listener.onDeleteGasto(gasto, position));
    }

    @Override
    public int getItemCount() {
        return gastosList.size();
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTipoGasto, textViewMonto;
        ImageButton btnEditar, btnBorrar;

        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTipoGasto = itemView.findViewById(R.id.textViewTipoGasto);
            textViewMonto = itemView.findViewById(R.id.textViewMonto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }

    // Interfaz para manejar los eventos de editar y borrar
    public interface OnGastoClickListener {
        void onEditGasto(Gasto gasto, int position);
        void onDeleteGasto(Gasto gasto, int position);
    }
}

