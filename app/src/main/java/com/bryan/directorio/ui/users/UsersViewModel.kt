package com.bryan.directorio.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bryan.directorio.DirectorioApp
import com.bryan.directorio.data.repository.User
import com.bryan.directorio.data.repository.UserRepository
import com.bryan.directorio.data.repository.asAppError
import com.bryan.directorio.util.NetworkMonitor
import com.bryan.directorio.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: UserRepository,
    network: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val state: StateFlow<UiState<List<User>>> = _state.asStateFlow()

    /** IDs favoritos guardados en Room (para la estrella de cada fila). */
    val favoriteIds: StateFlow<Set<Int>> = repository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val isOnline: StateFlow<Boolean> = network.isOnlineFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    init {
        // El ViewModel se crea una sola vez (sobrevive a la rotación),
        // por eso la carga inicial va aquí y no en onCreate/onResume.
        loadUsers()

        // Si vuelve la conexión y la pantalla estaba en error, recarga sola.
        viewModelScope.launch {
            network.isOnlineFlow.drop(1).filter { it }.collect {
                if (_state.value is UiState.Error) loadUsers()
            }
        }
    }

    fun loadUsers() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repository.getUsers()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.asAppError()) }
        }
    }

    fun toggleFavorite(user: User) {
        viewModelScope.launch {
            repository.toggleFavorite(user, isFavorite = user.id in favoriteIds.value)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as DirectorioApp
                UsersViewModel(app.repository, app.networkMonitor)
            }
        }
    }
}
