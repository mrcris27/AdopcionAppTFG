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

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    AnimalEntity getAnimalByIdSync(String id);

    // Obtener animales por protectora
    @Query("SELECT * FROM animals WHERE shelterId = :shelterId")
    LiveData<List<AnimalEntity>> getAnimalsByShelter(String shelterId);

    // Obtener animales por especie
    @Query("SELECT * FROM animals WHERE species = :species")
    LiveData<List<AnimalEntity>> getAnimalsBySpecies(String species);

    // Obtener animales por estado
    @Query("SELECT * FROM animals WHERE forAdoption = :inAdpotion")
    LiveData<List<AnimalEntity>> getAnimalsInAdoption(String inAdpotion);


    // Obtener animales por característica
    @Query("SELECT * FROM animals WHERE characteristics LIKE '%' || :characteristic || '%'")
    LiveData<List<AnimalEntity>> getAnimalsByCharacteristic(String characteristic);

    // Insertar/actualizar animal (si ya existe lo reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnimal(AnimalEntity animal);

    @Query("SELECT COUNT(*) FROM animals")
    int getAnimalCount();

    @Query("DELETE FROM animals")
    void deleteAllAnimals();

    @Query("DELETE FROM animals WHERE shelterId = :shelterId")
    void deleteAnimalsByShelter(String shelterId);

    @Query("DELETE FROM animals WHERE id NOT IN (:ids)")
    void deleteAnimalsNotIn(List<String> ids);

    @Query("DELETE FROM animals WHERE shelterId = :shelterId AND id NOT IN (:ids)")
    void deleteAnimalsByShelterNotIn(String shelterId, List<String> ids);

    // Eliminar animal
    @Delete
    void deleteAnimal(AnimalEntity animal);
}
