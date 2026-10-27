package com.bryan.directorio.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bryan.directorio.DirectorioApp
import com.bryan.directorio.data.repository.User
import com.bryan.directorio.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lee los favoritos SOLO de Room, por eso funciona sin conexión. */
class FavoritesViewModel(private val repository: UserRepository) : ViewModel() {

    /** null = todavía cargando desde la base de datos. */
    val favorites: StateFlow<List<User>?> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun remove(user: User) {
        viewModelScope.launch { repository.toggleFavorite(user, isFavorite = true) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as DirectorioApp
                FavoritesViewModel(app.repository)
            }
        }
    }
}
