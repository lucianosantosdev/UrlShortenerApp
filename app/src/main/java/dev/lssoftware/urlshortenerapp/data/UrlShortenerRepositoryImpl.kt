package dev.lssoftware.urlshortenerapp.data

import dev.lssoftware.urlshortenerapp.network.UrlShortenerAPI

class UrlShortenerRepositoryImpl(
    private val urlShortenerService: UrlShortenerAPI
) : UrlShortenerRepository {
    override suspend fun shortUrl(originalUrl: String): Result<String> {
        TODO("Not yet implemented")
    }
}