package com.example.adopciontfg.app.ui.navigation

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.local.entity.UserEntity
import com.example.adopciontfg.data.remote.FirebaseService
import com.example.adopciontfg.data.remote.RoleCallback
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.data.repository.UserRepository
import com.example.adopciontfg.data.settings.SettingsRepositoryImpl
import com.example.adopciontfg.data.settings.settingsDataStore
import com.example.adopciontfg.domain.settings.ShelterSettingsData
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// los dos puntos significan que hereda de una clase
class AuthViewModel (application: Application) : AndroidViewModel(application) {

    private val firebase = FirebaseService.getInstance()
    private val userRepos = UserRepository(application)
    private val shelterRepos = ShelterRepository(application)
    private val settingsRepository = SettingsRepositoryImpl(application.settingsDataStore, application)

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
            2. Creamos UserEntity con UID + foto local + resto de datos
            3. Guardamos UserEntity en Firestore y Room
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
                // La foto se copia al almacenamiento interno desde el repositorio.
                userRepos.saveUser(UserEntity(uid, name, surname, profilePic, email, bio))
                _authState.value = AuthState.Success("user")
            },
            { exception ->
                _authState.value = AuthState.Error(getRegisterErrorMessage(exception))
            }
        )
    }


    fun registerShelter(
        name: String,
        cif: String,
        phoneName: String,
        address: String,
        profilePic: String,
        email: String,
        adoptionFormUrl: String,
        password: String
    ) {
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
                // La foto se copia al almacenamiento interno desde el repositorio.
                shelterRepos.saveShelter(
                    ShelterEntity(
                        uid,
                        name,
                        cif,
                        profilePic,
                        email,
                        address,
                        phoneName,
                        adoptionFormUrl
                    )
                )
                saveRegisteredShelterSettings(
                    name = name,
                    cif = cif,
                    phone = phoneName,
                    address = address,
                    profilePic = profilePic,
                    email = email,
                    adoptionFormUrl = adoptionFormUrl
                )
            },
            { exception ->
                _authState.value = AuthState.Error(getRegisterErrorMessage(exception))
            }
        )
    }

    private fun saveRegisteredShelterSettings(
        name: String,
        cif: String,
        phone: String,
        address: String,
        profilePic: String,
        email: String,
        adoptionFormUrl: String
    ) {
        viewModelScope.launch {
            try {
                settingsRepository.saveShelterSettings(
                    ShelterSettingsData(
                        shelterName = name,
                        email = email,
                        phone = phone,
                        address = address,
                        cif = cif,
                        profilePhotoUri = profilePic,
                        adoptionFormUrl = adoptionFormUrl,
                    )
                )
            } catch (_: Exception) {
                // El registro ya está creado en Firebase; un fallo local no debe bloquear el acceso.
            } finally {
                _authState.value = AuthState.Success("shelter")
            }
        }
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
            "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.registration_email_already_in_use_error)
            "ERROR_INVALID_EMAIL" -> getString(R.string.login_invalid_email_error)
            "ERROR_WEAK_PASSWORD" -> getString(R.string.registration_password_min_length_error)
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