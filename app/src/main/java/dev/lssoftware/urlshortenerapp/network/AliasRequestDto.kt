package dev.lssoftware.urlshortenerapp.network

import kotlinx.serialization.Serializable

@Serializable
data class AliasRequestDto(
    val url: String,
)
