package com.bryan.directorio.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bryan.directorio.DirectorioApp
import com.bryan.directorio.data.repository.Post
import com.bryan.directorio.data.repository.User
import com.bryan.directorio.data.repository.UserRepository
import com.bryan.directorio.data.repository.asAppError
import com.bryan.directorio.util.AppError
import com.bryan.directorio.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Datos de la pantalla de detalle. */
data class DetailData(
    val user: User,
    val posts: List<Post>,
    /** true si se muestra la copia guardada en favoritos porque no hubo conexión. */
    val fromCache: Boolean = false,
    val postsError: AppError? = null
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: UserRepository
) : ViewModel() {

    /** El id llega como argumento de navegación ("detail/{userId}"). */
    private val userId: Int = checkNotNull(savedStateHandle["userId"])

    private val _state = MutableStateFlow<UiState<DetailData>>(UiState.Loading)
    val state: StateFlow<UiState<DetailData>> = _state.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.favoriteIds
        .map { userId in it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repository.getUserDetail(userId)
                .onSuccess { (user, posts) -> _state.value = UiState.Success(DetailData(user, posts)) }
                .onFailure { e ->
                    val error = e.asAppError()
                    // Sin Internet: si el usuario es favorito, se muestran sus datos guardados.
                    val cached = repository.getFavorite(userId)
                    _state.value = if (cached != null) {
                        UiState.Success(DetailData(cached, emptyList(), fromCache = true, postsError = error))
                    } else {
                        UiState.Error(error)
                    }
                }
        }
    }

    fun toggleFavorite() {
        val data = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { repository.toggleFavorite(data.user, isFavorite.value) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as DirectorioApp
                DetailViewModel(createSavedStateHandle(), app.repository)
            }
        }
    }
}
