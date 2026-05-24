package com.example.adopciontfg.app.ui.navigation

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.local.entity.UserEntity
import com.example.adopciontfg.data.remote.FirebaseService
import com.example.adopciontfg.data.remote.RoleCallback
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// los dos puntos significan que hereda de una clase
class AuthViewModel (application: Application) : AndroidViewModel(application) {

    private val app = getApplication<Application>()
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
                    _authState.value = AuthState.Error(app.getString(R.string.error_obteniendo_usuario))
                    return@login
                }
                firebase.getUserRole(uid, object : RoleCallback {
                    override fun onRole(role: String) {
                        //Shared preferences es como un mini diccionario persistente
                        getApplication<Application>()
                            .getSharedPreferences("auth", Context.MODE_PRIVATE)
                            .edit()
                            .putString("role", role)
                            .apply()
                        _authState.value = AuthState.Success(role)
                    }

                    override fun onError(message: String) {
                        _authState.value = AuthState.Error(message)
                    }
                })
            }, {
                exception ->
                _authState.value = AuthState.Error(getString(R.string.login_invalid_credentials_error))
            })
    }

    //Registro de usuarios

    /*
            Flujo de la función
            1. Registro en Firebase Auth → obtenemos el UID
            2. Subimos la foto a Storage → obtenemos la URL
            3. Creamos UserEntity con UID + URL + resto de datos
            4. Guardamos UserEntity en Firestore y Room
     */
    fun registerUser(name: String, surname: String, email: String, bio: String, profilePic: String, password: String) {
        _authState.value = AuthState.Loading

        //Se registra el usuario en Firebase Auth
        firebase.register(email, password,
            { authResult ->
                //Se obtiene el UID del usuario registrado
                //Si devuelve null muestra error y sale del método
                val uid = authResult.user?.uid ?: run {
                    _authState.value = AuthState.Error(getString(R.string.auth_generic_error))
                    return@register
                }
                // Si tenemos foto se sube la foto a Firebase Storage
                if (profilePic.isNotBlank()) {
                    firebase.uploadProfilePhoto(
                        getApplication(), uid, android.net.Uri.parse(profilePic),
                        { photoUrl ->
                            //Si se sube correctamente se crea el usuario en la base de datos con la foto vinculada
                            userRepos.saveUser(UserEntity(uid, name, surname, photoUrl, email, bio))
                            _authState.value = AuthState.Success("user")
                        },
                        //Si falla al subir la foto se crea el usuario sin la foto
                        {
                            userRepos.saveUser(UserEntity(uid, name, surname, "", email, bio))
                            _authState.value = AuthState.Success("user")
                        }
                    )
                } else {
                    //Si no hay foto se crea el usuario sin la foto
                    userRepos.saveUser(UserEntity(uid, name, surname, "", email, bio))
                    _authState.value = AuthState.Success("user")
                }
            },
            { exception ->
                _authState.value = AuthState.Error(getRegisterErrorMessage(exception))
            }
        )
    }


    fun registerShelter(name: String, cif : String, phoneName : String, address: String ,profilePic:String, email: String, password: String){
        _authState.value = AuthState.Loading

        //Se registra el usuario en Firebase Auth
        firebase.register(email, password,
            { authResult ->
                //Se obtiene el UID de la protectora registrada
                //Si devuelve null muestra error y sale del método
                val uid = authResult.user?.uid ?: run {
                    _authState.value = AuthState.Error(getString(R.string.auth_generic_error))
                    return@register
                }
                // Si tenemos foto se sube la foto a Firebase Storage
                if (profilePic.isNotBlank()) {
                    firebase.uploadProfilePhoto(
                        getApplication(), uid, android.net.Uri.parse(profilePic),
                        { photoUrl ->
                            //Si se sube correctamente se crea el usuario en la base de datos con la foto vinculada
                            shelterRepos.saveShelter(ShelterEntity(uid, name, cif, photoUrl, email,address, phoneName))
                            _authState.value = AuthState.Success("shelter")
                        },
                        //Si falla al subir la foto se crea el usuario sin la foto
                        {
                            shelterRepos.saveShelter(ShelterEntity(uid, name, cif, "", email,address, phoneName))
                            _authState.value = AuthState.Success("shelter")
                        }
                    )
                } else {
                    //Si no hay foto se crea el usuario sin la foto
                    shelterRepos.saveShelter(ShelterEntity(uid, name, cif, "", email,address, phoneName ))
                    _authState.value = AuthState.Success("shelter")
                }
            },
            { exception ->
                _authState.value = AuthState.Error(getRegisterErrorMessage(exception))
            }
        )
    }




    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        firebase.logout()
    }

    fun isLogged(): Boolean = firebase.isLoggedIn

    private fun getRegisterErrorMessage(exception: Exception): String {
        val errorMessage = exception.localizedMessage.orEmpty()
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.registro_email_already_in_use_error)
            "ERROR_INVALID_EMAIL" -> getString(R.string.login_invalid_email_error)
            "ERROR_WEAK_PASSWORD" -> getString(R.string.registro_password_min_length_error)
            else -> if (errorMessage.contains("CONFIGURATION_NOT_FOUND")) {
                getString(R.string.firebase_auth_configuration_error)
            } else {
                exception.localizedMessage ?: getString(R.string.auth_generic_error)
            }
        }
    }

    private fun getString(@StringRes resId: Int): String {
        return getApplication<Application>().getString(resId)
    }

}