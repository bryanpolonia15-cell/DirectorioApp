package com.bryan.directorio.util

/** Los estados posibles de una pantalla que carga datos. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: AppError) : UiState<Nothing>
}

/** Tipos de error que la interfaz sabe mostrar. */
sealed class AppError(val message: String) {
    data object NoInternet : AppError("No hay conexión a Internet. Revisa tu Wi-Fi o datos móviles.")
    data object Timeout : AppError("El servidor tardó demasiado en responder. Intenta de nuevo.")
    class Server(code: Int) : AppError("Error del servidor (código $code). Intenta más tarde.")
    data object NotFound : AppError("El usuario no existe.")
    class Unknown(detail: String?) : AppError("Ocurrió un error inesperado${detail?.let { ": $it" } ?: ""}.")
}
