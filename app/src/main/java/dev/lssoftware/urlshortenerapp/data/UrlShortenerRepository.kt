package dev.lssoftware.urlshortenerapp.data

interface UrlShortenerRepository {
    suspend fun shortUrl(originalUrl: String): Result<String>
}