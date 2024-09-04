package com.ola.gastos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import androidx.room.Delete;
import androidx.room.Update;


@Dao
public interface GastoDao {

    @Insert
    void insertarGasto(Gasto gasto);

    @Query("SELECT * FROM Gasto WHERE mes = :mes")
    List<Gasto> obtenerGastosPorMes(int mes);

    @Query("SELECT SUM(monto) FROM Gasto WHERE mes = :mes")
    double obtenerTotalPorMes(int mes);

    @Query("SELECT * FROM Gasto")
    List<Gasto> obtenerTodosLosGastos();

    @Query("SELECT DISTINCT mes FROM Gasto WHERE anio = :anio")
    List<Integer> obtenerMesesConGastos(int anio);

    @Delete
    void eliminarGasto(Gasto gasto);
    @Update
    void actualizarGasto(Gasto gasto);
}
