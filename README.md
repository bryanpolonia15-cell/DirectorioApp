# Directorio – App que consume un servicio web

App Android (Kotlin + Jetpack Compose) que consume la API pública [JSONPlaceholder](https://jsonplaceholder.typicode.com).

**Autor:** Bryan Andrés Polonia Pérez (1-19-1531) · UTESA · Programación de Dispositivos Móviles

## Funciones
- Lista de usuarios (`GET /users`) con nombre, correo y empresa.
- Detalle del usuario con sus publicaciones (`GET /users/{id}` y `GET /posts?userId={id}`).
- Favoritos guardados en el teléfono con **Room**; se ven sin conexión.
- Estados de interfaz: carga, éxito, error y sin Internet (con reintento automático al volver la conexión).

## Tecnologías
| Capa | Librería |
|---|---|
| UI | Jetpack Compose + Material 3, Navigation Compose |
| Presentación | ViewModel + StateFlow (MVVM) |
| Red | Retrofit 2 + Gson + OkHttp |
| Persistencia | Room (SQLite) |
| Asincronía | Corrutinas de Kotlin + Flow |

## Arquitectura (MVVM por capas)
```
ui/          → pantallas (Compose) + ViewModels
data/repository → UserRepository: decide API o Room, convierte errores
data/remote  → ApiService (Retrofit) + DTOs
data/local   → AppDatabase, FavoriteDao, FavoriteUserEntity (Room)
util/        → UiState, AppError, NetworkMonitor
```

## Cómo ejecutar
1. Abrir la carpeta en Android Studio (Ladybug o más reciente).
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador o teléfono con Android 7.0 (API 24) o superior.

Para probar el modo sin conexión: activar el modo avión y reabrir la app o tocar *Reintentar*.
