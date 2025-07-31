package dev.lssoftware.urlshortenerapp.network

import retrofit2.Response
import retrofit2.http.POST

interface UrlShortenerAPI {
    @POST("api/alias")
    suspend fun shortUrl(
        aliasRequest: AliasRequestDto
    ): Response<AliasResponseDto>
}