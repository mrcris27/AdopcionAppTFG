package com.example.adopciontfg.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.adopciontfg.data.local.AppDatabase;
import com.example.adopciontfg.data.local.dao.AnimalDao;
import com.example.adopciontfg.data.local.entity.AnimalEntity;
import com.example.adopciontfg.model.Characteristic;
import com.example.adopciontfg.model.Species;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimalRepository {

    private final AnimalDao animalDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;

    private static final String COLLECTION = "animals";

    public AnimalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.animalDao = db.animalDao();
        this.firestore = FirebaseFirestore.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ─── Obtener todos los animales ─────────────────────────────────────
    public LiveData<List<AnimalEntity>> getAllAnimals() {
        syncAnimalsFromFirebase();
        return animalDao.getAllAnimals();
    }

    // ─── Obtener animales por protectora ────────────────────────────────
    public LiveData<List<AnimalEntity>> getAnimalsByShelter(String shelterId) {
        syncAnimalsByShelterFromFirebase(shelterId);
        return animalDao.getAnimalsByShelter(shelterId);
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
        // Guarda en Firebase
        firestore.collection(COLLECTION)
                .document(animal.getId())
                .set(animal)
                .addOnSuccessListener(unused ->
                        // Si Firebase va bien, guarda en Room
                        executor.execute(() -> animalDao.insertAnimal(animal))
                );
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
        firestore.collection(COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<AnimalEntity> animals = querySnapshot.toObjects(AnimalEntity.class);
                    executor.execute(() -> {
                        for (AnimalEntity animal : animals) {
                            animalDao.insertAnimal(animal);
                        }
                    });
                });
    }

    private void syncAnimalsByShelterFromFirebase(String shelterId) {
        firestore.collection(COLLECTION)
                .whereEqualTo("shelterId", shelterId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<AnimalEntity> animals = querySnapshot.toObjects(AnimalEntity.class);
                    executor.execute(() -> {
                        for (AnimalEntity animal : animals) {
                            animalDao.insertAnimal(animal);
                        }
                    });
                });
    }
}
