package com.ola.gastos;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Gasto.class}, version = 2)
public abstract class GastosDatabase extends RoomDatabase {

    private static GastosDatabase instance;

    public abstract GastoDao gastoDao();

    public static synchronized GastosDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            GastosDatabase.class, "gastos_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}