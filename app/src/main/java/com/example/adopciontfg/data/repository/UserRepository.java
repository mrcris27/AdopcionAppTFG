package com.example.adopciontfg.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.dao.UserDao;
import com.example.adopciontfg.data.local.entity.UserEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;


    //Constructor
    public UserRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        this.userDao = db.userDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ----- Obtener usuario por ID -----
    public LiveData<UserEntity> getUserById(String userId) {
        syncUsersFromFirebase();
        return userDao.getUserById(userId);
    }

    // ----- Insertar/Actualizar usuario -----
    public void saveUser(UserEntity user) {
        firestore.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(unused -> // si funciona bien en firebase se actualiza en room
                        executor.execute(() -> userDao.saveUser(user))
                );
    }

    // ----- Eliminar usuario -----
    public void deleteUser(UserEntity user) {
        firestore.collection("users")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        // si funciona bien en firebase se actualiza en room
                        executor.execute(() -> userDao.deleteUser(user))
                );
    }

    // ----- Sincronización desde Firebase -----
    private void syncUsersFromFirebase() {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<UserEntity> users = querySnapshot.toObjects(UserEntity.class);
                    executor.execute(() -> {
                        for (UserEntity user : users) {
                            userDao.saveUser(user);
                        }
                    });

                });

    }






}
