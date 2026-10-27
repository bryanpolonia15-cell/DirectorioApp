package com.bryan.directorio

import android.app.Application
import com.bryan.directorio.data.local.AppDatabase
import com.bryan.directorio.data.remote.ApiService
import com.bryan.directorio.data.repository.UserRepository
import com.bryan.directorio.util.NetworkMonitor

/**
 * Clase Application: se crea una sola vez al iniciar el proceso.
 * Funciona como contenedor de dependencias (inyección manual).
 */
class DirectorioApp : Application() {

    lateinit var networkMonitor: NetworkMonitor
        private set
    lateinit var repository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkMonitor(this)
        repository = UserRepository(
            api = ApiService.create(),
            favoriteDao = AppDatabase.getInstance(this).favoriteDao(),
            network = networkMonitor
        )
    }
}
