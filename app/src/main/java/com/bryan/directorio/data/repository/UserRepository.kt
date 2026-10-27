package com.bryan.directorio.data.repository

import com.bryan.directorio.data.local.FavoriteDao
import com.bryan.directorio.data.remote.ApiService
import com.bryan.directorio.util.AppError
import com.bryan.directorio.util.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Única puerta de acceso a los datos.
 * Decide si los datos vienen de la API (Retrofit) o de la base local (Room)
 * y convierte cualquier excepción en un AppError, para que la app nunca se cierre.
 */
class UserRepository(
    private val api: ApiService,
    private val favoriteDao: FavoriteDao,
    private val network: NetworkMonitor
) {

    // ---------- Remoto ----------

    suspend fun getUsers(): Result<List<User>> = safeCall {
        api.getUsers().map { it.toUser() }
    }

    /** Usuario + publicaciones, pedidos en paralelo. */
    suspend fun getUserDetail(id: Int): Result<Pair<User, List<Post>>> = safeCall {
        coroutineScope {
            val user = async { api.getUser(id) }
            val posts = async { api.getPostsByUser(id) }
            user.await().toUser() to posts.await().map { it.toPost() }
        }
    }

    // ---------- Local (favoritos) ----------

    val favorites: Flow<List<User>> = favoriteDao.observeAll().map { list -> list.map { it.toUser() } }

    val favoriteIds: Flow<Set<Int>> = favoriteDao.observeIds().map { it.toSet() }

    suspend fun getFavorite(id: Int): User? = favoriteDao.getById(id)?.toUser()

    suspend fun toggleFavorite(user: User, isFavorite: Boolean) {
        if (isFavorite) favoriteDao.deleteById(user.id) else favoriteDao.insert(user.toEntity())
    }

    // ---------- Manejo de errores ----------

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
        if (!network.isOnline()) throw NoInternetException()
        Result.success(block())
    } catch (e: CancellationException) {
        throw e // no atrapar la cancelación de corrutinas
    } catch (e: Exception) {
        Result.failure(AppException(e.toAppError()))
    }

    private fun Exception.toAppError(): AppError = when (this) {
        is NoInternetException -> AppError.NoInternet
        is SocketTimeoutException -> AppError.Timeout
        is IOException -> if (network.isOnline()) AppError.Unknown("fallo de red") else AppError.NoInternet
        is HttpException -> if (code() == 404) AppError.NotFound else AppError.Server(code())
        else -> AppError.Unknown(message)
    }
}

private class NoInternetException : IOException()

/** Excepción que transporta un AppError hasta el ViewModel. */
class AppException(val error: AppError) : Exception(error.message)

fun Throwable.asAppError(): AppError = (this as? AppException)?.error ?: AppError.Unknown(message)
