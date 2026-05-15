package com.example.adopciontfg.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.adopciontfg.data.local.entity.ShelterEntity;

import java.util.List;

@Dao
public interface ShelterDao {

    // Obtener todas las protectoras
    @Query("SELECT * FROM shelters")
    LiveData<List<ShelterEntity>> getAllShelters();

    // Obtener protectora por id
    @Query("SELECT * FROM shelters WHERE id = :id LIMIT 1")
    LiveData<ShelterEntity> getShelterById(String id);

    // Obtener protectora por nombre
    //Con el LIKE busca por coincidencia parcial no total
    @Query("SELECT * FROM shelters WHERE name LIKE '%' || :name || '%'")
    LiveData<List<ShelterEntity>> getSheltersByName(String name);

    // Insertar/actualizar protectora (si ya existe la reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShelter(ShelterEntity shelter);


}
