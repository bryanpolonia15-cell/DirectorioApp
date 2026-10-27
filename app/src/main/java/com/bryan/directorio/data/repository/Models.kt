package com.bryan.directorio.data.repository

import com.bryan.directorio.data.local.FavoriteUserEntity
import com.bryan.directorio.data.remote.PostDto
import com.bryan.directorio.data.remote.UserDto

/** Modelo que usa la interfaz (independiente de la API y de Room). */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val city: String,
    val companyName: String
)

data class Post(
    val id: Int,
    val title: String,
    val body: String
)

// ---- Conversores (mappers) entre capas ----

fun UserDto.toUser() = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone.orEmpty(),
    website = website.orEmpty(),
    city = address?.city.orEmpty(),
    companyName = company?.name.orEmpty()
)

fun PostDto.toPost() = Post(id = id, title = title, body = body)

fun User.toEntity() = FavoriteUserEntity(
    id = id, name = name, username = username, email = email,
    phone = phone, website = website, city = city, companyName = companyName
)

fun FavoriteUserEntity.toUser() = User(
    id = id, name = name, username = username, email = email,
    phone = phone, website = website, city = city, companyName = companyName
)
