package com.example.adopciontfg.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.adopciontfg.data.local.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    // Obtener todos los animales favoritos de un usuario
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    LiveData<List<FavoriteEntity>> getFavoritesByUser(String userId);

    // Comprobar si un animal ya es favorito de un usuario
    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND animalId = :animalId")
    LiveData<Boolean> isFavorite(String userId, String animalId);

    // Añadir favorito
    //En el caso de que se llamase por error al insert más de una vez, se ignorará
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFavorite(FavoriteEntity favorite);

    // Eliminar favorito
    @Delete
    void deleteFavorite(FavoriteEntity favorite);
}