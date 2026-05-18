package com.example.adopciontfg.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Dao;

import com.example.adopciontfg.data.local.entity.AnimalEntity;

import java.util.List;

@Dao
public interface AnimalDao {

    // Obtener todos los animales
    @Query("SELECT * FROM animals")
    LiveData<List<AnimalEntity>> getAllAnimals();

    // Obtener animal por id
    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    LiveData<AnimalEntity> getAnimalById(String id);

    // Obtener animales por protectora
    @Query("SELECT * FROM animals WHERE shelterId = :shelterId")
    LiveData<List<AnimalEntity>> getAnimalsByShelter(String shelterId);

    // Obtener animales por especie
    @Query("SELECT * FROM animals WHERE species = :species")
    LiveData<List<AnimalEntity>> getAnimalsBySpecies(String species);

    // Obtener animales por característica
    @Query("SELECT * FROM animals WHERE characteristics LIKE '%' || :characteristic || '%'")
    LiveData<List<AnimalEntity>> getAnimalsByCharacteristic(String characteristic);

    // Insertar/actualizar animal (si ya existe lo reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnimal(AnimalEntity animal);

    @Query("SELECT COUNT(*) FROM animals")
    int getAnimalCount();

    // Eliminar animal
    @Delete
    void deleteAnimal(AnimalEntity animal);
}
