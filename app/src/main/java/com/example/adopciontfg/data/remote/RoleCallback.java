package com.example.adopciontfg.data.remote;

//Esta interfaz se usa para devolver el rol del usuario o en su defecto un error
//es el equivalente a un onSuccessListener()
public interface RoleCallback {
    void onRole(String role);
    void onError(String message);
}
