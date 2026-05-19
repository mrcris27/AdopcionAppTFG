package com.example.adopciontfg.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.adopciontfg.data.local.converters.Converters;
import com.example.adopciontfg.data.local.dao.AnimalDao;
import com.example.adopciontfg.data.local.dao.FavoriteDao;
import com.example.adopciontfg.data.local.dao.ShelterDao;
import com.example.adopciontfg.data.local.dao.SponsorshipDao;
import com.example.adopciontfg.data.local.dao.UserDao;
import com.example.adopciontfg.data.local.entity.AnimalEntity;
import com.example.adopciontfg.data.local.entity.FavoriteEntity;
import com.example.adopciontfg.data.local.entity.ShelterEntity;
import com.example.adopciontfg.data.local.entity.SponsorshipEntity;
import com.example.adopciontfg.data.local.entity.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                ShelterEntity.class,
                AnimalEntity.class,
                FavoriteEntity.class,
                SponsorshipEntity.class
        },
        version = 3,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    //todos los hilos ven siempre el valor más reciente de esta variable
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ShelterDao shelterDao();
    public abstract AnimalDao animalDao();
    public abstract FavoriteDao favoriteDao();
    public abstract SponsorshipDao sponsorshipDao();

    public static AppDatabase getInstance(Context context) {


        //Este patrón se llama Double-Checked Locking y es el estándar para Singletons en entornos multihilo.
        /*
            En este patrón se comprueba dos veces si es null.
            La primera es por rendimiento, si ya está creada directamente no entra
            La segunda es por si en el proceso de entrar en la primera se crea otra instancia desde otro hilo
         */
        if (instance == null) {
            //Si es nulo, crea la instancia
            //Con synchronized solo un hilo puede acceder a esta sección a la vez
            //lo que protege de que se creen dos instancias diferentes
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "adopcion_tfg_db"
                    ).build();
                }
            }
        }
        //Si no es nulo, devuelve la instancia
        return instance;
    }
}
