package com.bryan.directorio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bryan.directorio.ui.detail.DetailScreen
import com.bryan.directorio.ui.favorites.FavoritesScreen
import com.bryan.directorio.ui.theme.DirectorioTheme
import com.bryan.directorio.ui.users.UsersScreen

/**
 * Única Activity de la app. Las pantallas son composables y se navega con Navigation Compose.
 * Los logs del ciclo de vida permiten ver en Logcat cuándo ocurre cada método.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: se crea la interfaz; el ViewModel inicia la carga (solo la primera vez)")
        enableEdgeToEdge()
        setContent {
            DirectorioTheme { AppNavigation() }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: la UI vuelve a recolectar los StateFlow")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: la pantalla está en primer plano")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: la UI deja de recolectar (ahorra batería); la petición en curso sigue en el ViewModel")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: isChangingConfigurations=$isChangingConfigurations")
    }

    companion object { private const val TAG = "Ciclo de vida" }
}

private object Routes {
    const val USERS = "users"
    const val DETAIL = "detail/{userId}"
    const val FAVORITES = "favorites"
    fun detail(id: Int) = "detail/$id"
}

@androidx.compose.runtime.Composable
private fun AppNavigation() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.USERS) {
        composable(Routes.USERS) {
            UsersScreen(
                onUserClick = { nav.navigate(Routes.detail(it)) },
                onFavoritesClick = { nav.navigate(Routes.FAVORITES) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) {
            DetailScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onBack = { nav.popBackStack() },
                onUserClick = { nav.navigate(Routes.detail(it)) }
            )
        }
    }
}
