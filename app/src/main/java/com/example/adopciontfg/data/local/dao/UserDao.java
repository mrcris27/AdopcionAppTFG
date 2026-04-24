package com.example.adopciontfg.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.adopciontfg.data.local.entity.UserEntity;

@Dao
public interface UserDao {

    // Obtener usuario por ID
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserEntity> getUserById(String userId);

    // Insertar usuario (si ya existe lo reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    // Actualizar usuario
    @Update
    void updateUser(UserEntity user);

    // Eliminar usuario
    @Delete
    void deleteUser(UserEntity user);
}
