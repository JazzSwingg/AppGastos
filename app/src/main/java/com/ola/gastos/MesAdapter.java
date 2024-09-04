package com.ola.gastos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.util.Log;

public class MesAdapter extends RecyclerView.Adapter<MesAdapter.MesViewHolder> {
    private List<Mes> meses;

    public MesAdapter(List<Mes> meses) {
        this.meses = meses;
    }

    @NonNull
    @Override
    public MesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mes, parent, false);
        return new MesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesViewHolder holder, int position) {
        Mes mes = meses.get(position);
        if (mes != null) {
            holder.bind(mes);
        } else {
            Log.e("MesAdapter", "El objeto 'Mes' en la posición " + position + " es nulo");
        }
    }

    @Override
    public int getItemCount() {
        return meses.size();
    }

    static class MesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTipoGasto;
        private TextView textViewMonto;

        public MesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTipoGasto = itemView.findViewById(R.id.textViewTipoGasto);
            textViewMonto = itemView.findViewById(R.id.textViewMonto);
        }

        public void bind(Mes mes) {
            textViewTipoGasto.setText(mes.getNombre());  // Aquí puedes poner el nombre del tipo de gasto
            textViewMonto.setText("$" + String.valueOf(mes.calcularTotal()));  // Aquí se muestra el monto total con el símbolo de peso
        }
    }
}
