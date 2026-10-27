package com.bryan.directorio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bryan.directorio.data.repository.User
import com.bryan.directorio.util.AppError

/** Estado de CARGA. */
@Composable
fun LoadingView(text: String = "Cargando…") {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Estado de ERROR / SIN CONEXIÓN, con botón para reintentar. */
@Composable
fun ErrorView(
    error: AppError,
    onRetry: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    val offline = error is AppError.NoInternet
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (offline) Icons.Filled.WifiOff else Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            if (offline) "Sin conexión" else "Algo salió mal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(error.message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
        if (secondaryLabel != null && onSecondary != null) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onSecondary) { Text(secondaryLabel) }
        }
    }
}

/** Franja que avisa que no hay Internet mientras se navega. */
@Composable
fun OfflineBanner(text: String = "Sin conexión a Internet") {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
        Spacer(Modifier.width(8.dp))
        Text(text, color = MaterialTheme.colorScheme.onError, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Círculo con las iniciales del usuario. */
@Composable
fun Avatar(name: String, size: Int = 44) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(
        Modifier.size(size.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
    }
}

/** Fila de la lista: nombre, correo y empresa. */
@Composable
fun UserCard(user: User, isFavorite: Boolean, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(user.name)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(user.email, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    user.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
