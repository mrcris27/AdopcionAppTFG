package com.example.adopciontfg.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shelters")
public class ShelterEntity {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String cif;
    private String profilePicture; // URL de Firebase Storage
    private String email;
    private String address;
    private String phone;
    /** Enlace al Google Form de solicitudes de adopción de esta protectora */
    private String adoptionFormUrl;

    @Ignore
    public ShelterEntity() {
        this.id = "";
        this.name = "";
        this.cif = "";
        this.profilePicture = "";
        this.email = "";
        this.address = "";
        this.phone = "";
        this.adoptionFormUrl = "";
    }

    @Ignore
    public ShelterEntity(@NonNull String id, String name, String cif,
                         String profilePicture, String email,
                         String address, String phone) {
        this(id, name, cif, profilePicture, email, address, phone, "");
    }

    public ShelterEntity(@NonNull String id, String name, String cif,
                         String profilePicture, String email,
                         String address, String phone, String adoptionFormUrl) {
        this.id = id;
        this.name = name;
        this.cif = cif;
        this.profilePicture = profilePicture;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.adoptionFormUrl = adoptionFormUrl != null ? adoptionFormUrl : "";
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAdoptionFormUrl() { return adoptionFormUrl; }
    public void setAdoptionFormUrl(String adoptionFormUrl) {
        this.adoptionFormUrl = adoptionFormUrl != null ? adoptionFormUrl : "";
    }
}
