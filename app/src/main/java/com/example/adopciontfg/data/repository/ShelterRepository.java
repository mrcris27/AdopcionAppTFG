package com.example.adopciontfg.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.LocalPhotoStorage;
import com.example.adopciontfg.data.local.dao.ShelterDao;
import com.example.adopciontfg.data.local.entity.ShelterEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShelterRepository {

    private final ShelterDao shelterDao;
    private final FirebaseFirestore firestore;
    private final LocalPhotoStorage photoStorage;
    private final ExecutorService executor;
    private static final String COLLECTION = "shelters";


    // ---------CONSTRUCTOR-----------
    public ShelterRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.shelterDao = db.shelterDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.photoStorage = new LocalPhotoStorage(application);
        this.executor = Executors.newSingleThreadExecutor();

    }


    // ------------ Obtener todas las protectoras -------------
    public LiveData<List<ShelterEntity>> getAllShelters() {
        syncSheltersFromFirebase();
        return shelterDao.getAllShelters();
    }

    // ------------ Obtener protectora por id -------------
    public LiveData<ShelterEntity> getShelterById(String id) {
        syncSheltersFromFirebase();
        return shelterDao.getShelterById(id);
    }

    // ------------ Obtener todas las protectoras por nombre -------------
    public LiveData<List<ShelterEntity>> getSheltersByName(String name) {
        syncSheltersFromFirebase();
        return shelterDao.getSheltersByName(name);
    }

    public void refreshAllShelters(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        syncSheltersFromFirebase(onSuccess, onFailure);
    }

    public void refreshShelterById(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        syncShelterByIdFromFirebase(id, onSuccess, onFailure);
    }


    // ------------ Actualiza/Inserta una protectora en firebase ---------------
    // ---- .set() lo utiliza firebase tanto para actualizar como para insertar  ----
    public void saveShelter(ShelterEntity shelter) {

        executor.execute(() -> {
            ShelterEntity previousShelter = shelterDao.getShelterByIdSync(shelter.getId());
            ShelterEntity localShelter = copyShelterProfilePhoto(shelter);

            firestore.collection(COLLECTION)
                    .document(localShelter.getId())
                    .set(localShelter)
                    .addOnSuccessListener(unused -> // si funciona bien en firebase se actualiza en room
                            executor.execute(() -> {
                                shelterDao.insertShelter(localShelter);
                                deleteReplacedProfilePhoto(previousShelter, localShelter);
                            })
                    );
        });
    }

    private ShelterEntity copyShelterProfilePhoto(ShelterEntity shelter) {
        try {
            return new ShelterEntity(
                    shelter.getId(),
                    shelter.getName(),
                    shelter.getCif(),
                    photoStorage.copyPhotoIfNeeded(
                            shelter.getProfilePicture(),
                            "shelters/" + shelter.getId(),
                            "profile.jpg"
                    ),
                    shelter.getEmail(),
                    shelter.getAddress(),
                    shelter.getPhone(),
                    shelter.getAdoptionFormUrl()
            );
        } catch (Exception exception) {
            return new ShelterEntity(
                    shelter.getId(),
                    shelter.getName(),
                    shelter.getCif(),
                    "",
                    shelter.getEmail(),
                    shelter.getAddress(),
                    shelter.getPhone(),
                    shelter.getAdoptionFormUrl()
            );
        }
    }

    private void deleteReplacedProfilePhoto(ShelterEntity previousShelter, ShelterEntity currentShelter) {
        if (previousShelter == null) return;
        String oldPhoto = previousShelter.getProfilePicture();
        String currentPhoto = currentShelter.getProfilePicture();
        if (oldPhoto != null && !oldPhoto.equals(currentPhoto)) {
            photoStorage.deletePhoto(oldPhoto);
        }
    }


    // -------------- Sincronización desde Firebase -----------------
    private void syncSheltersFromFirebase() {
        syncSheltersFromFirebase(unused -> {}, error -> {});
    }

    private void syncSheltersFromFirebase(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection("shelters")  // 1. Ve a la colección
                .get(Source.SERVER)             // 2. Pide todos los documentos al servidor
                .addOnSuccessListener(querySnapshot -> {  // 3. Cuando responde...

                    List<ShelterEntity> shelters = querySnapshot.toObjects(ShelterEntity.class); // 4. Convierte a objetos Java

                    executor.execute(() -> {   // 5. En hilo secundario...
                        List<String> shelterIds = new ArrayList<>();
                        for (ShelterEntity shelter : shelters) {
                            shelterDao.insertShelter(shelter); // 6. Guarda cada uno en Room
                            shelterIds.add(shelter.getId());
                        }
                        if (shelterIds.isEmpty()) {
                            shelterDao.deleteAllShelters();
                        } else {
                            shelterDao.deleteSheltersNotIn(shelterIds);
                        }
                        onSuccess.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailure);
    }

    private void syncShelterByIdFromFirebase(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection(COLLECTION)
                .document(id)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    ShelterEntity shelter = documentSnapshot.toObject(ShelterEntity.class);
                    if (shelter == null) {
                        onSuccess.onSuccess(null);
                        return;
                    }
                    executor.execute(() -> {
                        shelterDao.insertShelter(shelter);
                        onSuccess.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailure);
    }




}
