package com.ola.gastos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class BarrasAdapter extends RecyclerView.Adapter<BarrasAdapter.BarraViewHolder> {
    private List<GastoAgrupado> listaGastos;

    public BarrasAdapter(List<GastoAgrupado> listaGastos) {
        this.listaGastos = listaGastos;
    }

    @NonNull
    @Override
    public BarraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new BarraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarraViewHolder holder, int position) {
        GastoAgrupado gasto = listaGastos.get(position);
        holder.textViewTipoGasto.setText(gasto.getTipo());
        holder.textViewMonto.setText(formatCLP(gasto.getMonto()));
    }

    @Override
    public int getItemCount() {
        return listaGastos.size();
    }

    public static class BarraViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTipoGasto, textViewMonto;

        public BarraViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTipoGasto = itemView.findViewById(R.id.textViewTipoGasto);
            textViewMonto = itemView.findViewById(R.id.textViewMonto);
        }
    }

    // Método para formatear en CLP y agregar el símbolo de peso
    private String formatCLP(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("es", "CL"));
        return "$" + formatter.format(amount);  // Agrega el símbolo de peso
    }
}
