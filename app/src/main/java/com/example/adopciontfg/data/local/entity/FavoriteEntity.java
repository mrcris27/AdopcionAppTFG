package com.example.adopciontfg.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "favorites",
        primaryKeys = {"userId", "animalId"},
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = AnimalEntity.class,
                        parentColumns = "id",
                        childColumns = "animalId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class FavoriteEntity {

    @NonNull
    private String userId;

    @NonNull
    private String animalId;

    public FavoriteEntity(@NonNull String userId, @NonNull String animalId) {
        this.userId = userId;
        this.animalId = animalId;
    }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getAnimalId() { return animalId; }
    public void setAnimalId(@NonNull String animalId) { this.animalId = animalId; }
}
