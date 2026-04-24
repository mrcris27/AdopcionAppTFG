package com.example.adopciontfg.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.adopciontfg.data.local.entity.SponsorshipEntity;

import java.util.List;

@Dao
public interface SponsorshipDao {

    // Obtener todos los apadrinamientos de un usuario
    @Query("SELECT * FROM sponsorships WHERE userId = :userId")
    LiveData<List<SponsorshipEntity>> getSponsorshipsByUser(String userId);

    // Obtener todos los usuarios que apadrinan un animal
    @Query("SELECT * FROM sponsorships WHERE animalId = :animalId")
    LiveData<List<SponsorshipEntity>> getSponsorshipsByAnimal(String animalId);

    // Comprobar si un usuario ya apadrina un animal
    @Query("SELECT COUNT(*) FROM sponsorships WHERE userId = :userId AND animalId = :animalId")
    LiveData<Boolean> isSponsoring(String userId, String animalId);

    // Insertar apadrinamiento
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSponsorship(SponsorshipEntity sponsorship);

    // Eliminar apadrinamiento
    @Delete
    void deleteSponsorship(SponsorshipEntity sponsorship);
}