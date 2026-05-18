package com.example.adopciontfg.data.remote;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseService {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    //volatile garantiza que se lea la variable siempre desde el disco para evitar posibles conflictos
    //derivados de que se modifique desde diferentes hilos
    private static volatile FirebaseService instance;

    private FirebaseService() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseService getInstance() {
        if (instance == null) {
            synchronized (FirebaseService.class) {
                if (instance == null) {
                    instance = new FirebaseService();
                }
            }
        }
        return instance;
    }

    // ─── Registro ────────────────────────────────────────────────────────
    public void register(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        auth.createUserWithEmailAndPassword(email, password)
                //Esto lo que hace es crear dos listeners que gestionará lo que pasa en caso de error
                // desde donde se llame al método
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ─── Login ───────────────────────────────────────────────────────────
    public void login(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    // ─── Recupera el rol de la cuenta para determinar si es usuario o protectora ───

    public void getUserRole(String id, RoleCallback callback) {
        firestore.collection("users").document(id).get()
                .addOnSuccessListener(doc -> {
                    //Si se encuentra en la collection de users se devuelve usuario directamente
                    if (doc.exists()) {
                        callback.onRole("user");

                    //Sino comprueba que esté en la collection de shelters y devuelve ese rol
                    // Si tampoco lo encuentra en esa colección devuelve error
                    } else {
                        firestore.collection("shelters").document(id).get()
                                .addOnSuccessListener(shelterDoc -> {
                                    if (shelterDoc.exists()) {
                                        callback.onRole("shelter");
                                    } else {
                                        callback.onError("Usuario no encontrado");
                                    }
                                })
                                .addOnFailureListener(e -> callback.onError(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));

    }




    // ─── Logout ──────────────────────────────────────────────────────────
    public void logout() {
        auth.signOut();
    }

    // ─── Obtener usuario actual ───────────────────────────────────────────
    // con getCurrentUser().getUid() sacamos el id del usuario
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // ─── Comprobar si hay sesión activa ───────────────────────────────────
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    // ─── Recuperar contraseña ─────────────────────────────────────────────
    //se le manda un email al usuario que quiere reestablecer su contraseña
    //todo el proceso lo gestiona firebase de manera automática
    //Se debe comprobar desde donde se llama a este método que el correo esté registrado con On succes / Failure
    public void resetPassword(String email, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }





}