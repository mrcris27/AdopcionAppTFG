package com.example.adopciontfg.app.ui.screens.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.state.DataRefreshError
import com.example.adopciontfg.app.ui.state.awaitDataRefresh
import com.example.adopciontfg.app.ui.state.toDataRefreshError
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.repository.ShelterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserHomeUiState(
    val query: String = "",
    val selectedTab: Int = 0,
    val tabs: List<Int> = listOf(R.string.list, R.string.map),
    val isLoadingShelters: Boolean = true,
    val isRefreshing: Boolean = false,
    val refreshError: DataRefreshError? = null,
    val shelters: List<ShelterEntity> = emptyList(),
    val filteredShelters: List<ShelterEntity> = emptyList()
)

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val shelterRepository: ShelterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState: StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shelterRepository.getAllShelters().asFlow().collect { loaded ->
                _uiState.update { state ->
                    val filtered = loaded.filter { shelter ->
                        state.query.isBlank() ||
                            shelter.name.orEmpty().contains(state.query, ignoreCase = true)
                    }
                    state.copy(
                        isLoadingShelters = false,
                        shelters = loaded,
                        filteredShelters = filtered
                    )
                }
            }
        }
        refreshShelters(showRefreshIndicator = false)
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                filteredShelters = state.shelters.filter { shelter ->
                    query.isBlank() || shelter.name.orEmpty().contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun refreshShelters() {
        refreshShelters(showRefreshIndicator = true)
    }

    private fun refreshShelters(showRefreshIndicator: Boolean) {
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = showRefreshIndicator, refreshError = null) }
            try {
                awaitDataRefresh { onSuccess, onFailure ->
                    shelterRepository.refreshAllShelters(
                        { onSuccess() },
                        { exception -> onFailure(exception) },
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(refreshError = exception.toDataRefreshError()) }
            } finally {
                if (showRefreshIndicator) {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun dismissRefreshError() {
        _uiState.update { it.copy(refreshError = null) }
    }
}
