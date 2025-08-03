package dev.lssoftware.urlshortenerapp.data

import dev.lssoftware.urlshortenerapp.network.AliasRequestDto
import dev.lssoftware.urlshortenerapp.network.UrlShortenerAPI

class UrlShortenerRepositoryImpl : UrlShortenerRepository {
    private val urlShortenerService = UrlShortenerAPI.create()

    override suspend fun shortenUrl(originalUrl: String): Result<String> {
    return try {
            val response = urlShortenerService.shortenUrl(AliasRequestDto(url = originalUrl))
            if (response.isSuccessful) {
                val shortenedUrl = response.body()?.alias ?: throw Exception("Shortened URL not found")
                Result.success(shortenedUrl)
            } else {
                Result.failure(Exception("Error shortening URL: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}