package dev.lssoftware.urlshortenerapp.data

interface UrlShortenerRepository {
    suspend fun shortenUrl(originalUrl: String): Result<String>
}