package com.example.adopciontfg.app.ui.navigation

import android.R
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.local.entity.UserEntity
import com.example.adopciontfg.data.remote.FirebaseService
import com.example.adopciontfg.data.remote.RoleCallback
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// los dos puntos significan que hereda de una clase
class AuthViewModel (application: Application) : AndroidViewModel(application) {

    private val firebase = FirebaseService.getInstance()
    private val userRepos = UserRepository(application)
    private val shelterRepos = ShelterRepository(application)

    // instancia de la sealed class AuthState
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    //contiene el valor que indica si se ha iniciado sesión o no, y es de solo lectura para la UI

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    //Esta clase obliga a tener los estados declarados dentro
    sealed class AuthState {
        //Estado inicial, no ha pasado nada
        object Idle : AuthState()

        //operación en curso
        object Loading : AuthState()

        //completada correctamente
        data class Success(val role: String) : AuthState()

        //Error, con el respectivo mensaje indicando que ha fallado
        data class Error(val message: String) : AuthState()
    }



    //Controla el inicio de sesión y determina si es usuario o protectora
    fun login(email: String, password: String){
        _authState.value = AuthState.Loading
        firebase.login(email, password,
            {
                //Almacena el ID, si es null manda mensaje de error y sale del método sin hacer nada más
                val uid = firebase.currentUser?.uid ?: run {
                    _authState.value = AuthState.Error("Error obteniendo usuario")
                    return@login
                }
                firebase.getUserRole(uid, object : RoleCallback {
                    override fun onRole(role: String) {
                        _authState.value = AuthState.Success(role)
                    }

                    override fun onError(message: String) {
                        _authState.value = AuthState.Error(message)
                    }
                })
            }, {
                exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
            })
    }

    //Registro de usuarios
    fun registerUser(name: String, surname : String, email: String, password: String){
        _authState.value = AuthState.Loading
        firebase.register(email, password,

            //Si se registra correctamente se crea el usuario en la base de datos
            {
                //Así se le da nombre al parámetro que viene
                    authResult ->
                authResult.user?.uid?.let { uid ->
                    //Se crea el usuario completo en la base de datos
                    userRepos.saveUser(UserEntity(uid, name, surname, "", email, ""))
                }
                _authState.value = AuthState.Success("user")
            },
            //Si falla se muestra el error
            { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
            })
    }


    fun registerShelter(name: String, cif : String, phoneName : String, address: String , email: String, password: String){
        _authState.value = AuthState.Loading
        firebase.register(email, password,

            //Si se registra correctamente se crea el usuario en la base de datos
            {
                //Así se le da nombre al parámetro que viene
                    authResult ->
                authResult.user?.uid?.let { uid ->
                    //Se crea el usuario completo en la base de datos
                    shelterRepos.saveShelter(
                        ShelterEntity(
                            uid,
                            name,
                            cif,
                            "",
                            email,
                            address,
                            phoneName
                        )
                    )

                }
                _authState.value = AuthState.Success("shelter")
            },
            //Si falla se muestra el error
            { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
            })
    }

    fun logout() {
        firebase.logout()
    }

    fun isLogged(): Boolean = firebase.isLoggedIn()

}