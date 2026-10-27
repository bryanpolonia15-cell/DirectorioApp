package com.bryan.directorio.data.remote

/**
 * Modelos que representan el JSON de JSONPlaceholder.
 * Gson los llena automáticamente porque los nombres coinciden con las claves del JSON.
 */
data class UserDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val website: String?,
    val address: AddressDto?,
    val company: CompanyDto?
)

data class AddressDto(
    val street: String?,
    val suite: String?,
    val city: String?
)

data class CompanyDto(
    val name: String?,
    val catchPhrase: String?
)

data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
