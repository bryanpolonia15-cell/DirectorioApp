package com.bryan.directorio.ui.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bryan.directorio.ui.components.ErrorView
import com.bryan.directorio.ui.components.LoadingView
import com.bryan.directorio.ui.components.OfflineBanner
import com.bryan.directorio.ui.components.UserCard
import com.bryan.directorio.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onUserClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: UsersViewModel = viewModel(factory = UsersViewModel.Factory)
) {
    // collectAsStateWithLifecycle: solo recolecta mientras la pantalla está
    // al menos en STARTED (se pausa en onStop y se reanuda en onStart).
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Directorio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = viewModel::loadUsers) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Star, contentDescription = "Favoritos")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (!isOnline && state is UiState.Success) OfflineBanner()

            when (val s = state) {
                is UiState.Loading -> LoadingView("Cargando usuarios…")

                is UiState.Error -> ErrorView(
                    error = s.error,
                    onRetry = viewModel::loadUsers,
                    secondaryLabel = "Ver favoritos guardados",
                    onSecondary = onFavoritesClick
                )

                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(s.data, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            isFavorite = user.id in favoriteIds,
                            onClick = { onUserClick(user.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(user) }
                        )
                    }
                }
            }
        }
    }
}
