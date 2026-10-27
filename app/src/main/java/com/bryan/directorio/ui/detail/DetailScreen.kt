package com.bryan.directorio.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bryan.directorio.data.repository.Post
import com.bryan.directorio.data.repository.User
import com.bryan.directorio.ui.components.Avatar
import com.bryan.directorio.ui.components.ErrorView
import com.bryan.directorio.ui.components.LoadingView
import com.bryan.directorio.ui.components.OfflineBanner
import com.bryan.directorio.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state is UiState.Success) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Favorito"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> LoadingView("Cargando datos y publicaciones…")
                is UiState.Error -> ErrorView(s.error, onRetry = viewModel::load)
                is UiState.Success -> DetailContent(s.data, onRetry = viewModel::load)
            }
        }
    }
}

@Composable
private fun DetailContent(data: DetailData, onRetry: () -> Unit) {
    if (data.fromCache) OfflineBanner("Sin conexión: mostrando datos guardados")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { UserHeader(data.user) }

        item {
            Text(
                "Publicaciones (${data.posts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (data.postsError != null) {
            item {
                Column {
                    Text("Las publicaciones necesitan conexión a Internet.", color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetry) { Text("Reintentar") }
                }
            }
        }

        items(data.posts, key = { it.id }) { PostCard(it) }
    }
}

@Composable
private fun UserHeader(user: User) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(user.name, size = 56)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("@${user.username}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            InfoRow(Icons.Filled.Email, user.email)
            InfoRow(Icons.Filled.Phone, user.phone)
            InfoRow(Icons.Filled.Public, user.website)
            InfoRow(Icons.Filled.LocationCity, user.city)
            InfoRow(Icons.Filled.Business, user.companyName)
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    if (text.isBlank()) return
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PostCard(post: Post) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(
                post.title.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(post.body, style = MaterialTheme.typography.bodySmall)
        }
    }
}
