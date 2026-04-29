package com.example.adopciontfg.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.adopciontfg.model.Characteristic;
import com.example.adopciontfg.model.Species;

import java.util.List;

@Entity(tableName = "animals")
public class AnimalEntity {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private boolean sex; // true = Female, false = Male
    private String mainPhoto;              // URL Firebase Storage
    private List<String> photos;           // URLs Firebase Storage (TypeConverter)
    private long birthDate;                // timestamp
    private String description;
    private Species species;               // Enum (TypeConverter)
    private List<Characteristic> characteristics; // Enum (TypeConverter)

    @NonNull
    private String shelterId;             // FK a ShelterEntity

    // Constructor
    public AnimalEntity(@NonNull String id, String name, boolean sex,
                        String mainPhoto, List<String> photos, long birthDate,
                        String description, Species species,
                        List<Characteristic> characteristics,
                        @NonNull String shelterId) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.mainPhoto = mainPhoto;
        this.photos = photos;
        this.birthDate = birthDate;
        this.description = description;
        this.species = species;
        this.characteristics = characteristics;
        this.shelterId = shelterId;
    }

    // Getters y Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isSex() { return sex; }
    public void setSex(boolean sex) { this.sex = sex; }

    public String getMainPhoto() { return mainPhoto; }
    public void setMainPhoto(String mainPhoto) { this.mainPhoto = mainPhoto; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public long getBirthDate() { return birthDate; }
    public void setBirthDate(long birthDate) { this.birthDate = birthDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Species getSpecies() { return species; }
    public void setSpecies(Species species) { this.species = species; }

    public List<Characteristic> getCharacteristics() { return characteristics; }
    public void setCharacteristics(List<Characteristic> characteristics) { this.characteristics = characteristics; }

    @NonNull
    public String getShelterId() { return shelterId; }
    public void setShelterId(@NonNull String shelterId) { this.shelterId = shelterId; }
}