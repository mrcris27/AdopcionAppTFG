package com.example.adopciontfg.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.LocalPhotoStorage;
import com.example.adopciontfg.data.local.dao.UserDao;
import com.example.adopciontfg.data.local.entity.UserEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final FirebaseFirestore firestore;
    private final LocalPhotoStorage photoStorage;
    private final ExecutorService executor;


    //Constructor
    public UserRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        this.userDao = db.userDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.photoStorage = new LocalPhotoStorage(application);
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ----- Obtener usuario por ID -----
    public LiveData<UserEntity> getUserById(String userId) {
        syncUsersFromFirebase();
        return userDao.getUserById(userId);
    }

    // ----- Insertar/Actualizar usuario -----
    public void saveUser(UserEntity user) {
        executor.execute(() -> {
            UserEntity previousUser = userDao.getUserByIdSync(user.getId());
            UserEntity localUser = copyUserProfilePhoto(user);

            firestore.collection("users")
                    .document(localUser.getId())
                    .set(localUser)
                    .addOnSuccessListener(unused -> // si funciona bien en firebase se actualiza en room
                            executor.execute(() -> {
                                userDao.saveUser(localUser);
                                deleteReplacedProfilePhoto(previousUser, localUser);
                            })
                    );
        });
    }

    // ----- Eliminar usuario -----
    public void deleteUser(UserEntity user) {
        firestore.collection("users")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        // si funciona bien en firebase se actualiza en room
                        executor.execute(() -> {
                            UserEntity storedUser = userDao.getUserByIdSync(user.getId());
                            userDao.deleteUser(user);
                            photoStorage.deletePhoto(
                                    storedUser == null ? user.getProfilePicture() : storedUser.getProfilePicture()
                            );
                        })
                );
    }

    private UserEntity copyUserProfilePhoto(UserEntity user) {
        try {
            return new UserEntity(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    photoStorage.copyPhotoIfNeeded(
                            user.getProfilePicture(),
                            "users/" + user.getId(),
                            "profile.jpg"
                    ),
                    user.getEmail(),
                    user.getBiography()
            );
        } catch (Exception exception) {
            return new UserEntity(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    "",
                    user.getEmail(),
                    user.getBiography()
            );
        }
    }

    private void deleteReplacedProfilePhoto(UserEntity previousUser, UserEntity currentUser) {
        if (previousUser == null) return;
        String oldPhoto = previousUser.getProfilePicture();
        String currentPhoto = currentUser.getProfilePicture();
        if (oldPhoto != null && !oldPhoto.equals(currentPhoto)) {
            photoStorage.deletePhoto(oldPhoto);
        }
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
