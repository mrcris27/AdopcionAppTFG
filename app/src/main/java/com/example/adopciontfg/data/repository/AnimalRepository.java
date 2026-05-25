package com.example.adopciontfg.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.LocalPhotoStorage;
import com.example.adopciontfg.data.local.dao.AnimalDao;
import com.example.adopciontfg.data.local.entity.AnimalEntity;
import com.example.adopciontfg.model.Characteristic;
import com.example.adopciontfg.model.Species;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimalRepository {

    private final AnimalDao animalDao;
    private final FirebaseFirestore firestore;
    private final LocalPhotoStorage photoStorage;
    private final ExecutorService executor;

    private static final String COLLECTION = "animals";

    public AnimalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.animalDao = db.animalDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.photoStorage = new LocalPhotoStorage(application);
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ─── Obtener todos los animales ─────────────────────────────────────
    public LiveData<List<AnimalEntity>> getAllAnimals() {
        syncAnimalsFromFirebase();
        return animalDao.getAllAnimals();
    }

    // ─── Obtener animal por id ──────────────────────────────────────────
    public LiveData<AnimalEntity> getAnimalById(String id) {
        syncAnimalsFromFirebase();
        return animalDao.getAnimalById(id);
    }

    // ─── Obtener animales por protectora ────────────────────────────────
    public LiveData<List<AnimalEntity>> getAnimalsByShelter(String shelterId) {
        syncAnimalsByShelterFromFirebase(shelterId);
        return animalDao.getAnimalsByShelter(shelterId);
    }

    public void refreshAllAnimals(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        syncAnimalsFromFirebase(onSuccess, onFailure);
    }

    public void refreshAnimalById(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        syncAnimalByIdFromFirebase(id, onSuccess, onFailure);
    }

    public void refreshAnimalsByShelter(String shelterId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        syncAnimalsByShelterFromFirebase(shelterId, onSuccess, onFailure);
    }

    // ─── Obtener animales por especie ───────────────────────────────────
    public LiveData<List<AnimalEntity>> getAnimalsBySpecies(Species species) {
        syncAnimalsFromFirebase();
        return animalDao.getAnimalsBySpecies(species.name());
    }

    // ─── Obtener animales por característica ────────────────────────────
    public LiveData<List<AnimalEntity>> getAnimalsByCharacteristic(Characteristic characteristic) {
        syncAnimalsFromFirebase();
        return animalDao.getAnimalsByCharacteristic(characteristic.name());
    }

    // ─── Insertar/Actualizar animal ────────────────────────────────────────────────
    //firebase gestiona de la misma manera un update y un insert
    public void updateAnimal(AnimalEntity animal) {
        updateAnimal(animal, unused -> {}, error -> {});
    }

    public void updateAnimal(AnimalEntity animal, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        executor.execute(() -> {
            AnimalEntity previousAnimal = animalDao.getAnimalByIdSync(animal.getId());
            try {
                AnimalEntity localAnimal = copyAnimalPhotos(animal);
                saveAnimal(localAnimal, previousAnimal, onSuccess, onFailure);
            } catch (Exception exception) {
                onFailure.onFailure(exception);
            }
        });
    }

    private void saveAnimal(
            AnimalEntity animal,
            AnimalEntity previousAnimal,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure
    ) {
        firestore.collection(COLLECTION)
                .document(animal.getId())
                .set(animal)
                .addOnSuccessListener(unused -> {
                        // Si Firebase va bien, guarda en Room
                        executor.execute(() -> {
                            animalDao.insertAnimal(animal);
                            deleteReplacedAnimalPhotos(previousAnimal, animal);
                        });
                        onSuccess.onSuccess(unused);
                })
                .addOnFailureListener(onFailure);
    }

    private AnimalEntity copyAnimalPhotos(AnimalEntity animal) throws Exception {
        String folderName = "animals/" + animal.getId();
        String localMainPhoto = photoStorage.copyPhotoIfNeeded(
                animal.getMainPhoto(),
                folderName,
                "main.jpg"
        );
        List<String> localPhotos = new ArrayList<>();
        List<String> photos = animal.getPhotos() == null ? new ArrayList<>() : animal.getPhotos();

        for (int index = 0; index < photos.size(); index++) {
            localPhotos.add(photoStorage.copyPhotoIfNeeded(
                    photos.get(index),
                    folderName,
                    "gallery_" + index + ".jpg"
            ));
        }

        return new AnimalEntity(
                animal.getId(),
                animal.getName(),
                animal.isSex(),
                localMainPhoto,
                localPhotos,
                animal.getBirthDate(),
                animal.getDescription(),
                animal.getSpecies(),
                animal.getCharacteristics(),
                animal.isForAdoption(),
                animal.getShelterId()
        );
    }

    // ─── Eliminar animal ────────────────────────────────────────────────
    public void deleteAnimal(AnimalEntity animal) {
        firestore.collection(COLLECTION)
                .document(animal.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        executor.execute(() -> {
                            AnimalEntity storedAnimal = animalDao.getAnimalByIdSync(animal.getId());
                            animalDao.deleteAnimal(animal);
                            photoStorage.deletePhotos(collectAnimalPhotoUris(
                                    storedAnimal == null ? animal : storedAnimal
                            ));
                        })
                );
    }

    private void deleteReplacedAnimalPhotos(AnimalEntity previousAnimal, AnimalEntity currentAnimal) {
        if (previousAnimal == null) return;
        photoStorage.deletePhotosNotIn(
                collectAnimalPhotoUris(previousAnimal),
                collectAnimalPhotoUris(currentAnimal)
        );
    }

    private List<String> collectAnimalPhotoUris(AnimalEntity animal) {
        List<String> photoUris = new ArrayList<>();
        if (animal == null) return photoUris;
        if (animal.getMainPhoto() != null && !animal.getMainPhoto().isBlank()) {
            photoUris.add(animal.getMainPhoto());
        }
        if (animal.getPhotos() != null) {
            for (String photo : animal.getPhotos()) {
                if (photo != null && !photo.isBlank()) {
                    photoUris.add(photo);
                }
            }
        }
        return photoUris;
    }

    // ─── Sincronización desde Firebase ──────────────────────────────────
    private void syncAnimalsFromFirebase() {
        syncAnimalsFromFirebase(unused -> {}, error -> {});
    }

    private void syncAnimalsFromFirebase(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection(COLLECTION)
                .get(Source.SERVER)
                .addOnSuccessListener(querySnapshot -> {
                    List<AnimalEntity> animals = querySnapshot.toObjects(AnimalEntity.class);
                    executor.execute(() -> {
                        List<String> animalIds = new ArrayList<>();
                        for (AnimalEntity animal : animals) {
                            animalDao.insertAnimal(animal);
                            animalIds.add(animal.getId());
                        }
                        if (animalIds.isEmpty()) {
                            animalDao.deleteAllAnimals();
                        } else {
                            animalDao.deleteAnimalsNotIn(animalIds);
                        }
                        onSuccess.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailure);
    }

    private void syncAnimalByIdFromFirebase(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection(COLLECTION)
                .document(id)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    AnimalEntity animal = documentSnapshot.toObject(AnimalEntity.class);
                    if (animal == null) {
                        onSuccess.onSuccess(null);
                        return;
                    }
                    executor.execute(() -> {
                        animalDao.insertAnimal(animal);
                        onSuccess.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailure);
    }

    private void syncAnimalsByShelterFromFirebase(String shelterId) {
        syncAnimalsByShelterFromFirebase(shelterId, unused -> {}, error -> {});
    }

    private void syncAnimalsByShelterFromFirebase(String shelterId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection(COLLECTION)
                .whereEqualTo("shelterId", shelterId)
                .get(Source.SERVER)
                .addOnSuccessListener(querySnapshot -> {
                    List<AnimalEntity> animals = querySnapshot.toObjects(AnimalEntity.class);
                    executor.execute(() -> {
                        List<String> animalIds = new ArrayList<>();
                        for (AnimalEntity animal : animals) {
                            animalDao.insertAnimal(animal);
                            animalIds.add(animal.getId());
                        }
                        if (animalIds.isEmpty()) {
                            animalDao.deleteAnimalsByShelter(shelterId);
                        } else {
                            animalDao.deleteAnimalsByShelterNotIn(shelterId, animalIds);
                        }
                        onSuccess.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailure);
    }
}
