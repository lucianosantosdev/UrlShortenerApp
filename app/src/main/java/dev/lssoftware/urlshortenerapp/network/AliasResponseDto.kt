package dev.lssoftware.urlshortenerapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AliasResponseDto(
    val alias: String,
    @SerialName("_links") val links: LinkDto
)

@Serializable
data class LinkDto(
    val self: String,
    val short: String
)