package com.example.adopciontfg.data

import android.content.Context
import com.example.adopciontfg.data.local.AppDatabase
import java.util.concurrent.Executors

/**
 * Inserta [sampleShelters] y [sampleAnimals] en Room cuando la base de datos está vacía.
 * Solo se ejecuta en builds de depuración para facilitar las pruebas sin Firebase.
 */
object DatabaseSeeder {

    private val executor = Executors.newSingleThreadExecutor()

    fun seedIfEmpty(context: Context) {
        executor.execute {
            val db = AppDatabase.getInstance(context)
            val shelterDao = db.shelterDao()
            val animalDao = db.animalDao()

            if (shelterDao.getShelterCount() == 0) {
                sampleShelters.forEach { shelterDao.insertShelter(it) }
            }
            if (animalDao.getAnimalCount() == 0) {
                sampleAnimals.forEach { animalDao.insertAnimal(it) }
            }
        }
    }
}
