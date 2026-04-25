package com.example.adopciontfg.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.dao.ShelterDao;
import com.example.adopciontfg.data.local.entity.ShelterEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShelterRepository {

    private final ShelterDao shelterDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;
    private static final String COLLECTION = "shelters";


    // ---------CONSTRUCTOR-----------
    public ShelterRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.shelterDao = db.shelterDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.executor = Executors.newSingleThreadExecutor();

    }


    // ------------ Obtener todas las protectoras -------------
    public LiveData<List<ShelterEntity>> getAllShelters() {
        syncSheltersFromFirebase();
        return shelterDao.getAllShelters();
    }

    // ------------ Obtener todas las protectoras por nombre -------------
    public LiveData<List<ShelterEntity>> getSheltersByName(String name) {
        syncSheltersFromFirebase();
        return shelterDao.getSheltersByName(name);
    }


    // ------------ Actualiza/Inserta una protectora en firebase ---------------
    // ---- .set() lo utiliza firebase tanto para actualizar como para insertar  ----
    public void saveShelter(ShelterEntity shelter) {

        firestore.collection(COLLECTION)
                .document(shelter.getId())
                .set(shelter)
                .addOnSuccessListener(unused -> // si funciona bien en firebase se actualiza en room
                        executor.execute(() -> shelterDao.insertShelter(shelter))
                );
    }


    // -------------- Sincronización desde Firebase -----------------
    private void syncSheltersFromFirebase() {
        firestore.collection("shelters")  // 1. Ve a la colección
                .get()                         // 2. Pide todos los documentos
                .addOnSuccessListener(querySnapshot -> {  // 3. Cuando responde...

                    List<ShelterEntity> shelters = querySnapshot.toObjects(ShelterEntity.class); // 4. Convierte a objetos Java

                    executor.execute(() -> {   // 5. En hilo secundario...
                        for (ShelterEntity shelter : shelters) {
                            shelterDao.insertShelter(shelter); // 6. Guarda cada uno en Room
                        }
                    });
                });
    }




}
