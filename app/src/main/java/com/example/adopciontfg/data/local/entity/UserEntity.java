package com.example.adopciontfg.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String surname;
    private String profilePicture;
    private String email;
    private String biography;

    // Constructor


    @Ignore
    public UserEntity() {
        this.id = "";
        this.name = "";
        this.surname = "";
        this.profilePicture = "";
        this.email = "";
        this.biography = "";
    }

    public UserEntity(@NonNull String id, String name, String surname,
                      String profilePicture, String email, String biography) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.profilePicture = profilePicture;
        this.email = email;
        this.biography = biography;
    }

    // Getters y Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}
