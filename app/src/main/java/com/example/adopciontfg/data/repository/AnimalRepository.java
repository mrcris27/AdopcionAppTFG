package com.example.adopciontfg.data.repository;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.dao.AnimalDao;
import com.example.adopciontfg.data.local.entity.AnimalEntity;
import com.example.adopciontfg.model.Characteristic;
import com.example.adopciontfg.model.Species;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimalRepository {

    private final AnimalDao animalDao;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final ExecutorService executor;

    private static final String COLLECTION = "animals";

    public AnimalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.animalDao = db.animalDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
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
        uploadAnimalPhotos(
                animal,
                uploadedAnimal -> saveAnimal(uploadedAnimal, onSuccess, onFailure),
                onFailure
        );
    }

    private void saveAnimal(AnimalEntity animal, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection(COLLECTION)
                .document(animal.getId())
                .set(animal)
                .addOnSuccessListener(unused -> {
                        // Si Firebase va bien, guarda en Room
                        executor.execute(() -> animalDao.insertAnimal(animal));
                        onSuccess.onSuccess(unused);
                })
                .addOnFailureListener(onFailure);
    }

    private void uploadAnimalPhotos(AnimalEntity animal, OnSuccessListener<AnimalEntity> onSuccess, OnFailureListener onFailure) {
        uploadPhotoIfNeeded(
                animal.getMainPhoto(),
                animal.getId(),
                "main.jpg",
                uploadedMainPhoto -> {
                    List<String> photos = animal.getPhotos() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(animal.getPhotos());
                    uploadGalleryPhotos(
                            animal,
                            uploadedMainPhoto,
                            photos,
                            0,
                            new ArrayList<>(),
                            onSuccess,
                            onFailure
                    );
                },
                onFailure
        );
    }

    private void uploadGalleryPhotos(
            AnimalEntity animal,
            String uploadedMainPhoto,
            List<String> photos,
            int index,
            List<String> uploadedPhotos,
            OnSuccessListener<AnimalEntity> onSuccess,
            OnFailureListener onFailure
    ) {
        if (index >= photos.size()) {
            onSuccess.onSuccess(
                    new AnimalEntity(
                            animal.getId(),
                            animal.getName(),
                            animal.isSex(),
                            uploadedMainPhoto,
                            uploadedPhotos,
                            animal.getBirthDate(),
                            animal.getDescription(),
                            animal.getSpecies(),
                            animal.getCharacteristics(),
                            animal.isForAdoption(),
                            animal.getShelterId()
                    )
            );
            return;
        }

        uploadPhotoIfNeeded(
                photos.get(index),
                animal.getId(),
                "gallery_" + index + ".jpg",
                uploadedPhoto -> {
                    uploadedPhotos.add(uploadedPhoto);
                    uploadGalleryPhotos(
                            animal,
                            uploadedMainPhoto,
                            photos,
                            index + 1,
                            uploadedPhotos,
                            onSuccess,
                            onFailure
                    );
                },
                onFailure
        );
    }

    private void uploadPhotoIfNeeded(
            String photoUri,
            String animalId,
            String fileName,
            OnSuccessListener<String> onSuccess,
            OnFailureListener onFailure
    ) {
        if (!isLocalPhotoUri(photoUri)) {
            onSuccess.onSuccess(photoUri == null ? "" : photoUri);
            return;
        }

        Uri uri = Uri.parse(photoUri);
        StorageReference ref = storage.getReference("animal_photos/" + animalId + "/" + fileName);
        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        if (exception != null) {
                            throw exception;
                        }
                        throw new IllegalStateException("No se pudo subir la foto del animal");
                    }
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> onSuccess.onSuccess(downloadUri.toString()))
                .addOnFailureListener(onFailure);
    }

    private boolean isLocalPhotoUri(String photoUri) {
        if (photoUri == null || photoUri.isBlank()) return false;

        String scheme = Uri.parse(photoUri).getScheme();
        return "content".equalsIgnoreCase(scheme) || "file".equalsIgnoreCase(scheme);
    }

    // ─── Eliminar animal ────────────────────────────────────────────────
    public void deleteAnimal(AnimalEntity animal) {
        firestore.collection(COLLECTION)
                .document(animal.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        executor.execute(() -> animalDao.deleteAnimal(animal))
                );
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
